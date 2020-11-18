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

  public static class ProblemBuilder {
    String message;
    String filename = "";
    int line = -1;
    int column = -1;

    /** Add a message using a format string. */
    public ProblemBuilder message(String format, Object... args) {
      message = String.format(format, args);
      return this;
    }

    /** Add a message without using a format string. */
    public ProblemBuilder message(String message) {
      this.message = message;
      return this;
    }

    public ProblemBuilder sourceFile(String filename) {
      if (filename == null) {
        throw new NullPointerException("filename cannot be null");
      }
      this.filename = filename;
      return this;
    }

    public ProblemBuilder sourceLine(int line) {
      this.line = line;
      return this;
    }

    public ProblemBuilder sourceColumn(int column) {
      this.column = column;
      return this;
    }

    public Error buildError() {
      checkNotNull();
      return new Error(message, filename, line, column);
    }

    public Warning buildWarning() {
      checkNotNull();
      return new Warning(message, filename, line, column);
    }

    private void checkNotNull() {
      if (message == null) {
        throw new NullPointerException("message cannot be null");
      }
    }
  }

  public static class Error extends Problem {
    protected Error(String message, String file, int line, int column) {
      super(message, file, line, column);
    }

    @Override
    public boolean isError() {
      return true;
    }
  }

  public static class Warning extends Problem {
    protected Warning(String message, String file, int line, int column) {
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

  protected Problem(String message, String file, int line, int column) {
    this.message = message;
    this.file = file;
    this.line = line;
    this.column = column;
  }

  public static ProblemBuilder builder() {
    return new ProblemBuilder();
  }

  /**
   * @return <code>true</code> if this is an error, <code>false</code> if it
   * is a warning
   */
  public abstract boolean isError();

  /**
   * Print the problem to the given PrintStream.
   */
  public final void print(PrintStream out) {
    out.println(this.toString());
  }

  @Override
  public String toString() {
    String kind = isError() ? "Error" : "Warning";
    String loc = file.isEmpty() ? "" : file;
    if (line != -1) {
      loc = loc.isEmpty() ? "" + line : String.format("%s:%d", loc, line);
      if (column != -1) {
        loc += ":" + column;
      }
    }
    if (!loc.isEmpty()) {
      loc = " at " + loc;
    }
    return String.format("%s%s: %s", kind, loc, message);
  }
}
