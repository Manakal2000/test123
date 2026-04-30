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

However, because each resource instance is short-lived, instance variables are not suitable for storing application data that must survive across requests. In this Smart Campus API, shared data such as rooms, sensors, and sensor readings is stored in a central `DataStore` class rather than inside resource classes. Thread-safe collections such as `ConcurrentHashMap` and `CopyOnWriteArrayList` are used so that multiple requests can access and update data safely. This design helps prevent race conditions, where multiple requests try to change the same data at the same time, while keeping the API lightweight and using in memory data instead of a database. 


---

#### Question 2: Why is the provision of ”Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation? 

Hypermedia, expressed through HATEOAS, is an advanced REST concept where responses include links that show clients what actions are available. Instead of requiring prior knowledge of all endpoints, the client can follow these links to interact with the API. This reduces tight coupling, as clients do not depend on fixed URLs and can adapt more easily to changes in the system. 

In this project, the discovery endpoint provides API metadata and links to important resource collections such as `/api/v1/rooms` and `/api/v1/sensors`. This makes the API easier to understand because the client can start from the root endpoint and discover available resources from there.

For client developers, a key advantage is that they do not need to rely on fixed URLs or separate documentation. As the API evolves, clients can use links returned by the server to move through available features. This supports easier maintenance, greater flexibility, and better discovery. Even though this project applies hypermedia in a basic way, the discovery endpoint still reflects the RESTful approach of making an API easier to explore and navigate. 


---

### Part 2 – Room Management

#### Question 3: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.

When returning a list of rooms, returning only room IDs would reduce the response payload size because the API would only send values such as `ROOM-101` or `ROOM-102`. This is more bandwidth efficient, especially if the system contains a very large number of rooms. However, it increases client-side processing because the client would need to send extra `GET /rooms/{roomId}` requests to retrieve details such as the room name, capacity, and assigned sensor IDs. Returning full room objects uses more network bandwidth because each response includes all room fields, which are `id`, `name`, `capacity`, and `sensorIds`. However, it is easier for the client because all required room metadata is available from one request without additional round trips.

In this project implementation, `GET /api/v1/rooms` returns full room objects. The `RoomResource.getRooms()` method creates a list from `DataStore.rooms.values()` and returns it as the response body. This means the client receives complete room information directly, including the room ID, human readable name, capacity, and the list of assigned sensor IDs.

This design is suitable for this coursework because the API uses in-memory data structures and a small demonstration dataset, so the bandwidth cost is acceptable. It also makes the API easier to test in Postman and clearer for client developers, because they can see the full state of each room in a single response.


---


#### Question 4: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

Yes, the DELETE operation is idempotent in terms of the final system state. An idempotent operation means that sending the same request multiple times should produce the same final state as sending it once.
In this implementation, when a room is deleted successfully, it is removed from the in-memory `DataStore`.

If the same DELETE request is sent again, the room is already absent, so the second request does not remove anything further. The response may change from success to `404 Not Found`, but the system’s state does not change, as the room is still absent. 

This is important in RESTful API design because requests may be repeated due to client or network retries. Idempotency ensures that repeating a DELETE operation does not cause unintended effects. In this API, a rule prevents deleting a room if sensors are linked to it. In such cases, a `409 Conflict` response is returned to maintain data integrity and avoid invalid sensor references.


---

### Part 3 – Sensor Operations & Linking

#### Question 5: We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells the JAX-RS runtime that the resource method only accepts request bodies with the media type `application json`. This is especially important for POST methods because the request body must be converted into a Java object, which is `Room`, `Sensor`, or `SensorReading`.

If a client sends data using another content type, such as `text` or `application xml`, the request does not match the media type expected by the method. JAX-RS handles this mismatch before the request reaches the resource method. The framework rejects the request and typically returns HTTP `415 Unsupported Media Type`.

This behaviour blocks unsupported data from reaching the application logic. It also clarifies the API contract by requiring JSON for creating rooms, sensors, or readings. This improves reliability, reduces parsing issues, and ensures consistent input handling across the API. 


---

#### Question 6: Why is using `@QueryParam` generally better for filtering a collection than putting the filter value in the URL path?

