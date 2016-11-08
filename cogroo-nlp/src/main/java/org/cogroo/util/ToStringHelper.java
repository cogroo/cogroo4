/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cogroo.util;

import java.util.LinkedList;
import java.util.List;


/**
 * Support class for {@link Objects#toStringHelper}.
 *
 * @author Jason Lee
 * @since 2.0
 */
public class ToStringHelper {
  private final String className;
  private final List<ValueHolder> valueHolders =
      new LinkedList<ValueHolder>();
  private boolean omitNullValues = false;

  /**
   * Use {@link Objects#toStringHelper(Object)} to create an instance.
   */
  private ToStringHelper(String className) {
    this.className = checkNotNull(className);
  }

  /**
   * Adds a name/value pair to the formatted output in {@code name=value}
   * format. If {@code value} is {@code null}, the string {@code "null"}
   * is used, unless {@link #omitNullValues()} is called, in which case this
   * name/value pair will not be added.
   */
  public ToStringHelper add(String name, Object value) {
    checkNotNull(name);
    addHolder(value).builder.append(name).append('=').append(value);
    return this;
  }

  /**
   * Adds a name/value pair to the formatted output in {@code name=value}
   * format.
   *
   * @since 11.0 (source-compatible since 2.0)
   */
  public ToStringHelper add(String name, boolean value) {
    checkNameAndAppend(name).append(value);
    return this;
  }

  /**
   * Adds a name/value pair to the formatted output in {@code name=value}
   * format.
   *
   * @since 11.0 (source-compatible since 2.0)
   */
  public ToStringHelper add(String name, char value) {
    checkNameAndAppend(name).append(value);
    return this;
  }

  /**
   * Adds a name/value pair to the formatted output in {@code name=value}
   * format.
   *
   * @since 11.0 (source-compatible since 2.0)
   */
  public ToStringHelper add(String name, double value) {
    checkNameAndAppend(name).append(value);
    return this;
  }

  /**
   * Adds a name/value pair to the formatted output in {@code name=value}
   * format.
   *
   * @since 11.0 (source-compatible since 2.0)
   */
  public ToStringHelper add(String name, float value) {
    checkNameAndAppend(name).append(value);
    return this;
  }

  /**
   * Adds a name/value pair to the formatted output in {@code name=value}
   * format.
   *
   * @since 11.0 (source-compatible since 2.0)
   */
  public ToStringHelper add(String name, int value) {
    checkNameAndAppend(name).append(value);
    return this;
  }

  /**
   * Adds a name/value pair to the formatted output in {@code name=value}
   * format.
   *
   * @since 11.0 (source-compatible since 2.0)
   */
  public ToStringHelper add(String name, long value) {
    checkNameAndAppend(name).append(value);
    return this;
  }

  private StringBuilder checkNameAndAppend(String name) {
    checkNotNull(name);
    return addHolder().builder.append(name).append('=');
  }

  /**
   * Adds an unnamed value to the formatted output.
   *
   * <p>It is strongly encouraged to use {@link #add(String, Object)} instead
   * and give value a readable name.
   */
  public ToStringHelper addValue(Object value) {
    addHolder(value).builder.append(value);
    return this;
  }

  /**
   * Adds an unnamed value to the formatted output.
   *
   * <p>It is strongly encouraged to use {@link #add(String, boolean)} instead
   * and give value a readable name.
   *
   * @since 11.0 (source-compatible since 2.0)
   */
  public ToStringHelper addValue(boolean value) {
    addHolder().builder.append(value);
    return this;
  }

  /**
   * Adds an unnamed value to the formatted output.
   *
   * <p>It is strongly encouraged to use {@link #add(String, char)} instead
   * and give value a readable name.
   *
   * @since 11.0 (source-compatible since 2.0)
   */
  public ToStringHelper addValue(char value) {
    addHolder().builder.append(value);
    return this;
  }

  /**
   * Adds an unnamed value to the formatted output.
   *
   * <p>It is strongly encouraged to use {@link #add(String, double)} instead
   * and give value a readable name.
   *
   * @since 11.0 (source-compatible since 2.0)
   */
  public ToStringHelper addValue(double value) {
    addHolder().builder.append(value);
    return this;
  }

  /**
   * Adds an unnamed value to the formatted output.
   *
   * <p>It is strongly encouraged to use {@link #add(String, float)} instead
   * and give value a readable name.
   *
   * @since 11.0 (source-compatible since 2.0)
   */
  public ToStringHelper addValue(float value) {
    addHolder().builder.append(value);
    return this;
  }

  /**
   * Adds an unnamed value to the formatted output.
   *
   * <p>It is strongly encouraged to use {@link #add(String, int)} instead
   * and give value a readable name.
   *
   * @since 11.0 (source-compatible since 2.0)
   */
  public ToStringHelper addValue(int value) {
    addHolder().builder.append(value);
    return this;
  }

  /**
   * Adds an unnamed value to the formatted output.
   *
   * <p>It is strongly encouraged to use {@link #add(String, long)} instead
   * and give value a readable name.
   *
   * @since 11.0 (source-compatible since 2.0)
   */
  public ToStringHelper addValue(long value) {
    addHolder().builder.append(value);
    return this;
  }

