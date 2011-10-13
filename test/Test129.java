package test;

import java.util.*;
import test.ast.*;

public class Test129 {

  public static void main(String[] args) {

    D d = new D("a");
    B b = new B(d);
    A a = new A(b, "b");

//    System.out.println("## start: ");
//    printOutNode(a);

    // Activate rewrite
    C c = (C)a.getB();
    E e = (E)c.getD();

//    System.out.println("## after access: ");
//    printOutNode(a);
//    printOutNode(b);
//    printOutNode(c);

    System.out.println("-- Dependencies/Cache after rewrites:");
    System.out.println("a:");
    a.dumpDependencies();
    a.dumpCachedValues();
    System.out.println("b:");
    b.dumpDependencies();
    b.dumpCachedValues();
    System.out.println("c:");
    c.dumpDependencies();
    c.dumpCachedValues();
    System.out.println("d:");
    d.dumpDependencies();
    d.dumpCachedValues();
    System.out.println("e:");
    e.dumpDependencies();
    e.dumpCachedValues();

    // Change
    a.setName("b");

//    System.out.println("## after setName: ");
//    printOutNode(a);

  
    System.out.println("-- Dependencies/Cache after setName:");
    System.out.println("a:");
    a.dumpDependencies();
    a.dumpCachedValues();
    System.out.println("b:");
    b.dumpDependencies();
    b.dumpCachedValues();
    System.out.println("c:");
    c.dumpDependencies();
    c.dumpCachedValues();
    System.out.println("d:");
    d.dumpDependencies();
    d.dumpCachedValues();
    System.out.println("e:");
    e.dumpDependencies();
    e.dumpCachedValues();


  
  }

/*
    public static void printOutNode(ASTNode node) {
      System.out.println("\tnode=" + node);
      for (int k = 0; k < node.getNumChildNoTransform(); k++) {
        System.out.println("\tnode/child[" + k + "]=" + node.getChildNoTransform(k));
        for (int i = 0; i < node.getChildNoTransform(k).getNumChildNoTransform(); i++) {
          System.out.println("\tnode/child[" + k + "]/child[" + i + "]=" + node.getChildNoTransform(k).getChildNoTransform(i));
        }
      }
      for (int k = 0; k < node.getNumChildNoTransform(); k++) {
        System.out.print("\tnode.inital[" + k + "]=");
        if (node.init_children != null && node.init_children[k] != null) {
          System.out.println(node.init_children[k]);
          for (int i = 0; i < node.init_children[k].getNumChildNoTransform(); i++) {
            System.out.println("\tnode.initial[" + k + "]/child[" + i + "]=" + node.init_children[k].getChildNoTransform(i));
          } 
        } else System.out.println("null");
      }
      for (int k = 0; k < node.getNumChildNoTransform(); k++) {
        System.out.print("\tnode.rewritten[" + k + "]=");
        if (node.rewritten_children != null) {
          System.out.println(node.rewritten_children[k]);
        } else System.out.println("false");
      }
    }
*/
}
