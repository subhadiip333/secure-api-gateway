🔐 Secure API Gateway with AES Encryption (Spring Boot)

This project implements a secure API gateway using Spring Boot, where all incoming requests and outgoing responses are encrypted using AES before reaching the client APIs.

The gateway acts as a wrapper layer that:

Encrypts client requests

Forwards them to backend APIs

Decrypts encrypted responses

Returns safe, readable data to the end user

✨ Key Features

AES-based request & response encryption

Centralized API gateway (wrapper service)

Global exception handling

Sensitive data masking before DB storage

API audit logging (request, response, latency, status)

Correlation ID for request tracking

🛠 Tech Stack

Java

Spring Boot

Spring Data JPA

Oracle DB

REST APIs

AES Cryptography

🎯 Use Case

Designed to protect sensitive data in internal microservice communication without changing existing client APIs significantly.
