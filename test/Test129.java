package test;

import java.util.*;
import test.ast.*;

public class Test129 {

  public static void main(String[] args) {

    System.gc();

    // construct base AST
//    D d = new D("a");
//    B b = new B(d);
//    A a = new A(b, "b");

    A a = new A(new B(new D("a")), "b");


    System.out.println("\n## start: ");
    printOutNode(a, "a");


    // activate rewrites
    a.getB();

/*
    ASTNode node = b.getChildNoTransform(0);
    node.setParent(null);
    node.setChild(null, 0);
    b = null;
*/
    System.gc();
    System.gc();

    System.out.println("\n## after rewrites: ");
    printOutNode(a, "a");
//System.out.println("\n--removed nodes:");
//    printOutNode(b, "b1");
//    printOutNode(d, "d1");


    // trigger change propagation
    a.setName("b");

    System.gc();



    System.out.println("\n## after setName: ");
    printOutNode(a, "a");
  
  }

   public static void printOutNode(ASTNode node, String prefix) {
      System.out.print("node: " + prefix + "=" + str(node));

      if (node != null) {

      System.out.println("  (parent=" + str(node.getParent()) + ",garbage=" + 
        (node.inc_state == ASTNode.inc_GARBAGE)  + ")");

      //node.dumpDependencies();
      //node.dumpCachedValues();



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
        //node.relativeNodeID()
        node.getClass().getName() + "@" + Integer.toHexString(node.hashCode()) 
        : "null";
    }

}
