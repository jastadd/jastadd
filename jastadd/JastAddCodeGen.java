package jastadd;

import ast.AST.*;

import java.io.*;

import java.util.*;

import jrag.*;
import jrag.AST.SimpleNode;
import jrag.AST.ASTAspectMethodDeclaration;
import jrag.AST.ASTAspectRefineMethodDeclaration;
import jrag.AST.ASTAspectFieldDeclaration;
import jrag.AST.ASTBlock;
import jrag.ClassBodyObject;

aspect JastAddCodeGen {
  public void Grammar.weaveInterfaceIntroductions() {
    for(int i = 0; i < getNumTypeDecl(); i++) {
      if(getTypeDecl(i) instanceof InterfaceDecl) {
        InterfaceDecl d = (InterfaceDecl)getTypeDecl(i);
        String name = d.name();
        //System.out.println("Processing interface " + name);
 
        for(int j = 0; j < getNumTypeDecl(); j++) {
          if(getTypeDecl(j) instanceof ASTDecl) {
            ASTDecl dest = (ASTDecl)getTypeDecl(j);
            if(dest.implementsInterface(name)) {
              //System.out.println("  implemented by " + dest.name());
              for(Iterator iter = d.getClassBodyDeclsItr(); iter.hasNext(); ) {
                ClassBodyObject o = (ClassBodyObject)iter.next();
                if(o.node instanceof ASTAspectMethodDeclaration || o.node instanceof ASTAspectFieldDeclaration) {
                    if(!dest.hasClassBodyDecl(o.signature()))
                        dest.classBodyDecls.add(new ClassBodyObject(o.node/*.fullCopy()*/, o.fileName, o.line, o.getAspectName()));
                }
                else if(o.node instanceof ASTAspectRefineMethodDeclaration) {
                  ClassBodyObject object = new ClassBodyObject(o.node/*.fullCopy()*/, o.fileName, o.line, o.getAspectName());
                  object.refinesAspect = o.refinesAspect;
                    dest.classBodyDecls.add(object);
                }
                else if(o.node instanceof ASTBlock) {
                  dest.classBodyDecls.add(new ClassBodyObject(o.node/*.fullCopy()*/, o.fileName, o.line, o.getAspectName()));
                }
              }
              for(Iterator iter = d.refinedClassBodyDecls.iterator(); iter.hasNext(); ) {
                  ClassBodyObject o = (ClassBodyObject)iter.next();
                  if(o.node instanceof ASTAspectMethodDeclaration || o.node instanceof ASTAspectFieldDeclaration) {
                      if(!dest.hasClassBodyDecl(o.signature()))
                          dest.refinedClassBodyDecls.add(new ClassBodyObject(o.node/*.fullCopy()*/, o.fileName, o.line, o.getAspectName()));
                  }
                  else if(o.node instanceof ASTAspectRefineMethodDeclaration) {
                    ClassBodyObject object = new ClassBodyObject(o.node/*.fullCopy()*/, o.fileName, o.line, o.getAspectName());
                  object.refinesAspect = o.refinesAspect;
                    dest.refinedClassBodyDecls.add(object);
                  }
                  else if(o.node instanceof ASTBlock) {
                    dest.classBodyDecls.add(new ClassBodyObject(o.node/*.fullCopy()*/, o.fileName, o.line, o.getAspectName()));
                  }
              }
             
              for(int k = 0; k < d.getNumSynDecl(); k++) {
                //System.out.println("    adding syndecl " + d.getSynDecl(k).signature());
                dest.addSynDecl((SynDecl)d.getSynDecl(k).fullCopy());
              }
             
              for(int k = 0; k < d.getNumSynEq(); k++) {
                //System.out.println("    adding syneq " + d.getSynEq(k).signature());
                dest.addSynEq((SynEq)d.getSynEq(k).fullCopy());
              }
             
              for(int k = 0; k < d.getNumInhDecl(); k++) {
                //System.out.println("    adding inhdecl " + d.getInhDecl(k).signature());
                dest.addInhDecl((InhDecl)d.getInhDecl(k).fullCopy());
              }
             
              for(int k = 0; k < d.getNumInhEq(); k++) {
                //System.out.println("    adding inheq " + d.getInhEq(k).signature());
                dest.addInhEq((InhEq)d.getInhEq(k).fullCopy());
              }
              for(int k = 0; k < d.getNumCollDecl(); k++) {
                dest.addCollDecl((CollDecl)d.getCollDecl(k).fullCopy());
              }
            }
          }
        }
      }
    }
  }

  public boolean ASTDecl.hasClassBodyDecl(String signature) {
    for(Iterator iter = getClassBodyDeclsItr(); iter.hasNext(); ) {
      ClassBodyObject o = (ClassBodyObject)iter.next();
      if(o.signature().equals(signature))
        return true;
    }
    return false;
  }

  public void Grammar.jastAddGen(File outputDir, String grammarName, String packageName, boolean publicModifier) {
    if(packageName != null && !packageName.equals("")) {
      File dir = new File(outputDir, packageName.replace('.', File.separatorChar));
      dir.mkdirs();
    }

    for(int i = 0; i < getNumTypeDecl(); i++) {
      getTypeDecl(i).jastAddGen(outputDir, grammarName, packageName, publicModifier);
    }
  }

  public String TypeDecl.modifiers() {
    if(modifiers == null || modifiers.equals(""))
      return "public ";
    else
      return modifiers.replaceAll("static ", "");
  }

  public String TypeDecl.typeParameters = "";

  public String TypeDecl.typeDeclarationString() {
    return "";
  }
  public String ClassDecl.typeDeclarationString() {
    String s = interfacesString();
    if(s.equals(""))
      return modifiers() + "class " + getIdDecl().getID() + typeParameters + " extends " + extendsName + " {";
    else
      return modifiers() + "class " + getIdDecl().getID() + typeParameters + " extends " + extendsName + " implements " +
        s + " {";
  }
  public String InterfaceDecl.typeDeclarationString() {
    String s = interfacesString();
    if(s.equals(""))
      return modifiers() + "interface " + getIdDecl().getID() + typeParameters + " {";
    else
      return modifiers() + "interface " + getIdDecl().getID() + typeParameters + " extends " + s + " {";
  }

  public String TypeDecl.classComment() {
    if(doxygen)
      return doxygenComment();
    else
      return javadocComment();
  }

  public abstract String TypeDecl.javadocTag();
  public String ClassDecl.javadocTag() { return "@ast class"; }
  public String InterfaceDecl.javadocTag() { return "@ast interface"; }
  public String ASTDecl.javadocTag() { return "@ast node"; }

  public String TypeDecl.javadocComment() {
    String comment = getComment();
    if (comment == null)
      return augmentClassComment("/**\n */");
    comment.trim();
    if (comment.length() < 5)
      return augmentClassComment("/**\n */");

    int end = comment.lastIndexOf("*/");
    int start = end == -1 ? -1 : comment.lastIndexOf("/**", end);

    if (start != -1)
      return augmentClassComment(comment.substring(start, end+2));
    else {
      return "\n" + augmentClassComment("/**\n */");
    }
  }

  public String TypeDecl.augmentClassComment(String comment) {
    StringBuffer sb = new StringBuffer();
    sb.append(comment.substring(0, comment.length()-2));
    sb.append("* " + javadocTag() + "\n");
    sb.append(" * @declaredat " + getFileName() + ":" + getStartLine() + "\n");
    sb.append(" */\n");
    return sb.toString();
  }

  public String TypeDecl.doxygenComment() {
    String extraString = "#LINE# \\file " + getFileName() + "\n#LINE# \\brief Declared in " + getFileName() + " at line " + getStartLine();
    String s = getComment();
    if(s != null)
      s = s.trim();
    else 
      s = "";
    if(s.length() == 0) {
      s = "/**\n * " + getIdDecl().getID() +"\n" + extraString.replaceAll("#LINE#", " * ") + "\n */\n";
    }
    else {
      int index = s.lastIndexOf("*/");
      if(index == -1) {
        s = s + "\n" + extraString.replaceAll("#LINE#", " // ") + "\n";
      }
      else {
        s = s.substring(0, index) + "\n" + extraString.replaceAll("#LINE#", " * ") + "\n" + s.substring(index, s.length());
      }
    }
    return s + "\n";
  }
    
  public void TypeDecl.jastAddGen(File outputDir, String grammarName, String packageName, boolean publicModifier) {
  }

  public void InterfaceDecl.jastAddGen(File outputDir, String grammarName, String packageName, boolean publicModifier) {
    File file = null;
    try {
      if(packageName != null && !packageName.equals("")) {
        file = new File(outputDir, packageName.replace('.', File.separatorChar) + File.separator + name() + ".java");
      }
      else {
        file = new File(outputDir, name() + ".java");
      }
      PrintStream stream = new PrintStream(new FileOutputStream(file));
      if(license != null && license.length() > 0)
        stream.println(license);

      if(packageName != null && !packageName.equals("")) {
        stream.println("package " + packageName + ";\n");
      }

      stream.print(env().genImportsList());
      stream.print(classComment());
      stream.println(typeDeclarationString());

      /*
      StringBuffer buf = new StringBuffer();
      for(Iterator iter = getClassBodyDeclsItr(); iter.hasNext(); ) {
        ClassBodyObject o = (ClassBodyObject)iter.next();
        jrag.AST.SimpleNode n = o.node;
        
        if(!(n instanceof ASTAspectMethodDeclaration) && !(n instanceof ASTAspectFieldDeclaration)) {
          buf.append("    // Declared in " + o.fileName + " at line " + o.line + "\n");
          n.unparseClassBodyDeclaration(buf, name(), false); //  Fix AspectJ
          buf.append("\n\n");
        }
      }
      stream.println(buf.toString());
      */

      stream.print(genMembers());
      stream.print(genAbstractSyns());
      //stream.print(genSynEquations());
      stream.print(genInhDeclarations());
      //stream.print(genInhEquations());
      stream.print(genInterfaceCollections());
      
      stream.println("}");
      stream.close();
    } catch (FileNotFoundException f) {
      System.err.println("Could not create file " + file.getName() + " in " + file.getParent());
      System.exit(1);
    }
  }
  
  public void ClassDecl.jastAddGen(File outputDir, String grammarName, String packageName, boolean publicModifier) {
    File file = null;
    try {
      if(packageName != null && !packageName.equals("")) {
        file = new File(outputDir, packageName.replace('.', File.separatorChar) + File.separator + name() + ".java");
      }
      else {
        file = new File(outputDir, name() + ".java");
      }
      PrintStream stream = new PrintStream(new FileOutputStream(file));
      if(license != null && license.length() > 0)
        stream.println(license);

      if(packageName != null && !packageName.equals("")) {
        stream.println("package " + packageName + ";\n");
      }

      stream.print(env().genImportsList());
      stream.print(classComment());
      stream.println(typeDeclarationString());
      StringBuffer buf = new StringBuffer();
      for(Iterator iter = getClassBodyDeclsItr(); iter.hasNext(); ) {
        ClassBodyObject o = (ClassBodyObject)iter.next();
        jrag.AST.SimpleNode n = o.node;
        
        //buf.append("    // Declared in " + o.fileName + " at line " + o.line + "\n");
        //if(!o.comments.equals(""))
          //buf.append(o.comments + " ");
        buf.append(o.modifiers);
        n.unparseClassBodyDeclaration(buf, name(), false); //  Fix AspectJ
        buf.append("\n\n");
      }
      stream.println(buf.toString());

      //stream.print(genMembers());
      stream.print(genAbstractSyns());
      //stream.print(genSynEquations());
      stream.print(genInhDeclarations());
      //stream.print(genInhEquations());
      
      stream.println("}");
      stream.close();
    } catch (FileNotFoundException f) {
      System.err.println("Could not create file " + file.getName() + " in " + file.getParent());
      System.exit(1);
    }
  }


  public void ASTDecl.jastAddGen(File outputDir, String grammarName, String packageName, boolean publicModifier) {
    File file = null;
    try {
      if(packageName != null && !packageName.equals("")) {
        file = new File(outputDir, packageName.replace('.', File.separatorChar) + File.separator + name() + ".java");
      }
      else {
        file = new File(outputDir, name() + ".java");
      }
      PrintStream stream = new PrintStream(new FileOutputStream(file));
      if(license != null && license.length() > 0)
        stream.println(license);

      if(packageName != null && !packageName.equals("")) {
        stream.println("package " + packageName + ";\n");
      }

      stream.print(env().genImportsList());

      if(name().equals("ASTNode")) {
        stream.print(JastAdd.getVersionInfo());
        
        if(doxygen) {
          for(Iterator iter = env().aspectMap.entrySet().iterator(); iter.hasNext(); ) {
            java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();
            String key = (String)entry.getKey();
            String s = (String)entry.getValue();
            if(s == null)
              s = "";
            else
              s = s.trim();
            String extraString = "#LINE# \\defgroup aspect_" + key + " Aspect: " + key;
            if(s.length() == 0) {
              s = "/**\n" + extraString.replaceAll("#LINE#", " * ") + "\n */\n";
            }
            else {
              int index = s.indexOf("/*");
              int offset = index + 2;
              while(offset < s.length() && s.charAt(offset) == '*')
                offset++;
              if(index == -1) {
                s = extraString.replaceAll("#LINE#", " // ") + "\n" + s;
              }
              else {
                s = s.substring(0, index) + "/**\n" + extraString.replaceAll("#LINE#", " * ") + "\n" + s.substring(offset, s.length());
              }
            }
            stream.print(s + "\n");
          }
        }
      }
    
      stream.print(classComment());
      stream.print("public ");
      if(hasAbstract()) {
        stream.print("abstract ");
      }
      stream.print("class " + name());
      if(ASTNode.java5 && (name().equals("Opt") || name().equals("List") || name().equals("ASTNode")))
        stream.print("<T extends ASTNode>");
      if(ASTNode.jjtree && name().equals("ASTNode")) {
        stream.print(" extends SimpleNode ");
      }
      else if(ASTNode.beaver && name().equals("ASTNode")) {
        stream.print(" extends beaver.Symbol ");
      }
      else if(hasSuperClass()) {
        stream.print(" extends ");
        String name = getSuperClass().name();
        if(ASTNode.java5) {
          if(name().equals("List") || name().equals("Opt"))
            name = name + "<T>";
          else if(name.equals("ASTNode"))
            name = name + "<ASTNode>";
        }
        stream.print(name);
      }
      stream.print(jastAddImplementsList());
      stream.println(" {");

      java.io.PrintWriter writer = new java.io.PrintWriter(stream);
      jjtGenFlushCache(writer);
      jjtGenCloneNode(writer, grammarName, ASTNode.jjtree, ASTNode.rewriteEnabled);
      writer.flush();

      /*
       // Now done before refinements
      jjtGen(new java.io.PrintWriter(stream), grammarName, JastAdd.jjtree, JastAdd.rewriteEnabled);
      int j = 0;
      for(Iterator iter = getComponents(); iter.hasNext(); ) {
        Components c = (Components)iter.next();
        c.jaddGen(stream, j, publicModifier, name());
        if(!(c instanceof TokenComponent)) {
          j++;
        }
      }
      */
      jastAddAttributes(stream);
      stream.println("}");
      stream.close();
    } catch (FileNotFoundException f) {
      System.err.println("Could not create file " + file.getName() + " in " + file.getParent());
      System.exit(1);
    }
  }

  public String ASTDecl.jastAddImplementsList() {
    StringBuffer buf = new StringBuffer();
    buf.append(" implements Cloneable");
    
    if(ASTNode.parentInterface) {
      for(Iterator iter = inhAttrSet(); iter.hasNext(); ) {
        buf.append(", Defines_" + (String)iter.next());
      }
    }
      
    for(Iterator iter = implementsList.iterator(); iter.hasNext(); ) {
      buf.append(", " + ((SimpleNode)iter.next()).unparse());
    }
    if(name().equals("ASTNode") && ASTNode.java5)
      buf.append(", Iterable<T>");
    String s = buf.toString();
    if(ASTNode.j2me) {
      if(s.equals(" implements Cloneable"))
        s = "";
      else 
        s = s.replace("Cloneable, ", "");
    }
    return s;
  }

  public String TypeDecl.interfacesString() {
    StringBuffer s = new StringBuffer();
    Iterator iter = implementsList.iterator();
    if(iter.hasNext()) {
      s.append(((SimpleNode)iter.next()).unparse());
      while(iter.hasNext()) {
        s.append(", " + ((SimpleNode)iter.next()).unparse());
      }
    }
    return s.toString();
  }
  
  public void ASTDecl.jastAddAttributes(PrintStream s) {
    s.print(genMembers());
    s.print(genAbstractSyns());
    s.print(genSynEquations());
    s.print(genInhDeclarations());
    s.print(genInhEquations());
    if(ASTNode.rewriteEnabled)
      s.print(genRewrites());
    s.print(genCollDecls());
    s.print(genCollContributions());
    emitInhEqSignatures(s);
  }

}

  

