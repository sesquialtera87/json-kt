@file:Suppress("unused")

package org.mth.json

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.JsonValue
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Marks the JSON builders as members of the same DSL: inside a nested block, the members of an enclosing
 * builder can no longer be called implicitly.
 */
@DslMarker
annotation class JsonDsl

/**
 * Converts this value to a [JsonValue].
 *
 * Supported: `null` (JSON `null`), [JsonValue] (returned as is), [String], [Boolean], [Int], [Short], [Byte], [Long],
 * [Float], [Double], [Enum] (its name), [Map] with [String] keys, [Iterable], [Array] and [Sequence] (JSON array).
 *
 * @throws IllegalArgumentException for any other type, or for a [Map] with a non-[String] key.
 */
fun Any?.toJsonValue(): JsonValue = when (this) {
    null -> Json.NULL
    is JsonValue -> this
    is String -> Json.value(this)
    is Boolean -> Json.value(this)
    is Int, is Short, is Byte -> Json.value((this as Number).toInt())
    is Long -> Json.value(this)
    is Float -> Json.value(this)
    is Double -> Json.value(this)
    is Enum<*> -> Json.value(name)
    is Map<*, *> -> JsonObject().also { json ->
        for ((key, value) in this) {
            require(key is String) { "JSON object keys must be strings, got: $key" }
            json.add(key, value.toJsonValue())
        }
    }
    is Iterable<*> -> JsonArray().also { json -> for (element in this) json.add(element.toJsonValue()) }
    is Array<*> -> asList().toJsonValue()
    is Sequence<*> -> asIterable().toJsonValue()
    else -> throw IllegalArgumentException("Type not representable as JSON: ${this::class}")
}

/**
 * Creates a new [JsonObject] using a declarative DSL syntax.
 *
 * @param init A lambda with receiver block initialization on [JsonObjectBuilder].
 * @return A fully constructed [JsonObject].
 */
inline fun jsonObject(init: JsonObjectBuilder.() -> Unit): JsonObject {
    return JsonObjectBuilder().apply(init).build()
}

/**
 * Creates a new [JsonArray] using a declarative DSL syntax.
 *
 * @param init A lambda with receiver block initialization on [JsonArrayBuilder].
 * @return A fully constructed [JsonArray].
 */
inline fun jsonArray(init: JsonArrayBuilder.() -> Unit): JsonArray {
    return JsonArrayBuilder().apply(init).build()
}

/**
 * A builder class implementing the Domain-Specific Language (DSL) for declarative [JsonObject] construction.
 */
@JsonDsl
class JsonObjectBuilder {
    @PublishedApi
    internal val jsonObject = JsonObject()

    /**
     * Appends a member named [name], converting [value] with [toJsonValue].
     *
     * Like [JsonObject.add], this does not check for duplicate names: adding the same name twice produces two members.
     *
     * @throws IllegalArgumentException if [value] cannot be represented as JSON.
     */
    fun add(name: String, value: Any?) {
        jsonObject.add(name, value.toJsonValue())
    }

    /** Appends a member named by this [String], see [add]. */
    infix fun String.to(value: Any?) {
        add(this, value)
    }

    /**
     * Declares a nested [JsonObject] member inside this object using infix syntax.
     *
     * @param inlineOptions A lambda block configuration executing on a new child [JsonObjectBuilder].
     */
    inline infix fun String.toObject(inlineOptions: JsonObjectBuilder.() -> Unit) {
        this to jsonObject(inlineOptions)
    }

    /**
     * Declares a nested [JsonArray] member inside this object using infix syntax.
     *
     * @param inlineOptions A lambda block configuration executing on a new child [JsonArrayBuilder].
     */
    inline infix fun String.toArray(inlineOptions: JsonArrayBuilder.() -> Unit) {
        this to jsonArray(inlineOptions)
    }

    /**
     * Builds and returns the structured [JsonObject] instance.
     *
     * @return The underlying [JsonObject].
     */
    fun build(): JsonObject = jsonObject
}

/**
 * A builder class implementing the Domain-Specific Language (DSL) for declarative [JsonArray] construction.
 */
@JsonDsl
class JsonArrayBuilder {
    @PublishedApi
    internal val jsonArray = JsonArray()

    /**
     * Appends [value] to the array, converting it with [toJsonValue].
     *
     * @throws IllegalArgumentException if [value] cannot be represented as JSON.
     */
    fun add(value: Any?) {
        jsonArray.add(value.toJsonValue())
    }

    /**
     * Appends a nested [JsonObject] element to this array.
     *
     * @param inlineOptions A lambda block configuration executing on a new child [JsonObjectBuilder].
     */
    inline fun addObject(inlineOptions: JsonObjectBuilder.() -> Unit) {
        jsonArray.add(jsonObject(inlineOptions))
    }

