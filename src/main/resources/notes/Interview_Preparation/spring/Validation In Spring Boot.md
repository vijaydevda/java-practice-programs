Validation in a Spring Boot web application is done using **Java Bean Validation (JSR 380/JSR 303)** — most commonly with **Hibernate Validator**, which is the default implementation included with Spring Boot.

Spring Boot integrates this seamlessly using **annotations**, typically in request DTOs, and handles validation automatically for incoming data.

---

### ✅ Common Use Cases

- Validating incoming JSON in `@RequestBody`
    
- Validating path variables or request parameters
    
- Automatically returning error responses for invalid input
    

---

### 🧱 Steps to Perform Validation in Spring Boot

---

### 1. **Add Dependency (Optional)**

If using Spring Boot starter for web, Hibernate Validator is included by default.  
If not:

```xml
<!-- Only needed if not already present -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

### 2. **Use Validation Annotations in DTO**

```java
import jakarta.validation.constraints.*;

public class UserRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Min(value = 18, message = "Age should be at least 18")
    private int age;

    @Email(message = "Email should be valid")
    private String email;

    // Getters and Setters
}
```

---

### 3. **Validate in Controller with `@Valid` or `@Validated`**

```java
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping
    public String createUser(@RequestBody @Valid UserRequest userRequest) {
        return "User is valid!";
    }
}
```

---

### 4. **Handling Validation Errors (Global Exception Handler)**

By default, Spring will return a `400 Bad Request` with error details if validation fails.

To customize this, use a handler in your global exception class:

```java
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
        errors.put(error.getField(), error.getDefaultMessage());
    });
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
}
```

---

### 🔧 Common Annotations

|Annotation|Description|
|---|---|
|`@NotNull`|Field must not be null|
|`@NotBlank`|Must not be null or empty (only for strings)|
|`@Min(value)` / `@Max(value)`|Numeric value range|
|`@Email`|Valid email format|
|`@Pattern(regex)`|Custom regex match|
|`@Size(min, max)`|Length of string or size of collection|

---

### 🧪 Example JSON Request

```json
POST /users
{
  "name": "",
  "age": 16,
  "email": "wrongformat"
}
```

**Response:**

```json
{
  "name": "Name is mandatory",
  "age": "Age should be at least 18",
  "email": "Email should be valid"
}
```

---

### ✅ Summary

|Feature|Used in|
|---|---|
|`@Valid`|For validating a single object (like DTO in request body)|
|`@Validated`|Used on class level for advanced/conditional validations|
|`BindingResult`|Used if you want to handle errors in the controller itself|

---

Would you like me to show how to handle validation in **path variables, request params**, or **custom validators** next?

Great! Let’s now explore **how to handle validation** in:

1. ✅ **Path variables and request parameters**
    
2. ✅ **Using `@Validated`**
    
3. ✅ **Creating custom validators**
    

---

## 🔹 1. **Validating Path Variables and Request Parameters**

You can’t use `@Valid` directly on primitives like `int`, but you **can** use it on method arguments if combined with `@Validated` at the controller level.

### ✅ Example: Validate request parameters

```java
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated  // Enables parameter-level validation
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public String getUserById(@PathVariable @Min(1) int id) {
        return "User with ID: " + id;
    }

    @GetMapping("/search")
    public String searchByAge(@RequestParam @Min(18) int age) {
        return "Users older than: " + age;
    }
}
```

📝 Without `@Validated` at the class level, the validation annotations like `@Min` will **not** be triggered for path/request parameters.

---

## 🔹 2. **Using `@Validated` vs `@Valid`**

|Feature|`@Valid`|`@Validated`|
|---|---|---|
|Package|`jakarta.validation.Valid`|`org.springframework.validation.annotation.Validated`|
|Use Case|Validating DTOs, nested fields|Validating method params, groups, advanced cases|
|Level|Field or parameter level|Class level|

---

## 🔹 3. **Custom Validator Example**

Let’s say you want to validate that a **username doesn't contain spaces**.

### ✅ Step 1: Create an Annotation

```java
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoSpacesValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSpaces {
    String message() default "Must not contain spaces";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

---

### ✅ Step 2: Create the Validator Class

```java
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoSpacesValidator implements ConstraintValidator<NoSpaces, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.contains(" ");
    }
}
```

---

### ✅ Step 3: Use in DTO

```java
public class UserRequest {

    @NoSpaces(message = "Username must not contain spaces")
    private String username;

    // other fields...
}
```

---

## ✅ Summary

|Type|Annotation Used|Notes|
|---|---|---|
|Request Body DTO|`@Valid`, `@Validated`|Validates whole objects|
|Path/Request Params|`@Min`, `@NotBlank`, etc. + `@Validated` on class|Validates primitives|
|Custom Rules|`@Constraint` + custom validator|For domain-specific checks|

---

Would you like a complete example project structure or demo with **validation + global error handling + Swagger docs**?