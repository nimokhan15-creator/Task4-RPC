
package com.example.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class JsonRpcServer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress("localhost", 8080), 0);

        server.createContext("/rpc", JsonRpcServer::handleRequest);
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("JSON-RPC server started on port 8080");
    }

    static void handleRequest(HttpExchange exchange) throws IOException {
        ObjectNode response = MAPPER.createObjectNode();

        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                response.put("jsonrpc", "2.0");
                response.putNull("id");
                response.putObject("error")
                        .put("code", -32600)
                        .put("message", "POST required");

                send(exchange, 405, response);
                return;
            }

            JsonNode request = MAPPER.readTree(exchange.getRequestBody());

            response.put("jsonrpc", "2.0");
            JsonNode id = request.get("id");

            if (id == null) {
                response.putNull("id");
            } else {
                response.set("id", id);
            }

            String method = request.path("method").asText();
            String name = request.path("params")
                    .path("name").asText("").trim();

            if (!"sayHello".equals(method)) {
                response.putObject("error")
                        .put("code", -32601)
                        .put("message", "Method not found");
            } else if (name.isEmpty()) {
                response.putObject("error")
                        .put("code", -32602)
                        .put("message", "Name must not be empty");
            } else {
                response.put("result", "Hello, " + name);
            }

            send(exchange, 200, response);

        } catch (Exception e) {
            response.removeAll();
            response.put("jsonrpc", "2.0");
            response.putNull("id");
            response.putObject("error")
                    .put("code", -32700)
                    .put("message", "Invalid JSON request");

            send(exchange, 200, response);
        }
    }

    private static void send(
            HttpExchange exchange,
            int status,
            ObjectNode response) throws IOException {

        byte[] body = MAPPER.writeValueAsBytes(response);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=utf-8");

        exchange.sendResponseHeaders(status, body.length);

        try (var output = exchange.getResponseBody()) {
            output.write(body);
        }
    }
}
