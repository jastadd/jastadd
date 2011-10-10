package test;

import java.util.*;
import test.ast.*;

public class Test128 {

  public static void main(String[] args) {

    B b = new B();
    A a = new A(b, "b");
/*
    System.out.println("## start: ");
    System.out.println("\ta=" + a);
    for (int k = 0; k < a.getNumChildNoTransform(); k++) {
      System.out.println("\ta/child[" + k + "]=" + a.getChildNoTransform(k));
      for (int i = 0; i < a.getChildNoTransform(k).getNumChildNoTransform(); i++) {
        System.out.println("\ta/child[" + k + "]/child[" + i + "]=" + a.getChildNoTransform(k).getChildNoTransform(i));
      }
    }
    for (int k = 0; k < a.getNumChildNoTransform(); k++) {
      System.out.print("\ta.initial[" + k + "]=");
      if (a.init_children != null && a.init_children[k] != null) {
        System.out.println(a.init_children[k]);
        for (int i = 0; i < a.init_children[k].getNumChildNoTransform(); i++) 
          System.out.println("\ta.initial[" + k + "]/child[" + i + "]=" + a.init_children[k].getChildNoTransform(i));
      } else System.out.println("null");
    }
*/    

    // Activate rewrite
    C c = (C)a.getB();
/*
    System.out.println("## after rewrite: ");
    System.out.println("\ta=" + a);
    for (int k = 0; k < a.getNumChildNoTransform(); k++) {
      System.out.println("\ta/child[" + k + "]=" + a.getChildNoTransform(k));
      for (int i = 0; i < a.getChildNoTransform(k).getNumChildNoTransform(); i++) {
        System.out.println("\ta/child[" + k + "]/child[" + i + "]=" + a.getChildNoTransform(k).getChildNoTransform(i));
      }
    }
    for (int k = 0; k < a.getNumChildNoTransform(); k++) {
      System.out.print("\ta.initial[" + k + "]=");
      if (a.init_children != null && a.init_children[k] != null) {
        System.out.println(a.init_children[k]);
        for (int i = 0; i < a.init_children[k].getNumChildNoTransform(); i++) 
          System.out.println("\ta.initial[" + k + "]/child[" + i + "]=" + a.init_children[k].getChildNoTransform(i));
      } else System.out.println("null");
    }
*/    
    System.out.println("-- Dependencies/Cache after rewrite:");
    System.out.println("a:");
    a.dumpDependencies();
    a.dumpCachedValues();
    System.out.println("b:");
    b.dumpDependencies();
    b.dumpCachedValues();
    System.out.println("c:");
    c.dumpDependencies();
    c.dumpCachedValues();

    // Change
    a.setName("b");
/*
    System.out.println("## after set: ");
    System.out.println("\ta=" + a);
    for (int k = 0; k < a.getNumChildNoTransform(); k++) {
      System.out.println("\ta/child[" + k + "]=" + a.getChildNoTransform(k));
      for (int i = 0; i < a.getChildNoTransform(k).getNumChildNoTransform(); i++) {
        System.out.println("\ta/child[" + k + "]/child[" + i + "]=" + a.getChildNoTransform(k).getChildNoTransform(i));
      }
    }
    for (int k = 0; k < a.getNumChildNoTransform(); k++) {
      System.out.print("\ta.initial[" + k + "]=");
      if (a.init_children != null && a.init_children[k] != null) {
        System.out.println(a.init_children[k]);
        for (int i = 0; i < a.init_children[k].getNumChildNoTransform(); i++) 
          System.out.println("\ta.initial[" + k + "]/child[" + i + "]=" + a.init_children[k].getChildNoTransform(i));
      } else System.out.println("null");
    }
*/  
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

  
  }
}
