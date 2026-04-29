# Smart Campus API

Module: 5COSC022W Client-Server Architectures | University of Westminster | 2025/26

---

## Project Overview

The Smart Campus REST API is a RESTful web service designed to support a university campus system. It allows users to manage different campus locations by creating rooms, assigning sensors, recording environmental data, and monitoring current conditions.
The API is structured around three main, related resources:

**Rooms** - represent physical spaces within the campus

**Sensors** - devices that collect environmental data (such as temperature or CO₂)

**Sensor Readings** - time-based values recorded by sensors

The system enforces key business rules to maintain consistency. Every sensor must be linked to an existing room, rooms cannot be deleted if they contain sensors, and sensors in **MAINTENANCE** status are not allowed to record new readings.
All data is stored in memory using thread-safe Java collections, as required, without using a database.

---

## Architecture Summary

The application uses a centralized in-memory data store (`DataStore`) that is shared across all JAX-RS resource classes. Since resource classes follow a per-request lifecycle, this shared store ensures that application data remains consistent across multiple requests.

To handle concurrent access, thread-safe collections such as `ConcurrentHashMap` and `CopyOnWriteArrayList` are used. This allows multiple HTTP requests to safely read and update data without causing race conditions.

All data in the system is managed in memory during runtime, and resources such as rooms, sensors, and readings are created dynamically through API requests.

Overall, this architecture keeps the application lightweight, avoids the need for a database, and satisfies the coursework requirement of using in-memory data storage.

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

## Steps on How to Build and Run The Project

### 1. Prerequisites

Make sure you have:

- **JDK 21**
- **Maven 3.x**
- a servlet container such as **Payara Server**

### 2. Build the project

Run the following inside the project folder:

```bash
mvn clean package
```

This will generate a WAR file in the `target/` directory.

### 3. Deploy the project

Deploy the generated file:

```text
target/SmartCampusAPI-1.0-SNAPSHOT.war
```

to your servlet container.

### 4. Example local URL after deployment

```text
http://localhost:8080/SmartCampusAPI/api/v1
```

## 5. Quick Run Summary
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

#### Question 1: Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

In JAX-RS, the default lifecycle of a resource class is per-request. This means that the JAX-RS runtime normally creates a new instance of a resource class for every incoming HTTP request. This behaviour is useful because each request is handled independently, and instance-level data inside one request is not automatically shared with another request. As a result, the risk of accidental shared mutable state inside resource objects is reduced.

If the runtime treated resource classes as singletons by default, the same resource object would be reused by many concurrent requests. In that case, any mutable instance variable could be accessed by multiple threads at the same time, which could cause race conditions, inconsistent values, or data corruption. Therefore, the per-request lifecycle provides safer request isolation.

However, because each resource instance is short-lived, instance variables are not suitable for storing application data that must survive across requests. In this Smart Campus API, shared data such as rooms, sensors, and sensor readings is stored in a central `DataStore` class rather than inside resource classes. Thread-safe collections such as `ConcurrentHashMap` and `CopyOnWriteArrayList` are used so that multiple requests can access and update data safely. This design helps prevent race conditions while still keeping the API lightweight and compliant with the coursework requirement of using in-memory data structures instead of a database.

---

#### Question 2: Why is the provision of ”Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation? 

Hypermedia, also known through the REST concept HATEOAS, is considered an advanced RESTful design principle because it allows the API response itself to guide the client. Instead of expecting the client developer to manually know every endpoint in advance, the server can return links to related resources and actions.

In this project, the discovery endpoint provides API metadata and links to important resource collections such as `/api/v1/rooms` and `/api/v1/sensors`. This makes the API easier to understand because the client can start from the root endpoint and discover available resources from there.

The main benefit for client developers is reduced dependency on hardcoded URLs and static documentation. If the API grows or changes in the future, clients can rely more on server-provided links to navigate the system. This improves maintainability, flexibility, and discoverability. Although this coursework implementation uses a simplified form of hypermedia, the discovery endpoint still demonstrates the RESTful idea of making an API easier to explore and navigate.

---

### Part 2 – Room Management

#### Question 3: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.

Returning only room IDs results in a smaller response payload, which reduces network bandwidth usage and improves performance, especially in large-scale systems. However, this approach increases client-side complexity because additional requests are required to retrieve full room details, leading to more round-trip communication.

In contrast, returning full room objects increases the response size but provides all necessary information in a single request. This reduces the number of API calls and simplifies client-side processing.

In this Smart Campus API, returning full room objects is more suitable because the dataset is relatively small and the focus is on clarity and ease of use. It allows clients to access complete room information directly without making multiple requests, improving usability during testing and development.

---

#### Question 4: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

Yes, the DELETE operation in this implementation is idempotent. Idempotency means that executing the same request multiple times results in the same final state on the server.

When a DELETE request is sent for a room, the room is removed from the in-memory `DataStore`. If the same request is repeated, the room no longer exists, so no further changes occur. The response may differ (for example, returning `404 Not Found`), but the system state remains unchanged.

Additionally, the API enforces a business rule that prevents deletion of rooms that still have assigned sensors, returning `409 Conflict`. This ensures data integrity while maintaining idempotent behavior.

