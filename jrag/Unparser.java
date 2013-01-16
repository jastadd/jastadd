package jrag;

import java.util.Set;

import jrag.AST.SimpleNode;
import jrag.AST.Token;

public class Unparser {
  public static void unparseImport(SimpleNode node, Set imports) {
      Token t = new Token();
      t.next = node.firstToken;

      StringBuffer buf = new StringBuffer(64);

      while(t != null && t != node.lastToken) {
        t = t.next;
        if (t.specialToken != null)
          buf.append(' ');
        buf.append(Util.addUnicodeEscapes(t.image));
      }

      imports.add(buf.toString().trim());
  }

  public static void unparseComment(SimpleNode node, StringBuffer buf) {
        Token tt = node.firstToken.specialToken;
        if (tt != null) {
            while (tt.specialToken != null) tt = tt.specialToken;
            while (tt != null) {
                buf.append(Util.addUnicodeEscapes(tt.image));
                tt = tt.next;
            }
        }
  }

  public static String unparseComment(SimpleNode node) {
    StringBuffer buf = new StringBuffer();
    unparseComment(node, buf);
    return buf.toString();
  }

}
