package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css",
                "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
        final var server = new Server(validPaths);
        server.addHandler("GET", "/messages", (request, responseStream) -> {
            String last = request.getQueryParam("last");
            String response = "GET messages response. Last: " + last;
            responseStream.write(("HTTP/1.1 200 OK\r\n\r\n" + response).getBytes());
            responseStream.flush();
        });


        server.addHandler("POST", "/messages", (request, responseStream) -> {
            String last = request.getQueryParam("last");
            String response = "POST messages response. Last: " + last;
            responseStream.write(("HTTP/1.1 200 OK\r\n\r\n" + response).getBytes());
            responseStream.flush();
        });

        // Запуск сервера
        server.listen(9999);
    }
}
