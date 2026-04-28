# Smart Campus API

This project is a RESTful API developed for the **5COSC022W Client-Server Architectures** coursework. It is implemented using **JAX-RS (Jersey)** and manages three main resources in a Smart Campus environment: **Rooms**, **Sensors**, and **Sensor Readings**. The API follows REST principles, uses nested resources for reading history, and includes custom exception mapping and logging for better reliability and maintainability.

---

## Overview

The API allows clients to:

- discover the main API entry point and available resource collections
- create and retrieve rooms
- register sensors and link them to existing rooms
- filter sensors by type
- add and retrieve historical readings for a specific sensor
- prevent invalid operations through custom error handling

This implementation uses **in-memory storage only** through Java collections, as required by the coursework. No database is used.

---

## Technology Stack

- **Java 21**
- **Maven**
- **JAX-RS / Jersey 3.1.5**
- **Jackson JSON provider**
- **WAR packaging** for deployment on a servlet container
- **In-memory collections** using `ConcurrentHashMap` and `CopyOnWriteArrayList`

---

## Project Structure

```text
src/main/java/com/smartcampus/api/
├── config/
│   └── ApplicationConfig.java
├── exception/
│   ├── LinkedResourceNotFoundException.java
│   ├── RoomNotEmptyException.java
│   └── SensorUnavailableException.java
├── filter/
│   └── ApiLoggingFilter.java
├── mapper/
│   ├── GlobalExceptionMapper.java
│   ├── LinkedResourceNotFoundExceptionMapper.java
│   ├── RoomNotEmptyExceptionMapper.java
│   └── SensorUnavailableExceptionMapper.java
├── model/
│   ├── ApiError.java
│   ├── ApiMessage.java
│   ├── Room.java
│   ├── Sensor.java
│   └── SensorReading.java
├── resource/
│   ├── DiscoveryResource.java
│   ├── RoomResource.java
│   ├── SensorResource.java
│   └── SensorReadingResource.java
└── store/
    └── DataStore.java
```

## Clone the Repository

Clone the project from GitHub:

```bash
git clone https://github.com/Manakal2000/test123.git
cd test123
```
---

## API Base Path

The JAX-RS application is configured with:

```java
@ApplicationPath("/api/v1")
```

When deployed locally as the `SmartCampusAPI` web application, the base URL is:

```text
http://localhost:8080/SmartCampusAPI/api/v1
```

---

## Main Features

### 1. Discovery Endpoint
Provides basic API metadata and top-level resource links.

**Example Response**
```json
{
  "version": "v1",
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```
---

### 2. Room Management
Supports creating rooms, retrieving all rooms, retrieving a single room, and deleting a room if it has no assigned sensors.

### 3. Sensor Management
Supports creating sensors, retrieving sensors, retrieving a single sensor, and filtering sensors by type using a query parameter.

### 4. Nested Reading Resource
Supports retrieving and adding readings under:

```text
/sensors/{sensorId}/readings
```

A successful reading creation also updates the parent sensor's `currentValue`.

### 5. Error Handling and Logging
The API includes:

- custom exception mappers for business-rule failures
- a global exception mapper for unexpected runtime errors
- request/response logging using JAX-RS filters

---

## How to Build and Run

### Prerequisites

Make sure you have:

- **JDK 21**
- **Maven 3.x**
- a servlet container such as **Payara Server**

### Build the project

Run the following inside the project folder:

```bash
mvn clean package
```

This will generate a WAR file in the `target/` directory.

### Deploy the project

Deploy the generated file:

```text
target/SmartCampusAPI-1.0-SNAPSHOT.war
```

to your servlet container.

### Example local URL after deployment

```text
http://localhost:8080/SmartCampusAPI/api/v1
```

