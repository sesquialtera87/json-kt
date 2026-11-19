![Java 11+](https://img.shields.io/badge/Java-11%2B-blue.svg?logo=java&logoColor=white)
![Kotlin 2.3+](https://img.shields.io/badge/Kotlin-2.2%2B-7F52FF.svg?logo=kotlin&logoColor=white)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack Release](https://jitpack.io/v/sesquialtera87/json-kt.svg)](https://jitpack.io/#sesquialtera87/json-kt)

# minimal-json (Java 11+ & Kotlin Extended Edition)

A lightweight fork of [minimal-json](README.original.md) for Java 11+. It adds Java Stream support and typed list extractions to the core, nullability annotations that Kotlin understands, and a **Kotlin module** with a builder DSL, null-safe navigation and exact numeric accessors.

> [!NOTE]
> This project is an evolved fork of `minimal-json` (originally developed by EclipseSource). For historical context, benchmarks, and the original documentation, see the [Original README](README.original.md).

## Modules

| Module | Artifact          | Description                                                                                  |
|--------|-------------------|----------------------------------------------------------------------------------------------|
| Core   | `minimal-json`    | The parser and writer. No runtime dependencies. Packages are still `com.eclipsesource.json`. |
| Kotlin | `minimal-json-kt` | Kotlin extensions on top of the core. Depends on the core and on `kotlin-stdlib`.            |

> [!WARNING]
> The core keeps the original package name. Do not put the original `com.eclipsesource.minimal-json` artifact on the same classpath: the two would contain the same classes.

## Installation (via JitPack)

The library is distributed through [JitPack](https://jitpack.io); it is not published on Maven Central. Replace `VERSION` with the latest release tag shown by the badge above.

JitPack publishes the modules of a multi-module repository under the group `com.github.sesquialtera87.json-kt`.

#### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<!-- Kotlin extensions (the core is pulled in transitively) -->
<dependency>
    <groupId>com.github.sesquialtera87.json-kt</groupId>
    <artifactId>minimal-json-kt</artifactId>
    <version>VERSION</version>
</dependency>

<!-- Or the Java core alone -->
<dependency>
    <groupId>com.github.sesquialtera87.json-kt</groupId>
    <artifactId>minimal-json</artifactId>
    <version>VERSION</version>
</dependency>
```

#### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Kotlin extensions (the core is pulled in transitively)
    implementation("com.github.sesquialtera87.json-kt:minimal-json-kt:VERSION")

    // Or the Java core alone
    implementation("com.github.sesquialtera87.json-kt:minimal-json:VERSION")
}
```

## Java core

The core keeps the original design: no runtime dependencies, fast parsing, small memory footprint. This edition adds the following.

### Streams

`JsonArray` and `JsonObject` expose `stream()` and `parallelStream()`:

```java
// Filter the file names in a JSON array
List<String> lilyPondFiles = jsonArray.stream()
    .map(JsonValue::asString)
    .filter(name -> name.endsWith(".ly"))
    .collect(Collectors.toList());

// Process the members of an object
jsonObject.stream()
    .filter(member -> member.getName().startsWith("auto"))
    .forEach(member -> System.out.println(member.getName()));
```

### Unmodifiable typed lists

`JsonArray` converts straight to unmodifiable lists:

```java
List<String> strings   = jsonArray.toStringList();
List<Integer> integers = jsonArray.toIntList();
List<Double> doubles   = jsonArray.toDoubleList();
List<Float> floats     = jsonArray.toFloatList();
List<Boolean> booleans = jsonArray.toBooleanList();
```

> [!NOTE]
> These methods throw an `UnsupportedOperationException` if an element has the wrong JSON type, and `toIntList()` throws a `NumberFormatException` for a number that is not an `int` (for example `1.5`).

### Nullability annotations

The public API is annotated with [JetBrains annotations](https://github.com/JetBrains/java-annotations) (`@NotNull`, `@Nullable`). The dependency has `provided` scope, so it is **not** added to your runtime classpath, and Kotlin reads the annotations even when the jar is absent. For Kotlin callers this means, among other things, that `JsonObject.get(name)` returns `JsonValue?` and that passing a nullable value to a `@NotNull` parameter does not compile.

### Pretty print

`WriterConfig.PRETTY_PRINT` indents with two spaces. Empty arrays and objects are written as `[]` and `{}` on a single line.

## Kotlin extensions

Everything below lives in the package `org.mth.json`:

```kotlin
import org.mth.json.*
```

### Builder DSL

`jsonObject { }` and `jsonArray { }` build nested structures declaratively:

```kotlin
val config = jsonObject {
    add("theme", "dark")                  // add(name, value)
    "tabSize" to 4                        // same thing, as an infix function
    "autoSave" to true
    "tags" to listOf("kotlin", "json")    // collections, maps, enums, ... (see below)

    "limits" toObject {                   // nested object
        "min" to 1
        "max" to 10
    }
    "recentFiles" toArray {               // nested array
        add("score1.ly")
        add("score2.ly")
        addObject { "name" to "score3.ly" }
        addArray { add(1); add(2) }
    }
}
```

- The builders are marked with `@DslMarker`, so inside a nested block you cannot call the members of an enclosing builder by accident.
- Like `JsonObject.add`, the DSL **keeps duplicate names**: adding the same name twice produces two members.
- Inside a `jsonObject { }` block, `to` refers to the builder's `to`, not to the standard library's. `mapOf("a" to 1)` does not compile there; write `mapOf(Pair("a", 1))` or build the map outside the block.

### Value conversion

Every builder function, factory and operator converts values with `Any?.toJsonValue()`. The supported types are:

| Kotlin type | JSON |
|---|---|
| `null` | `null` |
| `JsonValue` | itself |
| `String`, `Boolean` | string, boolean |
| `Int`, `Short`, `Byte`, `Long`, `Float`, `Double` | number |
| `Enum` | string (the constant's `name`) |
| `Map<String, *>` | object |
| `Iterable`, `Array`, `Sequence` | array |

Any other type throws an `IllegalArgumentException`, and so does a `Map` with a non-`String` key. `NaN` and infinite numbers are rejected, as in the core.

### Factories and operators

```kotlin
val obj = jsonObjectOf("name" to "kt", "version" to 2)
val arr = jsonArrayOf(1, "two", null, listOf(3, 4))   // [1,"two",null,[3,4]]

val fromMap  = mapOf("a" to 1, "b" to listOf(true)).toJsonObject()
val fromList = listOf(1, 2, 3).toJsonArray()

obj += "extra" to true    // JsonObject.plusAssign
arr += "more"             // JsonArray.plusAssign
```

> [!NOTE]
> `arr += listOf(1, 2)` appends **one** element (the array `[1,2]`), exactly like `add`. It does not append every element of the list.

### Null-safe navigation

On a `JsonValue` (or any nullable receiver), `[name]` and `[index]` return `null` instead of throwing when a step is missing or has the wrong type, so paths can be chained:

```kotlin
val root = """{"editor":{"font":{"size":14},"recent":["a.ly","b.ly"]}}""".parseJson()

root["editor"]["font"]["size"].intOrNull()     // 14
root["editor"]["recent"][1].stringOrNull()     // "b.ly"
root["editor"]["colors"][0].stringOrNull()     // null: missing member
root["editor"]["recent"][9].stringOrNull()     // null: index out of range
```

> [!NOTE]
> Kotlin prefers a member over an extension. On a non-null `JsonArray` the Java member `get(int)` is used, and it throws `IndexOutOfBoundsException` for an invalid index. On a non-null `JsonObject`, `get(name)` returns `JsonValue?`. After the first step the chain is always null-safe.

### Typed accessors

Each accessor returns `null` if the receiver is `null`, if it is a JSON `null`, or if its JSON type does not match:

| Accessor                                   | Result                             |
|--------------------------------------------|------------------------------------|
| `stringOrNull()`, `booleanOrNull()`        | `String?`, `Boolean?`              |
| `intOrNull()`, `longOrNull()`              | `Int?`, `Long?`                    |
| `floatOrNull()`, `doubleOrNull()`          | `Float?`, `Double?`                |
| `bigDecimalOrNull()`, `bigIntegerOrNull()` | exact `BigDecimal?`, `BigInteger?` |
| `objectOrNull()`, `arrayOrNull()`          | `JsonObject?`, `JsonArray?`        |

Numeric accessors are strict: `1.5`, `1.0` and `1e3` are `null` as `Int`, and `3000000000` is `null` as `Int` but valid as `Long`. `bigDecimalOrNull()` and `bigIntegerOrNull()` read the literal exactly, without rounding through `Double`:

```kotlin
val price = """{"price":12345678901234567890.123456789}""".parseJson()["price"]
price.bigDecimalOrNull()    // 12345678901234567890.123456789
price.doubleOrNull()        // 1.2345678901234567E19
```

### Reified access: `asTypeOrNull`

`asTypeOrNull<T>()` picks the accessor from the type argument:

```kotlin
val port: Int? = obj["port"].asTypeOrNull()
val name = obj["name"].asTypeOrNull<String>()
```

The supported types are `String`, `Int`, `Long`, `Float`, `Double`, `Boolean`, `BigDecimal`, `BigInteger`, `JsonObject` and `JsonArray`. The result is `null` for a missing member, a JSON `null`, a type mismatch, or a number that does not fit. Any other type argument throws an `IllegalArgumentException`, whatever the value is.

### Destructuring

`JsonObject.Member` supports destructuring, and `JsonObject` is `Iterable<Member>`:

```kotlin
for ((name, value) in jsonObject) {
    println("$name -> $value")
}

val port: Int? = obj["port"].asTypeOrNull()
val name = obj["name"].asTypeOrNull<String>()
```

### Parsing, maps and pretty printing

```kotlin
val value = """{"a":[1,2]}""".parseJson()    // String.parseJson(); throws ParseException if invalid
val map: Map<String, JsonValue> = value.asObject().toMap()
println(value.toPrettyString())
```

`JsonObject.toMap()` is a snapshot: if the object has duplicate names, the entry holds the last value, as `get` does.



## Breaking and other changes
<details>
<summary>from 1.0.x</summary>

Source-incompatible changes:

- **Core, Kotlin callers:** the nullability annotations make `JsonObject.get` return `JsonValue?`, and make `null` an error for `@NotNull` parameters (member names, `JsonValue` arguments, `Json.parse(String)`, writers). Java callers are not affected.
- **`asTypeOrNull`** now throws `IllegalArgumentException` for an unsupported `T` instead of returning `null`.
- **`add<T>(name, value)`** and the per-type `add`/`to` overloads of the builders are replaced by `add(name, Any?)`, `to(Any?)` and `add(Any?)`. Calls that compiled before still compile; values of types that used to be dropped silently by `to` now end up in the JSON.
- The `jsonObject` and `jsonArray` properties of the builders are no longer accessible.
- The extension operators `get`/`set` on `JsonObject` and `JsonArray` are removed; the Java members are used, as they always were.

Other changes:

- The core is published with the group `org.mth` (was `com.eclipsesource.minimal-json`).
- For Java callers, the Kotlin file facade is now `JsonDslKt` (was `JsonKt`).
- Pretty printing writes empty containers as `[]` and `{}`.

</details>

## Environment

- **Java core:** Java 11+.
- **Kotlin module:** built with Kotlin 2.3.21 for the JVM 11 target.
- **Tests:** JUnit 5.

## License

This project preserves the original **MIT License**. You are free to use, modify, and distribute it in both open-source and commercial software.
