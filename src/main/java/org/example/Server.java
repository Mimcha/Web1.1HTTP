package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final List<String> validPaths;
    private final ExecutorService executorService;

    public Server(int port) {
        this.port = port;
        this.validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html",
                "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
        this.executorService = Executors.newFixedThreadPool(64);
    }

    public void start() {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    var socket = serverSocket.accept();
                    executorService.submit(new ConnectionHandler(socket, validPaths));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
    public static void main(String[] args) {
        new Server(9999).start();
    }
}
