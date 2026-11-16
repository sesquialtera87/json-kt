/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.eclipsesource.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.eclipsesource.json.TestUtil.assertException;
import static com.eclipsesource.json.TestUtil.serializeAndDeserialize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;


public class JsonArrayTest {

    private JsonArray array;

    @BeforeEach
    public void setUp() {
        array = new JsonArray();
    }

    @Test
    public void copyConstructor_hasSameValues() {
        array.add(23);
        JsonArray copy = new JsonArray(array);

        assertEquals(array.values(), copy.values());
    }

    @Test
    public void copyConstructor_worksOnSafeCopy() {
        JsonArray copy = new JsonArray(array);
        array.add(23);

        assertTrue(copy.isEmpty());
    }

    @Test
    public void unmodifiableArray_hasSameValues() {
        array.add(23);
        JsonArray unmodifiableArray = JsonArray.unmodifiableArray(array);

        assertEquals(array.values(), unmodifiableArray.values());
    }

    @Test
    public void unmodifiableArray_reflectsChanges() {
        JsonArray unmodifiableArray = JsonArray.unmodifiableArray(array);
        array.add(23);

        assertEquals(array.values(), unmodifiableArray.values());
    }

    @Test
    public void unmodifiableArray_preventsModification() {
        JsonArray unmodifiableArray = JsonArray.unmodifiableArray(array);

        assertThrows(UnsupportedOperationException.class, () -> unmodifiableArray.add(23));
    }

    @Test
    public void isEmpty_isTrueAfterCreation() {
        assertTrue(array.isEmpty());
    }

    @Test
    public void isEmpty_isFalseAfterAdd() {
        array.add(true);

        assertFalse(array.isEmpty());
    }

    @Test
    public void size_isZeroAfterCreation() {
        assertEquals(0, array.size());
    }

    @Test
    public void size_isOneAfterAdd() {
        array.add(true);

        assertEquals(1, array.size());
    }

    @Test
    public void iterator_isEmptyAfterCreation() {
        assertFalse(array.iterator().hasNext());
    }

