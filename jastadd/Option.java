/**
 * The JastAdd tool (http://jastadd.org) is covered by the BSD License.
 *
 * Copyright (c) 2005, The JastAdd Team
 * All rights reserved.
 */
package jastadd;

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

  protected String desc;
  protected String name;
  protected String longName;
  protected String longNameLower;
  protected boolean nonStandard = false;
  protected boolean deprecated = false;
  protected boolean matched = false;
  protected String value;
  protected String defaultValue;
  protected boolean needsValue = false;
  protected boolean hasDefaultValue = false;

  /**
   * Create a new standard option without value argument.
   *
   * @param optionName The name of the option
   * @param description The description that will be printed in the help line
   * @param needsValue <code>true</code> if this option requires a value
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
    name = optionName;
    longName = OPTION_PREFIX + optionName;
    longNameLower = longName.toLowerCase();
    desc = description;
    nonStandard = isNonStandard;
    value = "";
    this.needsValue = needsValue;
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
   */
  public void printHelp() {
    int col = 18;
    System.err.print("  " + longName);
    col -= longName.length();
    if (col < 1)
      col = 1;
    for (int i = 0; i < col; ++i)
      System.err.print(' ');
    if (hasDefaultValue) {
      System.err.print(desc);
      System.err.println(" (default = \""
          + defaultValue + "\")");
    } else {
      System.err.println(desc);
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
   * @return The number of arguments matched.
   * Returns zero if there is no match.
   */
  public int match(String[] args, int index) {
    int offset = 0;
    boolean currentMatch = false;
    if (args[index].toLowerCase().equals(longNameLower)) {
      if (needsValue) {
        if (index < (args.length - 1) && !args[index+1].startsWith("--")) {
          value = args[index+1];
          offset = 2;
        } else {
          System.err.println("Warning: Missing value for option " + name);
          offset = 1;
        }
      }
      currentMatch = true;
      offset = 1;
    } else if (needsValue && args[index].toLowerCase().startsWith(longNameLower + "=")) {
      value = args[index].substring(longNameLower.length() + 1);
      currentMatch = true;
      offset = 1;
    }

    if (currentMatch && matched) {

      System.err.println("Warning: option " + name
          + " occurs more than once in the argument list!");

    } else if (currentMatch && deprecated) {

      System.err.println("Warning: option " + name
          + " has been deprecated! Use of this option is discouraged!");

    }

    matched = currentMatch || matched;

    return offset;
  }
}
