The **Bean Lifecycle in Spring** refers to the various stages a Spring-managed bean goes through from creation to destruction within the Spring IoC (Inversion of Control) container. Understanding this is important for managing resources, customizing initialization, and cleaning up properly.

---

🔁 **Spring Bean Lifecycle Steps**

Here’s a step-by-step breakdown:

---

#### 1. **Instantiation**

- The Spring container **creates a new instance** of the bean using the default or parameterized constructor (as defined).
    
- This happens before any properties are set.
    

---

#### 2. **Populate Properties (Dependency Injection)**

- Spring **injects dependencies** (via setter injection or field injection).
    
- All properties defined in the XML or annotations like `@Autowired`, `@Value`, etc., are injected at this stage.
    

---

#### 3. **`BeanNameAware` (Optional)**

- If the bean implements `BeanNameAware`, Spring calls:
    
    ```java
    setBeanName(String name)
    ```
    
    to inform the bean of its ID in the context.
    

---

#### 4. **`BeanFactoryAware` or `ApplicationContextAware` (Optional)**

- If implemented, Spring calls:
    
    ```java
    setBeanFactory(BeanFactory factory)
    ```
    
    or
    
    ```java
    setApplicationContext(ApplicationContext context)
    ```
    
    allowing access to the container.
    

---

#### 5. **`PostConstruct` / `InitializingBean` / Custom init method**

- Three ways to define initialization logic:
    
    - **`@PostConstruct`** annotated method (JSR-250)
        
    - Implement `InitializingBean` interface and override `afterPropertiesSet()`
        
    - Configure a custom init method in XML or using `@Bean(initMethod="init")`
        

---

#### 6. **BeanPostProcessor (Before Initialization)**

- Spring calls `postProcessBeforeInitialization()` for any `BeanPostProcessor` implementations.
    
- Useful for modifying beans before initialization logic.
    

---

#### 7. **Initialization Complete**

- At this stage, the bean is considered fully initialized and ready to use in the application.
    

---

#### 8. **BeanPostProcessor (After Initialization)**

- Spring calls `postProcessAfterInitialization()` for any `BeanPostProcessor`.
    
- Can be used to wrap beans with proxies or apply AOP concerns.
    

---

#### 9. **Bean Usage**

- The bean is used within the application as needed (handling requests, services, etc.).
    

---

#### 10. **`@PreDestroy` / `DisposableBean` / Custom destroy method**

- On container shutdown:
    
    - **`@PreDestroy`** annotated method (JSR-250)
        
    - Implement `DisposableBean` and override `destroy()`
        
    - Define custom destroy method with `@Bean(destroyMethod="cleanup")` or XML config
        

---

### 🔄 Summary Diagram (Lifecycle Hooks):

```
   Instantiation
        ↓
   Dependency Injection
        ↓
   Aware Interfaces (BeanNameAware, etc.)
        ↓
   Pre-initialization (BeanPostProcessor#postProcessBeforeInitialization)
        ↓
   Initialization (@PostConstruct / afterPropertiesSet / initMethod)
        ↓
   Post-initialization (BeanPostProcessor#postProcessAfterInitialization)
        ↓
   Bean in Use
        ↓
   Destruction (@PreDestroy / destroy / destroyMethod)
```

---

### ✅ Best Practices

- Use `@PostConstruct` and `@PreDestroy` for simple init/destroy logic.
    
- Use `BeanPostProcessor` for cross-cutting concerns like logging or proxying.
    
- Prefer annotations over XML configuration for better readability.
    

Would you like a code example as well?

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class MyBean implements BeanNameAware, InitializingBean, DisposableBean {

    public MyBean() {
        System.out.println("1. Constructor: Bean is instantiated");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("2. BeanNameAware: Bean name is " + name);
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("3. @PostConstruct: Bean is initializing");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("4. InitializingBean: afterPropertiesSet() called");
    }

    public void customInit() {
        System.out.println("5. Custom init-method invoked");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("6. DisposableBean: destroy() called");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("7. @PreDestroy: Bean is being destroyed");
    }

    public void customDestroy() {
        System.out.println("8. Custom destroy-method invoked");
    }
}
