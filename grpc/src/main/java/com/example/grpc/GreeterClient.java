
package com.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

public class GreeterClient {

    public static void main(String[] args) {
        String name = args.length > 0 ? args[0] : "Ada";

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        try {
            GreeterGrpc.GreeterBlockingStub stub =
                    GreeterGrpc.newBlockingStub(channel)
                            .withDeadlineAfter(2, TimeUnit.SECONDS);

            HelloReply reply = stub.sayHello(
                    HelloRequest.newBuilder()
                            .setName(name)
                            .build()
            );

            System.out.println(reply.getMessage());

        } catch (StatusRuntimeException e) {
            System.err.println("gRPC error: " + e.getStatus());
        } finally {
            channel.shutdownNow();
        }
    }
}
