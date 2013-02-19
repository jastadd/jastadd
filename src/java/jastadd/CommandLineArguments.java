/* Copyright (c) 2006, Görel Hedin <gorel.hedin@cs.lth.se>
 *               2012, Jesper Öqvist <jesper.oqvist@cs.lth.se>
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
package jastadd;

import java.util.*;

/**
 * A class for retrieving the command line arguments such as operands
 * and options.
 */
public class CommandLineArguments {

  private List<Option> options = new LinkedList<Option>();
  private List<String> operands = new ArrayList<String>();

  /**
   * Constructor
   */
  public CommandLineArguments() {
  }

  /**
   * Add an option
   * @param option
   */
  public void addOption(Option option) {
    options.add(option);
  }

  /**
   * Match the options against the command line arguments.
   * @param args Command-line arguments
   */
  public void match(String[] args) {
    int i = 0;
    Option lastMatch = null;
    while (i < args.length) {
      Iterator<Option> iter = options.iterator();
      while (iter.hasNext() && i < args.length) {
        Option option = iter.next();
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
    Iterator<Option> iter = options.iterator();
    while (iter.hasNext()) {
      Option option = iter.next();
      if (!option.nonStandard && !option.deprecated)
        option.printHelp();
    }
  }

  /**
   * Print the description for each non-standard command line option.
   */
  public void printNonStandardOptions() {
    Iterator<Option> iter = options.iterator();
    while (iter.hasNext()) {
      Option option = iter.next();
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
   * @param k Index of operand to return
   * @return the k'th operand at the end of the command line,
   * where the operands are numbered from 0 to getNumOperands()-1.
   */
  public String getOperand(int k) {
    return (String) operands.get(k);
  };
};
