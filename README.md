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

Returning only room IDs results in a smaller response payload, which reduces network bandwidth usage and improves response time, particularly in systems with large datasets. This approach minimizes serialization and data transfer costs. However, it introduces additional complexity on the client side, as the client must perform multiple follow-up requests (N+1 request problem) to retrieve full room details. This increases overall latency and can negatively impact performance due to additional HTTP round-trips.

In contrast, returning full room objects provides complete resource representations in a single response. Although this increases payload size and serialization overhead, it reduces the need for additional API calls and simplifies client-side processing. Clients can directly consume the data without implementing extra request logic.

From a RESTful design perspective, returning full representations aligns with the principle of providing self-descriptive and complete resources. It also avoids excessive client-server interactions, improving efficiency in typical use cases.

In this Smart Campus API, returning full room objects is appropriate because the system uses in-memory storage and handles a relatively small dataset. Therefore, the trade-off of slightly larger payload size is acceptable, while reducing client complexity and avoiding the N+1 request problem leads to a more practical and efficient design.
---

#### Question 4: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

Yes, the DELETE operation in this implementation is idempotent, which aligns with HTTP/REST semantics. Idempotency means that multiple identical requests result in the same final state on the server, regardless of how many times the operation is executed.

When a DELETE request is issued for a room, the resource is removed from the in-memory `DataStore`. If the same request is repeated, the resource no longer exists, so no additional state changes occur. Although the HTTP response may differ (e.g., the first request may return a success response such as `204 No Content`, while subsequent requests may return `404 Not Found`), the server state remains unchanged after the initial deletion.

From a REST perspective, idempotency is important for reliability in distributed systems. Network failures or timeouts may cause clients to retry requests, and idempotent operations ensure that these retries do not produce unintended side effects.

In this Smart Campus API, an additional business rule is enforced: a room cannot be deleted if it still has associated sensors. In such cases, the API returns `409 Conflict`, indicating that the request is valid but cannot be completed due to the current resource state. This constraint preserves referential integrity while still maintaining idempotent behaviour, as repeated DELETE requests will consistently result in the same outcome unless the system state changes.
---

### Part 3 – Sensor Operations & Linking

#### Question 5: We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

The `@Consumes(MediaType.APPLICATION_JSON)` annotation defines the expected media type of the request body and is used by the JAX-RS runtime during request matching and entity deserialization. It indicates that the resource method can only process requests with the `Content-Type: application/json` header.

If a client sends data using a different media type, such as `text/plain` or `application/xml`, the request fails during the content negotiation phase. JAX-RS checks the `Content-Type` header against the `@Consumes` annotation before invoking the resource method. Since the media type does not match, the runtime cannot select a suitable message body reader to convert the request payload into the corresponding Java object.

As a result, the request is rejected at the framework level, and JAX-RS returns an HTTP `415 Unsupported Media Type` response without executing the resource method. This prevents invalid or unsupported data formats from reaching the application logic.

From a technical perspective, this mechanism relies on JAX-RS providers (MessageBodyReader implementations), which are responsible for deserializing incoming JSON into Java objects. When the media type is unsupported, no appropriate provider is found, leading to the 415 response.

This behaviour enforces a strict API contract, ensures type safety during deserialization, and improves reliability by preventing malformed or incompatible input from being processed.

---

#### Question 6: Why is using `@QueryParam` generally better for filtering a collection than putting the filter value in the URL path?

Using `@QueryParam` is more appropriate for filtering because it represents an optional constraint applied to a collection resource rather than defining a new resource path. For example, `/api/v1/sensors?type=Temperature` still refers to the sensors collection, with the server applying a filter condition on the `type` attribute.

In contrast, embedding filter values in the path (e.g., `/api/v1/sensors/type/Temperature`) can incorrectly imply a hierarchical resource structure, which does not align with RESTful resource modeling. Path parameters are typically used to uniquely identify resources (e.g., `/sensors/{id}`), whereas query parameters are designed for searching, filtering, and refining results within a collection.

From a JAX-RS perspective, `@QueryParam` enables automatic binding of query string parameters to method arguments at runtime. This allows resource methods to accept optional filtering criteria without defining multiple endpoint variations. The filtering logic can then be applied dynamically within the method, typically by evaluating conditions on in-memory collections or streams.

Additionally, query parameters support extensibility and composability. Multiple filters can be combined in a single request (e.g., `/api/v1/sensors?type=Temperature&status=ACTIVE&roomId=LIB-301`), avoiding endpoint explosion and improving scalability. This also aligns with common API design practices for implementing flexible query operations.

Overall, using `@QueryParam` results in cleaner URI design, better separation of concerns, and more maintainable server-side code while providing a more efficient and flexible interface for clients.

---

### Part 4 – Deep Nesting with Sub-Resources

#### Question 7: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

