package jastadd;

import ast.AST.*;

import jrag.AST.*;

import java.util.*;
import java.io.*;

import jrag.*;

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
  public static String getVersionInfo() {
    return "\n// Generated with " + getVersionString() + "\n\n";
  }

  protected java.util.List files;
  protected java.util.List cacheFiles;

  protected Grammar root; // root of the ast for the ast-grammar file
  protected String pack;
  protected File outputDir;
  protected String grammar;
  protected boolean publicModifier;

  public static void main(String[] args) {
    new JastAdd().compile(args);
    Runtime.getRuntime().gc();
  }

  public void compile(String[] args) {
    try {
      files = new ArrayList();
      cacheFiles = new ArrayList();
      if (readArgs(args)) {
        System.exit(1);
      }

      long time = System.currentTimeMillis();

      root = new Grammar();
      root.abstractAncestors();

      // Parse ast-grammar
      //System.out.println("parsing grammars");
      Collection errors = new ArrayList();
      for(Iterator iter = files.iterator(); iter.hasNext(); ) {
        String fileName = (String)iter.next();
        if(fileName.endsWith(".ast")) {
          try {
            Ast parser = new Ast(new FileInputStream(fileName));
            parser.fileName = fileName;
            Grammar g = parser.Grammar();
            for(int i = 0; i < g.getNumTypeDecl(); i++) {
              root.addTypeDecl(g.getTypeDecl(i));
            }
            for(Iterator errorIter = parser.getErrors(); errorIter.hasNext(); ) {
              String[] s = ((String)errorIter.next()).split(";");
              errors.add("Syntax error in " + fileName + " at line " + s[0] + ", column " + s[1]);
            }

          } catch (ast.AST.TokenMgrError e) {
            System.out.println("Lexical error in " + fileName + ": " + e.getMessage());
            System.exit(1);
          } catch (ast.AST.ParseException e) {
            // Exceptions actually caught by error recovery in parser
          } catch (FileNotFoundException e) {
            System.out.println("File error: Abstract syntax grammar file " + fileName + " not found");
            System.exit(1);
          }
        }
      }

      if(!errors.isEmpty()) {
        for(Iterator iter = errors.iterator(); iter.hasNext(); )
          System.out.println(iter.next());
        System.exit(1);
      }

      long astParseTime = System.currentTimeMillis() - time;

      String astErrors = root.astErrors();

      long astErrorTime = System.currentTimeMillis() - time - astParseTime;

      if(!astErrors.equals("")) {
        System.out.println("Semantic error:");
        System.out.println(astErrors);
        System.exit(1);
      }

      ASTNode.resetGlobalErrors();

      {
        //System.out.println("generating ASTNode");
        java.io.StringWriter writer = new java.io.StringWriter();
        root.jjtGenASTNode$State(new PrintWriter(writer), grammar, ASTNode.jjtree, ASTNode.rewriteEnabled);

        jrag.AST.JragParser jp = new jrag.AST.JragParser(new java.io.StringReader(writer.toString()));
        jp.root = root;
        jp.setFileName("ASTNode");
        jp.className = "ASTNode";
        jp.pushTopLevelOrAspect(true);
        try {
          while(true)
            jp.AspectBodyDeclaration();
        } catch (Exception e) {
          String s = e.getMessage();
        }
        jp.popTopLevelOrAspect();
      }

      // Parse all jrag files and build tables
      for(Iterator iter = files.iterator(); iter.hasNext(); ) {
        String fileName = (String)iter.next();
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
            System.out.println(msg.toString());
            System.exit(1);
          } catch (FileNotFoundException e) {
            System.out.println("File error: Aspect file " + fileName + " not found");
            System.exit(1);
          } catch (Throwable e) {
            System.err.println("Exception occurred while parsing " + fileName);
            e.printStackTrace();
          }
        }
      }

      long jragParseTime = System.currentTimeMillis() - time - astErrorTime;

      root.weaveInterfaceIntroductions();

      //System.out.println("weaving aspect and attribute definitions");
      for(int i = 0; i < root.getNumTypeDecl(); i++) {
        if(root.getTypeDecl(i) instanceof ASTDecl) {
          ASTDecl decl = (ASTDecl)root.getTypeDecl(i);
          java.io.StringWriter writer = new java.io.StringWriter();
          decl.jjtGen(new PrintWriter(writer), grammar, ASTNode.jjtree, ASTNode.rewriteEnabled);

          jrag.AST.JragParser jp = new jrag.AST.JragParser(new java.io.StringReader(writer.toString()));
          jp.root = root;
          jp.setFileName("");
          jp.className = "ASTNode";
          jp.pushTopLevelOrAspect(true);
          try {
            while(true)
              jp.AspectBodyDeclaration();
          } catch (Exception e) {
            String s = e.getMessage();
          }
          jp.popTopLevelOrAspect();



          int j = 0;
          for(Iterator iter = decl.getComponents(); iter.hasNext(); ) {
            Components c = (Components)iter.next();
            if(c instanceof TokenComponent) {
              c.jaddGen(null, j, publicModifier, decl);
            }
            else {
              c.jaddGen(null, j, publicModifier, decl);
              j++;
            }
          }
        }
      }

      //System.out.println("processing refinements");
      root.processRefinements();


      for(Iterator iter = cacheFiles.iterator(); iter.hasNext(); ) {
        String fileName = (String)iter.next();
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
          System.out.println(msg.toString());
          System.exit(1);
        } catch (FileNotFoundException e) {
          System.out.println("File error: Aspect file " + fileName + " not found");
          System.exit(1);
        }
      }

      //System.out.println("weaving collection attributes");
      root.weaveCollectionAttributes();

      String err = root.errors();
      if(!err.equals("") || !ASTNode.globalErrors.equals("")) {
        System.out.println("Semantic errors: \n" + err + ASTNode.globalErrors);
        System.exit(1);
      }

      long jragErrorTime = System.currentTimeMillis() - time - jragParseTime;

      root.jastAddGen(outputDir, grammar, pack, publicModifier);
      try {
        root.createInterfaces(outputDir, pack);
      } catch (FileNotFoundException e) {
        System.out.println("File error: Output directory " + outputDir + " does not exist or is write protected");
        System.exit(1);
      }
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
    CommandLineArguments cla = new CommandLineArguments();
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
    Option noStatic = new Option("noStatic", "");
    Option deterministic = new Option("deterministic", "");
    Option j2me = new Option("j2me", "generate for J2ME");
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

    // set default values
    grammarOption.setDefaultValue("Unknown");
    defaultMap.setDefaultValue("new java.util.HashMap(4)");
    defaultSet.setDefaultValue("new java.util.HashSet(4)");
    outputDirOption.setValue(System.getProperty("user.dir"));
    packageOption.setDefaultValue("");

    cla.addOption(jjtree);
    cla.addOption(grammarOption);
    cla.addOption(defaultMap);
    cla.addOption(defaultSet);
    cla.addOption(lazyMaps);
    cla.addOption(noLazyMaps);
    cla.addOption(privateOption);
    cla.addOption(rewrite);
    cla.addOption(beaver);
    cla.addOption(noVisitCheck);
    cla.addOption(noCacheCycle);
    cla.addOption(noComponentCheck);
    cla.addOption(componentCheck);
    cla.addOption(noInhEqCheck);
    cla.addOption(suppressWarnings);
    cla.addOption(parentInterface);
    cla.addOption(refineLegacy);
    cla.addOption(noRefineLegacy);
    cla.addOption(stagedRewrites);
    cla.addOption(doc);
    cla.addOption(license);
    cla.addOption(java1_4);
    cla.addOption(debug);
    cla.addOption(synch);
    cla.addOption(noStatic);
    cla.addOption(deterministic);
    cla.addOption(j2me);
    cla.addOption(outputDirOption);
    cla.addOption(tracing);
    cla.addOption(cacheAll);
    cla.addOption(noCaching);
    cla.addOption(doxygen);
    cla.addOption(cacheNone);
    cla.addOption(cacheImplicit);
    cla.addOption(ignoreLazy);
    cla.addOption(packageOption);
    cla.addOption(version);
    cla.addOption(help);
    cla.addOption(printNonStandardOptions);

    // parse the argument list
    cla.match(args);

    if (printNonStandardOptions.matched()) {
      System.out.println("Non-standard options:");
      cla.printNonStandardOptions();
      System.exit(0);
    }

    if (jjtree.matched() && !grammarOption.matched()) {
      System.err.println("Missing grammar option. It is required in jjtree-mode!");
      return true;
    }

    ASTNode.jjtree = jjtree.matched();
    grammar = grammarOption.value();

    ASTNode.createDefaultMap = defaultMap.value();
    ASTNode.createDefaultSet = defaultSet.value();

    ASTNode.lazyMaps = !noLazyMaps.matched();

    publicModifier = !privateOption.matched();

    ASTNode.rewriteEnabled = rewrite.matched();
    ASTNode.beaver = beaver.matched();
    ASTNode.visitCheckEnabled = !noVisitCheck.matched();
    ASTNode.cacheCycle = !noCacheCycle.matched();
    ASTNode.componentCheck = componentCheck.matched();
    ASTNode.noInhEqCheck = noInhEqCheck.matched();

    ASTNode.suppressWarnings = suppressWarnings.matched();
    ASTNode.parentInterface = parentInterface.matched();

    ASTNode.refineLegacy = !noRefineLegacy.matched();

    ASTNode.stagedRewrites = stagedRewrites.matched();

    ASTNode.doc = doc.matched();

    ASTNode.license = "";
    if(license.matched()) {
      String fileName = license.value();
      try {
        if(fileName != null) {
          ASTNode.license = readFile(fileName);
        }
      } catch (java.io.IOException e) {
        System.err.println("Error loading license file " + fileName);
        System.exit(1);
      }
    }

    ASTNode.java5 = !java1_4.matched();

    if (debug.matched()) {
      ASTNode.debugMode = true;
      ASTNode.cycleLimit = 100;
      ASTNode.rewriteLimit = 100;
      ASTNode.visitCheckEnabled = true;
    }

    ASTNode.block = synch.matched();

    ASTNode.noStatic = noStatic.matched();

    ASTNode.deterministic = deterministic.matched();
    if(ASTNode.deterministic) {
      // overrides values set by the defaultMap and defaultSet options
      ASTNode.createDefaultMap = "new java.util.LinkedHashMap(4)";
      ASTNode.createDefaultSet = "new java.util.LinkedHashSet(4)";
    }

    ASTNode.j2me = j2me.matched();

    String outputDirName = outputDirOption.value();
    outputDir = new File(outputDirName);

    if(!outputDir.exists()) {
      System.out.println("Output directory does not exist");
      System.exit(1);
    }
    if(!outputDir.isDirectory()) {
      System.out.println("Output directory is not a directory");
      System.exit(1);
    }
    if(!outputDir.canWrite()) {
      System.out.println("Output directory is write protected");
      System.exit(1);
    }

    if(ASTNode.j2me) {
      if(ASTNode.deterministic) {
        System.out.println("J2ME can not be used in deterministic mode");
        System.exit(1);
      }
      if(ASTNode.debugMode) {
        System.out.println("J2ME can not be used in debug mode");
        System.exit(1);
      }
      if(ASTNode.java5) {
        System.out.println("J2ME can not be used in java5 mode");
        System.exit(1);
      }
      ASTNode.createDefaultMap = "new java.util.Hashtable(4)";
      ASTNode.createDefaultSet = "new ASTNode$State.HashtableBasedSet(4)";
      ASTNode.typeDefaultMap = "java.util.Hashtable";
      ASTNode.typeDefaultSet = "ASTNode$State.HashtableBasedSet";
      ASTNode.createContributorSet = "new ASTNode$State.HashtableBasedSet(4)";
    }

    ASTNode.tracing = tracing.matched();
    ASTNode.cacheAll = cacheAll.matched();
    ASTNode.noCaching = noCaching.matched();
    ASTNode.doxygen = doxygen.matched();

    // EMMA_2009-11-16: Handle new flags
    ASTNode.cacheNone = cacheNone.matched();
    ASTNode.cacheImplicit = cacheImplicit.matched();
    ASTNode.ignoreLazy = ignoreLazy.matched();

    pack = packageOption.value().replace('/', '.');
    int n = cla.getNumOperands();
    for (int k=0; k<n; k++) {
      String fileName = cla.getOperand(k);
      if(fileName.endsWith(".ast") || fileName.endsWith(".jrag") || fileName.endsWith(".jadd")) {
        files.add(fileName);
      }
      else if(fileName.endsWith(".caching")) {
        cacheFiles.add(fileName);
      }
      else {
        System.out.println("FileError: " + fileName + " is of unknown file type");
        return true;
      }
    }

    if (version.matched()) {
      // just print version and exit
      System.out.println(getVersionString());
      System.exit(0);
    }

    if (help.matched() || files.isEmpty()) {
      // just print version and exit
      System.out.println(getLongVersionString() + "\n");
      printHelp(cla);
      System.exit(0);
    }
    return false;
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
    Print help
    */
  public void printHelp(CommandLineArguments cla) {
    System.out.println("This program reads a number of .jrag, .jadd, and .ast files");
    System.out.println("and creates the nodes in the abstract syntax tree");
    System.out.println();
    System.out.println("The .jrag source files may contain declarations of synthesized ");
    System.out.println("and inherited attributes and their corresponding equations.");
    System.out.println("It may also contain ordinary Java methods and fields.");
    System.out.println();
    System.out.println("Source file syntax can be found at http://jastadd.org");
    System.out.println();
    System.out.println("Options:");
    cla.printHelp();
    System.out.println();
    System.out.println("Arguments:");
    System.out.println("Names of .ast, .jrag, .jadd and .caching source files");
    System.out.println();
    System.out.println("Example: The following command reads and translates files NameAnalysis.jrag");
    System.out.println("and TypeAnalysis.jrag, weaves PrettyPrint.jadd into the abstract syntax tree");
    System.out.println("defined in the grammar Toy.ast.");
    System.out.println("The result is the generated classes for the nodes in the AST that are placed");
    System.out.println("in the package ast.");
    System.out.println();
    System.out.println("java -jar jastadd2.jar --package=ast Toy.ast NameAnalysis.jrag TypeAnalysis.jrag PrettyPrinter.jadd");
  }
}
