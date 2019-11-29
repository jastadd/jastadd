/* Copyright (c) 2013-2015, Jesper Öqvist <jesper.oqvist@cs.lth.se>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Lund University nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jastadd.option;

import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Option requiring value.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class ValueOption extends Option<String> {

  /**
   * Additional description to be printed after the value descriptions.
   */
  protected String additionalDescription = "";

  /**
   * Defines whether this option accepts multiple values in a list.
   */
  protected boolean acceptsMultipleValues = false;

  /**
   * Defines whether this option requires a value.
   */
  protected boolean needsValue = true;

  /**
   * The user must input one of the allowed values, other values not
   * permitted. If restricted is {@code false}, then any value is
   * permitted.
   */
  protected boolean restricted = true;

  private final Set<String> acceptedValues = new LinkedHashSet<String>();
  private final Collection<String[]> valueDescriptions = new LinkedList<String[]>();
  private final Collection<String> values = new LinkedList<String>();
  private final Collection<String> defaultValues = new LinkedList<String>();

  /**
   * Create a new option.
   *
   * @param optionName The name of the option
   * @param description The description that will be printed in the help line
   */
  public ValueOption(String optionName, String description) {
    super(optionName, description);
  }

  /**
   * Add a default value for this option
   * @return this option object
   */
  public ValueOption defaultValue(String defaultValue) {
    addDefaultValue(defaultValue, "");
    return this;
  }

  public ValueOption needsValue(boolean needsValue) {
    this.needsValue = needsValue;
    return this;
  }

  /**
   * Allow the option to have any value.
   * @return self
   */
  public ValueOption acceptAnyValue() {
    restricted = false;
    return this;
  }

  public ValueOption acceptMultipleValues(boolean multipleValues) {
    this.acceptsMultipleValues = multipleValues;
    return this;
  }

  @Override
  public void matchWithoutArg(PrintStream err) {
    if (needsValue) {
      err.println("Warning: Missing value for option " + name);
    }
    onMatch(err);
  }

  @Override
  public void matchWithArg(PrintStream err, String arg) {
    onMatch(err, arg);
  }

  @Override
  protected String description() {
    if (defaultValues.isEmpty()) {
      return description;
    } else {
      StringBuilder desc = new StringBuilder();
      desc.append(description);
      desc.append(" (default=\'");
      boolean first = true;
      for (String value: defaultValues) {
        if (!first) {
          desc.append(",");
        }
        first = false;
        desc.append(value);
      }
      desc.append("\')");
      return desc.toString();
    }
  }

  @Override
  protected void printDescription(PrintStream out) {
    super.printDescription(out);
    if (restricted && !acceptedValues.isEmpty()) {
      printIndent(out, TAB_2);
      out.format("%s %s of the following%s values:",
          needsValue ? "Requires" : "Accepts",
          acceptsMultipleValues ? "some" : "one",
          needsValue ? "" : " optional");
      out.println();
      for (String[] s : valueDescriptions) {
        printIndent(out, TAB_3);
        String value = "'" + s[0] + "'";
        out.print(value);
        printIndent(out, Math.max(TAB_4 - value.length() - TAB_3, 1));
        printDescription(out, s[1], TAB_4);
      }
    }
    if (!additionalDescription.isEmpty()) {
      printIndent(out, TAB_2);
      printDescription(out, additionalDescription, TAB_2);
    }
  }

  /**
   * Adds an accepted value.
   *
   * For options which support a set of values.
   *
   * @param value The name of the value
   * @param desc The description of the value
   * @return self
   */
  public ValueOption addDefaultValue(String value, String desc) {
    acceptedValues.add(value);
    valueDescriptions.add(new String[]{value, desc});
    values.add(value);
    defaultValues.add(value);
    return this;
  }

  /**
   * Adds an accepted value.
   *
   * For options which support a set of values.
   *
   * @param value The name of the value
   * @param desc The description of the value
   * @return self
   */
  public ValueOption addAcceptedValue(String value, String desc) {
    acceptedValues.add(value);
    valueDescriptions.add(new String[]{value, desc});
    return this;
  }

  /**
   * Set an additional description text for this option.
   * @param desc additional description text
   * @return self
   */
  public ValueOption additionalDescription(String desc) {
    additionalDescription = desc;
    return this;
  }

  @Override
  protected void onMatch(PrintStream out) {
    reportWarnings(out);
    values.clear();
    isMatched = true;
  }

  /**
   * Match the option with argument.
   */
  protected final void onMatch(PrintStream out, String arg) {
    reportWarnings(out);

    values.clear();

    if (restricted) {
      StringTokenizer tokenizer = new StringTokenizer(arg, ",");
      boolean first = true;
      while (tokenizer.hasMoreTokens()) {
        if (!first && !acceptsMultipleValues) {
          out.println("Warning: too many values given to option '" + name +
              "'. The extraneous values will be ignored!");
          break;
        }
        first = false;
        String value = tokenizer.nextToken();
        reportWarnings(out, value);
        values.add(value);
      }
    } else {
      values.add(arg);
    }

    isMatched = true;
  }

  /**
   * Check problems and print warning messages.
   *
   * Prints warnings if the same option occurs more than once.
   * Prints warnings when a deprecated option is matched.
   *
   * @param out Output stream to print warnings to.
   * @param arg The argument given for the option.
   */
  public void reportWarnings(PrintStream out, String arg) {
    if (!acceptedValues.isEmpty() && !acceptedValues.contains(arg)) {
      out.format("Warning: the option '%s' does not accept the value '%s'", name, arg);
      out.println();
    }
  }

  /**
   * Returns only the first value if there are multiple values.
   */
  @Override
  public String value() {
    if (!values.isEmpty()) {
      return values.iterator().next();
    } else {
      return "";
    }
  }

  /**
   * @return {@code true} if the given value was set
   */
  public boolean hasValue(String query) {
    for (String value: values) {
      if (query.equals(value)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return Number of values for this option
   */
  public int numValues() {
    return values.size();
  }
}
