In Java, **Garbage Collection (GC)** is the process by which the **JVM automatically removes unused or unreachable objects** from memory to **free up resources** and **avoid memory leaks**.

---

## 🧹 Why Garbage Collection?

Java manages memory automatically. When objects are no longer reachable (i.e., no references point to them), they are eligible for garbage collection.

Garbage Collection helps with:

- Memory optimization
    
- Preventing memory leaks
    
- Managing object lifecycle without manual `free()` or `delete`
    

---

## 🧱 Java Heap Structure (Important for GC)

```
┌──────────────────────────┐
│        Old Generation    │◄── long-lived objects
├──────────────────────────┤
│     Young Generation     │◄── short-lived objects
│ ┌────────────┬─────────┐ │
│ │   Eden     │Survivors│ │
│ └────────────┴─────────┘ │
└──────────────────────────┘
```

- **Young Generation**:
    
    - New objects are allocated here.
        
    - Frequent GC (called **Minor GC**).
        
- **Old Generation**:
    
    - Long-living objects promoted from Young Gen.
        
    - Less frequent GC (called **Major GC / Full GC**).
        

---

## 🚀 Types of Garbage Collectors in Java

Different GCs are suited for different performance needs. Let’s look at each:

---

### 1. 🧒 **Serial Garbage Collector**

- **Single-threaded** GC (both minor and major GC).
    
- **Freezes all application threads** (Stop-the-world).
    
- Best for **small applications** or **single-core machines**.
    

📌 Enable with:

```bash
-XX:+UseSerialGC
```

---

### 2. 🧵 **Parallel Garbage Collector (Throughput Collector)**

- Uses **multiple threads** for GC.
    
- Still causes stop-the-world pauses but is **much faster than Serial GC**.
    
- Focuses on **high throughput**, not low latency.
    

📌 Enable with:

```bash
-XX:+UseParallelGC
```

---

### 3. 🪄 **Concurrent Mark-Sweep (CMS) Garbage Collector** (Deprecated since Java 9)

- Minimizes **pause times** by performing most of its work **concurrently** with the application.
    
- Complex GC but suitable for **low-latency apps**.
    

📌 Enable with:

```bash
-XX:+UseConcMarkSweepGC
```

❌ Deprecated in favor of G1 GC.

---

### 4. 🌳 **G1 (Garbage First) Garbage Collector**

- **Default GC from Java 9 onward**.
    
- Splits heap into multiple **regions**, not just two generations.
    
- Performs GC **concurrently**, incrementally, and **predictably**.
    
- Aims for **low pause times + good throughput**.
    

📌 Enable with:

```bash
-XX:+UseG1GC
```

✅ Good for most use-cases, production ready.

---

### 5. ⚡ **Z Garbage Collector (ZGC)**

- Focus: **Very low pause times** (<10ms), even with large heaps (100 GB+).
    
- Most of GC is **concurrent** with application threads.
    
- Pause-time is **independent of heap size**.
    

📌 Enable with:

```bash
-XX:+UseZGC
```

✅ Available since **Java 11**, production-ready in **Java 15+**.

---

### 6. 🚀 **Shenandoah Garbage Collector**

- Similar to ZGC, aims for **pause times in milliseconds**.
    
- Also performs **concurrent compaction**.
    
- Developed by Red Hat.
    

📌 Enable with:

```bash
-XX:+UseShenandoahGC
```

✅ Available from **Java 12+**, production in **Java 15+**.

---

### 🔁 GC Types Summary Table

|GC Type|Pause Time|Concurrency|Use Case|Option|
|---|---|---|---|---|
|**Serial GC**|High|No|Small apps, single-core systems|`-XX:+UseSerialGC`|
|**Parallel GC**|Medium|Yes (multi-threaded stop-the-world)|High throughput|`-XX:+UseParallelGC`|
|**CMS**|Low|Partial|Low-latency, deprecated|`-XX:+UseConcMarkSweepGC`|
|**G1 GC**|Low-medium|Yes|Balanced apps, default since Java 9|`-XX:+UseG1GC`|
|**ZGC**|Very Low|Fully|Huge heaps, ultra-low latency|`-XX:+UseZGC`|
|**Shenandoah**|Very Low|Fully|Large heaps, responsive apps|`-XX:+UseShenandoahGC`|

---

## 🔍 How to Monitor and Tune GC

- **GC Logs** (Java 9+):
    
    ```bash
    -Xlog:gc
    ```
    
- **Visual Tools**:
    
    - JVisualVM
        
    - JMC (Java Mission Control)
        
    - GCViewer
        
    - Prometheus + Grafana (via exporters)
        

---

## 🧪 Example Code to Trigger GC

```java
public class GCDemo {
    public static void main(String[] args) {
        for (int i = 0; i < 100_000; i++) {
            byte[] array = new byte[1024 * 1024]; // 1MB
        }
        System.gc(); // Suggest GC
        System.out.println("Suggested GC");
    }
}
```

---

## ✅ Summary

- Java GC is **automatic memory management**.
    
- JVM has several GC algorithms; choose based on **latency, throughput, and heap size**.
    
- GC tuning can significantly improve performance of Java applications.
    

---

Would you like help choosing the **best GC for your application** or analyzing **GC logs**?