    @Test
    public void iterator_hasNextAfterAdd() {
        array.add(true);

        Iterator<JsonValue> iterator = array.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(Json.TRUE, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void iterator_doesNotAllowModification() {
        assertThrows(UnsupportedOperationException.class, () -> {
            array.add(23);
            Iterator<JsonValue> iterator = array.iterator();
            iterator.next();
            iterator.remove();
        });
    }

    @Test
    public void iterator_detectsConcurrentModification() {
        assertThrows(ConcurrentModificationException.class, () -> {
            Iterator<JsonValue> iterator = array.iterator();
            array.add(23);
            iterator.next();
        });
    }

    @Test
    public void values_isEmptyAfterCreation() {
        assertTrue(array.values().isEmpty());
    }

    @Test
    public void values_containsValueAfterAdd() {
        array.add(true);

        assertEquals(1, array.values().size());
        assertEquals(Json.TRUE, array.values().get(0));
    }

    @Test
    public void values_reflectsChanges() {
        List<JsonValue> values = array.values();

        array.add(true);

        assertEquals(array.values(), values);
    }

    @Test
    public void values_preventsModification() {
        List<JsonValue> values = array.values();

        assertThrows(UnsupportedOperationException.class, () -> {
            values.add(Json.TRUE);
        });
    }

    @Test
    public void get_returnsValue() {
        array.add(23);

        JsonValue value = array.get(0);

        assertEquals(Json.value(23), value);
    }

    @Test
    public void get_failsWithInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
    }

    @Test
    public void add_int() {
        array.add(23);

        assertEquals("[23]", array.toString());
    }

    @Test
    public void add_int_enablesChaining() {
        assertSame(array, array.add(23));
    }

    @Test
    public void add_long() {
        array.add(23L);

        assertEquals("[23]", array.toString());
    }

    @Test
    public void add_long_enablesChaining() {
        assertSame(array, array.add(23L));
    }

    @Test
    public void add_float() {
        array.add(3.14f);

        assertEquals("[3.14]", array.toString());
    }

    @Test
    public void add_float_enablesChaining() {
        assertSame(array, array.add(3.14f));
    }

    @Test
    public void add_double() {
        array.add(3.14d);

        assertEquals("[3.14]", array.toString());
    }

    @Test
    public void add_double_enablesChaining() {
        assertSame(array, array.add(3.14d));
    }

    @Test
    public void add_boolean() {
        array.add(true);

        assertEquals("[true]", array.toString());
    }

    @Test
    public void add_boolean_enablesChaining() {
        assertSame(array, array.add(true));
    }

    @Test
    public void add_string() {
        array.add("foo");

        assertEquals("[\"foo\"]", array.toString());
    }

    @Test
    public void add_string_enablesChaining() {
        assertSame(array, array.add("foo"));
    }

    @Test
    public void add_string_toleratesNull() {
        array.add((String) null);

        assertEquals("[null]", array.toString());
    }

    @Test
    public void add_jsonNull() {
        array.add(Json.NULL);

        assertEquals("[null]", array.toString());
    }

    @Test
    public void add_jsonArray() {
        array.add(new JsonArray());

        assertEquals("[[]]", array.toString());
    }

    @Test
    public void add_jsonObject() {
        array.add(new JsonObject());

        assertEquals("[{}]", array.toString());
    }

    @Test
    public void add_json_enablesChaining() {
        assertSame(array, array.add(Json.NULL));
    }

    @Test
    public void add_json_nestedArray() {
        JsonArray innerArray = new JsonArray();
        innerArray.add(23);

        array.add(innerArray);

        assertEquals("[[23]]", array.toString());
    }

    @Test
    public void add_json_nestedArray_modifiedAfterAdd() {
        JsonArray innerArray = new JsonArray();
        array.add(innerArray);

        innerArray.add(23);

        assertEquals("[[23]]", array.toString());
    }

    @Test
    public void add_json_nestedObject() {
        JsonObject innerObject = new JsonObject();
        innerObject.add("a", 23);

        array.add(innerObject);

        assertEquals("[{\"a\":23}]", array.toString());
    }

    @Test
    public void add_json_nestedObject_modifiedAfterAdd() {
        JsonObject innerObject = new JsonObject();
        array.add(innerObject);

        innerObject.add("a", 23);

        assertEquals("[{\"a\":23}]", array.toString());
    }

    @Test
    public void set_int() {
        array.add(false);
        array.set(0, 23);

        assertEquals("[23]", array.toString());
    }

    @Test
    public void set_int_enablesChaining() {
        array.add(false);

        assertSame(array, array.set(0, 23));
    }

    @Test
    public void set_long() {
        array.add(false);

        array.set(0, 23L);

        assertEquals("[23]", array.toString());
    }

    @Test
    public void set_long_enablesChaining() {
        array.add(false);

        assertSame(array, array.set(0, 23L));
    }

    @Test
    public void set_float() {
        array.add(false);

        array.set(0, 3.14f);

        assertEquals("[3.14]", array.toString());
    }

    @Test
    public void set_float_enablesChaining() {
        array.add(false);

        assertSame(array, array.set(0, 3.14f));
    }

    @Test
    public void set_double() {
        array.add(false);

        array.set(0, 3.14d);

        assertEquals("[3.14]", array.toString());
    }

    @Test
    public void set_double_enablesChaining() {
        array.add(false);

        assertSame(array, array.set(0, 3.14d));
    }

    @Test
    public void set_boolean() {
        array.add(false);

        array.set(0, true);

        assertEquals("[true]", array.toString());
    }

    @Test
    public void set_boolean_enablesChaining() {
        array.add(false);

        assertSame(array, array.set(0, true));
    }

    @Test
    public void set_string() {
        array.add(false);

        array.set(0, "foo");

        assertEquals("[\"foo\"]", array.toString());
    }

    @Test
    public void set_string_enablesChaining() {
        array.add(false);

        assertSame(array, array.set(0, "foo"));
    }

    @Test
    public void set_jsonNull() {
        array.add(false);

        array.set(0, Json.NULL);

        assertEquals("[null]", array.toString());
    }

    @Test
    public void set_jsonArray() {
        array.add(false);

        array.set(0, new JsonArray());

        assertEquals("[[]]", array.toString());
    }

    @Test
    public void set_jsonObject() {
        array.add(false);

        array.set(0, new JsonObject());

        assertEquals("[{}]", array.toString());
    }

    @Test
    public void set_json_failsWithInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(0, Json.NULL));
    }

    @Test
    public void set_json_enablesChaining() {
        array.add(false);

        assertSame(array, array.set(0, Json.NULL));
    }

    @Test
    public void set_json_replacesDifferentArrayElements() {
        array.add(3).add(6).add(9);

        array.set(1, 4).set(2, 5);

        assertEquals("[3,4,5]", array.toString());
    }

    @Test
    public void remove_failsWithInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
    }

    @Test
    public void remove_removesElement() {
        array.add(23);

        array.remove(0);

        assertEquals("[]", array.toString());
    }

    @Test
    public void remove_keepsOtherElements() {
        array.add("a").add("b").add("c");

        array.remove(1);

        assertEquals("[\"a\",\"c\"]", array.toString());
    }

    @Test
    public void write_empty() throws IOException {
        JsonWriter writer = mock(JsonWriter.class);
        array.write(writer);

        InOrder inOrder = inOrder(writer);
        inOrder.verify(writer).writeArrayOpen();
        inOrder.verify(writer).writeArrayClose();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void write_withSingleValue() throws IOException {
        JsonWriter writer = mock(JsonWriter.class);
        array.add(23);

        array.write(writer);

        InOrder inOrder = inOrder(writer);
        inOrder.verify(writer).writeArrayOpen();
        inOrder.verify(writer).writeNumber("23");
        inOrder.verify(writer).writeArrayClose();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void write_withMultipleValues() throws IOException {
        JsonWriter writer = mock(JsonWriter.class);
        array.add(23).add("foo").add(false);

        array.write(writer);

        InOrder inOrder = inOrder(writer);
        inOrder.verify(writer).writeArrayOpen();
        inOrder.verify(writer).writeNumber("23");
        inOrder.verify(writer).writeArraySeparator();
        inOrder.verify(writer).writeString("foo");
        inOrder.verify(writer).writeArraySeparator();
        inOrder.verify(writer).writeLiteral("false");
        inOrder.verify(writer).writeArrayClose();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void isArray() {
        assertTrue(array.isArray());
    }

    @Test
    public void asArray() {
        assertSame(array, array.asArray());
    }

    @Test
    public void equals_trueForSameInstance() {
        assertEquals(array, array);
    }

    @Test
    public void equals_trueForEqualArrays() {
        assertEquals(array(), array());
        assertEquals(array("foo", "bar"), array("foo", "bar"));
    }

    @Test
    public void equals_falseForDifferentArrays() {
        assertNotEquals(array("foo", "bar"), array("foo", "bar", "baz"));
        assertNotEquals(array("foo", "bar"), array("bar", "foo"));
    }

    @Test
    public void equals_falseForNull() {
        assertNotEquals(null, array);
    }

    @Test
    public void equals_falseForSubclass() {
        assertNotEquals(array, new JsonArray(array) {
        });
    }

    @Test
    public void hashCode_equalsForEqualArrays() {
        assertEquals(array().hashCode(), array().hashCode());
        assertEquals(array("foo").hashCode(), array("foo").hashCode());
    }

    @Test
    public void hashCode_differsForDifferentArrays() {
        assertNotEquals(array().hashCode(), array("bar").hashCode());
        assertNotEquals(array("foo").hashCode(), array("bar").hashCode());
    }

    @Test
    public void canBeSerializedAndDeserialized() throws Exception {
        array.add(true).add(3.14d).add(23).add("foo").add(new JsonArray().add(false));

        assertEquals(array, serializeAndDeserialize(array));
    }

    @Test
    public void deserializedArrayCanBeAccessed() throws Exception {
        array.add(23);

        JsonArray deserializedArray = serializeAndDeserialize(array);

        assertEquals(23, deserializedArray.get(0).asInt());
    }

    private static JsonArray array(String... values) {
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(value);
        }
        return array;
    }

    @Test
    public void stream_ShouldProcessAllElementsSequentially() {
        array.add("score1.ly")
                .add("README.md")
                .add("score2.ly")
                .add(42);

        List<JsonValue> elements = array.stream()
                .collect(Collectors.toList());

        assertEquals(4, elements.size());
        assertEquals("score1.ly", elements.get(0).asString());
        assertFalse(array.stream().isParallel(), "Stream should be sequential");
    }

    @Test
    public void stream_ShouldAllowFilteringAndMapping() {
        array.add("score1.ly")
                .add("README.md")
                .add("score2.ly")
                .add(42);

        List<String> lilyPondFiles = array.stream()
                .filter(JsonValue::isString)
                .map(JsonValue::asString)
                .filter(name -> name.endsWith(".ly"))
                .collect(Collectors.toList());

        assertEquals(2, lilyPondFiles.size());
        assertTrue(lilyPondFiles.contains("score1.ly"));
        assertTrue(lilyPondFiles.contains("score2.ly"));
    }

    @Test
    public void parallelStream_ShouldBeParallel() {
        array.add("score1.ly")
                .add("README.md")
                .add("score2.ly")
                .add(42);

        assertTrue(array.parallelStream().isParallel(), "Stream should be parallel");

        long count = array.parallelStream().count();
        assertEquals(4, count);
    }

    @Test
    public void stream_OnEmptyArray_ShouldBeEmpty() {
        JsonArray emptyArray = new JsonArray();
        assertEquals(0, emptyArray.stream().count());
    }

    @Test
    void toStringList_ShouldReturnValidList() {
        var array = new JsonArray().add("score1.ly").add("score2.ly");
        List<String> list = array.toStringList();

        assertEquals(2, list.size());
        assertEquals("score1.ly", list.get(0));
        assertEquals("score2.ly", list.get(1));
    }

    @Test
    void toIntList_ShouldReturnValidList() {
        var array = new JsonArray().add(42).add(-10);
        List<Integer> list = array.toIntList();

        assertEquals(2, list.size());
        assertEquals(42, list.get(0));
        assertEquals(-10, list.get(1));
    }

    @Test
    void toDoubleList_ShouldReturnValidList() {
        var array = new JsonArray().add(3.14).add(2.71);
        List<Double> list = array.toDoubleList();

        assertEquals(2, list.size());
        assertEquals(3.14, list.get(0));
        assertEquals(2.71, list.get(1));
    }

    @Test
    void toFloatList_ShouldReturnValidList() {
        var array = new JsonArray().add(1.5f).add(0.0f);
        List<Float> list = array.toFloatList();

        assertEquals(2, list.size());
        assertEquals(1.5f, list.get(0));
        assertEquals(0.0f, list.get(1));
    }

    @Test
    void toBooleanList_ShouldReturnValidList() {
        var array = new JsonArray().add(true).add(false);
        List<Boolean> list = array.toBooleanList();

        assertEquals(2, list.size());
        assertTrue(list.get(0));
        assertFalse(list.get(1));
    }

    @Test
    void lists_ShouldBeUnmodifiable() {
        var array = new JsonArray().add("item");
        List<String> list = array.toStringList();

        // Verifica che la lista sia strutturalmente immutabile
        assertThrows(UnsupportedOperationException.class, () -> list.add("new-item"),
                "Should throw UnsupportedOperationException when trying to modify the list");

        assertThrows(UnsupportedOperationException.class, () -> list.remove(0),
                "Should throw UnsupportedOperationException when trying to remove elements");
    }

    @Test
    void toList_ShouldThrowOnTypeMismatch() {
        // Un array misto contenente una stringa e un intero
        var mixedArray = new JsonArray().add(100).add("not-an-int");

        // Deve fallire immediatamente quando tenta di mappare la stringa a intero
        assertThrows(UnsupportedOperationException.class, mixedArray::toIntList,
                "Should throw UnsupportedOperationException on type mismatch");
    }

    @Test
    void toList_OnEmptyArray_ShouldReturnEmptyList() {
        var emptyArray = new JsonArray();

        List<String> list = emptyArray.toStringList();
        assertTrue(list.isEmpty());

        // Anche se vuota, deve rimanere non modificabile
        assertThrows(UnsupportedOperationException.class, () -> list.add("test"));
    }
}
