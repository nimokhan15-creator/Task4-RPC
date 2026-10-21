# Task 4: gRPC and JSON-RPC in Java

## Overview

This project implements a Greeter service in two ways using Java 17 and Maven.

Both implementations provide `sayHello(name)` and return `Hello, <name>`.

## Requirements

- Java 17
- Maven

## How to run

1. Run all tests from the project root:

   ```bash
   mvn clean test
   ```

2. Run `GreeterServer` in IntelliJ. It listens on port `50051`.
3. Run `GreeterClient` in IntelliJ. The expected output is `Hello, Ada`.
4. Run `JsonRpcServer` in IntelliJ. It listens on port `8080`.
5. Run `JsonRpcClient` in IntelliJ. The expected output is `Hello, Ada`.

## Error handling

- gRPC returns `INVALID_ARGUMENT` when the name is empty.
- JSON-RPC returns an error object with code `-32602` when the name is empty.
- Both clients use two-second timeouts.

## Comparison

| Feature | gRPC | JSON-RPC over HTTP |
|---|---|---|
| Message format | Binary Protocol Buffers | Text-based JSON |
| Transport | HTTP/2 | HTTP |
| Speed | Designed for efficient service communication | Simple, but JSON parsing adds overhead |
| Ease of use | Requires a `.proto` file and generated code | Easy to inspect and test with HTTP tools |
| Tooling | Generated Java stubs, deadlines and status codes | HTTP client and JSON libraries |

Performance depends on the workload and network conditions.

## Project structure

- `grpc/` — gRPC protocol, server, client and tests.
- `rpc/` — JSON-RPC server, client and tests.
