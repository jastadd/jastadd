/* Copyright (c) 2012-2014, Jesper Öqvist <jesper.oqvist@cs.lth.se>
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

import org.jastadd.tinytemplate.TemplateContext;

/**
 * Handles matching and parsing of a command-line option.
 *
 * This kind of option does not take an argument.
 *
 * A warning is printed whenever a deprecated option is
 * matched.
 *
 * A warning is printed if an option occurs more than once.
 *
 * Options are not case sensitive.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 * @param <ValueType>
 */
abstract public class Option<ValueType> implements Comparable<Option<?>> {

  /**
   * The prefix string used for all command-line
   * options.
   */
  public static final String PREFIX = "--";

  /**
   * The option-value separator.
   */
  public static final String VALUE_SEPARATOR = "=";

  /**
   * Indentation of option name (first tab stop)
   */
  protected static final int TAB_1 = 2;

  /**
   * Indentation of option description (second tab stop)
   */
  protected static final int TAB_2 = 20;

  /**
   * Indentation of value name (third tab stop)
   */
  protected static final int TAB_3 = 22;

  /**
   * Indentation of value description (fourth tab stop)
   */
  protected static final int TAB_4 = 33;

  protected String description = "";
  protected String name;
  private String templateVariable = "";
  protected boolean isNonStandard = false;
  protected boolean isDeprecated = false;
  protected String deprecationMessage = "";
  protected String deprecatedSince = "";

  /**
   * True if this option has been matched.
   */
  protected boolean isMatched = false;

  /**
   * Create a new option.
   *
   * @param optionName The name of the option
   * @param description The description that will be printed in the help line
   */
  public Option(String optionName, String description) {
    this.name = optionName;
    this.description = description;
  }

  /**
   * Set the name of the corresponding template variable which this option
   * binds to.
   * @param templateName
   * @return self
   */
  public final Option<ValueType> templateName(String templateName) {
    templateVariable = templateName;
    return this;
  }

  /**
   * Make this a non-standard option
   * @return self
   */
  public final Option<ValueType> nonStandard() {
    isNonStandard = true;
    return this;
  }

  /**
   * Make this a option deprecated
   * @param since the version in which this option was deprecated
   * @return self
   */
  public final Option<ValueType> deprecated(String since) {
    isDeprecated = true;
    deprecatedSince = since;
    return this;
  }

  /**
   * Make this a option deprecated
   * @param since the version in which this option was deprecated
   * @return self
   * @param message deprecation message
   */
  public final Option<ValueType> deprecated(String since, String message) {
    deprecationMessage = message;
    return deprecated(since);
  }

  /**
   * Print help line for this option.
   * @param out output stream to print help to
   */
  public final void printHelp(PrintStream out) {
    printIndent(out, TAB_1);
    String longName = PREFIX + name;
    out.print(longName);
    printIndent(out, Math.max(TAB_2 - longName.length() - TAB_1, 1));
    printDescription(out);
  }

  /**
   * Print option description.
   */
  protected void printDescription(PrintStream out) {
    printDescription(out, description(), TAB_2);
  }

  protected String description() {
    if (isDeprecated && !deprecationMessage.isEmpty()) {
      return deprecationMessage;
    }
    return description;
  }

  protected void printDescription(PrintStream out, String desc, int col) {
    String[] lines = desc.split("\n", -1);
    for (int i = 0; i < lines.length; ++i) {
      if (i != 0) {
        printIndent(out, col);
      }
      out.println(lines[i]);
    }
  }

  protected void printIndent(PrintStream out, int nbr) {
    for (int i = 0; i < nbr; ++i) {
      out.print(' ');
    }
  }

  /**
   * Called when the option was matched against an argument list, with no
   * arguments for the option.
   *
   * @param err Error output stream
   */
  public void matchWithoutArg(PrintStream err) {
    onMatch(err);
  }

  /**
   * Called when the option was matched against an argument list, with an
   * embedded argument for the option.
   *
   * @param err Error output stream
   * @param arg The argument
   */
  public void matchWithArg(PrintStream err, String arg) {
    err.println("Warning: the option '" + name + "' does not take an argument. " +
        "The argument value '" + arg + "' will be ignored!");
    onMatch(err);
  }

  /**
   * Match the option, without argument.
   */
  protected void onMatch(PrintStream out) {
    reportWarnings(out);
    isMatched = true;
  }

  /**
   * @return {@code true} if the option has been matched
   */
  public boolean isMatched() {
    return isMatched;
  }

  /**
   * Check problems and print warning messages.
   *
   * Prints warnings if the same option occurs more than once.
   * Prints warnings when a deprecated option is matched.
   *
   * @param out Output stream to print warnings to.
   */
  public void reportWarnings(PrintStream out) {
    if (isMatched) {
      out.println("Warning: the option " + name
          + " occurs more than once in the argument list!");
    } else if (isDeprecated) {
      out.print("Warning: the option " + name +
          " has been deprecated since version " +
          deprecatedSince);
      if (!deprecationMessage.isEmpty()) {
        out.print(" (");
        out.print(deprecationMessage);
        out.print(")");
      }
      out.println(". Use of this option is discouraged!");
    }
  }

  @Override
  public String toString() {
    return PREFIX + name;
  }

  /**
   * @return {@code true} if this option is deprecated
   */
  public boolean isDeprecated() {
    return isDeprecated;
  }

  /**
   * @return the name of this option
   */
  public String name() {
    return name;
  }

  /**
   * Bind the current value of this option to a template variable in the
   * provided context. Only binds if this option has a templateName assigned.
   * @param context
   */
  public void bind(TemplateContext context) {
    if (!templateVariable.isEmpty()) {
      context.bind(templateVariable, value());
    }
  }

  /**
   * @return the current value of this option
   */
  abstract public ValueType value();

  @Override
  public int compareTo(Option other) {
    return name.compareTo(other.name);
  }
}
