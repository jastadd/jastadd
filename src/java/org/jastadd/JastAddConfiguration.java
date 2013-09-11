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
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.jastadd.tinytemplate.TemplateContext;

import org.jastadd.ast.AST.Grammar;

/**
 * Tracks JastAdd configuration options.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class JastAddConfiguration {

  private final CommandLineArguments options;
  private final Option astNode;
  private final Option list;
  private final Option opt;
  private final Option jjtree;
  private final Option grammarOption;
  private final Option defaultMap;
  private final Option defaultSet;
  private final Option lazyMaps;
  private final Option noLazyMaps;
  private final Option privateOption;
  private final Option rewrite;
  private final Option beaver;
  private final Option lineColumnNumbers;
  private final Option noVisitCheck;
  private final Option noCacheCycle;
  private final Option noComponentCheck;
  private final Option componentCheck;
  private final Option noInhEqCheck;
  private final Option suppressWarnings;
  private final Option refineLegacy;
  private final Option noRefineLegacy;
  private final Option stagedRewrites;
  private final Option doc;
  private final Option license;
  private final Option java1_4;
  private final Option debug;
  private final Option synch;
  private final Option noStatic;
  private final Option deterministic;
  private final Option outputDirOption;
  private final Option tracing;
  private final Option cache;
  
  private final Option cacheAll;
  private final Option noCaching;
  private final Option cacheNone;
  private final Option cacheImplicit;
  
  private final Option ignoreLazy;
  
  private final Option packageOption;
  private final Option version;
  private final Option help;
  private final Option printNonStandardOptions;
  private final Option indent;
  private final Option minListSize;
  private final Option incremental;
  private final Option fullFlush;

  /**
   * Constructor - sets up available options.
   * @param args Command-line arguments to build configuration from
   * @param err output stream to print configuration warnings to
   */
  public JastAddConfiguration(String[] args, PrintStream err) {
    astNode = new Option("ASTNode", "set the name of the ASTNode type", true);
    list = new Option("List", "set the name of the List type", true);
    opt = new Option("Opt", "set the name of the Opt type", true);
    jjtree = new Option("jjtree", "use jjtree base node, this requires --grammar to be set");
    grammarOption = new Option("grammar", "the name of the grammar's parser, required when using --jjtree", true);
    defaultMap = new Option("defaultMap", "use this expression to construct maps for attribute caches", true);
    defaultSet = new Option("defaultSet", "use this expression to construct sets for attribute caches", true);
    lazyMaps = new Option("lazyMaps", "use lazy maps");
    noLazyMaps = new Option("noLazyMaps", "don't use lazy maps");
    privateOption = new Option("private", "");
    rewrite = new Option("rewrite", "enable ReRAGs support");
    beaver = new Option("beaver", "use beaver base node");
    lineColumnNumbers = new Option("lineColumnNumbers", "interface for storing line and column numbers");
    noVisitCheck = new Option("noVisitCheck", "disable circularity check for attributes");
    noCacheCycle = new Option("noCacheCycle", "disable cache cycle optimization for circular attributes");
    noComponentCheck = new Option("noComponentCheck", "enable strongly connected component optimization for circular attributes");
    componentCheck = new Option("componentCheck", "disable strongly connected component optimization for circular attributes");
    noInhEqCheck = new Option("noInhEqCheck", "disable check for inherited equations");
    suppressWarnings = new Option("suppressWarnings", "suppress warnings when using Java 5");
    refineLegacy = new Option("refineLegacy", "enable the legacy refine syntax");
    noRefineLegacy = new Option("noRefineLegacy", "disable the legacy refine syntax");
    stagedRewrites = new Option("stagedRewrites", "");
    doc = new Option("doc", "generate javadoc like .html pages from sources");
    license = new Option("license", "include the given file in each generated file", true);
    java1_4 = new Option("java1.4", "generate for Java 1.4");
    debug = new Option("debug", "generate run-time checks for debugging");
    synch = new Option("synch", "");
    noStatic = new Option("noStatic", "the generated state field is non-static");
    deterministic = new Option("deterministic", "");
    outputDirOption = new Option("o", "optional base output directory, default is current directory", true);
    tracing = new Option("tracing", "weaves in code collecting evaluation information", false, false, true);
    packageOption = new Option("package", "optional package for generated files", true);
    version = new Option("version", "print version string and halts");
    help = new Option("help", "prints a short help output and halts");
    printNonStandardOptions = new Option("X", "print list of non-standard options and halt");
    indent = new Option("indent", "Type of indentation {2space|4space|8space|tab}", true);
    minListSize = new Option("minListSize", "Minimum (non-empty) list size", true);
    
    // Cache flags
    cache = new Option("cache", "Overrides 'lazy' configurations with one of the following options:\n" +
    "            all: caches all attributes\n" +
    "           none: caches no attributes\n" + 
    "         config: caches attributes according to a given .config file on the following format:\n" +
    "                   ((cache|uncache)<WS><ASPECT_NAME><WS>(syn|inh)<WS><NODE_TYPE><DOT><ATTR_NAME>\n" +
    "                   <LPAREN>(<ARG_TYPE> <ARG_NAME>)*<RPAREN><SEMICOLON><EOL>)*\n" +
    "       implicit: caches all attribute but also reads a .config file that takes precedence\n" +
    "        analyze: analyzes cache use during evaluation (when all attributes are cached)\n" +
    "                 the result is available via the API in org.jastadd.CacheAnalyzer", true);
    
    // TODO: Deprecated, removed when phased out
    cacheAll = new Option("cacheAll", "DEPRECATED: Replaced with --cache=all");
    noCaching = new Option("noCaching", "DEPRECATED: Replaced with --cache=none");// what does this actually do? the same as cacheNone?? - Jesper, Deprecated - Emma
    cacheNone = new Option("cacheNone", "DEPRECATED: Replaced with --cache=none");
    cacheImplicit = new Option("cacheImplicit", "DEPRECATED: Replaced with --cache=implicit");
    ignoreLazy = new Option("ignoreLazy", "DEPRECATED: ignores the \"lazy\" keyword");

    // Incremental flags
    incremental = new Option("incremental", "turns on incremental evaluation with the given configuration\n" +
    "    CONFIGURATION: ATTRIBUTE(,ATTRIBUTE)* (comma separated list of attributes)\n" +
    "    ATTRIBUTE: param  (dependency tracking on parameter level, not combinable\n" +
    "                       with attr, node, region)\n" +
    "    ATTRIBUTE: attr  (dependency tracking on attribute level, default, not\n" +
    "                      combinable with param, node, region)\n" +
    "    ATTRIBUTE: node  (dependency tracking on node level, not combinable with\n" +
    "                      param, attr, region)\n" +
    "    ATTRIBUTE: region (dependency tracking on region level, not combinable\n" +
    "                       with param, attr, node)\n" +
    "    ATTRIBUTE: flush (invalidate with flush, default, not combinable with mark)\n" +
    "    ATTRIBUTE: mark  (invalidate with mark, not combinable with flush, NOT\n" +
    "                      SUPPORTED YET)\n" +
    "    ATTRIBUTE: full  (full change propagation, default, not combinable with\n" +
    "                      limit)\n" +
    "    ATTRIBUTE: limit (limited change propagation, not combinable with full,\n" +
    "                      NOT SUPPORTED YET)\n" +
    "    ATTRIBUTE: debug (generate code for debugging and dumping of dependencies)", true);
    fullFlush = new Option("fullFlush", "full flush in incremental evaluation");


    defaultMap.setNonStandard();
    defaultSet.setNonStandard();
    privateOption.setNonStandard();
    stagedRewrites.setNonStandard();
    synch.setNonStandard();
    noStatic.setNonStandard();

    // Deprecated options
    suppressWarnings.setDeprecated();
    ignoreLazy.setDeprecated();
    cacheAll.setDeprecated();
    cacheNone.setDeprecated();
    cacheImplicit.setDeprecated();
    noCaching.setDeprecated();

    // Should these be deprecated? They are default ON
    //lazyMaps.setDeprecated();
    //refineLegacy.setDeprecated();
    //noComponentCheck.setDeprecated();

    // set default values
    astNode.setDefaultValue("ASTNode");
    list.setDefaultValue("List");
    opt.setDefaultValue("Opt");
    grammarOption.setDefaultValue("Unknown");
    defaultMap.setDefaultValue("new java.util.HashMap(4)");
    defaultSet.setDefaultValue("new java.util.HashSet(4)");
    outputDirOption.setValue(System.getProperty("user.dir"));
    packageOption.setDefaultValue("");
    indent.setDefaultValue("2space");
    minListSize.setDefaultValue("4");

    options = new CommandLineArguments();
    options.addOption(astNode);
    options.addOption(list);
    options.addOption(opt);
    options.addOption(jjtree);
    options.addOption(grammarOption);
    options.addOption(defaultMap);
    options.addOption(defaultSet);
    options.addOption(lazyMaps);
    options.addOption(noLazyMaps);
    options.addOption(privateOption);
    options.addOption(rewrite);
    options.addOption(beaver);
    options.addOption(lineColumnNumbers);
    options.addOption(noVisitCheck);
    options.addOption(noCacheCycle);
    options.addOption(noComponentCheck);
    options.addOption(componentCheck);
    options.addOption(noInhEqCheck);
    options.addOption(suppressWarnings);
    options.addOption(refineLegacy);
    options.addOption(noRefineLegacy);
    options.addOption(stagedRewrites);
    options.addOption(doc);
    options.addOption(license);
    options.addOption(java1_4);
    options.addOption(debug);
    options.addOption(synch);
    options.addOption(noStatic);
    options.addOption(deterministic);
    options.addOption(outputDirOption);
    options.addOption(tracing);
    options.addOption(cache);
    
    options.addOption(cacheAll);
    options.addOption(noCaching);
    options.addOption(cacheNone);
    options.addOption(cacheImplicit);
    options.addOption(ignoreLazy);
    
    options.addOption(packageOption);
    options.addOption(version);
    options.addOption(help);
    options.addOption(printNonStandardOptions);
    options.addOption(indent);
    options.addOption(incremental);
    options.addOption(fullFlush);

    // parse the argument list
    options.match(args, err);
  }

  /**
   * Output directory to write generated AST node types in.
   * @return The configured output directory
   */
  private File getOutputDir() {
    return new File(outputDirOption.value());

  }

  /**
   * @return <code>true</code> if public modifier option is enabled
   */
  public boolean getPublicModifier() {
    return !privateOption.matched();
  }

  /**
   * Initialize a grammar object with the current configuration.
   * @return Root Grammar node for the generated AST
   */
  public Grammar buildRoot() {
    Grammar root = new Grammar();

    root.outputDir = outputDirOption.value();
    root.packageName = packageOption.value();

    root.astNodeType = astNode.value();
    root.listType = list.value();
    root.optType = opt.value();

    root.blockBegin = "synchronized(" + root.astNodeType + ".class) {\n";
    root.blockEnd =   "}\n";
    root.createContributorSet = "new " + root.astNodeType + "$State.IdentityHashSet(4)";

    root.jjtree = jjtree.matched();
    root.parserName = grammarOption.value();

    root.createDefaultMap = defaultMap.value();
    root.createDefaultSet = defaultSet.value();

    // TODO make ASTNode.ind not-static
    if (indent.value().equals("2space")) {
      // Use 2 spaces for indentation
      root.ind = "  ";
    } else if (indent.value().equals("4space")) {
      // Use 4 spaces for indentation
      root.ind = "    ";
    } else if (indent.value().equals("8space")) {
      // Use 8 spaces for indentation
      root.ind = "        ";
    } else if (indent.value().equals("tab")) {
      // Use tabs for indentation
      root.ind = "\t";
    }

    try {
      root.minListSize = Integer.parseInt(minListSize.value());
    } catch (NumberFormatException e) {
      System.err.println("Warning: failed to parse minimum list size option!");
    }

    root.lazyMaps = !noLazyMaps.matched();

    root.rewriteEnabled = rewrite.matched();
    root.beaver = beaver.matched();
    root.lineColumnNumbers = lineColumnNumbers.matched();
    root.visitCheckEnabled = !noVisitCheck.matched();
    root.cacheCycle = !noCacheCycle.matched();
    root.componentCheck = componentCheck.matched();
    root.noInhEqCheck = noInhEqCheck.matched();

    root.refineLegacy = !noRefineLegacy.matched();

    root.stagedRewrites = stagedRewrites.matched();

    root.doc = doc.matched();

    root.license = "";
    if(license.matched()) {
      String fileName = license.value();
      try {
        if(fileName != null) {
          root.license = readFile(fileName);
        }
      } catch (java.io.IOException e) {
        System.err.println("Error loading license file " + fileName);
        System.exit(1);
      }
    }

    root.java5 = !java1_4.matched();

    if (debug.matched()) {
      root.debugMode = true;
      root.cycleLimit = 100;
      root.rewriteLimit = 100;
      root.visitCheckEnabled = true;
    }

    root.block = synch.matched();

    root.noStatic = noStatic.matched();

    root.deterministic = deterministic.matched();
    if(root.deterministic) {
      // overrides values set by the defaultMap and defaultSet options
      root.createDefaultMap = "new java.util.LinkedHashMap(4)";
      root.createDefaultSet = "new java.util.LinkedHashSet(4)";
    }

    if (tracing.matched() && tracing.value().isEmpty()) {
      root.tracing = true;
      root.traceCompute = true;
      root.traceCache = true;
      root.traceRewrite = true;
      root.traceCircularNTA = true;
      root.traceCircular = true;
      root.traceCopy = true;
    } else if (tracing.matched()) {
      Set<String> set = parseCommaSeparatedValue(tracing.value());
      root.tracing = true;
      root.traceCompute = set.contains("compute");
      root.traceCache = set.contains("cache");
      root.traceRewrite = set.contains("rewrite");
      root.traceCircularNTA = set.contains("circularNTA");
      root.traceCircular = set.contains("circular");
      root.traceCopy = set.contains("copy");
    }
        
    // Cache flag
    String cacheValue = cache.matched() ? cache.value() : "";
    if (cacheValue.equals("all")) root.cacheAll = true;
    else if (cacheValue.equals("none")) root.cacheNone = true;
    else if (cacheValue.equals("config")) root.cacheConfig = true;
    else if (cacheValue.equals("implicit")) root.cacheImplicit = true;
    else if (cacheValue.equals("analyze")) {
      root.cacheAnalyze = true;
      // this analysis needs full caching and tracing of cache usage
      root.cacheAll = true;
      root.traceCache = true;
    }
    
    // TODO: Deprecated, remove when phased out
    root.cacheAll |= cacheAll.matched();
    
    // TODO: Deprecated, remove when phased out
    root.cacheNone |= cacheNone.matched();
    
    // TODO: Deprecated, remove when phased out
    root.cacheNone |= noCaching.matched();
    
    // TODO: Deprecated, remove when phased out
    root.cacheImplicit |= cacheImplicit.matched();
    
    // Incremental flag
    root.incremental = incremental.matched();
    Set<String> incrementalArgSet = parseCommaSeparatedValue(root.incremental ? incremental.value() : "");
    root.incrementalLevelParam = incrementalArgSet.contains("param");
    root.incrementalLevelAttr = incrementalArgSet.contains("attr");
    root.incrementalLevelNode = incrementalArgSet.contains("node");
    root.incrementalLevelRegion = incrementalArgSet.contains("region");
    root.incrementalChangeFlush = incrementalArgSet.contains("flush");
    root.incrementalChangeMark = incrementalArgSet.contains("mark");
    root.incrementalPropFull = incrementalArgSet.contains("full");
    root.incrementalPropLimit = incrementalArgSet.contains("limit");
    root.incrementalDebug = incrementalArgSet.contains("debug");
    root.incrementalTrack = incrementalArgSet.contains("track");

    root.fullFlush = fullFlush.matched();

    // The first time we access templateContext the Grammar.ind option must
    // be set already!
    TemplateContext tt = root.templateContext();
    if (synch.matched()) {
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
    tt.bind("JJTree", root.jjtree);
    tt.bind("ParserName", root.parserName);

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

    return root;
  }

  /**
   * @return <code>true</code> if configuration has errors
   */
  public boolean checkProblems() {
    
    if (jjtree.matched() && !grammarOption.matched()) {
      System.err.println("Missing grammar option. It is required in jjtree-mode!");
      return true;
    }

    for (int i = 0; i < options.getNumOperands(); ++i) {
      String fileName = options.getOperand(i);

      if (!(fileName.endsWith(".ast")
          || fileName.endsWith(".jrag")
          || fileName.endsWith(".jadd")
          || fileName.endsWith(".config"))) {
        System.err.println("Unrecognized file extension: " + fileName);
        return true;
      }
    }
    
    File outputDir = getOutputDir();
    if (!outputDir.exists()) {
      System.err.println("Output directory " + outputDir.getAbsolutePath() +
        " does not exist");
      return true;
    }
    if (!outputDir.isDirectory()) {
      System.err.println("Output directory " + outputDir.getAbsolutePath() +
        " is not a directory");
      return true;
    }
    if (!outputDir.canWrite()) {
      System.err.println("Output directory " + outputDir.getAbsolutePath() +
        " is write protected");
      return true;
    }

    if (!checkIncrementalConfig()) {
      return true;
    }

    if (!checkCacheConfig()) {
      return true;
    }

    return false;
  }
  
  /**
   * Parse comma separated option value.
   * @param value The comma separated value to parse 
   * @return A set of values 
   */
  private Set<String> parseCommaSeparatedValue(String value) {
    Set<String> set = new HashSet<String>();
    StringTokenizer st = new StringTokenizer(value, ",");
    while (st.hasMoreTokens()) {
      set.add(st.nextToken().trim());
    }
    return set;
  }
  
  /**
   * Checks the cache configuration for errors.
   * @return true if no errors
   */
  private boolean checkCacheConfig() {
    boolean cacheAllFlag = cacheAll.matched();
    boolean cacheNoneFlag = cacheNone.matched();
    boolean noCachingFlag = noCaching.matched();
    boolean cacheImplicitFlag = cacheImplicit.matched();
    // Only one of cacheAll, cacheNone, noCaching, and cacheImplicit can be true
    if ((cacheAllFlag && (cacheNoneFlag || noCachingFlag || cacheImplicitFlag)) 
        || (cacheNoneFlag && (cacheAllFlag || noCachingFlag || cacheImplicitFlag))
        || (noCachingFlag && (cacheAllFlag || cacheNoneFlag || cacheImplicitFlag))
        || (cacheImplicitFlag && (cacheAllFlag || cacheNoneFlag || noCachingFlag))) {
      System.err.println("error: Conflict in cache configuration. " +
        "Cannot combine --cacheAll, --cacheNone, --noCaching, and --cacheImplicit flags.");
      return false;  
    }
    
    return true;
  }

  /**
   * Checks the incremental configuration for errors.
   * @return true if no errors
   */
  private boolean checkIncrementalConfig() {
    Set<String> incrementalArgSet = parseCommaSeparatedValue(incremental.matched() ? incremental.value() : "");
    boolean incrementalLevelParam = incrementalArgSet.contains("param");
    boolean incrementalLevelAttr = incrementalArgSet.contains("attr");
    boolean incrementalLevelNode = incrementalArgSet.contains("node");
    boolean incrementalLevelRegion = incrementalArgSet.contains("region");
    boolean incrementalChangeFlush = incrementalArgSet.contains("flush");
    boolean incrementalChangeMark = incrementalArgSet.contains("mark");
    boolean incrementalPropFull = incrementalArgSet.contains("full");
    boolean incrementalPropLimit = incrementalArgSet.contains("limit");
    // check level: only one level at a time
    if (incrementalLevelAttr && incrementalLevelNode ||
        incrementalLevelAttr && incrementalLevelParam ||
        incrementalLevelNode && incrementalLevelParam ||
        incrementalLevelParam && incrementalLevelRegion ||
        incrementalLevelAttr && incrementalLevelRegion ||
        incrementalLevelNode && incrementalLevelRegion) {
      System.err.println("error: Conflict in incremental evaluation configuration. " +
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
      System.err.println("error: Conflict in incremental evaluation configuration. " +
          "Cannot combine \"flush\" and \"mark\".");
      return false;
    }
    // check invalidate: no chosen strategy means default -- "flush"
    if (!incrementalChangeFlush && !incrementalChangeMark) {
      incrementalChangeFlush = true;
    }
    // check invalidate: currently not supporting mark strategy -- "mark"
    if (incrementalChangeMark) {
      System.err.println("error: Unsupported incremental evaluation configuration: " +
          "\"mark\".");
      return false;
    }
    // check propagation: only one strategy at a time
    if (incrementalPropFull && incrementalPropLimit) {
      System.err.println("error: Conflict in incremental evaluation configuration. " +
          "Cannot combine \"full\" and \"limit\".");
      return false;
    }
    // check propagation: no chosen strategy means default -- "full"
    if (!incrementalPropFull && !incrementalPropLimit) {
      incrementalPropFull = true;
    }
    // check propagation: currently not supporting limit strategy -- "limit" - we do now
    //if (root.incrementalPropLimit) {
    //    System.err.println("error: Unsupported incremental evaluation configuration: " +
    //        "\"limit\".");
    //    return false;
    //}
    return true;
  }

  private String readFile(String name) throws java.io.IOException {
    StringBuffer buf = new StringBuffer();
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

    for (int i = 0; i < options.getNumOperands(); ++i) {
      String fileName = options.getOperand(i);
      if (fileName.endsWith(".ast") || fileName.endsWith(".jrag")
          || fileName.endsWith(".jadd")) {
        files.add(fileName);
      }
    }

    return files;
  }

  /**
   * @return cache file list
   */
  public Collection<String> getCacheFiles() {
    Collection<String> cacheFiles = new ArrayList<String>();
    for (int i = 0; i < options.getNumOperands(); ++i) {
      String fileName = options.getOperand(i);
      if (fileName.endsWith(".config")) {
        cacheFiles.add(fileName);
      }
    }

    return cacheFiles;
  }

  /**
   * Print help
   * @param out output stream to print help to
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
    options.printHelp(out);
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
    options.printNonStandardOptions(out);
  }

  /**
   * @return <code>true</code> if the version string should be printed
   */
  public boolean shouldPrintVersion() {
    return version.matched();
  }

  /**
   * @return <code>true</code> if the help message should be printed
   */
  public boolean shouldPrintHelp() {
    return help.matched() || getFiles().isEmpty();
  }

  /**
   * @return <code>true</code> if non-standard options should be printed
   */
  public boolean shouldPrintNonStandardOptions() {
    return printNonStandardOptions.matched();
  }

  /**
   * @return <code>true</code> if the --tracing option is enabled
   */
  public boolean tracingEnabled() {
    return tracing.matched();
  }

  /**
   * @return <code>true</code> if the --cache=analyze option is enabled
   */
  public boolean cacheAnalyzeEnabled() {
    return cache.matched() && cache.value().equals("analyze");
  }

}
