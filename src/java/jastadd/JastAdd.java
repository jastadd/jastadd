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
package jastadd;

import ast.AST.*;

import jrag.AST.*;

import java.util.*;
import java.io.*;

import org.jastadd.tinytemplate.TemplateContext;

/**
 * JastAdd main class
 */
@SuppressWarnings("javadoc")
public class JastAdd {

  private static ResourceBundle resources = null;
  private static String resourcename = "JastAdd";
  private static String getString(String key) {
    if (resources == null) {
      try {
        resources = ResourceBundle.getBundle(resourcename);
      } catch (MissingResourceException e) {
        throw new Error("Could not open the resource " +
            resourcename);
      }
    }
    return resources.getString(key);
  }
  public static String getVersionString() {
    return "JastAdd2 " + getString("jastadd.version");
  }
  public static String getLongVersionString() {
    return "JastAdd2 (http://jastadd.org) version " +
      getString("jastadd.version");
  }

  protected java.util.List<String> files;
  protected java.util.List<String> cacheFiles;

  /**
   * Root of the AST for the parsed ast-grammar file
   */
  protected final Grammar root = new Grammar();
  protected String pack;
  protected File outputDir;
  protected boolean publicModifier;

  public static void main(String[] args) {
    new JastAdd().compile(args);
    Runtime.getRuntime().gc();
  }

