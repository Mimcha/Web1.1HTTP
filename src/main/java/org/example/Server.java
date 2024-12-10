
package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final Map<String, Map<String, Handler>> handlers = new HashMap<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(64);
    private final List<String> validPaths;

    public Server(List<String> validPaths) {
        this.validPaths = validPaths;
    }

    public void addHandler(String method, String path, Handler handler) {
        handlers.computeIfAbsent(method, k -> new HashMap<>()).put(path, handler);
    }

    public void listen(int port) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.submit(() -> handleConnection(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket socket) {
        try (

                BufferedOutputStream responseStream = new BufferedOutputStream(socket.getOutputStream());
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String requestLine = reader.readLine();
            if (requestLine == null) return;

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length != 3) return;

            String method = requestParts[0];
            String path = requestParts[1];

            Map<String, String> headers = new HashMap<>();
            String headerLine;
            while (!(headerLine = reader.readLine()).isEmpty()) {
                String[] headerParts = headerLine.split(": ");
                headers.put(headerParts[0], headerParts[1]);
            }

            InputStream bodyStream = null;
            if ("POST".equalsIgnoreCase(method)) {
                bodyStream = new ByteArrayInputStream(inputStream.readAllBytes());
            }

            Request request = new Request(method, path, headers, bodyStream);

            // Обработка запроса с использованием зарегистрированного обработчика
            Map<String, Handler> methodHandlers = handlers.get(method);
            if (methodHandlers != null) {
                Handler handler = methodHandlers.get(path);
                if (handler != null) {
                    handler.handle(request, responseStream);
                } else {
                    send404(responseStream);
                }
            } else {
                send404(responseStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send404(BufferedOutputStream responseStream) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        responseStream.write(response.getBytes());
        responseStream.flush();

    }
}
