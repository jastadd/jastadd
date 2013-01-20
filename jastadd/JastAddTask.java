package jastadd;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import java.util.*;
import java.io.*;
import ast.AST.ASTNode;

public class JastAddTask extends Task {
  public JastAddTask() {
    super();
  }
  public void init() {
    super.init();
  }

  private LinkedHashSet files = new LinkedHashSet();
  public void addConfiguredFileSet(FileSet fileset) {
    DirectoryScanner s = fileset.getDirectoryScanner(getProject());
    String[] files = s.getIncludedFiles();
    String baseDir = s.getBasedir().getPath();
    for(int i = 0; i < files.length; i++)
      this.files.add(baseDir + File.separator + files[i]);
  }

  // use jjtree node as base node type, requires the grammar option
  private boolean jjtree = false;
  public void setJjtree(boolean b) { jjtree = b; }

  private String grammar = null;
  public void setGrammar(String g) { grammar = g; }

  // use beaver node as base node type
  private boolean beaver = false;
  public void setBeaver(boolean b) { beaver = b; }

  // make the generated files belong to this package
  private String packageName = null;
  public void setPackage(String name) { packageName = name; }

  // place the generated files in this directory
  private String outdir = null;
  public void setOutdir(String dir) { outdir = dir; }

  // use these datastructures to hold cached attributes
  private String defaultMap = null;
  public void setDefaultMap(String map) { defaultMap = map; }
  private String defaultSet = null;
  public void setDefaultSet(String set) { defaultSet = set; }
  private boolean lazyMaps = true;
  public void setLazyMaps(boolean b) { lazyMaps = b; }

  // generate code for rewrites
  private boolean rewrite = false;
  public void setRewrite(boolean b) { rewrite = b; }

  // generate check for detection of circular evaluation of non circular attributes
  private boolean novisitcheck = false;
  public void setNovisitcheck(boolean b) { novisitcheck = b;}
  public void setVisitCheck(boolean b) { novisitcheck = !b; }

  // generate last cycle cache optimization for circular attributes
  private boolean noCacheCycle = false;
  public void setNoCacheCycle(boolean b) { noCacheCycle = b; }
  public void setCacheCycle(boolean b) { noCacheCycle = !b; }

  // generate strongly connected component optimization for circular attributes
  private boolean noComponentCheck = true; // disabled by default for now
  public void setNoComponentCheck(boolean b) { noComponentCheck = b; }
  public void setComponentCheck(boolean b) { noComponentCheck = !b; }

  // disable check for inherited equations
  private boolean noInhEqCheck = false;
  public void setNoInhEqCheck(boolean b) { noInhEqCheck = b; }

  // suppress warnings when using Java 5
  private boolean suppressWarnings = false;
  public void setSuppressWarnings(boolean b) { suppressWarnings = b; }

  // search equtions for inherited attributes using interfaces
  private boolean parentInterface = false;
  public void setParentInterface(boolean b) { parentInterface = b; }

  // generate javadoc like .html pages for sources
  private boolean doc = false;
  public void setDoc(boolean b) { doc = b; }

  // include the following license file in all generated files
  private String license = null;
  public void setLicense(String license) {
    this.license = license;
  }

  private boolean java14 = false;
  public void setJava14(boolean b) { java14 = b; }

  // generate run-time checks for debugging
  private boolean debug = false;
  public void setDebug(boolean b) { debug = b; }

  private boolean synch = false;
  public void setSynch(boolean b) { synch = b; }

  private boolean noStatic = false;
  public void setNoStatic(boolean b) { noStatic = b; }

  private boolean refineLegacy = true;
  public void setRefineLegacy(boolean b) { refineLegacy = b; }

  private boolean stagedRewrites = false;
  public void setStagedRewrites(boolean b) { stagedRewrites = b; }

  private boolean deterministic = false;
  public void setDeterministic(boolean b) { deterministic = b; }

  private boolean j2me = false;
  public void setj2me(boolean b) { j2me = b; }

  private boolean tracing = false;
  public void setTracing(boolean b) { tracing = b; }

  private boolean cacheAll = false;
  private boolean noCaching = false;
  public void setCacheAll(boolean b) {
    if(b)
      cacheAll = true;
    else
      noCaching = true;
  }

