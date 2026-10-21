
package com.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GreeterTest {

    private static io.grpc.Server server;
    private static ManagedChannel channel;
    private static GreeterGrpc.GreeterBlockingStub stub;

    @BeforeAll
    static void startServer() throws Exception {
        server = io.grpc.ServerBuilder.forPort(0)
                .addService(new GreeterServer.GreeterService())
                .build()
                .start();

        channel = ManagedChannelBuilder
                .forAddress("localhost", server.getPort())
                .usePlaintext()
                .build();

        stub = GreeterGrpc.newBlockingStub(channel);
    }

    @AfterAll
    static void stopServer() {
        if (channel != null) {
            channel.shutdownNow();
        }
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Test
    void sayHelloReturnsGreeting() {
        HelloReply reply = stub
                .withDeadlineAfter(2, java.util.concurrent.TimeUnit.SECONDS)
                .sayHello(HelloRequest.newBuilder()
                        .setName("Ada")
                        .build());

        assertEquals("Hello, Ada", reply.getMessage());
    }

    @Test
    void emptyNameReturnsInvalidArgument() {
        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> stub
                        .withDeadlineAfter(2,
                                java.util.concurrent.TimeUnit.SECONDS)
                        .sayHello(HelloRequest.newBuilder()
                                .setName("")
                                .build())
        );

        assertEquals(Status.Code.INVALID_ARGUMENT,
                exception.getStatus().getCode());
    }
}
