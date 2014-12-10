/* Copyright (c) 2005-2013, The JastAdd Team
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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.jastadd.option.FlagOption;
import org.jastadd.option.Option;

import java.io.*;

/**
 * JastAdd task for Apache Ant.
 */
@SuppressWarnings("javadoc")
public class JastAddTask extends Task {

  private final Configuration config = new Configuration();

  private void setOption(Option<?> option, boolean value) {
    if (option instanceof FlagOption) {
      if (value) {
        option.matchWithoutArg(System.err);
      } else {
        option.reportWarnings(System.err);
      }
    } else {
      option.matchWithArg(System.err, ""+value);
    }
  }

  private void setOption(Option<?> option, String value) {
    option.matchWithArg(System.err, value);
  }

  public JastAddTask() {
    super();
  }

  @Override
  public void init() {
    super.init();
  }

  public void addConfiguredFileSet(FileSet fileset) {
    DirectoryScanner s = fileset.getDirectoryScanner(getProject());
    String[] files = s.getIncludedFiles();
    String baseDir = s.getBasedir().getPath();
    for (int i = 0; i < files.length; i++) {
      config.filenames.add(baseDir + File.separator + files[i]);
    }
  }

  public void setASTNode(String arg) {
    setOption(config.ASTNodeOption, arg);
  }

  public void setList(String arg) {
    setOption(config.ListOption, arg);
  }

  public void setOpt(String arg) {
    setOption(config.OptOption, arg);
  }

  public void setASTNodeSuper(String arg) {
    setOption(config.ASTNodeSuperOption, arg);
  }
    
  public void setJjtree(boolean enable) {
    setOption(config.jjtreeOption, enable);
  }

  public void setGrammar(String arg) {
    setOption(config.grammarOption, arg);
  }

  public void setBeaver(boolean enable) {
    setOption(config.beaverOption, enable);
  }

  public void setLineColumnNumbers(boolean enable) {
    setOption(config.lineColumnNumbersOption, enable);
  }

  public void setPackage(String arg) {
    setOption(config.packageNameOption, arg);
  }

  public void setOutdir(String arg) {
    setOption(config.outputDirOption, arg);
  }

  public void setDefaultMap(String arg) {
    setOption(config.defaultMapOption, arg);
  }

  public void setDefaultSet(String arg) {
    setOption(config.defaultSetOption, arg);
  }

  public void setLazyMaps(boolean enable) {
    setOption(config.lazyMapsOption, enable);
  }

  public void setNoLazyMaps(boolean enable) {
    setOption(config.noLazyMapsOption, enable);
  }

  public void setRewrite(String arg) {
    setOption(config.rewriteOption, arg);
  }

  public void setVisitCheck(boolean enable) {
    setOption(config.visitCheckOption, enable);
  }

  public void setNovisitcheck(boolean enable) {
    setOption(config.noVisitCheckOption, enable);
  }

  public void setCacheCycle(boolean enable) {
    setOption(config.cacheCycleOption, enable);
  }

  public void setNoCacheCycle(boolean enable) {
    setOption(config.noCacheCycleOption, enable);
  }

  public void setComponentCheck(boolean enable) {
    setOption(config.componentCheckOption, enable);
  }

  public void setNoComponentCheck(boolean enable) {
    setOption(config.noComponentCheckOption, enable);
  }

  public void setNoInhEqCheck(boolean enable) {
    setOption(config.noInhEqCheckOption, enable);
  }

  public void setSuppressWarnings(boolean enable) {
    setOption(config.suppressWarningsOption, enable);
  }

  public void setDoc(boolean enable) {
    setOption(config.docOption, enable);
  }

  public void setDoxygen(boolean enable) {
    setOption(config.doxygenOption, enable);
  }

  public void setLicense(String arg) {
    setOption(config.licenseOption, arg);
  }

  public void setJava14(boolean enable) {
    setOption(config.java1_4Option, enable);
  }

  public void setDebug(boolean enable) {
    setOption(config.debugOption, enable);
  }

  public void setSynch(boolean enable) {
    setOption(config.synchOption, enable);
  }

  public void setStaticState(boolean enable) {
    setOption(config.staticStateOption, enable);
  }

  public void setNoStatic(boolean enable) {
    setOption(config.noStaticOption, enable);
  }

  public void setRefineLegacy(boolean enable) {
    setOption(config.refineLegacyOption, enable);
  }

  public void setNoRefineLegacy(boolean enable) {
    setOption(config.noRefineLegacyOption, enable);
  }

  public void setStagedRewrites(boolean enable) {
    setOption(config.stagedRewritesOption, enable);
  }

  public void setDeterministic(boolean enable) {
    setOption(config.deterministicOption, enable);
  }

  public void setTracing(String arg) {
    setOption(config.tracingOption, arg);
  }

  public void setCache(String arg) {
    setOption(config.cacheOption, arg);
  }

  public void setCacheAll(boolean enable) {
    setOption(config.cacheAllOption, enable);
  }

  public void setNoCaching(boolean enable) {
    setOption(config.noCachingOption, enable);
  }

  public void setCacheNone(boolean enable) {
    setOption(config.cacheNoneOption, enable);
  }

  public void setCacheImplicit(boolean enable) {
    setOption(config.cacheImplicitOption, enable);
  }

  public void setIgnoreLazy(boolean enable) {
    setOption(config.ignoreLazyOption, enable);
  }

  public void setIncremental(String arg) {
    setOption(config.incrementalOption, arg);
  }

  public void setFullFlush(boolean enable) {
    setOption(config.fullFlushOption, enable);
  }

  public void setFlush(String arg) {
    setOption(config.flushOption, arg);
  }

  public void setIndent(String arg) {
    setOption(config.indentOption, arg);
  }

  public void setMinListSize(String arg) {
    setOption(config.minListSizeOption, arg);
  }

  @Override
  public void execute() throws BuildException {
    System.err.println("generating node types and weaving aspects");
    JastAdd jastadd = new JastAdd(config);
    int exitVal = jastadd.compile(System.out, System.err);
    if (exitVal != 0) {
      throw new BuildException("Failed to generate AST!");
    }
    System.err.println("done");
  }
}
