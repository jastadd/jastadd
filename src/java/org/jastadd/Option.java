/* Copyright (c) 2012, Jesper Öqvist <jesper.oqvist@cs.lth.se>
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
package org.jastadd;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Handles matching and parsing of command line options.
 *
 * An option either takes no value argument or requires a
 * value. If the needsValue boolean is <code>true</code>
 * then the Option will attempt to parse a value to go with it.
 *
 * An option can be made deprecated by calling setDeprecated.
 * This causes a warning to be printed whenever the option is
 * matched.
 *
 * Options are not case sensitive.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class Option {

  /**
   * The prefix string used for all command-line
   * options.
   */
  public static final String OPTION_PREFIX = "--";
  
  protected static final String INDENT = "  ";
  protected static final int OPT_COL_WIDTH = 18;

  protected String desc;
  protected String additionalDesc;
  protected String name;
  protected String longName;
  protected String longNameLower;
  protected boolean nonStandard = false;
  protected boolean deprecated = false;
  protected boolean matched = false;
  protected String value;
  protected String defaultValue;
  protected boolean needsValue = false;
  protected boolean valueIsOptional = false;
  protected boolean hasDefaultValue = false;
  protected boolean acceptsMultipleValues = false;
  protected List<String[]> acceptedValues;

  /**
   * Create a new standard option without value argument.
   *
   * @param optionName The name of the option
   * @param description The description that will be printed in the help line
   */
  public Option(String optionName, String description) {
    this(optionName, description, false);
  }

  /**
   * Create a new standard option.
   *
   * @param optionName The name of the option
   * @param description The description that will be printed in the help line
   * @param needsValue <code>true</code> if this option requires a value
   */
  public Option(String optionName, String description, boolean needsValue) {
    this(optionName, description, needsValue, false);
  }

  /**
   * Create a new option.
   *
   * @param optionName The name of the option
   * @param description The description that will be printed in the help line
   * @param needsValue <code>true</code> if this option requires a value
   * @param isNonStandard <code>true</code> makes this option a
   * non-standard option
   */
  public Option(String optionName, String description, boolean needsValue,
      boolean isNonStandard) {
    this(optionName, description, needsValue, isNonStandard, false);
  }
  
  /**
   * Create a new option.
   *
   * @param optionName The name of the option
   * @param description The description that will be printed in the help line
   * @param needsValue <code>true</code> if this option requires a value
   * @param isNonStandard <code>true</code> makes this option a
   * non-standard option
   * @param valueIsOptional <code>true</code> if the value is optional
   */
  public Option(String optionName, String description, boolean needsValue,
      boolean isNonStandard, boolean valueIsOptional) {
    name = optionName;
    longName = OPTION_PREFIX + optionName;
    longNameLower = longName.toLowerCase();
    desc = description;
    nonStandard = isNonStandard;
    value = "";
    this.needsValue = needsValue;
    this.valueIsOptional = valueIsOptional;
  }

  /**
   * Make this option non-standard.
   */
  public void setNonStandard() {
    nonStandard = true;
  }

  /**
   * Make this option deprecated.
   */
  public void setDeprecated() {
    deprecated = true;
  }

  /**
   * @return <code>true</code> if the option has been matched
   */
  public boolean matched() {
    return matched;
  }

  /**
   * @return The options current value. This is only meaningful
   * to use if matched() returns <code>true</code>.
   */
  public String value() {
    return value;
  }

  /**
   * Set a value for this option.
   * This works just like a default value,  except that
   * it is not printed in the help line for the option.
   * @param v New value
   */
  public void setValue(String v) {
    value = v;
  }

  /**
   * Set a default value for this option
   * @param v The default value
   */
  public void setDefaultValue(String v) {
    defaultValue = v;
    value = v;
    hasDefaultValue = true;
  }

  /**
   * Print help line for this option.
   * @param out output stream to print help to
   */
  public void printHelp(PrintStream out) {
    int col = OPT_COL_WIDTH;
    out.print(INDENT + longName);
    col -= longName.length();
    if (col < 1)
      col = 1;
    printWhiteSpace(out, col);
    if (hasDefaultValue) {
      out.print(desc);
      out.println(" (default = \"" + defaultValue + "\")");
    } else {
      out.println(desc);
    }
    printValueHelp(out);
    printDesc(out, additionalDesc, OPTION_PREFIX.length() + OPT_COL_WIDTH + INDENT.length(), true);
  }
  
  protected void printValueHelp(PrintStream out) {
    if (acceptedValues == null) {
      return;
    }
    printWhiteSpace(out, OPT_COL_WIDTH + 2*INDENT.length());
    out.println("accepts " + (acceptsMultipleValues ? "multiple" : "one" ) + 
         " of the following" + (needsValue ? " " : " optional") + " values:");
    for (String[] s : acceptedValues) {
      printWhiteSpace(out, OPT_COL_WIDTH + 2*INDENT.length());
      out.print("'" + s[0] + "'");
      printWhiteSpace(out, OPT_COL_WIDTH/2 - s[0].length());
      printDesc(out, s[1], OPT_COL_WIDTH + OPT_COL_WIDTH/2 + 3*INDENT.length(), false);
    }
  }
  
  protected void printDesc(PrintStream out, String desc, int col, boolean indentFirstLine) {
    if (desc == null) {
      return;
    }
    StringTokenizer tok = new StringTokenizer(desc,"\n");
    while (tok.hasMoreTokens()) {
      if (indentFirstLine) {
        printWhiteSpace(out, col);
      }
      out.println(tok.nextToken());
      indentFirstLine = true;
    }
    
  }
  
  protected void printWhiteSpace(PrintStream out, int nbr) {
    for (int i = 0; i < nbr; ++i) {
      out.print(' ');
    }    
  }
  
  /**
   * Match this option against the argument list.
   *
   * Prints warnings if the same option occurs more than once.
   * Prints warnings when a deprecated option is matched.
   *
   * @param args The argument list array
   * @param index Offset in the argument list to check for a match
   * @param err output stream to print warnings to
   * @return The number of arguments matched.
   * Returns zero if there is no match.
   */
  public int match(String[] args, int index, PrintStream err) {
    int offset = 0;
    boolean currentMatch = false;
    if (args[index].toLowerCase().equals(longNameLower)) {
      if (needsValue) {
        if (index < (args.length - 1) && !args[index+1].startsWith(OPTION_PREFIX)) {
          value = args[index+1];
          offset = 2;
        } else {
          err.println("Warning: Missing value for option " + name);
          offset = 1;
        }
      }
      currentMatch = true;
      offset = 1;
    } else if ((needsValue || valueIsOptional) 
        && args[index].toLowerCase().startsWith(longNameLower + "=")) {
      value = args[index].substring(longNameLower.length() + 1);
      currentMatch = true;
      offset = 1;
    }

    if (currentMatch && matched) {

      err.println("Warning: option " + name
          + " occurs more than once in the argument list!");

    } else if (currentMatch && deprecated) {

      err.println("Warning: option " + name
          + " has been deprecated! Use of this option is discouraged!");

    }

    matched = currentMatch || matched;

    return offset;
  }
  
  /**
   * Sets the option to matched.
   * 
   * This is useful when another option makes this option matched.
   * For instance, cache=analyze sets the tracing option to true.
   * 
   * The configuration checks the state of the options rather than
   * the option flags in the root node when checking if an option
   * is matched. 
   */
  public void setToMatched() {
    matched = true;
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
    if (acceptedValues == null) {
      acceptedValues = new ArrayList<String[]>();
    }
    acceptedValues.add(new String[]{value, desc});
  }
  
  /**
   * Configures if this option accepts multiple values in a list
   * 
   * @param acceptsMultiple true if multiple values are supported
   */
  public void acceptsMultipleValues(boolean acceptsMultiple) {
    acceptsMultipleValues = acceptsMultiple;
  }
  
  /**
   * Adds an additional description
   * 
   * @param desc The additional description
   */
  public void addAdditionalDesc(String desc) {
    additionalDesc = desc;
  }
}
