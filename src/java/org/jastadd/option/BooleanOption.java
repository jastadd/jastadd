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

/**
 * Option that takes a boolean value.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class BooleanOption extends Option<Boolean> {

  private boolean defaultValue = false;
  private boolean value;

  /**
   * Create a new boolean option.
   *
   * @param optionName The name of the option
   * @param description The description that will be printed in the help line
   */
  public BooleanOption(String optionName, String description) {
    super(optionName, description);
    value = defaultValue;
  }

  /**
   * Set a default value for this option.
   * @return this option object
   */
  public BooleanOption defaultValue(boolean newDefault) {
    defaultValue = newDefault;
    this.value = defaultValue;
    return this;
  }

  @Override
  public void matchWithoutArg(PrintStream err) {
    onMatch(err);
  }

  @Override
  public void matchWithArg(PrintStream err, String arg) {
    onMatch(err, arg);
  }

  @Override
  protected void onMatch(PrintStream out) {
    reportWarnings(out);
    isMatched = true;
    value = true;// no argument -> match to true
  }

  /**
   * Match the option with argument.
   */
  protected final void onMatch(PrintStream out, String arg) {
    reportWarnings(out);

    String lc = arg.toLowerCase();
    if (lc.equals("true") || lc.equals("on") || lc.equals("yes") ||
        lc.equals("enable") || lc.equals("enabled")) {
      value = true;
    } else if (lc.equals("false") || lc.equals("off") || lc.equals("no") ||
        lc.equals("disable") || lc.equals("disabled")) {
      value = false;
    } else {
      out.println("Warning: unknown value for option '" + name +
          "' - the argument '" + arg + "' is not a boolean value.");
    }

    isMatched = true;
  }

  @Override
  protected String description() {
    return description + " (default='" + defaultValue + "')";
  }

  /**
   * @return the current boolean value of this option
   */
  @Override
  public Boolean value() {
    return value;
  }
}