---

### Part 3 – Sensor Operations & Linking

#### Question 5: We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch? 

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells the JAX-RS runtime that the resource method only accepts request bodies with the media type `application/json`. This is especially important for POST methods because the request body must be converted into a Java object, such as a `Room`, `Sensor`, or `SensorReading`.

If a client sends data using another content type, such as `text/plain` or `application/xml`, the request does not match the media type expected by the method. JAX-RS handles this mismatch before the request reaches the resource method. The framework rejects the request and typically returns HTTP `415 Unsupported Media Type`.

This behaviour is useful because it prevents incompatible data from entering the application logic. It also makes the API contract clearer: clients must send JSON when creating rooms, sensors, or readings. This improves reliability, reduces parsing errors, and keeps input handling consistent across the API.

---

#### Question 6: Why is using @QueryParam generally considered superior for filtering and searching collections compared to including the filter value in the URL path (e.g., /api/v1/sensors/type/CO2)?

Using `@QueryParam` is generally better for filtering because filtering is an optional modification of a collection request. For example, `/api/v1/sensors?type=Temperature` still represents the sensors collection, but the client is asking for only sensors of a particular type.

If the filter value is placed in the path, such as `/api/v1/sensors/type/Temperature`, it can make the URL look like `type/Temperature` is a separate resource hierarchy. This is less clean from a RESTful design perspective because `type` is not really a unique resource; it is a filtering condition.

Query parameters are also more flexible. If the API later supports more filters, such as status or roomId, they can be added easily, for example `/api/v1/sensors?type=Temperature&status=ACTIVE`. This avoids creating many separate endpoints and keeps the API scalable and easy to maintain.

---

### Part 4 – Deep Nesting with Sub-Resources

#### Question 7: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class? 

The Sub-Resource Locator pattern is useful because it allows nested resource logic to be separated into dedicated classes. In this project, sensors and their readings have a hierarchical relationship: a sensor can have many readings, and readings belong to a specific sensor. This is represented through the nested endpoint `/api/v1/sensors/{sensorId}/readings`.

Instead of placing every nested path inside one large `SensorResource` class, the reading-related logic can be delegated to a separate `SensorReadingResource` class. This improves separation of concerns because `SensorResource` can focus on sensor operations, while `SensorReadingResource` can focus on reading history and adding new readings.

This design makes the code easier to read, maintain, and extend. If future functionality is added, such as deleting readings or retrieving a specific reading by ID, those changes can be handled inside the reading resource without making the main sensor class too large or complex. It also reflects the real-world relationship between resources more clearly, which improves the overall RESTful structure of the API.

---

### Part 5 – Advanced Error Handling, Exception Mapping & Logging

#### Question 8: Why is HTTP 422 often more semantically accurate than 404 when the issue is a missing reference inside a valid JSON payload?

HTTP `404 Not Found` usually means that the requested URL or resource itself does not exist. For example, if a client requests a room by an ID that is not available, a 404 may be suitable.

However, when creating a sensor, the client sends a request to a valid endpoint such as `/api/v1/sensors`. The endpoint exists, and the JSON structure may also be valid. The problem is that the JSON body contains a `roomId` that does not refer to an existing room. In this case, the request is syntactically correct but semantically invalid.

Therefore, HTTP `422 Unprocessable Entity` is more accurate because it tells the client that the server understood the request format but cannot process it due to invalid domain data. This makes the error clearer for API consumers. It shows that the client should correct the referenced `roomId`, not the endpoint URL itself.

---

#### Question 9: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace? 

Exposing internal Java stack traces is dangerous because stack traces can reveal sensitive implementation details. They may show package names, class names, method names, file paths, framework versions, and internal execution flow. This information can help attackers understand how the application is structured.

An attacker could use these details to identify weak points, search for known vulnerabilities in the technologies being used, or craft more targeted attacks. Even if the error itself seems harmless, the information disclosed by a stack trace can reduce the security of the system.

A safer approach is to return a generic HTTP `500 Internal Server Error` response to the client while logging the detailed technical error only on the server side. This protects internal implementation details while still allowing developers to debug problems using server logs. In this project, the global exception mapper acts as a safety net to prevent raw Java errors from leaking to API users.

---

#### Question 10: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

Logging is a cross-cutting concern because it applies to the whole API, not just one endpoint. If logging statements are manually added inside every resource method, the code becomes repetitive, harder to maintain, and easier to make inconsistent. Some methods may log too much, others may log too little, and future changes would require editing many files.

JAX-RS filters provide a cleaner solution because they allow logging to be handled centrally. A `ContainerRequestFilter` can log incoming request details such as HTTP method and URI, while a `ContainerResponseFilter` can log outgoing response status codes. This ensures that all endpoints follow the same logging behaviour.

Filters also improve observability because they operate as part of the request-response pipeline. They can capture requests and responses in a consistent way without mixing logging logic with business logic. This keeps resource classes focused on handling API operations while the filter handles monitoring and diagnostics. In this project, the logging filter improves maintainability and helps track API activity during testing and demonstration.

---