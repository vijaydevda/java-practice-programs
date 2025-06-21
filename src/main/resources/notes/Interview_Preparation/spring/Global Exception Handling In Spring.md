Global Exception Handling in **Spring Boot** is a mechanism to handle exceptions across the entire application in a centralized manner rather than handling them individually in each controller.

---

### ✅ Why Use Global Exception Handling?

- Avoid repetitive try-catch in every controller or service.
    
- Return consistent error responses (status, message, timestamp, etc.)
    
- Better maintenance and debugging.
    

---

### 🧱 Key Components & Annotations:

|Component|Description|
|---|---|
|`@ControllerAdvice`|Indicates a global exception handler. Intercepts exceptions across the application.|
|`@ExceptionHandler`|Specifies which exception a method will handle.|
|`ResponseEntity<?>`|Used to customize the HTTP response including status, headers, and body.|
|`@RestControllerAdvice`|Same as `@ControllerAdvice` but automatically adds `@ResponseBody`.|

---

### ✅ Basic Setup

#### 1. **Create a Global Exception Handler Class**

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                "Resource not found"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericException(Exception ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                "Internal Server Error"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

#### 2. **Create a Custom Exception Class**

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

#### 3. **ErrorDetails Class (Optional – for standardized response)**

```java
import java.time.LocalDateTime;

public class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private String details;

    // Constructor, Getters, and Setters
}
```

---

### 🧪 Example Controller Throwing an Exception

```java
@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/user/{id}")
    public ResponseEntity<String> getUser(@PathVariable int id) {
        if (id <= 0) {
            throw new ResourceNotFoundException("User ID must be greater than zero.");
        }
        return ResponseEntity.ok("User " + id);
    }
}
```

---

### ✅ Return Example

**Request:** `GET /api/user/-1`  
**Response:**

```json
{
  "timestamp": "2025-06-19T08:10:25.736",
  "message": "User ID must be greater than zero.",
  "details": "Resource not found"
}
```

---

### 🧩 Best Practices

- Use specific custom exceptions per domain or use case.
    
- Group related exceptions (e.g., Validation, Authentication).
    
- Return proper HTTP status codes:
    
    - 400 – Bad Request
        
    - 401 – Unauthorized
        
    - 404 – Not Found
        
    - 500 – Internal Server Error
        
- Always log exceptions for troubleshooting.
    

---

Would you like to add **validation errors** or **custom error codes** next?