Using `@QueryParam` is better for filtering because it represents optional criteria applied to a collection, rather than defining a new resource path. For example, `/api/v1/sensors?type=Temperature` still refers to the sensors collection, with a filter applied.

In contrast, placing filter values in the path (e.g., `/api/v1/sensors/type/Temperature`) can incorrectly imply a hierarchical resource structure. Path parameters are intended for identifying specific resources, while query parameters are used for filtering and searching.

In JAX-RS, `@QueryParam` allows automatic binding of query parameters to method arguments, enabling flexible and dynamic filtering without creating multiple endpoints. It also supports combining multiple filters in a single request, improving scalability and avoiding endpoint proliferation.


---

### Part 4 – Deep Nesting with Sub-Resources

#### Question 7: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

The Sub-Resource Locator pattern in JAX-RS allows a parent resource to delegate handling of nested paths to another resource class. Instead of defining all endpoints in a single class, a method annotated with `@Path` returns a sub-resource instance that processes the remaining request path.

In this API, the endpoint `/api/v1/sensors/{sensorId}/readings` is handled by `SensorResource`, which delegates reading-related operations to `SensorReadingResource`. This reflects the hierarchical relationship between sensors and their readings.

This approach keeps sensor and reading logic in separate classes, improving separation of concerns and avoiding large, complex controllers. It also supports dynamic request handling, where JAX-RS matches the main path and passes the remaining path to the sub-resource. As a result, the API becomes more modular, scalable, and easier to maintain since new nested endpoints can be added within sub-resource classes without modifying the parent resource.


---

### Part 5 – Advanced Error Handling, Exception Mapping & Logging

#### Question 8: Why is HTTP 422 often more semantically accurate than 404 when the issue is a missing reference inside a valid JSON payload?

HTTP `404 Not Found` is used when the requested resource or URL does not exist. For example, requesting a room by an ID that is not present justifies a `404`.

In this case, the request is sent to a valid endpoint `(/api/v1/sensors)` with a valid JSON structure. The issue is that the `roomId` in the payload does not refer to an existing resource.

Therefore, HTTP `422 Unprocessable Entity` is more appropriate because the server understands the request but cannot process it due to invalid domain data. The problem lies in the payload, not the endpoint. In the project the `SensorResource` checks whether the roomId exists and throws a `LinkedResourceNotFoundException` if it is missing. This is then converted into a `422 response`, indicating that the referenced resource is invalid. 


---

#### Question 9: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

Exposing internal Java stack traces is dangerous because stack traces can reveal sensitive implementation details. They may show package names, class names, method names, file paths, framework versions, and internal execution flow. This information can help attackers understand how the application is structured.

An attacker could use these details to identify weak points, search for known vulnerabilities in the technologies being used, or craft more targeted attacks. Even if the error itself seems harmless, the information disclosed by a stack trace can reduce the security of the system.

A safer approach is to return a generic `HTTP 500 Internal Server` Error response to the client while logging the detailed technical error only on the server side. This protects internal implementation details while still allowing developers to debug problems using server logs. In this project, the `global exception mapper` acts as a safety net to prevent raw Java errors from leaking to API users.


---

#### Question 10: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

Logging is considered a cross-cutting concern because it applies to all API endpoints rather than a single resource. If logging is implemented manually using `Logger.info()` inside each resource method, it leads to code duplication, inconsistent logging behaviour, and increased maintenance effort. Any change to logging logic would require modifying multiple classes. JAX-RS filters provide a centralized and structured approach to handling logging. 

A `ContainerRequestFilter` can intercept incoming HTTP requests and log details such as the request method and URI, while a `ContainerResponseFilter` can capture outgoing responses, including status codes. This ensures consistent logging across all endpoints without modifying individual resource methods. 

From an architectural perspective, filters are part of the JAX-RS request-response pipeline, meaning they execute before and after resource methods. This allows logging to be applied uniformly, even for requests that fail validation or do not reach the resource layer. 

In this Smart Campus API, the `ApiLoggingFilter` implements both request and response filters to log key request details and response outcomes. This approach improves maintainability, enforces separation of concerns, and enhances observability of API behaviour during testing and debugging


---