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
import java.util.LinkedList;

import org.jastadd.option.ArgumentParser;
import org.jastadd.option.BooleanOption;
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

  /**
   * The name of the ASTNode type.
   */
  public String astNodeType = "ASTNode";

  /**
   * The name of the List type.
   */
  public String listType = "List";

  /**
   * The name of the Opt type.
   */
  public String optType = "Opt";

  /**
   * JJTree mode flag.
   */
  public boolean jjtree = false;

  /**
   * Parser/grammar name. Used in JJTree mode.
   */
  public String parserName = "";

  /**
   * Code to create default maps.
   */
  public String createDefaultMap = "new java.util.HashMap(4)";

  /**
   * Code to create default sets.
   */
  public String createDefaultSet = "new java.util.HashSet(4)";

  /**
   * Default map type.
   */
  public String typeDefaultMap = "java.util.Map";

  /**
   * Default set type.
   */
  public String typeDefaultSet = "java.util.Set";

  /**
   * Lazy maps flag.
   */
  public boolean lazyMaps = true;

  /**
   * Public modifier flag.
   * TODO make deprecated!
   */
  public boolean publicModifier = true;

  /**
   * Rewrite flag.
   */
  public boolean rewriteEnabled = false;
  
  /**
   * Evaluate rewrites with circular NTAS
   * --rewrite=circularNTA
   */
  public boolean rewriteCircularNTA = false;

  /**
   * Beaver symbol flag.
   * TODO make deprecated!
   */
  public boolean useBeaverSymbol = false;

  /**
   * Generate getter/setters for line and column numbers.
   */
  public boolean lineColumnNumbers = false;

  /**
   * Visit check flag.
   */
  public boolean visitCheckEnabled = true;

  /**
   * Cache cycle check flag.
   */
  public boolean cacheCycle = true;

  /**
   * Component check flag.
   */
  public boolean componentCheck = false;

  /**
   * Inh eq check flag.
   */
  public boolean inhEqCheck = true;

  /**
   * Lega refine syntax flag.
   * TODO make deprecated!
   */
  public boolean refineLegacy = true;

  /**
   * Staged rewrites flag.
   */
  public boolean stagedRewrites = false;

  /**
   * Documentation flag.
   * TODO review this option!
   */
  public boolean doc = false;

  /**
   * License header.
   */
  public String license = "";

  /**
   * Java 5 flag.
   * TODO make deprecated!
   */
  public boolean java5 = true;

  /**
   * Debug mode flag.
   */
  public boolean debugMode = false;

  /**
   * Cycle limit.
   */
  public int cycleLimit = 0;

  /**
   * Rewrite limit.
   */
  public int rewriteLimit = 0;

  /**
   * The relative path to the base output directory for generated code.
   */
  public String outputDir = System.getProperty("user.dir");

  /**
   * Use syncronized blocks flag.
   */
  public boolean block = false;

  /**
   * Synchronized block begin template.
   * TODO make non-null!
   */
  public String blockBegin = null;

  /**
   * Synchronized block end template.
   * TODO make non-null!
   */
  public String blockEnd = null;

  /**
   * Code to create contributor set.
   * TODO make non-null!
   */
  public String createContributorSet = null;

  /**
   * No-static flag.
   * TODO review this option!
   */
  public boolean noStatic = false;

  /**
   * The package name for the generated AST classes.
   */
  public String packageName = "";

  /**
   * If version name should be printed.
   */
  public boolean printVersion = false;

  /**
   * If help should be printed.
   */
  public boolean printHelp = false;

  /**
   * If non-standard option help should be printed.
   */
  public boolean printNonStandardOptions = false;

  /**
   * One level of indentation.
   */
  public String indent = "  ";

  /**
   * Indentation level cache.
   */
  protected java.util.List<String> indList = new ArrayList<String>(32);

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

  /**
   * The minimum list size (above zero)
   */
  public int minListSize = 4;

  /**
   * Tracing flag.
   */
  public boolean tracing = false;

  /**
   * trace=compute
   */
  public boolean traceCompute = false;

  /**
   * trace=cache
   */
  public boolean traceCache = false;

  /**
   * trace=rewrite
   */
  public boolean traceRewrite = false;

  /**
   * trace=circularNTA
   */
  public boolean traceCircularNTA = false;

  /**
   * trace=circular
   */
  public boolean traceCircular = false;

  /**
   * trace=copy
   */
  public boolean traceCopy = false;

  /**
   * trace=flush
   */
  public boolean traceFlush = false;

  /**
   * cache=all
   */
  public boolean cacheAll = false;

  /**
   * cache=none
   */
  public boolean cacheNone = false;

  /**
   * cache=config
   */
  public boolean cacheConfig = false;

  /**
   * cache=implicit
   */
  public boolean cacheImplicit = false;

  /**
   * cache=analyze
   */
  public boolean cacheAnalyze = false;

  /**
   * Incremental flag.
   */
  public boolean incremental = false;

  /**
   * incremental...
   */
  public boolean incrementalLevelParam = false;

  /**
   * incremental...
   */
  public boolean incrementalLevelAttr = false;

  /**
   * incremental...
   */
  public boolean incrementalLevelNode = false;

  /**
   * incremental...
   */
  public boolean incrementalLevelRegion = false;

  /**
   * incremental...
   */
  public boolean incrementalChangeFlush = false;

  /**
   * incremental...
   */
  public boolean incrementalChangeMark = false;

  /**
   * incremental...
   */
  public boolean incrementalPropFull = false;

  /**
   * incremental...
   */
  public boolean incrementalPropLimit = false;

  /**
   * incremental...
   */
  public boolean incrementalDebug = false;

  /**
   * incremental...
   */
  public boolean incrementalTrack = false;

  /**
   * Use of --flush 
   */
  // TODO: Make the default behavior false by deprecating default generation of flush methods
  public boolean flushEnabled = true;
  
  /**
   * --flush=attr
   */
  public boolean flushAttr = true;
  
  /**
   * --flush=rewrite
   */
  public boolean flushRewrite = false;
  
  /**
   * --flush=coll
   */
  public boolean flushColl = true;
  
  /**
   * TODO unused?
   */
  public boolean traceVisitCheck = false;

  /**
   * TODO unused?
   */
  public boolean circularEnabled = true;

  ValueOption ASTNodeOption = new ValueOption(
      "ASTNode", "set the name of the ASTNode type") {
    @Override
    public void onMatch(String arg) {
      astNodeType = arg;
    }
  };

  ValueOption ListOption = new ValueOption(
      "List", "set the name of the List type") {
    @Override
    public void onMatch(String arg) {
      listType = arg;
    }
  };

  ValueOption OptOption = new ValueOption(
      "Opt", "set the name of the Opt type") {
    @Override
    public void onMatch(String arg) {
      optType = arg;
    }
  };

  Option jjtreeOption = new Option(
      "jjtree", "use jjtree base node, this requires --grammar to be set") {
    @Override
    public void onMatch() {
      jjtree = true;
    }
  };

  ValueOption grammarOption = new ValueOption(
      "grammar", "the name of the grammar's parser, required when using --jjtree") {
    @Override
    public void onMatch(String arg) {
      parserName = arg;
    }
  };

  ValueOption defaultMapOption = new ValueOption(
      "defaultMap", "use this expression to construct maps for attribute caches") {
    {
      isNonStandard = true;
    }

    @Override
    public void onMatch(String arg) {
      createDefaultMap = arg;
    }
  };

  ValueOption defaultSetOption = new ValueOption(
      "defaultSet", "use this expression to construct sets for attribute caches") {
    {
      isNonStandard = true;
    }

    @Override
    public void onMatch(String arg) {
      createDefaultSet = arg;
    }
  };

  BooleanOption lazyMapsOption = new BooleanOption(
      "lazyMaps", "use lazy maps (default=on)") {
    @Override
    public void onMatch(boolean value) {
      lazyMaps = value;
    }
  };

  Option noLazyMapsOption = new Option(
      "noLazyMaps", "don't use lazy maps") {
    {
      // TODO make deprecated - replace with lazyMaps={true|false}
      // isDeprecated = true;
    }

    @Override
    public void onMatch() {
      lazyMaps = false;
    }
  };

  Option privateOption = new Option(
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
  };

  Option rewriteOption = new ValueOption(
      "rewrite", "enable rewrites (ReRAGs)") {
    {
      acceptsMultipleValues = false;
      needsValue = false;
      addAcceptedValue("cnta",
        "evaluate rewrites with circular NTAs");
    }
    @Override
    public void onMatch() {
      rewriteEnabled = true;
    }
    @Override
    public void onMatch(String arg) {
      rewriteEnabled = true;
      if (arg.equals("cnta")) {
        rewriteCircularNTA = true;
      }
    }
  };

  Option beaverOption = new Option(
      "beaver", "use beaver.Symbol base node") {
    @Override
    public void onMatch() {
      useBeaverSymbol = true;
    }
  };

  Option lineColumnNumbersOption = new Option(
      "lineColumnNumbers", "interface for storing line and column numbers") {
    @Override
    public void onMatch() {
      lineColumnNumbers = true;
    }
  };

  Option noVisitCheckOption = new Option(
      "noVisitCheck", "disable circularity check for attributes") {
    {
      // TODO make deprecated - replace with visitCheck={true|false}
      // isDeprecated = true;
    }

    @Override
    public void onMatch() {
      visitCheckEnabled = false;
    }
  };

  // TODO add boolean value
  Option visitCheckOption = new Option(
      "visitCheck", "enable circularity check for attributes") {
    @Override
    public void onMatch() {
      visitCheckEnabled = true;
    }
  };

  Option noCacheCycleOption = new Option(
      "noCacheCycle", "disable cache cycle optimization for circular attributes") {
    {
      // TODO make deprecated - replace with cacheCycle={true|false}
      // isDeprecated = true;
    }

    @Override
    public void onMatch() {
      cacheCycle = false;
    }
  };

  // TODO add boolean value
  Option cacheCycleOption = new Option(
      "cacheCycle", "enable cache cycle optimization for circular attributes") {
    @Override
    public void onMatch() {
      cacheCycle = true;
    }
  };

  Option noComponentCheckOption = new Option(
      "noComponentCheck", "enable strongly connected component optimization for circular attributes") {
    {
      // TODO make deprecated - replace with componentCheck={true|false}
      // isDeprecated = true;
    }

    @Override
    public void onMatch() {
      componentCheck = false;
    }
  };

  // TODO add boolean value
  Option componentCheckOption = new Option(
      "componentCheck", "disable strongly connected component optimization for circular attributes") {
    @Override
    public void onMatch() {
      componentCheck = true;
    }
  };

  Option noInhEqCheckOption = new Option(
      "noInhEqCheck", "disable check for inherited equations") {
    {
      // TODO make deprecated - replace with inhEqCheck={true|false}
      // isDeprecated = true;
    }

    @Override
    public void onMatch() {
      inhEqCheck = false;
    }
  };

  Option suppressWarningsOption = new Option(
      "suppressWarnings", "suppress warnings when using Java 5") {
    {
      isDeprecated = true;
    }

    @Override
    public void onMatch() {
    }
  };

  Option refineLegacyOption = new Option(
      "refineLegacy", "enable the legacy refine syntax") {
    {
      // TODO
      // isDeprecated = true;
    }

    @Override
    public void onMatch() {
      refineLegacy = true;
    }
  };

  Option noRefineLegacyOption = new Option(
      "noRefineLegacy", "disable the legacy refine syntax") {
    {
      // TODO make deprecated - replace with refineLegacy={true|false}
      // isDeprecated = true;
    }

    @Override
    public void onMatch() {
      refineLegacy = false;
    }
  };

  Option stagedRewritesOption = new Option(
      "stagedRewrites", "") {
    {
      isNonStandard = true;
    }

    @Override
    public void onMatch() {
      stagedRewrites = true;
    }
  };

  Option docOption = new Option(
      "doc", "generate javadoc like .html pages from sources") {
    {
      // TODO make deprecated
      // isDeprecated = true;
    }

    @Override
    public void onMatch() {
      doc = true;
    }
  };

  Option doxygenOption = new Option(
      "doxygen", "") {
    {
      isDeprecated = true;
    }

    @Override
    public void onMatch() {
    }
  };

  ValueOption licenseOption = new ValueOption(
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
  };

  Option java1_4Option = new Option(
      "java1.4", "generate for Java 1.4") {
    {
      // TODO make deprecated
      // isDeprecated = true;
    }

    @Override
    public void onMatch() {
      java5 = false;
    }
  };

  Option debugOption = new Option(
      "debug", "generate run-time checks for debugging") {
    @Override
    public void onMatch() {
      debugMode = true;
      cycleLimit = 100;
      rewriteLimit = 100;
      visitCheckEnabled = true;
    }
  };

  Option synchOption = new Option(
      "synch", "") {
    {
      isNonStandard = true;
    }

    @Override
    public void onMatch() {
      block = true;
    }
  };

  Option noStaticOption = new Option(
      "noStatic", "the generated state field is non-static") {
    {
      // TODO make deprecated - replace with staticStateField={true|false}
      // isDeprecated = true;
      isNonStandard = true;
    }

    @Override
    public void onMatch() {
      noStatic = true;
    }
  };

  ValueOption oOption = new ValueOption(
      "o", "optional base output directory, default is current directory") {
    @Override
    public void onMatch(String dir) {
      outputDir = dir;
    }
  };

  ValueOption tracingOption = new ValueOption(
      "tracing", "weaves in code collecting evaluation events") {
    {
      acceptsMultipleValues = true;
      needsValue = false;
      addAcceptedValue("compute",
          "trace begin and end of attribute computation");
      addAcceptedValue("cache",
          "trace value cached, read cache, and cache aborted");
      addAcceptedValue("rewrite", "trace rewrite evaluation");
      addAcceptedValue("circular", "trace circular attribute evaluation");
      addAcceptedValue("circularNTA", "trace circular attribute evaluation");
      addAcceptedValue("copy", "trace node copy operations");
      addAcceptedValue("flush", "trace flush operations");
      additionalDescription = "all events are collected by default\n"
          + "the result is available via the API in org.jastadd.Tracer";
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
      traceFlush = true;
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
      } else if (arg.equals("flush")) {
        traceFlush = true;
      }
    }
  };
  
  ValueOption flushOption = new ValueOption(
      "flush", "adds flushing of cached values") {
    {
      acceptsMultipleValues = true;
      needsValue = false;
      addAcceptedValue("full", "flushing of all computed values (combines attr, coll, and rewrite)");
      addAcceptedValue("attr", "adds flushing of attributes (syn,inh)");
      addAcceptedValue("coll", "adds flushing of collection attributes");
      addAcceptedValue("rewrite", "adds flushing of rewrites");
      additionalDescription = "default is 'attr' and 'coll'";
    }

    @Override
    public void onMatch() {
      flushEnabled = true;
      flushAttr = true;
      flushColl = true;
      flushRewrite = false;
    }

    @Override
    public void onMatch(String arg) {
      flushEnabled = true;
      if (arg.equals("full")) {
        flushAttr = true;
        flushColl = true;
        flushRewrite = true;
      } else if (arg.equals("attr")) {
        flushAttr = true;
      } else if (arg.equals("coll")) {
        flushColl = true;
      } else if (arg.equals("rewrite")) {
        flushRewrite = true;
      } 
    }
  };
  
  ValueOption packageOption = new ValueOption(
      "package", "optional package name for generated classes") {
    @Override
    public void onMatch(String name) {
      packageName = name;
    }
  };

  Option versionOption = new Option(
      "version", "print version string and halts") {
    @Override
    public void onMatch() {
      printVersion = true;
    }
  };

  Option helpOption = new Option(
      "help", "prints a short help output and halts") {
    @Override
    public void onMatch() {
      printHelp = true;
    }
  };

  Option XOption = new Option(
      "X", "print list of non-standard options and halt") {
    @Override
    public void onMatch() {
      printNonStandardOptions = true;
    }
  };

  ValueOption indentOption = new ValueOption(
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
  };

  ValueOption minListSizeOption = new ValueOption(
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
  };

  ValueOption cacheOption = new ValueOption(
      "cache", "global cache configuration overriding 'lazy'") {
    {
      acceptsMultipleValues = false;
      addAcceptedValue("all", "cache all attributes");
      addAcceptedValue("none", "disable attribute caching");
      addAcceptedValue("config",
          "cache attributes according to a given .config file");
      addAcceptedValue("implicit",
          "cache all attribute but also read a .config file that takes precedence");
      addAcceptedValue(
          "analyze",
          "analyze the cache use during evaluation (when all attributes are cached)\n"
              + "the result is available via the API in org.jastadd.CacheAnalyzer");
      additionalDescription = ".config files have the following format:\n"
          + " ((cache|uncache) NodeType.AttrName((ParamType(,ParamType)*)?);)*";
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
  };

  Option cacheAllOption = new Option(
      "cacheAll", "Replaced by --cache=all") {
    {
      isDeprecated = true;
    }

    @Override
    public void onMatch() {
    }
  };

  Option noCachingOption = new Option(
      "noCaching", "Replaced by --cache=none") {
    {
      isDeprecated = true;
    }

    @Override
    public void onMatch() {
    }
  };

  Option cacheNoneOption = new Option(
      "cacheNone", "Replaced by --cache=none") {
    {
      isDeprecated = true;
    }

    @Override
    public void onMatch() {
    }
  };

  Option cacheImplicitOption = new Option(
      "cacheImplicit", "Replaced by --cache=implicit") {
    {
      isDeprecated = true;
    }

    @Override
    public void onMatch() {
    }
  };

  Option ignoreLazyOption = new Option(
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
  };

  ValueOption incrementalOption = new ValueOption(
      "incremental", "turns on incremental evaluation with the given configuration") {
    {
      acceptsMultipleValues = true;
      addAcceptedValue("param", "dependency tracking on parameter level");
      addAcceptedValue("region", "dependency tracking on region level");
      addAcceptedValue("flush", "invalidate with flush (default)");// Default on? Any way to disable??
      addAcceptedValue("full", "full change propagation (default)");// Default on? Any way to disable??
      addAcceptedValue("debug",
          "generate code for debugging and dumping of dependencies");
    }

    @Override
    public void onMatch(String arg) {
      incremental = true;
      flushRewrite = true; // assuming that flushing is enabled by default with 'attr' and 'coll'
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
  };

  Option fullFlushOption = new Option(
      "fullFlush", "Replaced by --flush=full") {
    {
      isDeprecated = true;
    }

    @Override
    public void onMatch() {
      flushEnabled = true;
      flushAttr = true;
      flushColl = true;
      flushRewrite = true;
    }
  };

  Collection<String> filenames = new LinkedList<String>();

  /**
   * Constructor to parse options from argument list.
   * @param args Command-line arguments to build configuration from
   * @param err output stream to print configuration warnings to
   */
  public Configuration(String[] args, PrintStream err) {
    this();

    // parse the argument list
    argParser.parseArgs(args, err);
    filenames  = argParser.getFilenames();
  }

  /**
   * Constructor - sets up available options.
   */
  public Configuration() {
    argParser = new ArgumentParser();
    argParser.addOption(ASTNodeOption);
    argParser.addOption(ListOption);
    argParser.addOption(OptOption);
    argParser.addOption(jjtreeOption);
    argParser.addOption(grammarOption);
    argParser.addOption(defaultMapOption);
    argParser.addOption(defaultSetOption);
    argParser.addOption(lazyMapsOption);
    argParser.addOption(noLazyMapsOption);
    argParser.addOption(privateOption);
    argParser.addOption(rewriteOption);
    argParser.addOption(beaverOption);
    argParser.addOption(lineColumnNumbersOption);
    argParser.addOption(noVisitCheckOption);
    argParser.addOption(visitCheckOption);
    argParser.addOption(noCacheCycleOption);
    argParser.addOption(cacheCycleOption);
    argParser.addOption(noComponentCheckOption);
    argParser.addOption(componentCheckOption);
    argParser.addOption(noInhEqCheckOption);
    argParser.addOption(suppressWarningsOption);
    argParser.addOption(refineLegacyOption);
    argParser.addOption(noRefineLegacyOption);
    argParser.addOption(stagedRewritesOption);
    argParser.addOption(docOption);
    argParser.addOption(doxygenOption);
    argParser.addOption(licenseOption);
    argParser.addOption(java1_4Option);
    argParser.addOption(debugOption);
    argParser.addOption(synchOption);
    argParser.addOption(noStaticOption);
    argParser.addOption(oOption);
    argParser.addOption(tracingOption);
    argParser.addOption(flushOption);
    argParser.addOption(packageOption);
    argParser.addOption(versionOption);
    argParser.addOption(helpOption);
    argParser.addOption(XOption);
    argParser.addOption(indentOption);
    argParser.addOption(minListSizeOption);
    argParser.addOption(cacheOption);
    argParser.addOption(cacheAllOption);
    argParser.addOption(noCachingOption);
    argParser.addOption(cacheNoneOption);
    argParser.addOption(cacheImplicitOption);
    argParser.addOption(ignoreLazyOption);
    argParser.addOption(incrementalOption);
    argParser.addOption(fullFlushOption);
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
    root.setConfiguration(this);

    // TODO handle this in a nicer way!
    blockBegin = "synchronized(" + astNodeType + ".class) {\n";
    blockEnd =   "}\n";
    createContributorSet = "new " + astNodeType + "$State.IdentityHashSet(4)";

    // Configuration object must be set before creating root template context!
    TemplateContext tt = root.templateContext();
    if (block) {
      tt.bindExpansion("SynchBegin", "SynchronizedBlockBegin");
      tt.bindExpansion("SynchEnd", "SynchronizedBlockEnd");
    } else {
      tt.bind("SynchBegin", "");
      tt.bind("SynchEnd", "");
    }

    // Bind global template variables:
    if (packageName.isEmpty()) {
      tt.bind("PackageDecl", "");
    } else {
      tt.bind("PackageDecl", "package " + packageName + ";");
    }
    tt.bind("ASTNode", astNodeType);
    tt.bind("List", listType);
    tt.bind("Opt", optType);
    tt.bind("NoStatic", noStatic);
    tt.bind("DebugMode", debugMode);
    tt.bind("MinListSize", "" + minListSize);
    tt.bind("LazyMaps", lazyMaps);
    tt.bind("CircularEnabled", circularEnabled);
    tt.bind("ComponentCheck", componentCheck);
    tt.bind("CacheCycle", cacheCycle);
    tt.bind("Java5", java5);
    tt.bind("Beaver", useBeaverSymbol);
    tt.bind("VisitCheckEnabled", visitCheckEnabled);
    tt.bind("TraceVisitCheck", traceVisitCheck);
    tt.bind("CreateDefaultMap", createDefaultMap);
    tt.bind("DefaultMapType", typeDefaultMap);
    tt.bind("CreateDefaultSet", createDefaultSet);
    tt.bind("DefaultSetType", typeDefaultSet);
    tt.bind("CreateContributorSet", createContributorSet);

    // Rewrites
    tt.bind("RewriteEnabled", rewriteEnabled);
    tt.bind("RewriteLimit", "" + rewriteLimit);
    tt.bind("HasRewriteLimit", rewriteLimit > 0);
    tt.bind("StagedRewrites", stagedRewrites);
    tt.bind("RewriteCircularNTA", rewriteCircularNTA);

    // JJTree
    tt.bind("JJTree", jjtree);
    tt.bind("ParserName", parserName);

    // Flush
    tt.bind("FlushEnabled", flushEnabled);
    tt.bind("FlushAttr", flushAttr);
    tt.bind("FlushColl", flushColl);
    tt.bind("FlushRewrite", flushRewrite);

    // Incremental
    tt.bind("IncrementalEnabled", incremental);
    tt.bind("IncrementalLevelParam", incrementalLevelParam);
    tt.bind("IncrementalLevelAttr", incrementalLevelAttr);
    tt.bind("IncrementalLevelNode", incrementalLevelNode);
    tt.bind("IncrementalLevelRegion", incrementalLevelRegion);
    tt.bind("IncrementalChangeFlush", incrementalChangeFlush);
    tt.bind("IncrementalChangeMark", incrementalChangeMark);
    tt.bind("IncrementalPropFull", incrementalPropFull);
    tt.bind("IncrementalPropLimit", incrementalPropLimit);
    tt.bind("IncrementalDebug", incrementalDebug);
    tt.bind("IncrementalTrack", incrementalTrack);
    tt.bind("DDGNodeName", astNodeType + "$DepGraphNode");

    // Tracing
    tt.bind("TracingEnabled", tracing);
    tt.bind("TraceCompute", traceCompute);
    tt.bind("TraceCache", traceCache);
    tt.bind("TraceRewrite", traceRewrite);
    tt.bind("TraceCircularNTA", traceCircularNTA);
    tt.bind("TraceCircular", traceCircular);
    tt.bind("TraceCopy", traceCopy);
    tt.bind("TraceFlush", traceFlush);

    // Cache
    tt.bind("CacheAnalyzeEnabled", cacheAnalyze);

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
    return printHelp;
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
