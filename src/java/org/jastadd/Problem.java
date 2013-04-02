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
package org.jastadd;

import java.io.PrintStream;

/**
 * JastAdd code generation problem.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public abstract class Problem {

  @SuppressWarnings("javadoc")
  public static class Error extends Problem {

    public Error(String message) {
      super(message);
    }

    public Error(String message, String fileName) {
      super(message, fileName);
    }

    public Error(String message, int line) {
      super(message, line);
    }

    public Error(String message, int line, int column) {
      super(message, line, column);
    }

    public Error(String message, String file, int line, int column) {
      super(message, file, line, column);
    }

    @Override
    public boolean isError() {
      return true;
    }
  }

  @SuppressWarnings("javadoc")
  public static class Warning extends Problem {

    public Warning(String message) {
      super(message);
    }

    public Warning(String message, String fileName) {
      super(message, fileName);
    }

    public Warning(String message, int line) {
      super(message, line);
    }

    public Warning(String message, int line, int column) {
      super(message, line, column);
    }

    public Warning(String message, String file, int line, int column) {
      super(message, file, line, column);
    }

    @Override
    public boolean isError() {
      return false;
    }
  }

  private final String message;
  private final String file;
  private final int line;
  private final int column;

  /**
   * @param message
   */
  public Problem(String message) {
    this(message, -1, -1);
  }

  /**
   * @param message
   * @param fileName
   */
  public Problem(String message, String fileName) {
    this(message, fileName, -1, -1);
  }

  /**
   * @param message
   * @param line
   */
  public Problem(String message, int line) {
    this(message, -1, -1);
  }

  /**
   * @param message
   * @param line
   * @param column
   */
  public Problem(String message, int line, int column) {
    this(message, "", line, column);
  }

  /**
   * @param message
   * @param file
   * @param line
   * @param column
   */
  public Problem(String message, String file, int line, int column) {
    this.message = message;
    this.file = file;
    this.line = line;
    this.column = column;
  }

  /**
   * @return <code>true</code> if this is an error, <code>false</code> if it
   * is a warning
   */
  public abstract boolean isError();

  /**
   * Print the problem to the given PrintStream
   * @param out
   */
  public final void print(PrintStream out) {
    out.println(this.toString());
  }

  @Override
  public String toString() {
    String kind = isError() ? "Error" : "Warning";
    String loc = file.isEmpty() ? "" : file;
    if (line != -1) {
      loc = loc.isEmpty() ? "" + line : loc + ":" + line;
      if (column != -1) {
        loc += ":" + column;
      }
    }
    if (!loc.isEmpty()) {
      loc = " at " + loc;
    }
    return kind + loc + ": " + message;
  }
}
