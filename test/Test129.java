package test;

import java.util.*;
import test.ast.*;

public class Test129 {

  public static void main(String[] args) {

    System.gc();

    // construct base AST
    B b = new B(new D("a"));
    A a = new A(b, "b");

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

    System.out.println("\n## after rewrites: ");
    printOutNode(a, "a");


    // trigger change propagation
    a.setName("b");

    System.gc();

    System.out.println("\n## after setName: ");
    printOutNode(a, "a");
  
  }

   public static void printOutNode(ASTNode node, String prefix) {
      System.out.println("node: " + prefix + "=" + str(node));
      node.dumpDependencies();
      node.dumpCachedValues();
      System.out.println("node: " + prefix + ".parent=" + str(node.getParent()));
      for (int k = 0; k < node.getNumChildNoTransform(); k++) {
        printOutNode(node.getChildNoTransform(k), prefix + "/child[" + k + "]");
        System.out.print("node: " + prefix + ".init[" + k + "]=");
        if (node.init_children != null && node.init_children[k] != null) {
          System.out.println(str(node.init_children[k]));
          System.out.println("node: " + prefix + ".init[" + k + "].parent=" + str(node.init_children[k].getParent()));
          node.init_children[k].dumpDependencies();
          node.init_children[k].dumpCachedValues();
          for (int i = 0; i < node.init_children[k].getNumChildNoTransform(); i++) {
            System.out.println("node: " + prefix + ".init[" + k + "]/child[" + i + "]=" + 
        		str(node.init_children[k].getChildNoTransform(i)));
            System.out.println("node: " + prefix + ".init[" + k + "]/child[" + i + "].parent=" + 
        		str(node.init_children[k].getChildNoTransform(i).getParent()));

          } 
        } else System.out.println("null");
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