  /**
   * Returns a string in the format specified by {@link
   * Objects#toStringHelper(Object)}.
   */
  @Override public String toString() {
    // create a copy to keep it consistent in case value changes
    boolean omitNullValuesSnapshot = omitNullValues;
    boolean needsSeparator = false;
    StringBuilder builder = new StringBuilder(32).append(className)
        .append('{');
    for (ValueHolder valueHolder : valueHolders) {
      if (!omitNullValuesSnapshot || !valueHolder.isNull) {
        if (needsSeparator) {
          builder.append(", ");
        } else {
          needsSeparator = true;
        }
        // must explicitly cast it, otherwise GWT tests might fail because
        // it tries to access StringBuilder.append(StringBuilder), which is
        // a private method
        // TODO(user): change once 5904010 is fixed
        CharSequence sequence = valueHolder.builder;
        builder.append(sequence);
      }
    }
    return builder.append('}').toString();
  }

  private ValueHolder addHolder() {
    ValueHolder valueHolder = new ValueHolder();
    valueHolders.add(valueHolder);
    return valueHolder;
  }

  private ValueHolder addHolder(Object value) {
    ValueHolder valueHolder = addHolder();
    valueHolder.isNull = (value == null);
    return valueHolder;
  }

  private static final class ValueHolder {
    final StringBuilder builder = new StringBuilder();
    boolean isNull;
  }
  
  
  

  /**
   * Creates an instance of {@link ToStringHelper}.
   *
   * <p>This is helpful for implementing {@link Object#toString()}.
   * Specification by example: <pre>   {@code
   *   // Returns "ClassName{}"
   *   Objects.toStringHelper(this)
   *       .toString();
   *
   *   // Returns "ClassName{x=1}"
   *   Objects.toStringHelper(this)
   *       .add("x", 1)
   *       .toString();
   *
   *   // Returns "MyObject{x=1}"
   *   Objects.toStringHelper("MyObject")
   *       .add("x", 1)
   *       .toString();
   *
   *   // Returns "ClassName{x=1, y=foo}"
   *   Objects.toStringHelper(this)
   *       .add("x", 1)
   *       .add("y", "foo")
   *       .toString();
   *   }}
   *
   *   // Returns "ClassName{x=1}"
   *   Objects.toStringHelper(this)
   *       .omitNullValues()
   *       .add("x", 1)
   *       .add("y", null)
   *       .toString();
   *   }}</pre>
   *
   * <p>Note that in GWT, class names are often obfuscated.
   *
   * @param self the object to generate the string for (typically {@code this}),
   *        used only for its class name
   * @since 2.0
   */
  public static ToStringHelper toStringHelper(Object self) {
    return new ToStringHelper(simpleName(self.getClass()));
  }

  /**
   * Creates an instance of {@link ToStringHelper} in the same manner as
   * {@link Objects#toStringHelper(Object)}, but using the name of {@code clazz}
   * instead of using an instance's {@link Object#getClass()}.
   *
   * <p>Note that in GWT, class names are often obfuscated.
   *
   * @param clazz the {@link Class} of the instance
   * @since 7.0 (source-compatible since 2.0)
   */
  public static ToStringHelper toStringHelper(Class<?> clazz) {
    return new ToStringHelper(simpleName(clazz));
  }

  /**
   * Creates an instance of {@link ToStringHelper} in the same manner as
   * {@link Objects#toStringHelper(Object)}, but using {@code className} instead
   * of using an instance's {@link Object#getClass()}.
   *
   * @param className the name of the instance type
   * @since 7.0 (source-compatible since 2.0)
   */
  public static ToStringHelper toStringHelper(String className) {
    return new ToStringHelper(className);
  }

  /**
   * {@link Class#getSimpleName()} is not GWT compatible yet, so we
   * provide our own implementation.
   */
  private static String simpleName(Class<?> clazz) {
    String name = clazz.getName();

    // the nth anonymous class has a class name ending in "Outer$n"
    // and local inner classes have names ending in "Outer.$1Inner"
    name = name.replaceAll("\\$[0-9]+", "\\$");

    // we want the name of the inner class all by its lonesome
    int start = name.lastIndexOf('$');

    // if this isn't an inner class, just find the start of the
    // top level class name.
    if (start == -1) {
      start = name.lastIndexOf('.');
    }
    return name.substring(start + 1);
  }

  /**
   * Returns the first of two given parameters that is not {@code null}, if
   * either is, or otherwise throws a {@link NullPointerException}.
   *
   * <p><b>Note:</b> if {@code first} is represented as an {@code Optional<T>},
   * this can be accomplished with {@code first.or(second)}. That approach also
   * allows for lazy evaluation of the fallback instance, using
   * {@code first.or(Supplier)}.
   *
   * @return {@code first} if {@code first} is not {@code null}, or
   *     {@code second} if {@code first} is {@code null} and {@code second} is
   *     not {@code null}
   * @throws NullPointerException if both {@code first} and {@code second} were
   *     {@code null}
   * @since 3.0
   */
  public static <T> T firstNonNull(T first, T second) {
    return first != null ? first : checkNotNull(second);
  }

  public static <T> T checkNotNull(T reference) {
    if (reference == null) {
      throw new NullPointerException();
    }
    return reference;
  }
}