  public void compile(String[] args) {
    try {
      files = new ArrayList<String>();
      cacheFiles = new ArrayList<String>();
      if (readArgs(args)) {
        System.exit(1);
      }

      long time = System.currentTimeMillis();

      root.abstractAncestors();

      // Parse ast-grammar
      //System.out.println("parsing grammars");
      Collection<String> errors = new ArrayList<String>();
      for (Iterator<String> iter = files.iterator(); iter.hasNext();) {
        String fileName = iter.next();
        if(fileName.endsWith(".ast")) {
          try {
            Ast parser = new Ast(new FileInputStream(fileName));
            parser.fileName = fileName;
            Grammar g = parser.Grammar();
            for(int i = 0; i < g.getNumTypeDecl(); i++) {
              root.addTypeDecl(g.getTypeDecl(i));
            }

            // EMMA_2011-12-12: Adding region declarations for incremental evaluation
            for (int i = 0; i < g.getNumRegionDecl(); i++) {
              root.addRegionDecl(g.getRegionDecl(i));
            }
            //

            for (Iterator<?> errorIter = parser.getErrors(); errorIter.hasNext();) {
              String[] s = ((String) errorIter.next()).split(";");
              errors.add("Syntax error in " + fileName + " at line " + s[0] +
                  ", column " + s[1]);
            }

          } catch (ast.AST.TokenMgrError e) {
            System.err.println("Lexical error in " + fileName + ": " + e.getMessage());
            System.exit(1);
          } catch (ast.AST.ParseException e) {
            // Exceptions actually caught by error recovery in parser
          } catch (FileNotFoundException e) {
            System.err.println("File error: Abstract syntax grammar file " + fileName + " not found");
            System.exit(1);
          }
        }
      }

      if(!errors.isEmpty()) {
        for (Iterator<String> iter = errors.iterator(); iter.hasNext();) {
          System.err.println(iter.next());
        }
        System.exit(1);
      }

      long astParseTime = System.currentTimeMillis() - time;

      String astErrors = root.astErrors();

      long astErrorTime = System.currentTimeMillis() - time - astParseTime;

      if(!astErrors.equals("")) {
        System.err.println("Semantic error:");
        System.err.println(astErrors);
        System.exit(1);
      }

      ASTNode.resetGlobalErrors();

      {
        //System.out.println("generating ASTNode");
        java.io.StringWriter writer = new java.io.StringWriter();
        root.jjtGenASTNode$State(new PrintWriter(writer));

        jrag.AST.JragParser jp = new jrag.AST.JragParser(new java.io.StringReader(writer.toString()));
        jp.root = root;
        jp.setFileName("ASTNode");
        jp.className = "ASTNode";
        jp.pushTopLevelOrAspect(true);
        try {
          while(true)
            jp.AspectBodyDeclaration();
        } catch (Exception e) {
          // TODO: handle error?
          // String s = e.getMessage();
        }
        jp.popTopLevelOrAspect();
      }

      // ES_2011-09-06: Incremental evaluation
      if (root.incremental) {
        java.io.StringWriter writer = new java.io.StringWriter();
        root.jjtGenASTNode$DepGraphNode(new PrintWriter(writer));
        jrag.AST.JragParser jp = new jrag.AST.JragParser(
            new java.io.StringReader(writer.toString()));
        jp.root = root;
        jp.setFileName("ASTNode");
        jp.className = "ASTNode";
        jp.pushTopLevelOrAspect(true);
        try {
          while(true)
            jp.AspectBodyDeclaration();
        } catch (Exception e) {
          // TODO: handle error?
          // String s = e.getMessage();
        }
        jp.popTopLevelOrAspect();
      }

      // Parse all jrag files and build tables
      for (Iterator<String> iter = files.iterator(); iter.hasNext();) {
        String fileName = iter.next();
        if(fileName.endsWith(".jrag") || fileName.endsWith(".jadd")) {
          try {
            FileInputStream inputStream = new FileInputStream(fileName);
            JragParser jp = new JragParser(inputStream);
            jp.inputStream = inputStream; // Hack to make input stream visible for ast-parser
            jp.root = root;
            jp.setFileName(fileName);
            ASTCompilationUnit au = jp.CompilationUnit();
            root.addCompUnit(au);
          } catch (jrag.AST.ParseException e) {
            StringBuffer msg = new StringBuffer();
            msg.append("Syntax error in " + fileName + " at line " + e.currentToken.next.beginLine + ", column " +
                e.currentToken.next.beginColumn);
            System.err.println(msg.toString());
            System.exit(1);
          } catch (FileNotFoundException e) {
            System.err.println("File error: Aspect file " + fileName + " not found");
            System.exit(1);
          } catch (Throwable e) {
            System.err.println("Exception occurred while parsing " + fileName);
            e.printStackTrace();
          }
        }
      }

      long jragParseTime = System.currentTimeMillis() - time - astErrorTime;

      root.processInterfaceRefinements();
      root.weaveInterfaceIntroductions();

      //System.out.println("weaving aspect and attribute definitions");
      for(int i = 0; i < root.getNumTypeDecl(); i++) {
        if(root.getTypeDecl(i) instanceof ASTDecl) {
          ASTDecl decl = (ASTDecl)root.getTypeDecl(i);
          java.io.StringWriter writer = new java.io.StringWriter();
          decl.jjtGen(new PrintWriter(writer));

          jrag.AST.JragParser jp = new jrag.AST.JragParser(new java.io.StringReader(writer.toString()));
          jp.root = root;
          jp.setFileName("");
          jp.className = "ASTNode";
          jp.pushTopLevelOrAspect(true);
          try {
            while(true)
              jp.AspectBodyDeclaration();
          } catch (Exception e) {
            // TODO: handle error?
            // String s = e.getMessage();
          }
          jp.popTopLevelOrAspect();

          int j = 0;
          for (Iterator<?> iter = decl.getComponents(); iter.hasNext();) {
            Components c = (Components) iter.next();
            if (c instanceof TokenComponent) {
              c.jaddGen(null, j, publicModifier, decl);
            } else {
              c.jaddGen(null, j, publicModifier, decl);
              j++;
            }
          }
        }
      }

      //System.out.println("processing refinements");
      root.processRefinements();

      for (Iterator<String> iter = cacheFiles.iterator(); iter.hasNext();) {
        String fileName = iter.next();
        //System.out.println("Processing cache file: " + fileName);
        try {
          FileInputStream inputStream = new FileInputStream(fileName);
          JragParser jp = new JragParser(inputStream);
          jp.inputStream = inputStream; // Hack to make input stream visible for ast-parser
          jp.root = root;
          jp.setFileName(fileName);
          jp.CacheDeclarations();
        } catch (jrag.AST.ParseException e) {
          StringBuffer msg = new StringBuffer();
          msg.append("Syntax error in " + fileName + " at line " + e.currentToken.next.beginLine + ", column " +
              e.currentToken.next.beginColumn);
          System.err.println(msg.toString());
          System.exit(1);
        } catch (FileNotFoundException e) {
          System.err.println("File error: Aspect file " + fileName + " not found");
          System.exit(1);
        }
      }

      //System.out.println("weaving collection attributes");
      root.weaveCollectionAttributes();

      String err = root.errors();
      if(!err.equals("") || !ASTNode.globalErrors.equals("")) {
        System.err.println("Semantic errors: \n" + err + ASTNode.globalErrors);
        System.exit(1);
      }

      long jragErrorTime = System.currentTimeMillis() - time - jragParseTime;

      root.jastAddGen(outputDir, root.parserName, pack, publicModifier);
      try {
        root.createInterfaces(outputDir, pack);
      } catch (FileNotFoundException e) {
        System.err.println("File error: Output directory " + outputDir + " does not exist or is write protected");
        System.exit(1);
      }
      
      @SuppressWarnings("unused")
      long codegenTime = System.currentTimeMillis() - time - jragErrorTime;

      //System.out.println("AST parse time: " + astParseTime + ", AST error check: " + astErrorTime + ", JRAG parse time: " + 
      //    jragParseTime + ", JRAG error time: " + jragErrorTime + ", Code generation: " + codegenTime);
    }
    catch(NullPointerException e) {
      e.printStackTrace();
      throw e;
    }
    catch(ArrayIndexOutOfBoundsException e) {
      e.printStackTrace();
      throw e;
    }
    catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Read and process command line arguments
   *
   * @param args Command line arguments
   */
  public boolean readArgs(String[] args) {
    CommandLineArguments options = new CommandLineArguments();
    Option jjtree = new Option("jjtree", "use jjtree base node, this requires --grammar to be set");
    Option grammarOption = new Option("grammar", "the name of the grammar's parser, required when using --jjtree", true);
    Option defaultMap = new Option("defaultMap", "use this expression to construct maps for attribute caches", true);
    Option defaultSet = new Option("defaultSet", "use this expression to construct sets for attribute caches", true);
    Option lazyMaps = new Option("lazyMaps", "use lazy maps");
    Option noLazyMaps = new Option("noLazyMaps", "don't use lazy maps");
    Option privateOption = new Option("private", "");
    Option rewrite = new Option("rewrite", "enable ReRAGs support");
    Option beaver = new Option("beaver", "use beaver base node");
    Option noVisitCheck = new Option("noVisitCheck", "disable circularity check for attributes");
    Option noCacheCycle = new Option("noCacheCycle", "disable cache cycle optimization for circular attributes");
    Option noComponentCheck = new Option("noComponentCheck", "enable strongly connected component optimization for circular attributes");
    Option componentCheck = new Option("componentCheck", "disable strongly connected component optimization for circular attributes");
    Option noInhEqCheck = new Option("noInhEqCheck", "disable check for inherited equations");
    Option suppressWarnings = new Option("suppressWarnings", "suppress warnings when using Java 5");
    Option parentInterface = new Option("parentInterface", "search equations for inherited attributes using interfaces");
    Option refineLegacy = new Option("refineLegacy", "enable the legacy refine syntax");
    Option noRefineLegacy = new Option("noRefineLegacy", "disable the legacy refine syntax");
    Option stagedRewrites = new Option("stagedRewrites", "");
    Option doc = new Option("doc", "generate javadoc like .html pages from sources");
    Option license = new Option("license", "include the given file in each generated file", true);
    Option java1_4 = new Option("java1.4", "generate for Java 1.4");
    Option debug = new Option("debug", "generate run-time checks for debugging");
    Option synch = new Option("synch", "");
    Option noStatic = new Option("noStatic", "the generated state field is non-static");
    Option deterministic = new Option("deterministic", "");
    Option outputDirOption = new Option("o", "optional base output directory, default is current directory", true);
    Option tracing = new Option("tracing", "weaves in code generating a cache tree");
    Option cacheAll = new Option("cacheAll", "cache all attributes");
    Option noCaching = new Option("noCaching", "");// what does this actually do? the same as cacheNone?? - Jesper
    Option doxygen = new Option("doxygen", "enhance navigation and documentation when using doxygen");
    Option cacheNone = new Option("cacheNone", "cache no attributes, except NTAs");
    Option cacheImplicit = new Option("cacheImplicit", "make caching implicit, .caching files have higher priority");
    Option ignoreLazy = new Option("ignoreLazy", "ignore the \"lazy\" keyword");
    Option packageOption = new Option("package", "optional package for generated files", true);
    Option version = new Option("version", "print version string and halts");
    Option help = new Option("help", "prints a short help output and halts");
    Option printNonStandardOptions = new Option("X", "print list of non-standard options and halt");
    Option indent = new Option("indent", "Type of indentation {2space|4space|8space|tab}", true);

    // Incremental flags
    Option incremental = new Option("incremental", "turns on incremental evaluation with the given configuration\n" +
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
    Option fullFlush = new Option("fullFlush", "full flush in incremental evaluation");


    defaultMap.setNonStandard();
    defaultSet.setNonStandard();
    privateOption.setNonStandard();
    stagedRewrites.setNonStandard();
    synch.setNonStandard();
    noStatic.setNonStandard();
    noCaching.setNonStandard();

    // Should these be deprecated? They are default ON
    //lazyMaps.setDeprecated();
    //refineLegacy.setDeprecated();
    //noComponentCheck.setDeprecated();

    doxygen.setDeprecated();

    // set default values
    grammarOption.setDefaultValue("Unknown");
    defaultMap.setDefaultValue("new java.util.HashMap(4)");
    defaultSet.setDefaultValue("new java.util.HashSet(4)");
    outputDirOption.setValue(System.getProperty("user.dir"));
    packageOption.setDefaultValue("");
    indent.setDefaultValue("2space");

    options.addOption(jjtree);
    options.addOption(grammarOption);
    options.addOption(defaultMap);
    options.addOption(defaultSet);
    options.addOption(lazyMaps);
    options.addOption(noLazyMaps);
    options.addOption(privateOption);
    options.addOption(rewrite);
    options.addOption(beaver);
    options.addOption(noVisitCheck);
    options.addOption(noCacheCycle);
    options.addOption(noComponentCheck);
    options.addOption(componentCheck);
    options.addOption(noInhEqCheck);
    options.addOption(suppressWarnings);
    options.addOption(parentInterface);
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
    options.addOption(cacheAll);
    options.addOption(noCaching);
    options.addOption(doxygen);
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
    options.match(args);

    if (printNonStandardOptions.matched()) {
      System.err.println("Non-standard options:");
      options.printNonStandardOptions();
      System.exit(0);
    }

    if (jjtree.matched() && !grammarOption.matched()) {
      System.err.println("Missing grammar option. It is required in jjtree-mode!");
      return true;
    }

    root.jjtree = jjtree.matched();
    root.parserName = grammarOption.value();

    root.createDefaultMap = defaultMap.value();
    root.createDefaultSet = defaultSet.value();

    if (indent.value().equals("2space")) {
      // Use 2 spaces for indentation
      ASTNode.ind = "  ";
    } else if (indent.value().equals("4space")) {
      // Use 4 spaces for indentation
      ASTNode.ind = "    ";
    } else if (indent.value().equals("8space")) {
      // Use 8 spaces for indentation
      ASTNode.ind = "        ";
    } else if (indent.value().equals("tab")) {
      // Use tabs for indentation
      ASTNode.ind = "\t";
    }

    root.lazyMaps = !noLazyMaps.matched();

    publicModifier = !privateOption.matched();

    root.rewriteEnabled = rewrite.matched();
    root.beaver = beaver.matched();
    root.visitCheckEnabled = !noVisitCheck.matched();
    root.cacheCycle = !noCacheCycle.matched();
    root.componentCheck = componentCheck.matched();
    root.noInhEqCheck = noInhEqCheck.matched();

    root.suppressWarnings = suppressWarnings.matched();
    root.parentInterface = parentInterface.matched();

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

    String outputDirName = outputDirOption.value();
    outputDir = new File(outputDirName);

    if(!outputDir.exists()) {
      System.err.println("Output directory " + outputDir.getAbsolutePath() +
        " does not exist");
      System.exit(1);
    }
    if(!outputDir.isDirectory()) {
      System.err.println("Output directory " + outputDir.getAbsolutePath() +
        " is not a directory");
      System.exit(1);
    }
    if(!outputDir.canWrite()) {
      System.err.println("Output directory " + outputDir.getAbsolutePath() +
        " is write protected");
      System.exit(1);
    }

    root.tracing = tracing.matched();
    root.cacheAll = cacheAll.matched();
    root.noCaching = noCaching.matched();
    root.doxygen = doxygen.matched();

    // Handle new flags
    root.cacheNone = cacheNone.matched();
    root.cacheImplicit = cacheImplicit.matched();
    root.ignoreLazy = ignoreLazy.matched();

    // ES_2011-09-06: Handle incremental flag
    root.incremental = incremental.matched();
    String incrementalConfig;
    if (incremental.matched()) {
      incrementalConfig = incremental.value();
    } else {
      incrementalConfig = "";
    }
    Map<String, String> incrementalArgMap = parseIncrementalConfig(incrementalConfig);
    root.incrementalLevelParam = incrementalArgMap.containsKey("param");
    root.incrementalLevelAttr = incrementalArgMap.containsKey("attr");
    root.incrementalLevelNode = incrementalArgMap.containsKey("node");
    root.incrementalLevelRegion = incrementalArgMap.containsKey("region");
    root.incrementalChangeFlush = incrementalArgMap.containsKey("flush");
    root.incrementalChangeMark = incrementalArgMap.containsKey("mark");
    root.incrementalPropFull = incrementalArgMap.containsKey("full");
    root.incrementalPropLimit = incrementalArgMap.containsKey("limit");
    root.incrementalDebug = incrementalArgMap.containsKey("debug");
    root.incrementalTrack = incrementalArgMap.containsKey("track");
    if (!checkIncrementalConfig()) {
      return true;
    }

    // ES_2011-10-10: Handle full flush flag
    root.fullFlush = fullFlush.matched();

    pack = packageOption.value().replace('/', '.');
    int n = options.getNumOperands();
    for (int k=0; k<n; k++) {
      String fileName = options.getOperand(k);
      if(fileName.endsWith(".ast") || fileName.endsWith(".jrag") || fileName.endsWith(".jadd")) {
        files.add(fileName);
      }
      else if(fileName.endsWith(".caching")) {
        cacheFiles.add(fileName);
      }
      else {
        System.err.println("FileError: " + fileName + " is of unknown file type");
        return true;
      }
    }

    if (version.matched()) {
      // just print version and exit
      System.err.println(getVersionString());
      System.exit(0);
    }

    if (help.matched() || files.isEmpty()) {
      // just print version and exit
      System.err.println(getLongVersionString() + "\n");
      printHelp(options);
      System.exit(0);
    }
    
    // Bind template variables the first time we access templateContext the
    // Grammar.ind option needs to have been set already!
    TemplateContext tt = root.templateContext();
    if (synch.matched()) {
      tt.bindExpansion("SynchBegin", "synchronized-block.begin");
      tt.bindExpansion("SynchEnd", "synchronized-block.end");
    } else {
      tt.bind("SynchBegin", "");
      tt.bind("SynchEnd", "");
    }
    tt.bind("CreateDefaultMap", root.createDefaultMap);
    tt.bind("DefaultMapType", root.typeDefaultMap);
    tt.bind("CreateDefaultSet", root.createDefaultSet);
    tt.bind("DefaultSetType", root.typeDefaultSet);
    tt.bind("CreateContributorSet", root.createContributorSet);
    
    return false;
  }

  // ES_2011-09-06: Incremental evaluation
  //   Method parsing the incremental configuration argument given to
  //   the flag turning on incremental evaluation.
  private Map<String, String> parseIncrementalConfig(String str) {
    Map<String, String> map = new HashMap<String, String>();
    StringTokenizer st = new StringTokenizer(str, ",");
    while (st.hasMoreTokens()) {
      map.put(st.nextToken().trim(), "");
    }
    return map;
  }
  // ES_2011-09-19: Check of incremental configuration.
  private boolean checkIncrementalConfig() {

    // check level: only one level at a time
    if (root.incrementalLevelAttr && root.incrementalLevelNode ||
        root.incrementalLevelAttr && root.incrementalLevelParam ||
        root.incrementalLevelNode && root.incrementalLevelParam ||
        root.incrementalLevelParam && root.incrementalLevelRegion ||
        root.incrementalLevelAttr && root.incrementalLevelRegion ||
        root.incrementalLevelNode && root.incrementalLevelRegion) {
      System.err.println("error: Conflict in incremental evaluation configuration. " +
          "Cannot combine \"param\", \"attr\", \"node\" and \"region\".");
      return false;
        }
    // check level: no chosen level means default -- "attr"
    if (!root.incrementalLevelAttr && !root.incrementalLevelNode && 
        !root.incrementalLevelParam && !root.incrementalLevelRegion) {
      root.incrementalLevelAttr = true;
        }

    // check invalidate: only one strategy at a time
    if (root.incrementalChangeFlush && root.incrementalChangeMark) {
      System.err.println("error: Conflict in incremental evaluation configuration. " +
          "Cannot combine \"flush\" and \"mark\".");
      return false;            
    }
    // check invalidate: no chosen strategy means default -- "flush"
    if (!root.incrementalChangeFlush && !root.incrementalChangeMark) {
      root.incrementalChangeFlush = true;
    }
    // check invalidate: currently not supporting mark startegy -- "mark"
    if (root.incrementalChangeMark) {
      System.err.println("error: Unsupported incremental evaluation configuration: " +
          "\"mark\".");
      return false;            
    }

    // check propagation: only one strategy at a time
    if (root.incrementalPropFull && root.incrementalPropLimit) {
      System.err.println("error: Conflict in incremental evaluation configuration. " +
          "Cannot combine \"full\" and \"limit\".");
      return false;                    
    }
    // check propagation: no chosen strategy means default -- "full"
    if (!root.incrementalPropFull && !root.incrementalPropLimit) {
      root.incrementalPropFull = true;
    }
    // check propagration: currently not supporting limit strategy -- "limit" - we do now
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

  @SuppressWarnings("unused")
  private void checkMem() {
    Runtime runtime = Runtime.getRuntime();
    long total = runtime.totalMemory();
    long free = runtime.freeMemory();
    long use = total-free;
    System.out.println("Before GC: Total " + total + ", use " + use);
    runtime.gc();
    total = runtime.totalMemory();
    free = runtime.freeMemory();
    use = total-free;
    System.out.println("After GC: Total " + total + ", use " + use);
  }

  /**
   * Print help
   * @param cla Command line arguments to list in the help output
   */
  public void printHelp(CommandLineArguments cla) {
    System.err.println("This program reads a number of .jrag, .jadd, and .ast files");
    System.err.println("and creates the nodes in the abstract syntax tree");
    System.err.println();
    System.err.println("The .jrag source files may contain declarations of synthesized ");
    System.err.println("and inherited attributes and their corresponding equations.");
    System.err.println("It may also contain ordinary Java methods and fields.");
    System.err.println();
    System.err.println("Source file syntax can be found at http://jastadd.org");
    System.err.println();
    System.err.println("Options:");
    cla.printHelp();
    System.err.println();
    System.err.println("Arguments:");
    System.err.println("Names of .ast, .jrag, .jadd and .caching source files");
    System.err.println();
    System.err.println("Example: The following command reads and translates files NameAnalysis.jrag");
    System.err.println("and TypeAnalysis.jrag, weaves PrettyPrint.jadd into the abstract syntax tree");
    System.err.println("defined in the grammar Toy.ast.");
    System.err.println("The result is the generated classes for the nodes in the AST that are placed");
    System.err.println("in the package ast.");
    System.err.println();
    System.err.println("java -jar jastadd2.jar --package=ast Toy.ast NameAnalysis.jrag TypeAnalysis.jrag PrettyPrinter.jadd");
  }
}
