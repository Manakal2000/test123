# Smart Campus API

This project is a RESTful API developed for the **5COSC022W Client-Server Architectures** coursework. It is implemented using **JAX-RS (Jersey)** and manages three main resources in a Smart Campus environment: **Rooms**, **Sensors**, and **Sensor Readings**. The API follows REST principles, uses nested resources for reading history, and includes custom exception mapping and logging for better reliability and maintainability.

---

## Project Overview

The Smart Campus REST API is a RESTful web service designed to support a university smart campus environment. It provides a system for managing IoT-based sensors across different campus locations, allowing users to create rooms, assign sensors, record environmental data, and observe real-time conditions.

The API is built around three main resources that are logically connected:

- **Rooms** — represent physical spaces within the campus where sensors are deployed  
- **Sensors** — devices (such as Temperature or CO2 sensors) that collect environmental data  
- **Sensor Readings** — time-based data values captured by sensors  

The system also applies important business rules to ensure consistency. A sensor must always be linked to an existing room, rooms cannot be removed if they still contain sensors, and sensors marked as **MAINTENANCE** are not allowed to accept new readings.

All data in this implementation is stored in memory using thread-safe Java collections, in accordance with the coursework requirements, without using a database.

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
``` id="n2gdbs"
1.Clone the repository
2.Build the project
3.Deploy WAR file
4.Open API in browser or Postman
```
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

#### 1. Explain the default lifecycle of a JAX-RS resource class. Is a new instance created for every request or is it treated as a singleton? How does this affect in-memory data structures?

In JAX-RS, the default lifecycle of a resource class is per-request. This means that the JAX-RS runtime normally creates a new instance of a resource class for every incoming HTTP request. This behaviour is useful because each request is handled independently, and instance-level data inside one request is not automatically shared with another request. As a result, the risk of accidental shared mutable state inside resource objects is reduced.

If the runtime treated resource classes as singletons by default, the same resource object would be reused by many concurrent requests. In that case, any mutable instance variable could be accessed by multiple threads at the same time, which could cause race conditions, inconsistent values, or data corruption. Therefore, the per-request lifecycle provides safer request isolation.

However, because each resource instance is short-lived, instance variables are not suitable for storing application data that must survive across requests. In this Smart Campus API, shared data such as rooms, sensors, and sensor readings is stored in a central `DataStore` class rather than inside resource classes. Thread-safe collections such as `ConcurrentHashMap` and `CopyOnWriteArrayList` are used so that multiple requests can access and update data safely. This design helps prevent race conditions while still keeping the API lightweight and compliant with the coursework requirement of using in-memory data structures instead of a database.

---

#### 2. Why is hypermedia considered a hallmark of advanced RESTful design? How does it benefit client developers?

Hypermedia, also known through the REST concept HATEOAS, is considered an advanced RESTful design principle because it allows the API response itself to guide the client. Instead of expecting the client developer to manually know every endpoint in advance, the server can return links to related resources and actions.

In this project, the discovery endpoint provides API metadata and links to important resource collections such as `/api/v1/rooms` and `/api/v1/sensors`. This makes the API easier to understand because the client can start from the root endpoint and discover available resources from there.

The main benefit for client developers is reduced dependency on hardcoded URLs and static documentation. If the API grows or changes in the future, clients can rely more on server-provided links to navigate the system. This improves maintainability, flexibility, and discoverability. Although this coursework implementation uses a simplified form of hypermedia, the discovery endpoint still demonstrates the RESTful idea of making an API easier to explore and navigate.

---

### Part 2 – Room Management

#### 3. When returning a list of rooms, what are the implications of returning only IDs versus returning full room objects?

Returning only room IDs produces a smaller response payload. This reduces bandwidth usage and can improve performance, especially in very large systems with thousands of rooms. However, it also means the client receives limited information. If the client needs the room name, capacity, or assigned sensors, it must send additional requests for each room. This increases round-trip communication and makes client-side processing more complex.

Returning full room objects increases the size of the response, but it gives the client all important information in one request. This improves usability because the client can immediately display or process room details without making extra API calls.

In this Smart Campus API, returning full room objects is a practical design choice because the coursework focuses on clarity, testing, and demonstrating RESTful resource representation. Since the project uses in-memory data and a manageable dataset, the increased payload size is acceptable. It also makes the API easier to test in Postman and easier for client developers to understand.

---

#### 4. Is the DELETE operation idempotent in your implementation? Provide a detailed justification.

Yes, the DELETE operation is idempotent in terms of the final system state. An idempotent operation means that sending the same request multiple times should produce the same final state as sending it once.

In this implementation, when a room is deleted successfully, it is removed from the in-memory `DataStore`. If the same DELETE request is sent again, the room is already absent, so the second request does not remove anything further. The response may change from success to `404 Not Found`, but the final state of the system remains the same: the room does not exist.

This is important in RESTful API design because clients or networks may sometimes retry requests. Idempotency ensures that repeated DELETE requests do not create unexpected side effects. Additionally, this API includes a business rule that prevents deleting a room if sensors are still assigned to it. In that case, the API returns `409 Conflict`, preserving data integrity and preventing orphaned sensor references.

---

### Part 3 – Sensor Operations & Linking