    /**
     * Appends a nested [JsonArray] element to this array.
     *
     * @param inlineOptions A lambda block configuration executing on a new child [JsonArrayBuilder].
     */
    inline fun addArray(inlineOptions: JsonArrayBuilder.() -> Unit) {
        jsonArray.add(jsonArray(inlineOptions))
    }

    /**
     * Builds and returns the structured [JsonArray] instance.
     *
     * @return The underlying [JsonArray].
     */
    fun build(): JsonArray = jsonArray
}

// --- Destructuring Operations ---

/** Destructuring component operator providing the member field key/name. */
operator fun JsonObject.Member.component1(): String = this.name

/** Destructuring component operator providing the member underlying [JsonValue]. */
operator fun JsonObject.Member.component2(): JsonValue = this.value

// --- Null-safe Accessors ---

/*
 * Each accessor returns `null` if the receiver is `null` (e.g. a missing member), if it is a JSON `null` literal,
 * or if its JSON type does not match. Numeric accessors are strict: the literal must be representable in the target
 * type, so `1.5`, `1.0` or `1e3` are `null` as [Int] and `3000000000` is `null` as [Int] but valid as [Long].
 * [BigDecimal] and [BigInteger] read the literal exactly, without rounding.
 */

/** Returns this value as a [String] if it is a JSON string, `null` otherwise. */
fun JsonValue?.stringOrNull(): String? = if (this != null && isString) asString() else null

/** Returns this value as a [Boolean] if it is a JSON boolean, `null` otherwise. */
fun JsonValue?.booleanOrNull(): Boolean? = if (this != null && isBoolean) asBoolean() else null

/** Returns this value as an [Int] if it is a JSON number that fits, `null` otherwise. */
fun JsonValue?.intOrNull(): Int? = numberLiteral()?.toIntOrNull()

/** Returns this value as a [Long] if it is a JSON number that fits, `null` otherwise. */
fun JsonValue?.longOrNull(): Long? = numberLiteral()?.toLongOrNull()

/** Returns this value as a [Float] if it is a JSON number, `null` otherwise. */
fun JsonValue?.floatOrNull(): Float? = numberLiteral()?.toFloatOrNull()

/** Returns this value as a [Double] if it is a JSON number, `null` otherwise. */
fun JsonValue?.doubleOrNull(): Double? = numberLiteral()?.toDoubleOrNull()

/** Returns this value as an exact [BigDecimal] if it is a JSON number, `null` otherwise. */
fun JsonValue?.bigDecimalOrNull(): BigDecimal? = numberLiteral()?.toBigDecimalOrNull()

/** Returns this value as an exact [BigInteger] if it is a JSON integer literal, `null` otherwise. */
fun JsonValue?.bigIntegerOrNull(): BigInteger? = numberLiteral()?.toBigIntegerOrNull()

/** Returns this value as a [JsonObject] if it is a JSON object, `null` otherwise. */
fun JsonValue?.objectOrNull(): JsonObject? = if (this != null && isObject) asObject() else null

/** Returns this value as a [JsonArray] if it is a JSON array, `null` otherwise. */
fun JsonValue?.arrayOrNull(): JsonArray? = if (this != null && isArray) asArray() else null

/** The literal text of a JSON number, or `null` if this is not a JSON number. */
private fun JsonValue?.numberLiteral(): String? = if (this != null && isNumber) toString() else null

// --- Reified Type Smart Casts ---

/**
 * Attempts to safely cast or extract a [JsonValue] into the target primitive or structural type [T].
 *
 * Supported types are [String], [Int], [Long], [Float], [Double], [Boolean], [BigDecimal], [BigInteger], [JsonObject]
 * and [JsonArray]; see the corresponding `xxxOrNull` accessors.
 *
 * Returns `null` if the receiver is `null` (e.g. a missing member), if it is a JSON `null` literal, if its JSON
 * type does not match [T], or if a JSON number cannot be represented as [T] (e.g. `1.5` or `3000000000` as [Int]).
 *
 * @return The mapped type representation of type [T], or `null` if conversion fails or if the value is null.
 * @throws IllegalArgumentException if [T] is not a supported type, whatever the value is.
 */
inline fun <reified T> JsonValue?.asTypeOrNull(): T? = when (T::class) {
    String::class -> stringOrNull()
    Int::class -> intOrNull()
    Long::class -> longOrNull()
    Float::class -> floatOrNull()
    Double::class -> doubleOrNull()
    Boolean::class -> booleanOrNull()
    BigDecimal::class -> bigDecimalOrNull()
    BigInteger::class -> bigIntegerOrNull()
    JsonObject::class -> objectOrNull()
    JsonArray::class -> arrayOrNull()
    else -> throw IllegalArgumentException("Unsupported type ${T::class.java.name}")
} as T?