  private boolean doxygen = false;
  public void setDoxygen(boolean b) {
    doxygen = b;
  }

  // EMMA_2009-11-16: Adding a new ant attributes cacheNone to replace noCaching
  private boolean cacheNone = false;
  public void setCacheNone(boolean b) { cacheNone = true; }

  // EMMA_2009-11-16: Adding a new ant attribute cacheImplicit, same direct effect as cacheAll but with 
  // the difference that the cache configuration has higher priority
  private boolean cacheImplicit = false;
  public void setCacheImplicit(boolean b) { cacheImplicit = true; }

  // EMMA_2009-11-16: Adding a new ant attribute ignoreLazy, to remove the imediate need to strip a 
  // JastAdd specification of its lazy keywords in order to experiment with a cache configuration
  private boolean ignoreLazy = false;
  public void setIgnoreLazy(boolean b) { ignoreLazy = true; }

  public void execute() throws BuildException {
    if(jjtree && grammar == null)
      throw new BuildException("JJTree option requires grammar to be set");
    if(jjtree && beaver)
      throw new BuildException("Can not generate AST for both JJTree and Beaver");
    if(files.size() == 0)
      throw new BuildException("JastAdd requires grammar and aspect files");

    StringBuffer name = new StringBuffer();
    if(outdir != null) {
      name.append(outdir);
      if(!outdir.endsWith(File.separator))
        name.append(File.separator);
    }
    if(packageName != null) {
      name.append(packageName.replace('.', File.separatorChar) + File.separator);
    }
    name.append("ASTNode.java");
    File generated = new File(name.toString());
    if(generated.exists()) {
      boolean changed = false;
      for(Iterator iter = files.iterator(); iter.hasNext(); ) {
        String fileName = (String)iter.next();
        File file = new File(fileName);
        if(!file.exists() || file.lastModified() > generated.lastModified())
          changed = true;
      }
      if(!changed) {
        return;
      }
    }
    ArrayList args = new ArrayList();
    if(jjtree) {
      args.add("--jjtree");
      args.add("--grammar=" + grammar);
    }
    if(beaver)              args.add("--beaver");

    if(packageName != null) args.add("--package=" + packageName);
    if(outdir != null)      args.add("--o=" + outdir);

    if(defaultMap != null)  args.add("--defaultMap=" + defaultMap);
    if(defaultSet != null)  args.add("--defaultSet=" + defaultSet);
    if(lazyMaps)            args.add("--lazyMaps");
    else                    args.add("--noLazyMaps");

    if(rewrite)             args.add("--rewrite");
    if(novisitcheck)        args.add("--novisitcheck");
    if(noCacheCycle)        args.add("--noCacheCycle");
    if(noComponentCheck)    args.add("--noComponentCheck");

    if(noInhEqCheck)        args.add("--noInhEqCheck");
    if(suppressWarnings)    args.add("--suppressWarnings");
    if(parentInterface)     args.add("--parentInterface");

    if(doc)     args.add("--doc");
    if(debug)   args.add("--debug");

    if(license != null)     args.add("--license=" + license);

    if(java14) args.add("--java1.4");

    if(synch) args.add("--synch");

    if(noStatic) args.add("--noStatic");

    if(refineLegacy) args.add("--refineLegacy");

    if(stagedRewrites) args.add("--stagedRewrites");

    if(deterministic) args.add("--deterministic");

    if(j2me) args.add("--j2me");

    if(tracing) args.add("--tracing");
    if(cacheAll) {
      args.add("--cacheAll");
    }
    if(noCaching) {
      args.add("--noCaching");
    }
    if(doxygen) {
      args.add("--doxygen");
    }

    // EMMA_2009-11-16: Adding ant task attributes as JastAdd arguments
    if (cacheNone) args.add("--cacheNone");
    if (cacheImplicit) args.add("--cacheImplicit");
    if (ignoreLazy) args.add("--ignoreLazy");

    args.addAll(files);

    int i = 0;
    String[] argsArray = new String[args.size()];
    for(Iterator iter = args.iterator(); iter.hasNext(); i++)
      argsArray[i] = ((String)iter.next()).trim();
    System.err.println("generating node types and weaving aspects");
    JastAdd.main(argsArray);
    System.err.println("done");
  }
}