The Sub-Resource Locator pattern is a JAX-RS design approach that allows a parent resource to delegate request handling of nested paths to a separate resource class at runtime. Instead of defining all nested endpoints within a single resource class, a method annotated with `@Path` returns another resource instance, which then handles further request processing.

In this Smart Campus API, sensors and their readings have a hierarchical relationship, where a sensor contains multiple readings. This is represented using the nested endpoint `/api/v1/sensors/{sensorId}/readings`. The `SensorResource` acts as the parent resource, while a sub-resource locator method returns an instance of `SensorReadingResource` to handle all reading-related operations.

From an architectural perspective, this pattern improves separation of concerns by isolating domain-specific logic into dedicated classes. `SensorResource` is responsible for sensor-level operations, while `SensorReadingResource` manages reading-related functionality. This reduces class size and prevents the creation of a large, monolithic controller.

Technically, sub-resource locators enable dynamic request dispatching. The JAX-RS runtime resolves the initial path, then delegates the remaining path segments to the returned sub-resource instance. This results in a modular routing mechanism that mirrors the hierarchical structure of the domain model.

This approach also improves maintainability and scalability. New endpoints (e.g., `/readings/{id}` or DELETE operations) can be added within the sub-resource class without modifying the parent resource. It reduces code duplication, simplifies testing, and supports cleaner URI design.

Overall, the Sub-Resource Locator pattern leads to a more modular, extensible, and maintainable API architecture, especially in complex systems with deeply nested resource relationships.

---

### Part 5 – Advanced Error Handling, Exception Mapping & Logging

#### Question 8: Why is HTTP 422 often more semantically accurate than 404 when the issue is a missing reference inside a valid JSON payload?

HTTP `404 Not Found` is normally used when the requested URL or target resource cannot be found. For example, if a client sends a request to retrieve a specific room and that room ID does not exist, returning `404 Not Found` is appropriate because the requested resource itself is missing.

However, in this Smart Campus API, the missing reference problem happens during sensor creation. The client sends a `POST` request to a valid endpoint, `/api/v1/sensors`, with a JSON body containing a `roomId`. In this case, the endpoint exists and the JSON structure may be valid, but the `roomId` inside the request body refers to a room that does not exist in the `DataStore`.

Therefore, HTTP `422 Unprocessable Entity` is more semantically accurate because the server understands the request and can parse the JSON, but it cannot process the request due to invalid domain data. The issue is not the `/api/v1/sensors` endpoint; the issue is the invalid linked resource reference inside the payload.

In this implementation, `SensorResource` checks whether the provided `roomId` exists before saving the sensor. If the room is not found, a `LinkedResourceNotFoundException` is thrown, and the exception mapper converts it into a `422 Unprocessable Entity` response with a structured JSON error message. This gives the client a clearer explanation that the `roomId` must be corrected rather than the endpoint URL.

---

#### Question 9: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

Exposing internal Java stack traces to external API consumers is a serious security risk because it reveals sensitive implementation details about the system. A typical stack trace may include package names, class names, method calls, file paths, framework and library versions, and the exact execution flow that led to the error.

From an attacker’s perspective, this information can be used to understand the internal architecture and technology stack of the application. For example, knowing the frameworks and versions in use allows attackers to search for known vulnerabilities (such as publicly disclosed CVEs) and target unpatched components. File paths and class structures may also reveal how the application is organized, making it easier to identify potential entry points for exploitation.

In addition, stack traces can expose details about validation logic, request handling, or business rules, which can help attackers craft more targeted attacks such as injection attacks, parameter tampering, or denial-of-service attempts. In some cases, improperly handled errors may even leak sensitive data.

To mitigate these risks, APIs should not expose raw stack traces to clients. Instead, a generic HTTP `500 Internal Server Error` response should be returned, while detailed error information is securely logged on the server side.

In this Smart Campus API, a `GlobalExceptionMapper` is used to intercept unhandled exceptions and convert them into structured JSON error responses. This ensures that internal implementation details are not exposed to API consumers, while still allowing developers to diagnose issues using server-side logs.

---

#### Question 10: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

Logging is considered a cross-cutting concern because it applies to all API endpoints rather than a single resource. If logging is implemented manually using `Logger.info()` inside each resource method, it leads to code duplication, inconsistent logging behaviour, and increased maintenance effort. Any change to logging logic would require modifying multiple classes.

JAX-RS filters provide a centralized and structured approach to handle logging. A `ContainerRequestFilter` can intercept incoming HTTP requests and log details such as the request method and URI, while a `ContainerResponseFilter` can capture outgoing responses, including status codes. This ensures consistent logging across all endpoints without modifying individual resource methods.

From an architectural perspective, filters are part of the JAX-RS request–response pipeline, meaning they execute before and after resource methods. This allows logging to be applied uniformly, even for requests that fail validation or do not reach the resource layer.

In this Smart Campus API, the `ApiLoggingFilter` implements both request and response filters to log key request details and response outcomes. This approach improves maintainability, enforces separation of concerns, and enhances observability of API behaviour during testing and debugging.

---