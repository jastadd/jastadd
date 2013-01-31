/*
 * Compilers and Interpreters
 *
 * GH 001106 Created
 * JO 120427 Refactored
 */
package jastadd;

import java.util.*;

/**
 * A class for retrieving the command line arguments such as operands
 * and options.
 */
public class CommandLineArguments {

  private List options = new LinkedList();
  private List operands = new ArrayList();

  public CommandLineArguments() {
  }

  public void addOption(Option option) {
    options.add(option);
  }

  /**
   * Match the options against the command line arguments.
   */
  public void match(String[] args) {
    int i = 0;
    Option lastMatch = null;
    while (i < args.length) {
      Iterator iter = options.iterator();
      while (iter.hasNext() && i < args.length) {
        Option option = (Option) iter.next();
        int num = option.match(args, i);
        if (num > 0) {

          lastMatch = option;
          i += num;
          continue;

        } else if (lastMatch == option) {

          if (args[i].startsWith(Option.OPTION_PREFIX)) {
            System.err.println("Unknown option \"" + args[i]
                + "\" will be ignored");
          } else {
            // none of the options match this argument
            // -- it's an operand
            operands.add(args[i]);
          }

          i += 1;

        } else if (lastMatch == null) {

          lastMatch = option;

        }
      }
    }
  }

  /**
   * Print the description for each standard, non-deprecated,
   * command line option.
   */
  public void printHelp() {
    Iterator iter = options.iterator();
    while (iter.hasNext()) {
      Option option = (Option) iter.next();
      if (!option.nonStandard && !option.deprecated)
        option.printHelp();
    }
  }

  /**
   * Print the description for each non-standard command line option.
   */
  public void printNonStandardOptions() {
    Iterator iter = options.iterator();
    while (iter.hasNext()) {
      Option option = (Option) iter.next();
      if (option.nonStandard)
        option.printHelp();
    }
  }

  /**
   * @return the number of operands at the end of the command line.
   */
  public int getNumOperands() {
    return operands.size();
  };
  
  /**
   * @return the k'th operand at the end of the command line,
   * where the operands are numbered from 0 to getNumOperands()-1.
   */
  public String getOperand(int k) {
    return (String) operands.get(k);
  };
};
