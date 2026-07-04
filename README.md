![Java 11+](https://img.shields.io/badge/Java-11%2B-blue.svg?logo=java&logoColor=white)
![Kotlin 1.6+](https://img.shields.io/badge/Kotlin-1.6%2B-7F52FF.svg?logo=kotlin&logoColor=white)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack Release](https://jitpack.io/v/sesquialtera87/json-kt.svg)](https://jitpack.io/#sesquialtera87/json-kt)


# minimal-json (Java 11+ & Kotlin Extended Edition)

A modern, lightweight, and enhanced fork of the renowned bare-bones JSON parser. This edition bumps the compilation target to **Java 11**, introduces native **Java Stream API** support, implements type-safe unmodifiable collections, and packs a dedicated **Kotlin DSL** module for a seamless, idiomatic developer experience.

> [!NOTE]
> This project is an evolved fork of `minimal-json` (originally developed by EclipseSource). For historical context, benchmarks, and original documentation, please refer to the [Original README](README.original.md).

---

## Key Features (Java Core)

The Java core preserves the original design principles: **zero dependencies**, blistering performance, and a minimal memory footprint. The new enhancements drastically simplify modern JSON data manipulation.

### 1. Native Java Stream API Support
`JsonArray` and `JsonObject` now expose native `stream()` and `parallelStream()` methods, allowing you to process JSON structures lazily and functionally:

```java
// Filter specific files from a JSON array in Java
List<String> lilyPondFiles = jsonArray.stream()
    .map(JsonValue::asString)
    .filter(name -> name.endsWith(".ly"))
    .toList();

// Process object members efficiently
jsonObject.stream()
    .filter(member -> member.getName().startsWith("auto"))
    .forEach(member -> System.out.println(member.getName()));
```


### 2. Unmodifiable Type-Safe List Extractions
To avoid repetitive stream mapping boilerplate, `JsonArray` provides fail-fast shorthand methods that immediately return structurally unmodifiable lists (`Collectors.toUnmodifiableList()`):
```java
List<String> strings   = jsonArray.toStringList();
List<Integer> integers = jsonArray.toIntList();
List<Double> doubles   = jsonArray.toDoubleList();
List<Float> floats     = jsonArray.toFloatList();
List<Boolean> booleans = jsonArray.toBooleanList();
```
> [!NOTE]
> These methods throw an `UnsupportedOperationException` immediately if a type mismatch is encountered in an array element.

## Kotlin Extensions (minimal-json-kt)
Leveraging Kotlin's advanced interoperability, the Kotlin module elevates your workflow with high-expressiveness and declarative patterns.

### 1. Declarative DSL Syntax (`jsonObject` & `jsonArray`)
Construct nested, highly visual JSON objects without manually instantiating nested builders:
```kotlin
// Constructing complex JSON structures seamlessly
val config = jsonObject {
    // Fluent shorthand argument syntax
    add("theme", "dark")
    add("tabSize", 4)

    // Idiomatic Kotlin 'to' infix function syntax
    "autoSave" to true
    "editorMode" to "advanced"

    // Nested DSL structures combined with 'to' function
    "recentFiles" to jsonArray {
        add("score1.ly")
        add("score2.ly")
    }
}
```

### 2. Clean Synthetic Properties
Thanks to Kotlin’s property synthesis, the newly added stream conversion methods can be accessed as clean, read-only properties, dropping unnecessary parentheses:
```kotlin
// Syntactically clean access to unmodifiable lists
val standardFiles = jsonArray.toStringList
    .filter { it.startsWith("score") }
```

### 3. Destructuring Operators for Members
`JsonObject.Member` natively supports Kotlin's destructuring components (`component1()` and `component2()`), allowing you to unpack key-value pairs effortlessly inside loops:
```kotlin
// Destructuring directly into key (name) and value
for ((key, value) in jsonObject.stream) {
    println("Property: $key -> Value: $value")
}
```

### 4. Reified Type Smart Casts (`asTypeOrNull`)
An inline extension function leveraging reified type parameters lets you safely attempt to cast or extract value payloads. It gracefully returns null on missing fields, JSON null declarations, or structural type mismatches without raising exceptions:
```kotlin
val port: Int? = jsonValue.asTypeOrNull<Int>()
val missing: String? = jsonValue.asTypeOrNull<String>() // Safely returns null if not a String
```

## 🛠️ Environment & Compatibility
**Java Core**: Targets Java 11+ to utilize optimized `java.util.stream` collection collectors natively.

**Kotlin**: Target optimized for Kotlin 1.6+ setups.

**Testing**: Fully migrated to JUnit 5 ensuring reliable, isolated unit tests.

## 📦 Installation (via JitPack)
This library is hosted and distributed via **JitPack**. You can add it to your project by configuring your build tool as follows:
#### Maven
Add the JitPack repository inside your `<repositories>` block, and include the dependency:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.sesquialtera87.json-kt</groupId>
    <artifactId>json-kt</artifactId>
    <version>x.x.x</version>
</dependency>
```

#### Gradle (Kotlin DSL)
Add the repository to your `repositories` block and declare the library in your `dependencies`:
```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.sesquialtera87.json-kt:json-kt:x.x.x")
}
```

## 📄 License 
This project preserves the original **MIT License**. You are free to use, modify, and distribute it in both open-source and commercial software suites.