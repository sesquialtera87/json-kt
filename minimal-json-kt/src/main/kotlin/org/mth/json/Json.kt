package org.mth.json

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.JsonValue

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
class JsonObjectBuilder {
    private val jsonObject = JsonObject()

    /** Associates a [String] key with an [Int] value. */
    infix fun String.to(value: Int) { jsonObject.add(this, value) }

    /** Associates a [String] key with a [Long] value. */
    infix fun String.to(value: Long) { jsonObject.add(this, value) }

    /** Associates a [String] key with a [Float] value. */
    infix fun String.to(value: Float) { jsonObject.add(this, value) }

    /** Associates a [String] key with a [Double] value. */
    infix fun String.to(value: Double) { jsonObject.add(this, value) }

    /** Associates a [String] key with a [Boolean] value. */
    infix fun String.to(value: Boolean) { jsonObject.add(this, value) }

    /** Associates a [String] key with a nullable [String] value. */
    infix fun String.to(value: String?) { jsonObject.add(this, value) }

    /** Associates a [String] key with an existing [JsonValue]. */
    infix fun String.to(value: JsonValue) { jsonObject.add(this, value) }

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
class JsonArrayBuilder {
    val jsonArray = JsonArray()

    /** Appends an [Int] value to the array. */
    fun add(value: Int) { jsonArray.add(value) }

    /** Appends a [Long] value to the array. */
    fun add(value: Long) { jsonArray.add(value) }

    /** Appends a [Float] value to the array. */
    fun add(value: Float) { jsonArray.add(value) }

    /** Appends a [Double] value to the array. */
    fun add(value: Double) { jsonArray.add(value) }

    /** Appends a [Boolean] value to the array. */
    fun add(value: Boolean) { jsonArray.add(value) }

    /** Appends a nullable [String] value to the array. */
    fun add(value: String?) { jsonArray.add(value) }

    /** Appends an existing [JsonValue] to the array. */
    fun add(value: JsonValue) { jsonArray.add(value) }

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

// =========================================================================
// EXTENSION OPERATORS & TYPE CONVERSIONS
// =========================================================================

// --- JsonObject Operators ---

/**
 * Allows array-like getter safe access to a member value using indexing syntax (`jsonObject["key"]`).
 *
 * @param name The name string of the member to look up.
 * @return The corresponding [JsonValue], or `null` if the member name is missing.
 */
operator fun JsonObject.get(name: String): JsonValue? {
    return this.get(name)
}

/** Sets or overwrites a member with an [Int] value using indexing syntax (`jsonObject["key"] = value`). */
operator fun JsonObject.set(name: String, value: Int) { this.set(name, Json.value(value)) }

/** Sets or overwrites a member with a [Long] value using indexing syntax (`jsonObject["key"] = value`). */
operator fun JsonObject.set(name: String, value: Long) { this.set(name, Json.value(value)) }

/** Sets or overwrites a member with a [Float] value using indexing syntax (`jsonObject["key"] = value`). */
operator fun JsonObject.set(name: String, value: Float) { this.set(name, Json.value(value)) }

/** Sets or overwrites a member with a [Double] value using indexing syntax (`jsonObject["key"] = value`). */
operator fun JsonObject.set(name: String, value: Double) { this.set(name, Json.value(value)) }

/** Sets or overwrites a member with a [Boolean] value using indexing syntax (`jsonObject["key"] = value`). */
operator fun JsonObject.set(name: String, value: Boolean) { this.set(name, Json.value(value)) }

/** Sets or overwrites a member with a nullable [String] value using indexing syntax (`jsonObject["key"] = value`). */
operator fun JsonObject.set(name: String, value: String?) { this.set(name, Json.value(value)) }

/** Sets or overwrites a member with an existing [JsonValue] using indexing syntax (`jsonObject["key"] = value`). */
operator fun JsonObject.set(name: String, value: JsonValue) { this.set(name, value) }

// --- JsonArray Operators ---

/**
 * Accesses an element at the specified position within the [JsonArray] using index syntax (`jsonArray[index]`).
 *
 * @param index The zero-based index of the element to return.
 * @return The [JsonValue] found at the given index.
 * @throws IndexOutOfBoundsException If the index is out of bounds.
 */
operator fun JsonArray.get(index: Int): JsonValue = this.get(index)

/** Replaces the element at the specified position in this array with an [Int] value. */
operator fun JsonArray.set(index: Int, value: Int) { this.set(index, Json.value(value)) }

/** Replaces the element at the specified position in this array with a [Long] value. */
operator fun JsonArray.set(index: Int, value: Long) { this.set(index, Json.value(value)) }

/** Replaces the element at the specified position in this array with a [Float] value. */
operator fun JsonArray.set(index: Int, value: Float) { this.set(index, Json.value(value)) }

/** Replaces the element at the specified position in this array with a [Double] value. */
operator fun JsonArray.set(index: Int, value: Double) { this.set(index, Json.value(value)) }

/** Replaces the element at the specified position in this array with a [Boolean] value. */
operator fun JsonArray.set(index: Int, value: Boolean) { this.set(index, Json.value(value)) }

/** Replaces the element at the specified position in this array with a nullable [String] value. */
operator fun JsonArray.set(index: Int, value: String?) { this.set(index, Json.value(value)) }

/** Replaces the element at the specified position in this array with another [JsonValue]. */
operator fun JsonArray.set(index: Int, value: JsonValue) { this.set(index, value) }

// --- Destructuring Operations ---

/** Destructuring component operator providing the member field key/name. */
operator fun JsonObject.Member.component1(): String = this.name

/** Destructuring component operator providing the member underlying [JsonValue]. */
operator fun JsonObject.Member.component2(): JsonValue = this.value

// --- Reified Type Smart Casts ---

/**
 * Attempts to safely cast or extract a [JsonValue] into the target primitive or structural type [T].
 *
 * If the literal evaluates to a JSON `null`, or if the requested type mismatch fails structural conversion,
 * this extension function will catch the exception and gracefully return `null`.
 *
 * @return The mapped type representation of type [T], or `null` if conversion fails or if the value is null.
 */
inline fun <reified T> JsonValue.asTypeOrNull(): T? {
    if (this.isNull) return null
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
    }
}