/* Copyright (c) 2013, Jesper Öqvist <jesper.oqvist@cs.lth.se>
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
public class ValueOption extends Option {

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

  private Set<String> acceptedValues = new LinkedHashSet<String>();
  private Collection<String[]> valueDescriptions = new LinkedList<String[]>();

  /**
   * Create a new option.
   *
   * @param optionName The name of the option
   * @param description The description that will be printed in the help line
   */
  public ValueOption(String optionName, String description) {
    super(optionName, description);
  }

  @Override
  public void matchWithoutArg(PrintStream err) {
    err.println("Warning: Missing value for option " + name);
    doMatch(err);
  }

  @Override
  public void matchWithArg(PrintStream err, String arg) {
    doMatch(err, arg);
  }

  @Override
  public int matchWithSeparateArg(PrintStream err, String arg) {
    doMatch(err, arg);
    return 1;
  }

  @Override
  protected void printDescription(PrintStream out) {
    super.printDescription(out);
    if (!acceptedValues.isEmpty()) {
      printIndent(out, TAB_2);
      out.println(
          (needsValue ? "requires " : "accepts ") +
          (acceptsMultipleValues ? "some" : "one" ) +
           " of the following" + (needsValue ? " " : " optional") + " values:");
      for (String[] s : valueDescriptions) {
        printIndent(out, TAB_3);
        out.print("'" + s[0] + "'");
        printIndent(out, Math.max(TAB_4 - s[0].length() - TAB_3, 1));
        printDescription(out, s[1], TAB_3);
      }
    }
    if (!additionalDescription.isEmpty()) {
      printIndent(out, TAB_2);
      out.println(additionalDescription);
    }
  }

  /**
   * Adds an accepted value.
   *
   * For options which support a set of values.
   *
   * @param value The name of the value
   * @param desc The description of the value
   */
  public void addAcceptedValue(String value, String desc) {
    acceptedValues.add(value);
    valueDescriptions.add(new String[]{value, desc});
  }

  /**
   * Match the option with argument.
   */
  protected final void doMatch(PrintStream out, String arg) {
    reportWarnings(out);

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
      onMatch(value);
    }

    alreadyMatched = true;
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
      out.println("Warning: the option '" + name +
          "' does not accept the value '" + arg + "'");
    }
  }

  /**
   * Called when this option is matched with argument.
   *
   * Override this method to handle the option.
   *
   * @param arg The argument given for the option.
   */
  public void onMatch(String arg) {
  }
}