## Quick Run Summary
1.Clone the repository
2.Build the project
3.Deploy WAR file
4.Open API in browser or Postman

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1` | Discovery endpoint |
| GET | `/api/v1/rooms` | Get all rooms |
| POST | `/api/v1/rooms` | Create a new room |
| GET | `/api/v1/rooms/{roomId}` | Get one room by ID |
| DELETE | `/api/v1/rooms/{roomId}` | Delete a room if it has no sensors |
| GET | `/api/v1/sensors` | Get all sensors |
| GET | `/api/v1/sensors?type=Temperature` | Filter sensors by type |
| GET | `/api/v1/sensors/{sensorId}` | Get one sensor by ID |
| POST | `/api/v1/sensors` | Create a new sensor |
| GET | `/api/v1/sensors/{sensorId}/readings` | Get readings for a sensor |
| POST | `/api/v1/sensors/{sensorId}/readings` | Add a reading to a sensor |

---
## Sample JSON Response

```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 27.5,
  "roomId": "LIB-301"
}
```

## System Flow

Client → API Endpoint → Resource Class → DataStore → JSON Response

## Design Decisions
- Used thread-safe collections (ConcurrentHashMap) for shared data
- Used sub-resource pattern to reduce complexity
- Used exception mappers for structured error handling
- Used query parameters for flexible filtering
- Used centralized logging instead of manual logging

## Sample curl Commands

### 1. Get API discovery information

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1
```

### 2. Create a room

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":120}'
```

### 3. Get all rooms

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 4. Create a sensor linked to an existing room

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":25.5,"roomId":"LIB-301"}'
```

### 5. Filter sensors by type

```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=Temperature"
```

### 6. Add a sensor reading

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":27.8}'
```

### 7. Try deleting a room that still has assigned sensors

```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

---

## Error Handling

The API returns JSON error responses instead of raw server errors.

| Scenario | Status Code | Description |
|---|---|---|
| Room still contains assigned sensors | `409 Conflict` | Triggered by `RoomNotEmptyException` |
| Sensor references a room that does not exist | `422 Unprocessable Entity` | Triggered by `LinkedResourceNotFoundException` |
| Reading posted to a maintenance sensor | `403 Forbidden` | Triggered by `SensorUnavailableException` |
| Missing room or sensor resource | `404 Not Found` | Returned directly from resource methods |
| Invalid request body or missing required fields | `400 Bad Request` | Validation failure |
| Unexpected runtime failure | `500 Internal Server Error` | Handled by global exception mapper |

---

## Logging

The `ApiLoggingFilter` implements both request and response filters. It logs:

- the HTTP method
- the request URI
- the final response status code

This keeps logging centralized and avoids repeating log statements in every resource method.

---

## Report Answers

### Part 1 – Service Architecture & Setup

**1. Explain the default lifecycle of a JAX-RS resource class. Is a new instance created for every request or is it treated as a singleton? How does this affect in-memory data structures?**

By default, JAX-RS resource classes follow a **per-request lifecycle**. This means a new instance of the resource class is typically created for each incoming HTTP request rather than using one shared singleton instance. This is useful because it reduces the risk of accidental shared mutable state inside resource objects.

However, this project stores application data in shared in-memory collections, so the data itself must live outside the resource class. To handle this safely, the implementation uses shared static collections inside `DataStore`, including thread-safe structures such as `ConcurrentHashMap` and `CopyOnWriteArrayList`. These collections allow multiple concurrent requests to read and modify data without easily corrupting the application state.

---

**2. Why is hypermedia considered a hallmark of advanced RESTful design? How does it benefit client developers?**

Hypermedia, often described through HATEOAS, improves RESTful design by allowing the server to guide clients through the API using links inside responses. Instead of forcing the client to hard-code every endpoint, the API can return resource locations such as `/api/v1/rooms` and `/api/v1/sensors` from the discovery endpoint.

This benefits client developers because the API becomes easier to explore and less tightly coupled to fixed URL knowledge. If the API grows in future, clients can rely more on server-provided navigation and less on external documentation alone.

---

### Part 2 – Room Management

**3. When returning a list of rooms, what are the implications of returning only IDs versus full room objects?**

