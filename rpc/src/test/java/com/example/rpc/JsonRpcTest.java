
package com.example.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class JsonRpcTest {

    private static HttpServer server;
    private static HttpClient client;
    private static ObjectMapper mapper;
    private static URI endpoint;

    @BeforeAll
    static void startServer() throws Exception {
        mapper = new ObjectMapper();
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        server = HttpServer.create(
                new InetSocketAddress("localhost", 0), 0);
        server.createContext("/rpc", JsonRpcServer::handleRequest);
        server.start();

        endpoint = URI.create(
                "http://localhost:" + server.getAddress().getPort() + "/rpc");
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    private JsonNode call(String name) throws Exception {
        var body = mapper.createObjectNode();
        body.put("jsonrpc", "2.0");
        body.put("method", "sayHello");
        body.putObject("params").put("name", name);
        body.put("id", 1);

        var request = HttpRequest.newBuilder(endpoint)
                .timeout(Duration.ofSeconds(2))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(body)))
                .build();

        var response = client.send(
                request, HttpResponse.BodyHandlers.ofString());

        return mapper.readTree(response.body());
    }

    @Test
    void sayHelloReturnsGreeting() throws Exception {
        JsonNode response = call("Ada");

        assertEquals("Hello, Ada", response.path("result").asText());
        assertFalse(response.has("error"));
    }

    @Test
    void emptyNameReturnsErrorObject() throws Exception {
        JsonNode response = call("");

        assertTrue(response.has("error"));
        assertEquals("Name must not be empty",
                response.path("error").path("message").asText());
    }
}
