/*******************************************************************************
 * Copyright (c) 2015 EclipseSource.
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

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;


/**
 * Enables human readable JSON output by inserting whitespace between values.after commas and
 * colons. Example:
 *
 * <pre>
 * jsonValue.writeTo(writer, PrettyPrint.singleLine());
 * </pre>
 */
public class PrettyPrint extends WriterConfig {

  private final char[] indentChars;

  protected PrettyPrint(char[] indentChars) {
    this.indentChars = indentChars;
  }

  /**
   * Print every value on a separate line. Use tabs (<code>\t</code>) for indentation.
   *
   * @return A PrettyPrint instance for wrapped mode with tab indentation
   */
  @NotNull
  public static PrettyPrint singleLine() {
    return new PrettyPrint(null);
  }

  /**
   * Print every value on a separate line. Use the given number of spaces for indentation.
   *
   * @param number
   *          the number of spaces to use
   * @return A PrettyPrint instance for wrapped mode with spaces indentation
   */
  @NotNull
  public static PrettyPrint indentWithSpaces(int number) {
    if (number < 0) {
      throw new IllegalArgumentException("number is negative");
    }
    char[] chars = new char[number];
    Arrays.fill(chars, ' ');
    return new PrettyPrint(chars);
  }

  /**
   * Do not break lines, but still insert whitespace between values.
   *
   * @return A PrettyPrint instance for single-line mode
   */
  @NotNull
  public static PrettyPrint indentWithTabs() {
    return new PrettyPrint(new char[] {'\t'});
  }

  @Override
  protected JsonWriter createWriter(Writer writer) {
    return new PrettyPrintWriter(writer, indentChars);
  }

  private static class PrettyPrintWriter extends JsonWriter {

    private final char[] indentChars;
    private int indent;
    // True right after '[' or '{': the line break is deferred until we know the container is not empty.
    private boolean afterOpen;

    private PrettyPrintWriter(Writer writer, char[] indentChars) {
      super(writer);
      this.indentChars = indentChars;
    }

    @Override
    protected void writeLiteral(String value) throws IOException {
      flushOpen();
      super.writeLiteral(value);
    }

    @Override
    protected void writeNumber(String string) throws IOException {
      flushOpen();
      super.writeNumber(string);
    }

    @Override
    protected void writeString(String string) throws IOException {
      flushOpen();
      super.writeString(string);
    }

    @Override
    protected void writeMemberName(String name) throws IOException {
      flushOpen();
      super.writeMemberName(name);
    }

    @Override
    protected void writeArrayOpen() throws IOException {
      flushOpen();
      indent++;
      writer.write('[');
      afterOpen = true;
    }

    @Override
    protected void writeArrayClose() throws IOException {
      indent--;
      closeContainer();
      writer.write(']');
    }

    @Override
    protected void writeArraySeparator() throws IOException {
      writer.write(',');
      if (!writeNewLine()) {
        writer.write(' ');
      }
    }

    @Override
    protected void writeObjectOpen() throws IOException {
      flushOpen();
      indent++;
      writer.write('{');
      afterOpen = true;
    }

    @Override
    protected void writeObjectClose() throws IOException {
      indent--;
      closeContainer();
      writer.write('}');
    }

    @Override
    protected void writeMemberSeparator() throws IOException {
      writer.write(':');
      writer.write(' ');
    }

    @Override
    protected void writeObjectSeparator() throws IOException {
      writer.write(',');
      if (!writeNewLine()) {
        writer.write(' ');
      }
    }

    private void flushOpen() throws IOException {
      if (afterOpen) {
        afterOpen = false;
        writeNewLine();
      }
    }

    private void closeContainer() throws IOException {
      if (afterOpen) {
        // empty container: print it as "[]" or "{}"
        afterOpen = false;
      } else {
        writeNewLine();
      }
    }

    private boolean writeNewLine() throws IOException {
      if (indentChars == null) {
        return false;
      }
      writer.write('\n');
      for (int i = 0; i < indent; i++) {
        writer.write(indentChars);
      }
      return true;
    }

  }

}
