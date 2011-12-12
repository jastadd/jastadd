package test;

import java.util.*;
import test.ast.*;

public class Test180 {

  public static void main(String[] args) {

    // construction

    B b1Init = new B("a");
    test.ast.List listInit = new test.ast.List();
    listInit.add(b1Init);
    A a = new A(listInit);

    assert(listInit == a.getChildNoTransform(0));
    assert(b1Init == a.getChildNoTransform(0).getChildNoTransform(0));
    
    System.out.println("\n## after construction: ");
    printOutNode(a, "a");

    // access

    test.ast.List listAccess = (test.ast.List)a.getChild(0);
    B b1Access = a.getB(0);

    assert(listAccess == a.getChildNoTransform(0));
    assert(b1Access == a.getChildNoTransform(0).getChildNoTransform(0));

    System.out.println("\n## after access: ");
    printOutNode(a, "a");


    // computation

    C c = b1Access.decl();

    System.out.println("\n## after attribute computation: ");
    printOutNode(a, "a");

System.exit(2);

    System.out.println("## Dependencies/Cache in current after change:");
    a.dumpDependencies();
    a.dumpCachedValues();
    a.getChildNoTransform(0).dumpDependencies();
    a.getChildNoTransform(0).dumpCachedValues();
    a.getChildNoTransform(0).getChildNoTransform(0).dumpDependencies();
    a.getChildNoTransform(0).getChildNoTransform(0).dumpCachedValues();
    a.getChildNoTransform(0).getChildNoTransform(1).dumpDependencies();
    a.getChildNoTransform(0).getChildNoTransform(1).dumpCachedValues();
    System.out.println("#### Dependencies/Cache in previous accessed AST node:");
    b1Access.dumpDependencies();
    b1Access.dumpCachedValues();
    System.out.println("#### Dependencies/Cache in returned node:");
    c.dumpDependencies();
    c.dumpCachedValues();


  }


   public static void printOutNode(ASTNode node, String prefix) {
      System.out.print("node: " + prefix + "=" + str(node));

      if (node != null) {

      System.out.println("  (parent=" + str(node.getParent()) + ")"); 
      // + ",garbage=" + (node.inc_state == ASTNode.inc_GARBAGE)  + ")");

      node.dumpDependencies();
      node.dumpCachedValues();

      for (int k = 0; k < node.getNumChildNoTransform(); k++) {
        printOutNode(node.getChildNoTransform(k), prefix + "/child[" + k + "]");
      }

      for (int k = 0; k < node.getNumChildNoTransform(); k++) {

        if (node.init_children != null) {
            printOutNode(node.init_children[k], prefix + ".init[" + k + "]");
        }
      }

      } else {
        System.out.println();
      }
    }

    public static String relativeNodeID(ASTNode node) {
      ASTNode parent = node.getParent();
      StringBuffer buf = new StringBuffer();
      int index = -1;
      if (parent != null) {
        buf.append(parent.relativeNodeID() + "/");
        index = parent.getIndexOfChild(node);
      }
      buf.append(node.getClass().getSimpleName());
      if (index > -1) {
        buf.append("[" + index + "]");
      }
      return buf.toString();
    }

    public static String str(ASTNode node) {
      return node != null ? 
        node.relativeNodeID()
        //node.getClass().getName() + "@" + Integer.toHexString(node.hashCode()) 
        : "null";
    }

/*
  private static String printHandlerNodes(ASTNode node) {
    StringBuffer buf = new StringBuffer();
    if (node.getParent_handler != null)
      buf.append("parent="+node.getParent_handler.fNode);
    if (node.numChildren_handler != null)
      buf.append(", num="+node.numChildren_handler.fNode);
    for (int i = 0; i < node.getNumChildNoTransform(); i++) {
        if (node.getChildNoTransform(i).getChild_handler != null &&
              node.getChildNoTransform(i).getChild_handler[i] != null)
          buf.append("child(" + i + ")="+node.getChildNoTransform(i).getChild_handler[i].fNode);
    }
    return buf.toString();
  }
*/
}
