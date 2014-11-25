/* Copyright (c) 2012-2013, Jesper Öqvist <jesper.oqvist@cs.lth.se>
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses command-line arguments based on a set of options.
 * Non-options are added to a list of file names.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class ArgumentParser {

  private final Map<String,Option<?>> options = new HashMap<String,Option<?>>();
  private final Collection<String> filenames = new ArrayList<String>();

  /**
   * Add a command-line option.
   * @param option
   */
  public void addOption(Option<?> option) {
    String name = (Option.PREFIX + option.name).toLowerCase();
    options.put(name, option);
  }

  /**
   * Add a collection of command-line options.
   * @param additionalOptions
   */
  public void addOptions(Collection<Option<?>> additionalOptions) {
    for (Option<?> option: additionalOptions) {
      String name = (Option.PREFIX + option.name).toLowerCase();
      options.put(name, option);
    }
  }

  /**
   * Parse command-line arguments.
   * @param args Command-line arguments
   * @param err output stream to print warnings to
   */
  public void parseArgs(String[] args, PrintStream err) {
    int i = 0;
    while (i < args.length) {
      String arg = args[i].toLowerCase();
      if (arg.startsWith(Option.PREFIX)) {
        Option<?> option = null;
        int sep = args[i].indexOf(Option.VALUE_SEPARATOR);
        if (sep != -1) {
          String namePart = arg.substring(0, sep);
          String argPart = args[i].substring(sep+1);
          option = options.get(namePart);
          if (option != null) {
            option.matchWithArg(err, argPart);
          }
        } else {
          option = options.get(arg);
          if (option != null) {
            option.matchWithoutArg(err);
          }
        }
        if (option == null) {
          err.println("Warning: unknown option '" + arg + "' will be ignored");
        }
      } else {
        // not an option - add to filename list
        filenames.add(args[i]);
      }
      i += 1;
    }
  }

  /**
   * Print descriptions of all standard non-deprecated options.
   * @param out The output stream to print descriptions to.
   */
  public void printHelp(PrintStream out) {
    for (Option<?> option: options.values()) {
      if (!option.isNonStandard && !option.isDeprecated) {
        option.printHelp(out);
      }
    }
  }

  /**
   * Print descriptions of all non-standard options.
   * @param out The output stream to print descriptions to.
   */
  public void printNonStandardOptions(PrintStream out) {
    for (Option<?> option: options.values()) {
      if (option.isNonStandard) {
        option.printHelp(out);
      }
    }
  }

  /**
   * @return All non-option arguments (assumed to be filenames).
   */
  public Collection<String> getFilenames() {
    return filenames;
  };
};
