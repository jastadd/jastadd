package test;

import java.util.*;
import test.ast.*;

public class Test128 {

  public static void main(String[] args) {

    Runtime runtime = Runtime.getRuntime();
    long used = runtime.totalMemory() - runtime.freeMemory();
    System.out.println("  -> [used=" + used/1000 + "kb]"); 

    // create initial AST
    A a = new A(new B(), "b", new D());
    long usedNow = runtime.totalMemory()-runtime.freeMemory();
    long mem = (usedNow - used)/1000;
    System.gc();
    System.out.println("\n## start: ");
    printOutNode(a, "a");
    System.out.println("  -> [mem=" + mem +"kb][used=" + usedNow/1000 + "kb]"); 


    // activate rewrite    
    used = runtime.totalMemory() - runtime.freeMemory();
    a.getB();
    usedNow = runtime.totalMemory()-runtime.freeMemory();
    mem = (usedNow - used)/1000;
    System.gc();
    System.out.println("\n## after rewrite: ");
    printOutNode(a, "a");
    System.out.println("  -> [mem=" + mem +"kb][used=" + usedNow/1000 + "kb]");
    

    // create link to rewritten child from sibling
    used = runtime.totalMemory() - runtime.freeMemory();
    a.getD().sibling();
    usedNow = runtime.totalMemory()-runtime.freeMemory();
    mem = (usedNow - used)/1000;
    System.gc();
    System.out.println("\n## after sibling: ");
    printOutNode(a, "a");
    System.out.println("  -> [mem=" + mem +"kb][used=" + usedNow/1000 + "kb]");


    // trigger change propagation
    used = runtime.totalMemory() - runtime.freeMemory();
    a.setName("b");
    usedNow = runtime.totalMemory()-runtime.freeMemory();
    mem = (usedNow - used)/1000;
    System.gc();
    System.out.println("\n## after set: ");
	printOutNode(a, "a");
    System.out.println("  -> [mem=" + mem +"kb][used=" + usedNow/1000 + "kb]");
   
  }

   public static void printOutNode(ASTNode node, String prefix) {
      System.out.println("node: " + prefix + "=" + str(node));
      node.dumpDependencies();
      node.dumpCachedValues();
      System.out.println("node: " + prefix + ".parent=" + str(node.getParent()));
      for (int k = 0; k < node.getNumChildNoTransform(); k++) {
        printOutNode(node.getChildNoTransform(k), prefix + "/child[" + k + "]");
        System.out.print("node: " + prefix + ".inital[" + k + "]=");
        if (node.init_children != null && node.init_children[k] != null) {
          System.out.println(str(node.init_children[k]));
          System.out.println("node: " + prefix + ".inital[" + k + "].parent=" + str(node.init_children[k].getParent()));
          node.init_children[k].dumpDependencies();
          node.init_children[k].dumpCachedValues();
          for (int i = 0; i < node.init_children[k].getNumChildNoTransform(); i++) {
            System.out.println("\t" + prefix + ".initial[" + k + "]/child[" + i + "]=" + 
        		str(node.init_children[k].getChildNoTransform(i)));
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
      return node != null ? node.getClass().getName() + "@" + Integer.toHexString(node.hashCode()) : "null";
    }


}
