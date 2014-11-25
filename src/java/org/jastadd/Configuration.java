/* Copyright (c) 2013-2014, Jesper Öqvist <jesper.oqvist@cs.lth.se>
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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.jastadd.ast.AST.Grammar;
import org.jastadd.option.ArgumentParser;
import org.jastadd.option.BooleanOption;
import org.jastadd.option.FlagOption;
import org.jastadd.option.Option;
import org.jastadd.option.ValueOption;
import org.jastadd.tinytemplate.TemplateContext;

/**
 * Tracks JastAdd configuration options.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class Configuration {

  /**
   * Indentation level cache.
   */
  protected java.util.List<String> indList = new ArrayList<String>(32);

  /**
   * One level of indentation.
   */
  public String indent = "  ";

  /**
   * License to include at start of each source file.
   */
  public String license = "";

  /**
   * Builds an indentation string equal to a certain level of
   * indentation.
   *
   * @param level the required indentation level
   * @return the indentation string
   */
  public final String ind(int level) {
    while (indList.size() <= level) {
      if (indList.size() == 0) {
        indList.add("");
      } else {
        indList.add(indList.get(indList.size()-1) + indent);
      }
    }
    return indList.get(level);
  }

  Option<String> ASTNodeOption = new ValueOption("ASTNode",
      "set the name of the ASTNode type")
      .unrestricted()
      .defaultValue("ASTNode")
      .templateName("ASTNode");

  Option<String> ListOption = new ValueOption("List",
      "set the name of the List type")
      .unrestricted()
      .defaultValue("List")
      .templateName("List");

  Option<String> OptOption = new ValueOption("Opt",
      "set the name of the Opt type")
      .unrestricted()
      .defaultValue("Opt")
      .templateName("Opt");

  Option<Boolean> jjtreeOption = new FlagOption("jjtree",
      "use jjtree base node, this requires --grammar to be set")
      .templateName("JJTree");

  Option<String> grammarOption = new ValueOption("grammar",
      "the name of the grammar's parser, required when using --jjtree")
      .templateName("ParserName");

  Option<String> defaultMapOption = new ValueOption(
      "defaultMap", "use this expression to construct maps for attribute caches")
      .unrestricted()
      .defaultValue("new java.util.HashMap(4)")
      .nonStandard()
      .templateName("CreateDefaultMap");

  Option<String> defaultSetOption = new ValueOption(
      "defaultSet", "use this expression to construct sets for attribute caches")
      .unrestricted()
      .defaultValue("new java.util.HashSet(4)")
      .nonStandard()
      .templateName("CreateDefaultSet");

  Option<Boolean> lazyMapsOption = new BooleanOption("lazyMaps", "use lazy maps")
      .defaultValue(true)
      .templateName("LazyMaps");

  Option<Boolean> privateOption = new FlagOption("private",
      "generated methods will use the private modifier")
      .templateName("PrivateModifier")
      .nonStandard();

  ValueOption rewriteOption = new ValueOption("rewrite",
      "enable and select rewrite mode (ReRAGs)")
      .needsValue(false)
      .acceptMultipleValues(false)
      .addDefaultValue("none", "rewrites are disabled")
      .addAcceptedValue("regular", "rewrites do not use NTAs")
      .addAcceptedValue("cnta", "evaluate rewrites with circular NTAs");

  Option<Boolean> beaverOption = new FlagOption("beaver",
      "use beaver.Symbol base node")
      .templateName("Beaver");

  Option<Boolean> lineColumnNumbersOption = new FlagOption("lineColumnNumbers",
      "generate interface for storing line and column numbers");

  Option<Boolean> visitCheckOption = new BooleanOption("visitCheck",
      "enable circularity check for attributes")
      .defaultValue(true)
      .templateName("VisitCheckEnabled");

  Option<Boolean> traceVisitCheckOption = new BooleanOption("traceVisitCheck",
      "just print an error rather than throwing a circularity check exception")
      .nonStandard()
      .templateName("TraceVisitCheck");

  Option<Boolean> cacheCycleOption = new BooleanOption("cacheCycle",
      "enable cache cycle optimization for circular attributes")
      .defaultValue(true)
      .templateName("CacheCycle");

  Option<Boolean> componentCheckOption = new BooleanOption("componentCheck",
      "strongly connected component checking for circular attributes")
      .templateName("ComponentCheck");

  Option<Boolean> inhEqCheckOption = new BooleanOption("inhEqCheck",
      "enalbe check for inherited equations")
      .defaultValue(true);

  Option<Boolean> suppressWarningsOption = new FlagOption(
      "suppressWarnings", "attempt to suppress Java warnings")
      .deprecated("2.1.2");

  Option<Boolean> refineLegacyOption = new BooleanOption("refineLegacy",
      "enable the legacy refine syntax")
      .defaultValue(true);

  Option<Boolean> stagedRewritesOption = new FlagOption("stagedRewrites",
      "")// TODO description
      .templateName("StagedRewrites")
      .nonStandard();

  Option<Boolean> doxygenOption = new FlagOption("doxygen",
      "")
      .deprecated("2.1.5", "this option currently does nothing");

  Option<Boolean> cacheAllOption = new FlagOption("cacheAll",
      "")
      .deprecated("2.1.5", "this option currently does nothing");

  Option<Boolean> noCachingOption = new FlagOption("noCaching",
      "")
      .deprecated("2.1.5", "this option currently does nothing");

  Option<Boolean> cacheNoneOption = new FlagOption("cacheNone",
      "")
      .deprecated("2.1.5", "this option currently does nothing");

  Option<Boolean> cacheImplicitOption = new FlagOption("cacheImplicit",
      "")
      .deprecated("2.1.5", "this option currently does nothing");

  Option<Boolean> ignoreLazyOption = new FlagOption("ignoreLazy",
      "")
      .deprecated("2.1.5", "this option currently does nothing");

  Option<Boolean> fullFlushOption = new FlagOption("fullFlush",
      "")
      .deprecated("2.1.5", "this option currently does nothing");

  Option<Boolean> docOption = new FlagOption("doc",
      "generate javadoc like .html pages from sources")
      .deprecated("2.1.9", "this option currently does nothing");

  Option<Boolean> java1_4Option = new FlagOption("java1.4",
      "generate for Java 1.4")
      .deprecated("2.1.9");

  Option<Boolean> noLazyMapsOption = new FlagOption("noLazyMaps",
      "")
      .deprecated("2.1.9", "replaced by --lazyMaps=false");

  Option<Boolean> noVisitCheckOption = new FlagOption("noVisitCheck",
      "")
      .deprecated("2.1.9", "replaced by --visitCheck=false");

  Option<Boolean> noCacheCycleOption = new FlagOption("noCacheCycle",
      "")
      .deprecated("2.1.9", "replaced by --cacheCycle=false");

  Option<Boolean> noRefineLegacyOption = new FlagOption("noRefineLegacy",
      "")
      .deprecated("2.1.9", "replaced by --refineLegacy=false");

  Option<Boolean> noComponentCheckOption = new FlagOption("noComponentCheck",
      "")
      .deprecated("2.1.9", "currently has no effect");

  Option<Boolean> noInhEqCheckOption = new FlagOption("noInhEqCheck",
      "")
      .deprecated("2.1.9", "replaced by --inhEqCheck=false");

  Option<Boolean> noStaticOption = new FlagOption(
      "noStatic", "the generated state field is non-static")
      .deprecated("2.1.9", "replaced by --staticState=false")
      .nonStandard();

  Option<Boolean> deterministicOption = new FlagOption("deterministic",
      "ensure deterministic collection attribute iteration order")
      .deprecated("2.1.9");

  Option<String> licenseOption = new ValueOption("license",
      "include the given file as a header in each generated file") {
    @Override
    public void reportWarnings(PrintStream out, String filename) {
      super.reportWarnings(out, filename);
      if (filename.isEmpty()) {
        out.println("Warning: empty license file name");
      } else {
        try {
          readFile(filename);
        } catch (java.io.IOException e) {
          out.println("Warning: could not read license file " + filename);
        }
      }
    }
  };

  Option<Boolean> debugOption = new FlagOption("debug",
      "generate run-time checks for debugging")
      .templateName("DebugMode");

  Option<Boolean> synchOption = new FlagOption("synch",
      "generate synchronized blocks around all AST-accessing methods")
      .nonStandard();

  Option<Boolean> staticStateOption = new BooleanOption("staticState",
      "the generated state field is static")
      .defaultValue(true)
      .templateName("StaticState")
      .nonStandard();

  ValueOption outputDirOption = new ValueOption("o",
      "optional base output directory, default is current directory")
      .unrestricted()
      .defaultValue(System.getProperty("user.dir"));

  ValueOption tracingOption = new ValueOption("tracing",
      "weaves in code collecting evaluation events")
      .acceptMultipleValues(true)
      .needsValue(false)
      .addDefaultValue("none", "tracing is disabled")
      .addAcceptedValue("all", "trace all events")
      .addAcceptedValue("compute", "trace begin and end of attribute computation")
      .addAcceptedValue("cache", "trace value cached, read cache, and cache aborted")
      .addAcceptedValue("rewrite", "trace rewrite evaluation")
      .addAcceptedValue("circular", "trace circular attribute evaluation")
      .addAcceptedValue("circularNTA", "trace circular attribute evaluation")
      .addAcceptedValue("copy", "trace node copy operations")
      .addAcceptedValue("flush", "trace flush operations")
      .additionalDescription("all events are collected by default\n"
          + "the result is available via the API in org.jastadd.Tracer");

  ValueOption flushOption = new ValueOption("flush",
      "adds flushing of cached values")
      .acceptMultipleValues(true)
      .needsValue(false)
      .addAcceptedValue("full", "flushing of all computed values (combines attr, coll, and rewrite)")
      .addDefaultValue("attr", "adds flushing of attributes (syn,inh)")
      .addDefaultValue("coll", "adds flushing of collection attributes")
      .addAcceptedValue("rewrite", "adds flushing of rewrites");

  ValueOption packageNameOption = new ValueOption("package",
      "optional package name for generated classes");

  FlagOption versionOption = new FlagOption("version",
      "print version string and halt");

  FlagOption helpOption = new FlagOption("help",
      "prints a short help output and halts");

  FlagOption printNonStandardOptionsOption = new FlagOption("X",
      "print list of non-standard options and halt");

  ValueOption indentOption = new ValueOption("indent",
      "type of indentation to use")
      .addDefaultValue("2space", "two spaces")
      .addAcceptedValue("4space", "four spaces")
      .addAcceptedValue("8space", "eight spaces")
      .addAcceptedValue("tab", "use tabs");

  ValueOption minListSizeOption = new ValueOption("minListSize",
      "minimum (non-empty) list size") {
    {
      unrestricted();
      defaultValue("4");
      templateName("MinListSize");
      nonStandard();
    }
    @Override
    public void reportWarnings(PrintStream out, String arg) {
      super.reportWarnings(out, arg);
      try {
        int size = Integer.parseInt(arg);
        if (size < 0) {
          out.println("Warning: minimum list size option must have a positive integer value!");
        }
      } catch (NumberFormatException e) {
        out.println("Warning: minimum list size option must be an integer!");
      }
    }
  };

  ValueOption cacheOption = new ValueOption("cache",
      "global cache configuration overriding 'lazy' keyword")
      .acceptMultipleValues(false)
      .addAcceptedValue("none", "disable attribute caching")
      .addAcceptedValue("all", "cache all attributes")
      .addAcceptedValue("config", "cache attributes according to a given .config file")
      .addAcceptedValue("implicit", "cache all attribute but also read a .config file that takes precedence")
      .addAcceptedValue( "analyze", "analyze the cache use during evaluation (when all attributes are cached)\n"
              + "the result is available via the API in org.jastadd.CacheAnalyzer")
      .additionalDescription(".config files have the following format:\n"
          + " ((cache|uncache) NodeType.AttrName((ParamType(,ParamType)*)?);)*");

  ValueOption incrementalOption = new ValueOption("incremental",
      "incremental evaluation")
      .acceptMultipleValues(true)
      .addDefaultValue("none", "incremental evaluation disabled")
      .addAcceptedValue("param", "dependency tracking on parameter level")
      .addAcceptedValue("region", "dependency tracking on region level")
      .addAcceptedValue("flush", "invalidate with flush")
      .addAcceptedValue("full", "full change propagation")
      .addAcceptedValue("debug", "generate code for debugging and dumping of dependencies");

  Option<Boolean> dotOption = new FlagOption("dot",
      "generate a Dot graph from the grammar")
      .nonStandard();

  Collection<String> filenames = new LinkedList<String>();

  /**
   * Indicates if there were unknown command-line options
   */
  final boolean unknownOptions;

  /**
   * Parse options from an argument list.
   * @param args Command-line arguments to build configuration from
   * @param err output stream to print configuration warnings to
   */
  public Configuration(String[] args, PrintStream err) {
    ArgumentParser argParser = argParser();
    unknownOptions = !argParser.parseArgs(args, err);
    filenames  = argParser.getFilenames();
  }

  /**
   * Create an uninitialized configuration.
   */
  public Configuration() {
    unknownOptions = false;
  }

  private ArgumentParser argParser() {
    ArgumentParser parser = new ArgumentParser();
    parser.addOptions(allOptions());
    return parser;
  }

  private Collection<Option<?>> allOptions() {
    Collection<Option<?>> allOptions = new LinkedList<Option<?>>();
    allOptions.add(ASTNodeOption);
    allOptions.add(ListOption);
    allOptions.add(OptOption);
    allOptions.add(jjtreeOption);
    allOptions.add(grammarOption);
    allOptions.add(defaultMapOption);
    allOptions.add(defaultSetOption);
    allOptions.add(lazyMapsOption);
    allOptions.add(privateOption);
    allOptions.add(rewriteOption);
    allOptions.add(beaverOption);
    allOptions.add(lineColumnNumbersOption);
    allOptions.add(visitCheckOption);
    allOptions.add(traceVisitCheckOption);
    allOptions.add(cacheCycleOption);
    allOptions.add(componentCheckOption);
    allOptions.add(inhEqCheckOption);
    allOptions.add(suppressWarningsOption);
    allOptions.add(refineLegacyOption);
    allOptions.add(licenseOption);
    allOptions.add(debugOption);
    allOptions.add(synchOption);
    allOptions.add(outputDirOption);
    allOptions.add(staticStateOption);
    allOptions.add(tracingOption);
    allOptions.add(flushOption);
    allOptions.add(packageNameOption);
    allOptions.add(versionOption);
    allOptions.add(helpOption);
    allOptions.add(printNonStandardOptionsOption);
    allOptions.add(indentOption);
    allOptions.add(minListSizeOption);
    allOptions.add(cacheOption);
    allOptions.add(incrementalOption);

    // new since 2.1.11
    allOptions.add(dotOption);

    // deprecated in 2.1.5
    allOptions.add(doxygenOption);
    allOptions.add(cacheAllOption);
    allOptions.add(noCachingOption);
    allOptions.add(cacheNoneOption);
    allOptions.add(cacheImplicitOption);
    allOptions.add(ignoreLazyOption);
    allOptions.add(fullFlushOption);

    // deprecated in 2.1.9
    allOptions.add(docOption);
    allOptions.add(java1_4Option);
    allOptions.add(noLazyMapsOption);
    allOptions.add(noVisitCheckOption);
    allOptions.add(noCacheCycleOption);
    allOptions.add(noRefineLegacyOption);
    allOptions.add(noComponentCheckOption);
    allOptions.add(noInhEqCheckOption);
    allOptions.add(noStaticOption);
    allOptions.add(deterministicOption);
    return allOptions;
  }

  /**
   * Output directory to write generated AST node types in.
   * @return The configured output directory
   */
  public File outputDir() {
    return new File(outputDirOption.value());

  }

  /**
   * @return <code>true</code> if public modifier option is enabled
   */
  public boolean getPublicModifier() {
    return !privateOption.value();
  }

  /**
   * Initialize a grammar object with the current configuration.
   * @return Root Grammar node for the generated AST
   */
  public Grammar buildRoot() {
    Grammar root = new Grammar();
    root.setConfiguration(this);

    indent = indent();
    license = license();

    // Configuration object must be set before creating root template context!
    TemplateContext tt = root.templateContext();

    // Global locking
    tt.bind("SynchBegin", synchronizedBlockBegin(tt));
    tt.bind("SynchEnd", synchronizedBlockEnd(tt));

    for (Option<?> option: allOptions()) {
      option.bind(tt);
    }

    // Bind global template variables:
    String packageName = packageName();
    if (packageName.isEmpty()) {
      tt.bind("PackageDecl", "");
    } else {
      tt.bind("PackageDecl", "package " + packageName + ";");
    }

    // Default attribute cache sets/maps
    tt.bind("DefaultMapType", typeDefaultMap());
    tt.bind("DefaultSetType", typeDefaultSet());
    tt.bind("ContributorSetType", typeDefaultContributorSet());
    tt.bind("CreateContributorSet", createContributorSet());

    // Rewrites
    tt.bind("RewriteEnabled", rewriteEnabled());
    tt.bind("RewriteLimit", rewriteLimit());
    tt.bind("HasRewriteLimit", rewriteLimit() > 0);
    tt.bind("RewriteCircularNTA", rewriteCircularNTA());

    // Flush
    tt.bind("FlushEnabled", flushEnabled());
    tt.bind("FlushAttr", flushAttr());
    tt.bind("FlushColl", flushColl());
    tt.bind("FlushRewrite", flushRewrite());

    // Incremental
    tt.bind("IncrementalEnabled", incremental());
    tt.bind("IncrementalLevelParam", incrementalLevelParam());
    tt.bind("IncrementalLevelAttr", incrementalLevelAttr());
    tt.bind("IncrementalLevelNode", incrementalLevelNode());
    tt.bind("IncrementalLevelRegion", incrementalLevelRegion());
    tt.bind("IncrementalChangeFlush", incrementalChangeFlush());
    tt.bind("IncrementalChangeMark", incrementalChangeMark());
    tt.bind("IncrementalPropFull", incrementalPropFull());
    tt.bind("IncrementalPropLimit", incrementalPropLimit());
    tt.bind("IncrementalDebug", incrementalDebug());
    tt.bind("IncrementalTrack", incrementalTrack());
    tt.bind("DDGNodeName", astNodeType() + "$DepGraphNode");

    // Tracing
    tt.bind("TracingEnabled", tracingEnabled());

    tt.bind("TraceCompute", traceCompute());
    tt.bind("TraceCache", traceCache());
    tt.bind("TraceRewrite", traceRewrite());
    tt.bind("TraceCircularNTA", traceCircularNTA());
    tt.bind("TraceCircular", traceCircular());
    tt.bind("TraceCopy", traceCopy());
    tt.bind("TraceFlush", traceFlush());

    // Cache
    tt.bind("CacheAnalyzeEnabled", cacheAnalyzeEnabled());

    // Set template variables to accommodate deprecated options
    // (the deprecated options may alter the value of the template variable)
    tt.bind("VisitCheckEnabled", visitCheckEnabled());
    tt.bind("CacheCycle", cacheCycle());
    tt.bind("StaticState", staticState());
    tt.bind("StagedRewrites", stagedRewrites());
    tt.bind("Java5", !java1_4Option.value());
    tt.bind("LazyMaps", lazyMaps());

    return root;
  }

  /**
   * @param out Where to print warning/error messages.
   * @return {@code true} if configuration has fatal errors
   */
  public boolean checkProblems(PrintStream out) {

    if (unknownOptions) {
      return true;
    }

    if (jjtreeOption.value() && grammarOption.value().isEmpty()) {
      out.println("Error: No grammar name given. A grammar name is required in JJTree-mode!");
      return true;
    }

    Collection<String> grammarFiles = new LinkedList<String>();
    for (String filename: filenames) {
      if (filename.endsWith(".ast")) {
        grammarFiles.add(filename);
      }
    }
    if (grammarFiles.isEmpty()) {
      out.println("Error: No grammar files specified.");
      return true;
    }

    for (String filename: filenames) {
      if (!(filename.endsWith(".ast")
          || filename.endsWith(".jrag")
          || filename.endsWith(".jadd")
          || filename.endsWith(".config"))) {
        out.println("Error: Unrecognized file extension: " + filename);
        return true;
      }
    }

    File outputDir = outputDir();
    if (!outputDir.exists()) {
      out.println("Error: Output directory " + outputDir.getAbsolutePath() +
        " does not exist");
      return true;
    }
    if (!outputDir.isDirectory()) {
      out.println("Error: Output directory " + outputDir.getAbsolutePath() +
        " is not a directory");
      return true;
    }
    if (!outputDir.canWrite()) {
      out.println("Error: Output directory " + outputDir.getAbsolutePath() +
        " is write protected");
      return true;
    }

    if (!checkIncrementalConfig(out)) {
      return true;
    }

    if (!checkCacheConfig(out)) {
      return true;
    }

    return false;
  }

  /**
   * Checks the cache configuration for errors.
   * @param out Error output stream
   * @return true if no errors
   */
  private boolean checkCacheConfig(PrintStream out) {
    if (cacheOption.numValues() > 1) {
      out.println("Error: only one cache option may be enabled");
      return false;
    }
    return true;
  }

  /**
   * Checks the incremental configuration for errors.
   * @param out Error output stream
   * @return true if no errors
   */
  private boolean checkIncrementalConfig(PrintStream out) {
    // check level: only one level at a time
    if (incrementalLevelAttr() && incrementalLevelNode() ||
        incrementalLevelAttr() && incrementalLevelParam() ||
        incrementalLevelNode() && incrementalLevelParam() ||
        incrementalLevelParam() && incrementalLevelRegion() ||
        incrementalLevelAttr() && incrementalLevelRegion() ||
        incrementalLevelNode() && incrementalLevelRegion()) {
      out.println("error: Conflict in incremental evaluation configuration. " +
          "Cannot combine \"param\", \"attr\", \"node\" and \"region\".");
      return false;
    }
    // check invalidate: only one strategy at a time
    if (incrementalChangeFlush() && incrementalChangeMark()) {
      out.println("error: Conflict in incremental evaluation configuration. " +
          "Cannot combine \"flush\" and \"mark\".");
      return false;
    }
    // check invalidate: currently not supporting mark strategy -- "mark"
    if (incrementalChangeMark()) {
      out.println("error: Unsupported incremental evaluation configuration: " +
          "\"mark\".");
      return false;
    }
    // check propagation: only one strategy at a time
    if (incrementalPropFull() && incrementalPropLimit()) {
      out.println("error: Conflict in incremental evaluation configuration. " +
          "Cannot combine \"full\" and \"limit\".");
      return false;
    }
    // check propagation: currently not supporting limit strategy -- "limit" - we do now
    //if (root.incrementalPropLimit) {
    //    out.println("error: Unsupported incremental evaluation configuration: " +
    //        "\"limit\".");
    //    return false;
    //}
    return true;
  }

  private static final String readFile(String name) throws java.io.IOException {
    StringBuilder buf = new StringBuilder();
    java.io.Reader reader = new java.io.BufferedReader(new java.io.FileReader(name));
    char[] cbuf = new char[1024];
    int i = 0;
    while((i = reader.read(cbuf)) != -1)
      buf.append(String.valueOf(cbuf, 0, i));
    reader.close();
    return buf.toString();
  }

  /**
   * @return jrag jadd and ast file list
   */
  public Collection<String> getFiles() {
    Collection<String> files = new ArrayList<String>();

    for (String filename: filenames) {
      if (filename.endsWith(".ast") || filename.endsWith(".jrag")
          || filename.endsWith(".jadd")) {
        files.add(filename);
      }
    }

    return files;
  }

  /**
   * @return cache file list
   */
  public Collection<String> getCacheFiles() {
    Collection<String> cacheFiles = new ArrayList<String>();

    for (String filename: filenames) {
      if (filename.endsWith(".config")) {
        cacheFiles.add(filename);
      }
    }

    return cacheFiles;
  }

  /**
   * Print help
   * @param out Output stream to print help to.
   */
  public void printHelp(PrintStream out) {
    out.println("This program reads a number of .jrag, .jadd, and .ast files");
    out.println("and creates the nodes in the abstract syntax tree");
    out.println();
    out.println("The .jrag source files may contain declarations of synthesized ");
    out.println("and inherited attributes and their corresponding equations.");
    out.println("It may also contain ordinary Java methods and fields.");
    out.println();
    out.println("Source file syntax can be found at http://jastadd.org");
    out.println();
    out.println("Options:");
    argParser().printHelp(out);
    out.println();
    out.println("Arguments:");
    out.println("Names of .ast, .jrag, .jadd and .config source files");
    out.println();
    out.println("Example: The following command reads and translates files NameAnalysis.jrag");
    out.println("and TypeAnalysis.jrag, weaves PrettyPrint.jadd into the abstract syntax tree");
    out.println("defined in the grammar Toy.ast.");
    out.println("The result is the generated classes for the nodes in the AST that are placed");
    out.println("in the package ast.");
    out.println();
    out.println("java -jar jastadd2.jar --package=ast Toy.ast NameAnalysis.jrag TypeAnalysis.jrag PrettyPrinter.jadd");
  }

  /**
   * Print non-standard options
   * @param out output stream to print help to
   */
  public void printNonStandardOptions(PrintStream out) {
    out.println("Non-standard options:");
    argParser().printNonStandardOptions(out);
  }

  /**
   * @return <code>true</code> if the version string should be printed
   */
  public boolean shouldPrintVersion() {
    return versionOption.value();
  }

  /**
   * @return <code>true</code> if the help message should be printed
   */
  public boolean shouldPrintHelp() {
    return helpOption.value();
  }

  /**
   * @return <code>true</code> if non-standard options should be printed
   */
  public boolean shouldPrintNonStandardOptions() {
    return printNonStandardOptionsOption.value();
  }

  /**
   * @return <code>true</code> if the --tracing option is enabled
   */
  public boolean tracingEnabled() {
    return !tracingOption.hasValue("none") ||
      cacheAnalyzeEnabled();
  }
  public boolean traceAll() {
    return tracingOption.hasValue("all") || tracingOption.value().isEmpty();
  }
  public boolean traceCompute() {
    return traceAll() || tracingOption.hasValue("compute");
  }
  public boolean traceCache() {
    return traceAll() || tracingOption.hasValue("cache") ||
        // cache analysis requires full caching and tracing of cache usage
        cacheAnalyzeEnabled();
  }
  public boolean traceRewrite() {
    return traceAll() || tracingOption.hasValue("rewrite");
  }
  public boolean traceCircularNTA() {
    return traceAll() || tracingOption.hasValue("circularNTA");
  }
  public boolean traceCircular() {
    return traceAll() || tracingOption.hasValue("circular");
  }
  public boolean traceCopy() {
    return traceAll() || tracingOption.hasValue("copy");
  }
  public boolean traceFlush() {
    return traceAll() || tracingOption.hasValue("flush");
  }

  /**
   * @return <code>true</code> if the --cache=analyze option is enabled
   */
  public boolean cacheAnalyzeEnabled() {
    return cacheOption.hasValue("analyze");
  }

  /**
   * @return ASTNode type name
   */
  public String astNodeType() {
    return ASTNodeOption.value();
  }

  /**
   * @return List type name
   */
  public String listType() {
    return ListOption.value();
  }

  /**
   * @return Opt type name
   */
  public String optType() {
    return OptOption.value();
  }

  public String indent() {
    String arg = indentOption.value();
    if (arg.equals("2space")) {
      // Use 2 spaces for indentation
      return "  ";
    } else if (arg.equals("4space")) {
      // Use 4 spaces for indentation
      return "    ";
    } else if (arg.equals("8space")) {
      // Use 8 spaces for indentation
      return "        ";
    } else if (arg.equals("tab")) {
      // Use tabs for indentation
      return "\t";
    }
    return "  ";
  }

  public String license() {
    String filename = licenseOption.value();
    if (!filename.isEmpty()) {
      try {
        return readFile(filename);
      } catch (IOException e) {
      }
    }
    return "";
  }

  public String synchronizedBlockBegin(TemplateContext tc) {
    return synchOption.value() ? tc.expand("SynchronizedBlockBegin") : "";
  }

  public String synchronizedBlockEnd(TemplateContext tc) {
    return synchOption.value() ? tc.expand("SynchronizedBlockEnd") : "";
  }

  public boolean visitCheckEnabled() {
    if (debugOption.value()) {
      return true;
    }
    if (visitCheckOption.isMatched()) {
      return visitCheckOption.value();
    }
    // fallback on deprecated option
    return !noVisitCheckOption.value();
  }

  public boolean cacheAll() {
    return cacheOption.hasValue("all") ||
        // cache analysis requires full caching and tracing of cache usage
        cacheAnalyzeEnabled();
  }

  public boolean cacheNone() {
    return cacheOption.hasValue("none");
  }

  public boolean cacheConfig() {
    return cacheOption.hasValue("config");
  }

  public boolean cacheImplicit() {
    return cacheOption.hasValue("implicit");
  }

  public boolean incremental() {
    return !incrementalOption.hasValue("none");
  }

  public boolean incrementalLevelParam() {
    return incrementalOption.hasValue("param");
  }
  public boolean incrementalLevelAttr() {
    return incrementalOption.hasValue("attr") ||
        // no chosen level means default -- "attr"
        (!incrementalLevelNode()
            && !incrementalLevelParam()
            && !incrementalLevelRegion());
  }
  public boolean incrementalLevelNode() {
    return incrementalOption.hasValue("node");
  }
  public boolean incrementalLevelRegion() {
    return incrementalOption.hasValue("region");
  }
  public boolean incrementalChangeFlush() {
    return incrementalOption.hasValue("flush") ||
        // no chosen strategy means default -- "flush"
        !incrementalChangeMark();
  }
  public boolean incrementalChangeMark() {
    return incrementalOption.hasValue("mark");
  }
  public boolean incrementalPropFull() {
    return incrementalOption.hasValue("full") ||
        // no chosen strategy means default -- "full"
       !incrementalPropLimit();
  }
  public boolean incrementalPropLimit() {
    return incrementalOption.hasValue("limit");
  }
  public boolean incrementalDebug() {
    return incrementalOption.hasValue("debug");
  }
  public boolean incrementalTrack() {
    return incrementalOption.hasValue("track");
  }

  public boolean lazyMaps() {
    if (lazyMapsOption.isMatched()) {
      return lazyMapsOption.value();
    }
    // fallback on deprecated option
    return !noLazyMapsOption.value();
  }

  public String packageName() {
    return packageNameOption.value();
  }

  public boolean refineLegacy() {
    if (refineLegacyOption.isMatched()) {
      return refineLegacyOption.value();
    }
    // fallback on deprecated option
    return !noRefineLegacyOption.value();
  }

  public boolean rewriteCircularNTA() {
    return rewriteOption.hasValue("cnta");
  }

  public boolean rewriteEnabled() {
    return !rewriteOption.hasValue("none");
  }

  public boolean stagedRewrites() {
    return stagedRewritesOption.value();
  }

  public String typeDefaultMap() {
    return "java.util.Map";
  }

  public String typeDefaultSet() {
    return "java.util.Set";
  }

  public String typeDefaultContributorSet() {
    return "java.util.Collection";
  }

  public boolean debugMode() {
    return debugOption.value();
  }

  public boolean jjtree() {
    return jjtreeOption.value();
  }

  public boolean flushEnabled() {
    return !flushOption.hasValue("none");
  }

  public boolean flushAttr() {
    return flushOption.hasValue("attr")
      || flushOption.value().isEmpty()
      || flushOption.hasValue("full");
  }

  public boolean flushColl() {
    return flushOption.hasValue("coll")
      || flushOption.value().isEmpty()
      || flushOption.hasValue("full");
  }

  public boolean flushRewrite() {
    return flushOption.hasValue("rewrite")
      || incremental()
      || flushOption.hasValue("full");
  }

  public String createDefaultMap() {
    return defaultMapOption.value();
  }

  public String createDefaultSet() {
    return defaultSetOption.value();
  }

  public String createContributorSet() {
    return "new java.util.LinkedList()";
  }

  public boolean inhEqCheck() {
    if (inhEqCheckOption.isMatched()) {
      return inhEqCheckOption.value();
    }
    // fallback on deprecated option
    return !noInhEqCheckOption.value();
  }

  public int rewriteLimit() {
    return debugMode() ? 100 : 0;
  }

  public boolean useBeaverSymbol() {
    return beaverOption.value();
  }

  public boolean componentCheck() {
    return componentCheckOption.value();
  }

  public boolean lineColumnNumbers() {
    return lineColumnNumbersOption.value();
  }

  public boolean cacheCycle() {
    if (cacheCycleOption.isMatched()) {
      return cacheCycleOption.value();
    }
    // fallback on deprecated option
    return !noCacheCycleOption.value();
  }

  public boolean staticState() {
    if (staticStateOption.isMatched()) {
      return staticStateOption.value();
    }
    // fallback on deprecated option
    return !noStaticOption.value();
  }

  public boolean java5() {
    return !java1_4Option.value();
  }

  /**
   * @return minimum number of list items in a non-empty List node
   */
  public int minListSize() {
      try {
        int size = Integer.parseInt(minListSizeOption.value());
        if (size < 0) {
          return 0;
        } else {
          return size;
        }
      } catch (NumberFormatException e) {
        return 4;
      }
  }

  /**
   * @return {@code true} if a Dot graph should be generated from the grammar
   */
  public boolean shouldGenerateDotGraph() {
    return dotOption.value();
  }
}
