@file:Suppress("unused")

package org.mth.json

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
    val jsonObject = JsonObject()

    inline fun <reified T> add(name: String, value: T) {
        when (T::class) {
            String::class -> jsonObject.add(name, value as String)
            Int::class -> jsonObject.add(name, value as Int)
            Long::class -> jsonObject.add(name, value as Long)
            Float::class -> jsonObject.add(name, value as Float)
            Double::class -> jsonObject.add(name, value as Double)
            Boolean::class -> jsonObject.add(name, value as Boolean)
            JsonObject::class -> jsonObject.add(name, value as JsonObject)
            JsonArray::class -> jsonObject.add(name, value as JsonArray)
            else -> throw IllegalArgumentException("Unsupported type ${T::class}")
        }
    }

    /** Associates a [String] key with an [Int] value. */
    infix fun String.to(value: Int) {
        jsonObject.add(this, value)
    }

    /** Associates a [String] key with a [Long] value. */
    infix fun String.to(value: Long) {
        jsonObject.add(this, value)
    }

    /** Associates a [String] key with a [Float] value. */
    infix fun String.to(value: Float) {
        jsonObject.add(this, value)
    }

    /** Associates a [String] key with a [Double] value. */
    infix fun String.to(value: Double) {
        jsonObject.add(this, value)
    }

    /** Associates a [String] key with a [Boolean] value. */
    infix fun String.to(value: Boolean) {
        jsonObject.add(this, value)
    }

    /** Associates a [String] key with a nullable [String] value. */
    infix fun String.to(value: String?) {
        jsonObject.add(this, value)
    }

    /** Associates a [String] key with an existing [JsonValue]. */
    infix fun String.to(value: JsonValue) {
        jsonObject.add(this, value)
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
class JsonArrayBuilder {
    val jsonArray = JsonArray()

    /** Appends an [Int] value to the array. */
    fun add(value: Int) {
        jsonArray.add(value)
    }

    /** Appends a [Long] value to the array. */
    fun add(value: Long) {
        jsonArray.add(value)
    }

    /** Appends a [Float] value to the array. */
    fun add(value: Float) {
        jsonArray.add(value)
    }

    /** Appends a [Double] value to the array. */
    fun add(value: Double) {
        jsonArray.add(value)
    }

    /** Appends a [Boolean] value to the array. */
    fun add(value: Boolean) {
        jsonArray.add(value)
    }

    /** Appends a nullable [String] value to the array. */
    fun add(value: String?) {
        jsonArray.add(value)
    }

    /** Appends an existing [JsonValue] to the array. */
    fun add(value: JsonValue) {
        jsonArray.add(value)
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