
package com.example.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class GreeterServer {

    public static void main(String[] args)
            throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(50051)
                .addService(new GreeterService())
                .build()
                .start();

        System.out.println("gRPC server started on port 50051");

        Runtime.getRuntime().addShutdownHook(
                new Thread(server::shutdown)
        );

        server.awaitTermination();
    }

    static class GreeterService extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(
                HelloRequest request,
                StreamObserver<HelloReply> responseObserver) {

            String name = request.getName().trim();

            if (name.isEmpty()) {
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription("Name must not be empty")
                                .asRuntimeException()
                );
                return;
            }

            HelloReply reply = HelloReply.newBuilder()
                    .setMessage("Hello, " + name)
                    .build();

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
