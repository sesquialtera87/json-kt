@file:Suppress("unused")

package org.mth.json

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.JsonValue

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

// --- Reified Type Smart Casts ---

/**
 * Attempts to safely cast or extract a [JsonValue] into the target primitive or structural type [T].
 *
 * Returns `null` if the receiver is `null` (e.g. a missing member), if it is a JSON `null` literal, if its JSON
 * type does not match [T], or if a JSON number cannot be represented as [T] (e.g. `1.5` or `3000000000` as [Int]).
 *
 * @return The mapped type representation of type [T], or `null` if conversion fails or if the value is null.
 */
inline fun <reified T> JsonValue?.asTypeOrNull(): T? {
    if (this == null || this.isNull) return null
    return try {
        when (T::class) {
            String::class -> this.asString() as T
            Int::class -> this.asInt() as T
            Long::class -> this.asLong() as T
            Float::class -> this.asFloat() as T
            Double::class -> this.asDouble() as T
            Boolean::class -> this.asBoolean() as T
            JsonObject::class -> this.asObject() as T
            JsonArray::class -> this.asArray() as T
            else -> null
        }
    } catch (_: UnsupportedOperationException) {
        null
    } catch (_: NumberFormatException) {
        null
    }
}