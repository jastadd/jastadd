package jrag;

/**
   Represents an association between a class body of a jrag file
   and the name of the jrag file where it was found.
   The file name is used to generate meaningful error messages.
*/

import jrag.AST.SimpleNode;
import java.io.File;

public class ClassBodyObject {
    public String fileName;
    public int line;
    public SimpleNode node;
    public String refinesAspect;
    public String replaceAspect;
    public String comments;
    public String aspectName;
    
    public ClassBodyObject(SimpleNode node, String fileName, int line, String comments, String aspectName) {
      this.fileName = fileName;
      this.line = line;
      this.node = node;
      this.comments = comments;
      this.aspectName = aspectName;
    }
    public ClassBodyObject(SimpleNode node, String fileName, int line, String aspectName) {
      this(node, fileName, line, "", aspectName);
    }

    public String signature() {
      String s = node.signature().replace('.', '_');
      s = s.replace('<', '_');
      s = s.replace('>', '_');
      return s;
    }

    public String getFileName() { return fileName; }
    public int getStartLine() { return line; }

    public String getAspectName() { return aspectName; }

    public String legacyAspectName() {
      String name = fileName;
      if(name.endsWith(".jrag"))
        name = name.substring(0, name.length() - 5);
      else if(name.endsWith(".jadd"))
        name = name.substring(0, name.length() - 5);
      else if(name.endsWith(".ast"))
        name = name.substring(0, name.length() - 4);
      String pattern = File.separator.equals("\\") ? pattern = "\\\\" : File.separator;
      String[] names = name.split(pattern);
      return names[names.length-1];
    }
    public String aspectName() {
      return getAspectName();
    }

}
