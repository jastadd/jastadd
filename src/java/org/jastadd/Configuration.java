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

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import org.jastadd.option.ArgumentParser;
import org.jastadd.option.Option;
import org.jastadd.option.ValueOption;
import org.jastadd.tinytemplate.TemplateContext;
import org.jastadd.ast.AST.Grammar;

/**
 * Tracks JastAdd configuration options.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class Configuration {

  private final ArgumentParser argParser;

  public String astNodeType = "ASTNode";

  public String listType = "List";

  public String optType = "Opt";

  public boolean jjtree = false;

  public String parserName = "";

  public String createDefaultMap = "new java.util.HashMap(4)";

  public String createDefaultSet = "new java.util.HashSet(4)";

  public boolean lazyMaps = true;

  public boolean publicModifier = true;

  public boolean rewriteEnabled = false;

  public boolean beaverSymbol = false;

  public boolean lineColumnNumbers = false;

  public boolean visitCheckEnabled = true;

  public boolean cacheCycle = true;

  public boolean componentCheck = false;

  public boolean inhEqCheck = true;

  public boolean refineLegacy = true;

  public boolean stagedRewrites = false;

  public boolean doc = false;

  public String license = "";

  public boolean java5 = true;

  public boolean debugMode = false;

  public int cycleLimit = 0;

  public int rewriteLimit = 0;

  public String outputDir = System.getProperty("user.dir");

  public boolean block = false;

  public boolean noStatic = false;

  public boolean deterministic = false;

  public String packageName = "";

  public boolean printVersion = false;

  public boolean printHelp = false;

  public boolean printNonStandardOptions = false;

  public String indent = "  ";

  public int minListSize = 4;

  public boolean tracing = false;
  public boolean traceCompute = false;
  public boolean traceCache = false;
  public boolean traceRewrite = false;
  public boolean traceCircularNTA = false;
  public boolean traceCircular = false;
  public boolean traceCopy = false;

  public boolean cacheAll = false;
  public boolean cacheNone = false;
  public boolean cacheConfig = false;
  public boolean cacheImplicit = false;
  public boolean cacheAnalyze = false;

  public boolean incremental = false;
  public boolean incrementalLevelParam = false;
  public boolean incrementalLevelAttr = false;
  public boolean incrementalLevelNode = false;
  public boolean incrementalLevelRegion = false;
  public boolean incrementalChangeFlush = false;
  public boolean incrementalChangeMark = false;
  public boolean incrementalPropFull = false;
  public boolean incrementalPropLimit = false;
  public boolean incrementalDebug = false;
  public boolean incrementalTrack = false;

  public boolean fullFlush = false;

  /**
   * Constructor - sets up available options.
   * @param args Command-line arguments to build configuration from
   * @param err output stream to print configuration warnings to
   */
  public Configuration(String[] args, PrintStream err) {

    argParser = new ArgumentParser();
    argParser.addOption(new ValueOption(
          "ASTNode", "set the name of the ASTNode type") {
      @Override
      public void onMatch(String arg) {
        astNodeType = arg;
      }
    });
    argParser.addOption(new ValueOption(
          "List", "set the name of the List type") {
      @Override
      public void onMatch(String arg) {
        listType = arg;
      }
    });
    argParser.addOption(new ValueOption(
          "Opt", "set the name of the Opt type") {
      @Override
      public void onMatch(String arg) {
        optType = arg;
      }
    });
    argParser.addOption(new Option(
          "jjtree", "use jjtree base node, this requires --grammar to be set") {
      @Override
      public void onMatch() {
        jjtree = true;
      }
    });
    argParser.addOption(new ValueOption(
          "grammar", "the name of the grammar's parser, required when using --jjtree") {
      @Override
      public void onMatch(String arg) {
        parserName = arg;
      }
    });
    argParser.addOption(new ValueOption(
          "defaultMap", "use this expression to construct maps for attribute caches") {
      {
        isNonStandard = true;
      }
      @Override
      public void onMatch(String arg) {
        createDefaultMap = arg;
      }
    });
    argParser.addOption(new ValueOption(
          "defaultSet", "use this expression to construct sets for attribute caches") {
      {
        isNonStandard = true;
      }
      @Override
      public void onMatch(String arg) {
        createDefaultSet = arg;
      }
    });
    argParser.addOption(new Option(
          "lazyMaps", "use lazy maps") {
      {
        // TODO make deprecated
        // isDeprecated = true;
      }
      @Override
      public void onMatch() {
        lazyMaps = true;
      }
    });
    argParser.addOption(new Option(
          "noLazyMaps", "don't use lazy maps") {
      @Override
      public void onMatch() {
        lazyMaps = false;
      }
    });
    argParser.addOption(new Option(
          "private", "") {
      {
        // TODO make deprecated
        // isDeprecated = true;
        isNonStandard = true;
      }
      @Override
      public void onMatch() {
        publicModifier = false;
      }
    });
    argParser.addOption(new Option(
          "rewrite", "enable rewrites (ReRAGs)") {
      @Override
      public void onMatch() {
        rewriteEnabled = true;
      }
    });
    argParser.addOption(new Option(
          "beaver", "use beaver.Symbol base node") {
      @Override
      public void onMatch() {
        beaverSymbol = true;
      }
    });
    argParser.addOption(new Option(
          "lineColumnNumbers", "interface for storing line and column numbers") {
      @Override
      public void onMatch() {
        lineColumnNumbers = true;
      }
    });
    argParser.addOption(new Option(
          "noVisitCheck", "disable circularity check for attributes") {
      @Override
      public void onMatch() {
        visitCheckEnabled = false;
      }
    });
    argParser.addOption(new Option(
          "noCacheCycle", "disable cache cycle optimization for circular attributes") {
      @Override
      public void onMatch() {
        cacheCycle = false;
      }
    });
    argParser.addOption(new Option(
          "noComponentCheck", "enable strongly connected component optimization for circular attributes") {
      {
        // TODO make deprecated
        // isDeprecated = true;
      }
      @Override
      public void onMatch() {
        componentCheck = false;
      }
    });
    argParser.addOption(new Option(
          "componentCheck", "disable strongly connected component optimization for circular attributes") {
      @Override
      public void onMatch() {
        componentCheck = true;
      }
    });
    argParser.addOption(new Option(
          "noInhEqCheck", "disable check for inherited equations") {
      @Override
      public void onMatch() {
        inhEqCheck = false;
      }
    });
    argParser.addOption(new Option(
          "suppressWarnings", "suppress warnings when using Java 5") {
      {
        isDeprecated = true;
      }
      @Override
      public void onMatch() {
      }
    });
    argParser.addOption(new Option(
          "refineLegacy", "enable the legacy refine syntax") {
      {
        // TODO
        // isDeprecated = true;
      }
      @Override
      public void onMatch() {
        refineLegacy = true;
      }
    });
    argParser.addOption(new Option(
          "noRefineLegacy", "disable the legacy refine syntax") {
      @Override
      public void onMatch() {
        refineLegacy = false;
      }
    });
    argParser.addOption(new Option(
          "stagedRewrites", "") {
      {
        isNonStandard = true;
      }
      @Override
      public void onMatch() {
        stagedRewrites = true;
      }
    });
    argParser.addOption(new Option(
          "doc", "generate javadoc like .html pages from sources") {
      @Override
      public void onMatch() {
        doc = true;
      }
    });
    argParser.addOption(new ValueOption(
          "license", "include the given file in each generated file") {
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
      @Override
      public void onMatch(String filename) {
        if (!filename.isEmpty()) {
          try {
            license = readFile(filename);
          } catch (java.io.IOException e) {
          }
        }
      }
    });
    argParser.addOption(new Option(
          "java1.4", "generate for Java 1.4") {
      {
        // TODO
        //isDeprecated = true;
      }
      @Override
      public void onMatch() {
        java5 = false;
      }
    });
    argParser.addOption(new Option(
          "debug", "generate run-time checks for debugging") {
      @Override
      public void onMatch() {
        debugMode = true;
        cycleLimit = 100;
        rewriteLimit = 100;
        visitCheckEnabled = true;
      }
    });
    argParser.addOption(new Option(
          "synch", "") {
      {
        isNonStandard = true;
      }
      @Override
      public void onMatch() {
        block = true;
      }
    });
    argParser.addOption(new Option(
          "noStatic", "the generated state field is non-static") {
      {
        isNonStandard = true;
      }
      @Override
      public void onMatch() {
        noStatic = true;
      }
    });
    argParser.addOption(new Option(
          "deterministic", "") {
      @Override
      public void onMatch() {
        deterministic = true;
        // overrides values set by the defaultMap and defaultSet options
        createDefaultMap = "new java.util.LinkedHashMap(4)";
        createDefaultSet = "new java.util.LinkedHashSet(4)";
      }
    });
    argParser.addOption(new ValueOption(
          "o", "optional base output directory, default is current directory") {
      @Override
      public void onMatch(String dir) {
        outputDir = dir;
      }
    });
    argParser.addOption(new ValueOption(
          "tracing", "weaves in code collecting evaluation events") {
      {
        acceptsMultipleValues(true);
        addAcceptedValue("compute", "trace begin and end of attribute computation");
        addAcceptedValue("cache", "trace value cached, read cache, and cache aborted");
        addAcceptedValue("rewrite", "trace rewrite evaluation");
        addAcceptedValue("circular", "trace circular attribute evaluation");
        addAcceptedValue("circularNTA", "trace circular attribute evaluation");
        addAcceptedValue("copy", "trace node copy operations");
        additionalDescription = "all events are collected by default\n" +
          "the result is available via the API in org.jastadd.Tracer";
      }
      @Override
      public void onMatch() {
        tracing = true;
        traceCompute = true;
        traceCache = true;
        traceRewrite = true;
        traceCircularNTA = true;
        traceCircular = true;
        traceCopy = true;
      }
      @Override
      public void onMatch(String arg) {
        tracing = true;
        if (arg.equals("compute")) {
          traceCompute = true;
        } else if (arg.equals("cache")) {
          traceCache = true;
        } else if (arg.equals("rewrite")) {
          traceRewrite = true;
        } else if (arg.equals("circularNTA")) {
          traceCircularNTA = true;
        } else if (arg.equals("circular")) {
          traceCircular = true;
        } else if (arg.equals("copy")) {
          traceCopy = true;
        }
      }
    });
    argParser.addOption(new ValueOption(
          "package", "optional package name for generated classes") {
      @Override
      public void onMatch(String name) {
        packageName = name;
      }
    });
    argParser.addOption(new Option(
          "version", "print version string and halts") {
      @Override
      public void onMatch() {
        printVersion = true;
      }
    });
    argParser.addOption(new Option(
          "help", "prints a short help output and halts") {
      @Override
      public void onMatch() {
        printHelp = true;
      }
    });
    argParser.addOption(new Option(
          "X", "print list of non-standard options and halt") {
      @Override
      public void onMatch() {
        printNonStandardOptions = true;
      }
    });
    argParser.addOption(new ValueOption(
          "indent", "type of indentation to use (default=2space)") {
      {
        addAcceptedValue("2space", "two spaces");
        addAcceptedValue("4space", "four spaces");
        addAcceptedValue("8space", "eight spaces");
        addAcceptedValue("tab", "use tabs");
      }
      @Override
      public void onMatch(String arg) {
        if (arg.equals("2space")) {
          // Use 2 spaces for indentation
          indent = "  ";
        } else if (arg.equals("4space")) {
          // Use 4 spaces for indentation
          indent = "    ";
        } else if (arg.equals("8space")) {
          // Use 8 spaces for indentation
          indent = "        ";
        } else if (arg.equals("tab")) {
          // Use tabs for indentation
          indent = "\t";
        }
      }
    });
    argParser.addOption(new ValueOption(
          "minListSize", "Minimum (non-empty) list size (default=4)") {
      @Override
      public void reportWarnings(PrintStream out, String arg) {
        super.reportWarnings(out, arg);
        try {
          Integer.parseInt(arg);
        } catch (NumberFormatException e) {
          out.println("Warning: failed to parse minimum list size option!");
        }
      }
      @Override
      public void onMatch(String arg) {
        try {
          int size = Integer.parseInt(arg);
          minListSize = size;
        } catch (NumberFormatException e) {
        }
      }
    });
    argParser.addOption(new ValueOption(
          "cache", "global cache configuration overriding 'lazy'") {
      {
        acceptsMultipleValues(false);
        addAcceptedValue("all", "cache all attributes");
        addAcceptedValue("none", "disable attribute caching");
        addAcceptedValue("config", "cache attributes according to a given .config file");
        addAcceptedValue("implicit", "cache all attribute but also read a .config file that takes precedence");
        addAcceptedValue("analyze", "analyze the cache use during evaluation (when all attributes are cached)\n" +
          "the result is available via the API in org.jastadd.CacheAnalyzer");
        additionalDescription = ".config files have the following format:\n" +
          " ((cache|uncache) NodeType.AttrName((ParamType(,ParamType)*)?);)*";
      }
      @Override
      public void onMatch() {
      }
      @Override
      public void onMatch(String arg) {
        // Cache flag
        if (arg.equals("all")) {
          cacheAll = true;
        } else if (arg.equals("none")) {
          cacheNone = true;
        } else if (arg.equals("config")) {
          cacheConfig = true;
        } else if (arg.equals("implicit")) {
          cacheImplicit = true;
        } else if (arg.equals("analyze")) {
          cacheAnalyze = true;
          // analysis requires full caching and tracing of cache usage
          cacheAll = true;
          tracing = true;
          traceCache = true;
        }

      }
    });
    argParser.addOption(new Option(
          "cacheAll", "Replaced by --cache=all") {
      {
        isDeprecated = true;
      }
      @Override
      public void onMatch() {
      }
    });
    argParser.addOption(new Option(
          "noCaching", "Replaced by --cache=none") {
      {
        isDeprecated = true;
      }
      @Override
      public void onMatch() {
      }
    });
    argParser.addOption(new Option(
          "cacheNone", "Replaced by --cache=none") {
      {
        isDeprecated = true;
      }
      @Override
      public void onMatch() {
      }
    });
    argParser.addOption(new Option(
          "cacheImplicit", "Replaced by --cache=implicit") {
      {
        isDeprecated = true;
      }
      @Override
      public void onMatch() {
      }
    });
    argParser.addOption(new Option(
          "ignoreLazy", "ignores the \"lazy\" keyword") {
      {
        isDeprecated = true;
      }
      {
        isDeprecated = true;
      }
      @Override
      public void onMatch() {
      }
    });
    argParser.addOption(new ValueOption(
          "incremental", "turns on incremental evaluation with the given configuration") {
      {
        acceptsMultipleValues(true);
        addAcceptedValue("param", "dependency tracking on parameter level");
        addAcceptedValue("region", "dependency tracking on region level");
        addAcceptedValue("flush", "invalidate with flush (default)");// Default on: Any way to disable??
        addAcceptedValue("full", "full change propagation (default)");// Default on: Any way to disable??
        addAcceptedValue("debug", "generate code for debugging and dumping of dependencies");
      }
      @Override
      public void onMatch(String arg) {
        incremental = true;
        if (arg.equals("param")) {
          incrementalLevelParam = true;
        } else if (arg.equals("attr")) {
          incrementalLevelAttr = true;
        } else if (arg.equals("node")) {
          incrementalLevelNode = true;
        } else if (arg.equals("region")) {
          incrementalLevelRegion = true;
        } else if (arg.equals("flush")) {
          incrementalChangeFlush = true;
        } else if (arg.equals("mark")) {
          incrementalChangeMark = true;
        } else if (arg.equals("full")) {
          incrementalPropFull = true;
        } else if (arg.equals("limit")) {
          incrementalPropLimit = true;
        } else if (arg.equals("debug")) {
          incrementalDebug = true;
        } else if (arg.equals("track")) {
          incrementalTrack = true;
        }
      }
    });
    argParser.addOption(new Option(
          "fullFlush", "support for full flushing of attribute caches and rewrites") {
      @Override
      public void onMatch() {
        fullFlush = true;
      }
    });

    // parse the argument list
    argParser.parseArgs(args, err);
  }

  /**
   * Output directory to write generated AST node types in.
   * @return The configured output directory
   */
  private File getOutputDir() {
    return new File(outputDir);

  }

  /**
   * @return <code>true</code> if public modifier option is enabled
   */
  public boolean getPublicModifier() {
    return publicModifier;
  }

  /**
   * Initialize a grammar object with the current configuration.
   * @return Root Grammar node for the generated AST
   */
  public Grammar buildRoot() {
    Grammar root = new Grammar();

    root.outputDir = outputDir;
    root.packageName = packageName;

    root.astNodeType = astNodeType;
    root.listType = listType;
    root.optType = optType;

    root.blockBegin = "synchronized(" + root.astNodeType + ".class) {\n";
    root.blockEnd =   "}\n";
    root.createContributorSet = "new " + root.astNodeType + "$State.IdentityHashSet(4)";

    root.jjtree = jjtree;
    root.parserName = parserName;

    root.createDefaultMap = createDefaultMap;
    root.createDefaultSet = createDefaultSet;

    root.ind = indent;

    root.minListSize = minListSize;

    root.lazyMaps = lazyMaps;

    root.rewriteEnabled = rewriteEnabled;
    root.beaver = beaverSymbol;
    root.lineColumnNumbers = lineColumnNumbers;
    root.visitCheckEnabled = visitCheckEnabled;
    root.cacheCycle = cacheCycle;
    root.componentCheck = componentCheck;
    root.noInhEqCheck = !inhEqCheck;

    root.refineLegacy = refineLegacy;

    root.stagedRewrites = stagedRewrites;

    root.doc = doc;

    root.license = license;

    root.java5 = java5;

    root.debugMode = debugMode;
    root.cycleLimit = cycleLimit;
    root.rewriteLimit = rewriteLimit;
    root.visitCheckEnabled = visitCheckEnabled;

    root.block = block;

    root.noStatic = noStatic;

    root.deterministic = deterministic;

    root.tracing = tracing;
    root.traceCompute = traceCompute;
    root.traceCache = traceCache;
    root.traceRewrite = traceRewrite;
    root.traceCircularNTA = traceCircularNTA;
    root.traceCircular = traceCircular;
    root.traceCopy = traceCopy;

    // TODO: Deprecated, remove when phased out
    root.cacheAll = cacheAll;

    // TODO: Deprecated, remove when phased out
    root.cacheNone = cacheNone;

    // TODO: Deprecated, remove when phased out
    root.cacheImplicit = cacheImplicit;

    root.cacheConfig = cacheConfig;
    root.cacheAnalyze = cacheAnalyze;

    // Incremental flag
    root.incremental = incremental;
    root.incrementalLevelParam = incrementalLevelParam;
    root.incrementalLevelAttr = incrementalLevelAttr;
    root.incrementalLevelNode = incrementalLevelNode;
    root.incrementalLevelRegion = incrementalLevelRegion;
    root.incrementalChangeFlush = incrementalChangeFlush;
    root.incrementalChangeMark = incrementalChangeMark;
    root.incrementalPropFull = incrementalPropFull;
    root.incrementalPropLimit = incrementalPropLimit;
    root.incrementalDebug = incrementalDebug;
    root.incrementalTrack = incrementalTrack;

    root.fullFlush = fullFlush;

    // The first time we access templateContext the Grammar.ind option must
    // be set already!
    TemplateContext tt = root.templateContext();
    if (block) {
      tt.bindExpansion("SynchBegin", "SynchronizedBlockBegin");
      tt.bindExpansion("SynchEnd", "SynchronizedBlockEnd");
    } else {
      tt.bind("SynchBegin", "");
      tt.bind("SynchEnd", "");
    }

    // Bind global template variables:
    if (root.packageName.isEmpty()) {
      tt.bind("PackageDecl", "");
    } else {
      tt.bind("PackageDecl", "package " + root.packageName + ";");
    }
    tt.bind("ASTNode", root.astNodeType);
    tt.bind("List", root.listType);
    tt.bind("Opt", root.optType);
    tt.bind("NoStatic", root.noStatic);
    tt.bind("DebugMode", root.debugMode);
    tt.bind("MinListSize", "" + root.minListSize);
    tt.bind("Deterministic", root.deterministic);
    tt.bind("LazyMaps", root.lazyMaps);
    tt.bind("CircularEnabled", root.circularEnabled);
    tt.bind("ComponentCheck", root.componentCheck);
    tt.bind("CacheCycle", root.cacheCycle);
    tt.bind("Java5", root.java5);
    tt.bind("Beaver", root.beaver);
    tt.bind("VisitCheckEnabled", root.visitCheckEnabled);
    tt.bind("TraceVisitCheck", root.traceVisitCheck);
    tt.bind("RewriteLimit", "" + root.rewriteLimit);
    tt.bind("HasRewriteLimit", root.rewriteLimit > 0);
    tt.bind("StagedRewrites", root.stagedRewrites);
    tt.bind("RewriteEnabled", root.rewriteEnabled);
    tt.bind("CreateDefaultMap", root.createDefaultMap);
    tt.bind("DefaultMapType", root.typeDefaultMap);
    tt.bind("CreateDefaultSet", root.createDefaultSet);
    tt.bind("DefaultSetType", root.typeDefaultSet);
    tt.bind("CreateContributorSet", root.createContributorSet);

    // JJTree
    tt.bind("JJTree", jjtree);
    tt.bind("ParserName", parserName);

    // Flush
    tt.bind("FullFlush", root.fullFlush);

    // Incremental
    tt.bind("IncrementalEnabled", root.incremental);
    tt.bind("IncrementalLevelParam", root.incrementalLevelParam);
    tt.bind("IncrementalLevelAttr", root.incrementalLevelAttr);
    tt.bind("IncrementalLevelNode", root.incrementalLevelNode);
    tt.bind("IncrementalLevelRegion", root.incrementalLevelRegion);
    tt.bind("IncrementalChangeFlush", root.incrementalChangeFlush);
    tt.bind("IncrementalChangeMark", root.incrementalChangeMark);
    tt.bind("IncrementalPropFull", root.incrementalPropFull);
    tt.bind("IncrementalPropLimit", root.incrementalPropLimit);
    tt.bind("IncrementalDebug", root.incrementalDebug);
    tt.bind("IncrementalTrack", root.incrementalTrack);
    tt.bind("DDGNodeName", root.astNodeType + "$DepGraphNode");

    // Tracing
    tt.bind("TracingEnabled", root.tracing);
    tt.bind("TraceCompute", root.traceCompute);
    tt.bind("TraceCache", root.traceCache);
    tt.bind("TraceRewrite", root.traceRewrite);
    tt.bind("TraceCircularNTA", root.traceCircularNTA);
    tt.bind("TraceCircular", root.traceCircular);
    tt.bind("TraceCopy", root.traceCopy);

    // Cache
    tt.bind("CacheAnalyzeEnabled", root.cacheAnalyze);

    return root;
  }

  /**
   * @param out Where to print warning/error messages.
   * @return {@code true} if configuration has fatal errors
   */
  public boolean checkProblems(PrintStream out) {

    if (jjtree && parserName.isEmpty()) {
      out.println("Error: No grammar name given. A grammar name is required in JJTree-mode!");
      return true;
    }

    for (String filename: argParser.getFilenames()) {
      if (!(filename.endsWith(".ast")
          || filename.endsWith(".jrag")
          || filename.endsWith(".jadd")
          || filename.endsWith(".config"))) {
        out.println("Error: Unrecognized file extension: " + filename);
        return true;
      }
    }

    File outputDir = getOutputDir();
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
    int num = (cacheAll?1:0) + (cacheNone?1:0) + (cacheImplicit?1:0);
    if (num > 1) {
      out.println("error: Conflict in cache configuration. " +
        "Only one of --cacheAll, --cacheNone, and --cacheImplicit can be enabled.");
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
    if (incrementalLevelAttr && incrementalLevelNode ||
        incrementalLevelAttr && incrementalLevelParam ||
        incrementalLevelNode && incrementalLevelParam ||
        incrementalLevelParam && incrementalLevelRegion ||
        incrementalLevelAttr && incrementalLevelRegion ||
        incrementalLevelNode && incrementalLevelRegion) {
      out.println("error: Conflict in incremental evaluation configuration. " +
          "Cannot combine \"param\", \"attr\", \"node\" and \"region\".");
      return false;
    }
    // check level: no chosen level means default -- "attr"
    if (!incrementalLevelAttr && !incrementalLevelNode &&
        !incrementalLevelParam && !incrementalLevelRegion) {
      incrementalLevelAttr = true;
    }
    // check invalidate: only one strategy at a time
    if (incrementalChangeFlush && incrementalChangeMark) {
      out.println("error: Conflict in incremental evaluation configuration. " +
          "Cannot combine \"flush\" and \"mark\".");
      return false;
    }
    // check invalidate: no chosen strategy means default -- "flush"
    if (!incrementalChangeFlush && !incrementalChangeMark) {
      incrementalChangeFlush = true;
    }
    // check invalidate: currently not supporting mark strategy -- "mark"
    if (incrementalChangeMark) {
      out.println("error: Unsupported incremental evaluation configuration: " +
          "\"mark\".");
      return false;
    }
    // check propagation: only one strategy at a time
    if (incrementalPropFull && incrementalPropLimit) {
      out.println("error: Conflict in incremental evaluation configuration. " +
          "Cannot combine \"full\" and \"limit\".");
      return false;
    }
    // check propagation: no chosen strategy means default -- "full"
    if (!incrementalPropFull && !incrementalPropLimit) {
      incrementalPropFull = true;
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

    for (String filename: argParser.getFilenames()) {
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

    for (String filename: argParser.getFilenames()) {
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
    argParser.printHelp(out);
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
    argParser.printNonStandardOptions(out);
  }

  /**
   * @return <code>true</code> if the version string should be printed
   */
  public boolean shouldPrintVersion() {
    return printVersion;
  }

  /**
   * @return <code>true</code> if the help message should be printed
   */
  public boolean shouldPrintHelp() {
    return printHelp || getFiles().isEmpty();
  }

  /**
   * @return <code>true</code> if non-standard options should be printed
   */
  public boolean shouldPrintNonStandardOptions() {
    return printNonStandardOptions;
  }

  /**
   * @return <code>true</code> if the --tracing option is enabled
   */
  public boolean tracingEnabled() {
    return tracing;
  }

  /**
   * @return <code>true</code> if the --cache=analyze option is enabled
   */
  public boolean cacheAnalyzeEnabled() {
    return cacheAnalyze;
  }

}
