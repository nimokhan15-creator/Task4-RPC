
package com.example.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class JsonRpcClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        String name = args.length > 0 ? args[0] : "Ada";

        ObjectNode requestBody = MAPPER.createObjectNode();
        requestBody.put("jsonrpc", "2.0");
        requestBody.put("method", "sayHello");
        requestBody.putObject("params").put("name", name);
        requestBody.put("id", 1);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/rpc"))
                .timeout(Duration.ofSeconds(2))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        MAPPER.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());

        JsonNode json = MAPPER.readTree(response.body());

        if (json.has("error")) {
            System.err.println("JSON-RPC error: "
                    + json.path("error").path("message").asText());
        } else {
            System.out.println(json.path("result").asText());
        }
    }
}