#### 5. What happens if a client sends data in a format other than `application/json` when the method uses `@Consumes(MediaType.APPLICATION_JSON)`?

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells the JAX-RS runtime that the resource method only accepts request bodies with the media type `application/json`. This is especially important for POST methods because the request body must be converted into a Java object, such as a `Room`, `Sensor`, or `SensorReading`.

If a client sends data using another content type, such as `text/plain` or `application/xml`, the request does not match the media type expected by the method. JAX-RS handles this mismatch before the request reaches the resource method. The framework rejects the request and typically returns HTTP `415 Unsupported Media Type`.

This behaviour is useful because it prevents incompatible data from entering the application logic. It also makes the API contract clearer: clients must send JSON when creating rooms, sensors, or readings. This improves reliability, reduces parsing errors, and keeps input handling consistent across the API.

---

#### 6. Why is using `@QueryParam` generally better for filtering a collection than putting the filter value in the URL path?

Using `@QueryParam` is generally better for filtering because filtering is an optional modification of a collection request. For example, `/api/v1/sensors?type=Temperature` still represents the sensors collection, but the client is asking for only sensors of a particular type.

If the filter value is placed in the path, such as `/api/v1/sensors/type/Temperature`, it can make the URL look like `type/Temperature` is a separate resource hierarchy. This is less clean from a RESTful design perspective because `type` is not really a unique resource; it is a filtering condition.

Query parameters are also more flexible. If the API later supports more filters, such as status or roomId, they can be added easily, for example `/api/v1/sensors?type=Temperature&status=ACTIVE`. This avoids creating many separate endpoints and keeps the API scalable and easy to maintain.

---

### Part 4 – Deep Nesting with Sub-Resources

#### 7. What are the architectural benefits of the Sub-Resource Locator pattern?

The Sub-Resource Locator pattern is useful because it allows nested resource logic to be separated into dedicated classes. In this project, sensors and their readings have a hierarchical relationship: a sensor can have many readings, and readings belong to a specific sensor. This is represented through the nested endpoint `/api/v1/sensors/{sensorId}/readings`.

Instead of placing every nested path inside one large `SensorResource` class, the reading-related logic can be delegated to a separate `SensorReadingResource` class. This improves separation of concerns because `SensorResource` can focus on sensor operations, while `SensorReadingResource` can focus on reading history and adding new readings.

This design makes the code easier to read, maintain, and extend. If future functionality is added, such as deleting readings or retrieving a specific reading by ID, those changes can be handled inside the reading resource without making the main sensor class too large or complex. It also reflects the real-world relationship between resources more clearly, which improves the overall RESTful structure of the API.

---

### Part 5 – Advanced Error Handling, Exception Mapping & Logging

#### 8. Why is HTTP 422 often more semantically accurate than 404 when the issue is a missing reference inside a valid JSON payload?

HTTP `404 Not Found` usually means that the requested URL or resource itself does not exist. For example, if a client requests a room by an ID that is not available, a 404 may be suitable.

However, when creating a sensor, the client sends a request to a valid endpoint such as `/api/v1/sensors`. The endpoint exists, and the JSON structure may also be valid. The problem is that the JSON body contains a `roomId` that does not refer to an existing room. In this case, the request is syntactically correct but semantically invalid.

Therefore, HTTP `422 Unprocessable Entity` is more accurate because it tells the client that the server understood the request format but cannot process it due to invalid domain data. This makes the error clearer for API consumers. It shows that the client should correct the referenced `roomId`, not the endpoint URL itself.

---

#### 9. From a cybersecurity standpoint, what are the risks of exposing internal Java stack traces to external API consumers?

Exposing internal Java stack traces is dangerous because stack traces can reveal sensitive implementation details. They may show package names, class names, method names, file paths, framework versions, and internal execution flow. This information can help attackers understand how the application is structured.

An attacker could use these details to identify weak points, search for known vulnerabilities in the technologies being used, or craft more targeted attacks. Even if the error itself seems harmless, the information disclosed by a stack trace can reduce the security of the system.

A safer approach is to return a generic HTTP `500 Internal Server Error` response to the client while logging the detailed technical error only on the server side. This protects internal implementation details while still allowing developers to debug problems using server logs. In this project, the global exception mapper acts as a safety net to prevent raw Java errors from leaking to API users.

---

#### 10. Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging rather than manually inserting `Logger.info()` statements inside every resource method?

Logging is a cross-cutting concern because it applies to the whole API, not just one endpoint. If logging statements are manually added inside every resource method, the code becomes repetitive, harder to maintain, and easier to make inconsistent. Some methods may log too much, others may log too little, and future changes would require editing many files.

JAX-RS filters provide a cleaner solution because they allow logging to be handled centrally. A `ContainerRequestFilter` can log incoming request details such as HTTP method and URI, while a `ContainerResponseFilter` can log outgoing response status codes. This ensures that all endpoints follow the same logging behaviour.

Filters also improve observability because they operate as part of the request-response pipeline. They can capture requests and responses in a consistent way without mixing logging logic with business logic. This keeps resource classes focused on handling API operations while the filter handles monitoring and diagnostics. In this project, the logging filter improves maintainability and helps track API activity during testing and demonstration.

---

## Notes

- This API is designed for coursework demonstration and uses in-memory storage only.
- Data resets when the server or deployed application is restarted.
- Requests in Postman should generally be executed in a logical order because sensors depend on rooms, and readings depend on sensors.