Returning only room IDs reduces payload size and uses less bandwidth, which can be useful in very large systems. However, it forces the client to make extra requests if full room details are needed.

Returning full room objects increases the response size but makes the API easier to use because the client receives all important information in one request. In this project, returning full room objects is a practical choice because it improves clarity and simplicity for testing and demonstration.

---

**4. Is the DELETE operation idempotent in this implementation? Justify your answer.**

Yes, the DELETE operation is idempotent in terms of the final system state. If a room is successfully deleted once, sending the same DELETE request again will not delete anything further because the room no longer exists. The first request may return success, while later requests may return `404 Not Found`, but the overall result remains the same: the room is absent from the system.

---

### Part 3 – Sensor Operations & Linking

**5. What happens if a client sends data in a format other than `application/json` when the method uses `@Consumes(MediaType.APPLICATION_JSON)`?**

When a resource method is annotated with `@Consumes(MediaType.APPLICATION_JSON)`, JAX-RS expects the request body to be sent as JSON. If a client sends another content type such as `text/plain` or `application/xml`, the framework will reject the request before it reaches the method logic. This typically results in an HTTP `415 Unsupported Media Type` response.

This behaviour helps enforce consistent input handling and prevents the method from receiving data in an unsupported format.

---

**6. Why is using `@QueryParam` generally better for filtering a collection than putting the filter value in the path?**

A query parameter is more appropriate for filtering because the client is still requesting the same collection resource, only with an optional filter applied. For example, `/sensors?type=Temperature` still refers to the sensors collection.

If the filter is placed in the path, such as `/sensors/type/Temperature`, it can wrongly suggest that `type/Temperature` is a separate resource hierarchy rather than a filtered view of the same collection. Query parameters are also easier to extend when multiple filters are needed.

---

### Part 4 – Deep Nesting with Sub-Resources

**7. What are the architectural benefits of the Sub-Resource Locator pattern?**

The Sub-Resource Locator pattern helps keep the API modular by delegating nested paths to dedicated classes. In this project, `SensorResource` handles the main sensor collection, while `SensorReadingResource` is responsible for `/sensors/{sensorId}/readings`.

This separation improves readability, reduces class size, and makes the code easier to maintain. Instead of building one very large controller class that handles every nested path, each resource class focuses on a clear responsibility.

---

### Part 5 – Advanced Error Handling, Exception Mapping & Logging

**8. Why is HTTP 422 often more semantically accurate than 404 when a sensor references a room ID that does not exist?**

A `404 Not Found` response usually means the target URL itself does not exist. In this case, the request is still being sent to a valid endpoint such as `/api/v1/sensors`. The real problem is that the JSON body contains a `roomId` value that refers to a missing linked resource.

For that reason, `422 Unprocessable Entity` is often more accurate. It communicates that the request format is valid, but the meaning of the data is unacceptable because the referenced room does not exist.

---

**9. What are the cybersecurity risks of exposing internal Java stack traces to clients?**

Exposing stack traces can reveal sensitive internal details such as class names, package names, method names, server structure, and implementation libraries. An attacker could use this information to understand the architecture of the application and look for weaknesses more effectively.

Returning a generic `500 Internal Server Error` message while logging the full technical details only on the server side is safer because it limits what external users can learn about the system.

---

**10. Why is it advantageous to use JAX-RS filters for logging rather than adding log statements to every resource method?**

Logging is a cross-cutting concern because it applies to all endpoints. Using JAX-RS filters allows the application to handle request and response logging in one centralized place. This avoids code duplication, keeps resource methods cleaner, and makes future logging changes easier to manage.

In this project, the logging filter automatically records request method, request URI, and response status code for every API call.

---

## Notes

- This API is designed for coursework demonstration and uses in-memory storage only.
- Data resets when the server or deployed application is restarted.
- Requests in Postman should generally be executed in a logical order because sensors depend on rooms, and readings depend on sensors.

