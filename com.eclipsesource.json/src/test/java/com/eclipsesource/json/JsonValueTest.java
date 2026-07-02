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

import com.eclipsesource.json.TestUtil.RunnableEx;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static com.eclipsesource.json.TestUtil.assertException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;


public class JsonValueTest {

    @Test
    public void writeTo() throws IOException {
        JsonValue value = new JsonObject();
        Writer writer = new StringWriter();

        value.writeTo(writer);

        assertEquals("{}", writer.toString());
    }

    @Test
    public void writeTo_failsWithNullWriter() {
        final JsonValue value = new JsonObject();

        assertException(NullPointerException.class, (RunnableEx) () -> value.writeTo(null, WriterConfig.MINIMAL));
    }

    @Test
    public void writeTo_failsWithNullConfig() {
        final JsonValue value = new JsonObject();

        assertException(NullPointerException.class, (RunnableEx) () -> value.writeTo(new StringWriter(), null));
    }

    @Test
    public void toString_failsWithNullConfig() {
        final JsonValue value = new JsonObject();

        assertException(NullPointerException.class, (RunnableEx) () -> value.toString(null));
    }

    @Test
    public void writeTo_doesNotCloseWriter() throws IOException {
        JsonValue value = new JsonObject();
        Writer writer = spy(new StringWriter());

        value.writeTo(writer);

        verify(writer, never()).close();
    }

    @Test
    public void asObject_failsOnIncompatibleType() {
        assertException(UnsupportedOperationException.class, (Runnable) Json.NULL::asObject);
    }

    @Test
    public void asArray_failsOnIncompatibleType() {
        assertException(UnsupportedOperationException.class, (Runnable) Json.NULL::asArray);
    }

    @Test
    public void asString_failsOnIncompatibleType() {
        assertException(UnsupportedOperationException.class, (Runnable) Json.NULL::asString);
    }

    @Test
    public void asInt_failsOnIncompatibleType() {
        assertException(UnsupportedOperationException.class, (Runnable) Json.NULL::asInt);
    }

    @Test
    public void asLong_failsOnIncompatibleType() {
        assertException(UnsupportedOperationException.class, (Runnable) Json.NULL::asLong);
    }

    @Test
    public void asFloat_failsOnIncompatibleType() {
        assertException(UnsupportedOperationException.class, (Runnable) Json.NULL::asFloat);
    }

    @Test
    public void asDouble_failsOnIncompatibleType() {
        assertException(UnsupportedOperationException.class, (Runnable) Json.NULL::asDouble);
    }

    @Test
    public void asBoolean_failsOnIncompatibleType() {
        assertException(UnsupportedOperationException.class, (Runnable) Json.NULL::asBoolean);
    }

    @Test
    public void isXxx_returnsFalseForIncompatibleType() {
        JsonValue jsonValue = new JsonValue() {
            @Override
            void write(JsonWriter writer) {
            }
        };

        assertFalse(jsonValue.isArray());
        assertFalse(jsonValue.isObject());
        assertFalse(jsonValue.isString());
        assertFalse(jsonValue.isNumber());
        assertFalse(jsonValue.isBoolean());
        assertFalse(jsonValue.isNull());
        assertFalse(jsonValue.isTrue());
        assertFalse(jsonValue.isFalse());
    }

}
