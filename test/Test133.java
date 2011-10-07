package test;

import java.util.*;
import test.ast.*;

public class Test133 {

  public static void main(String[] args) {


    System.out.println("======= Construct AST");
    A a = new A(new test.ast.List().add(new B()).add(new B()));

    System.out.print("## start: a=" + a + ", a.list=" + a.getChildNoTransform(0));
    for (int i = 0; i < a.getNumChildNoTransform(); i++) 
      System.out.print(", a.list.child[" + i + "]=" + a.getChildNoTransform(0).getChildNoTransform(i));
    System.out.print("\n\ta.initial[0]=");
    if (a.init_children != null && a.init_children[0] != null) {
      System.out.print(a.init_children[0]);
      for (int i = 0; i < a.init_children[0].getNumChildNoTransform(); i++) 
        System.out.print(", a.initial[0].child[" + i + "]=" + a.init_children[0].getChildNoTransform(i));
      System.out.println();
    } else System.out.println("null");
    a.dumpDependencies();
    a.getChildNoTransform(0).dumpDependencies();    
    a.getChildNoTransform(0).getChildNoTransform(0).dumpDependencies();
    a.getChildNoTransform(0).getChildNoTransform(1).dumpDependencies();

    System.out.println("======== Access first child of List");
    // Trigger rewrite
    B b1 = a.getB(0);

    System.out.print("## first access: a=" + a + ", a.list=" + a.getChildNoTransform(0));
    for (int i = 0; i < a.getNumChildNoTransform(); i++) 
      System.out.print(", a.list.child[" + i + "]=" + a.getChildNoTransform(0).getChildNoTransform(i));
    System.out.print("\n\ta.initial[0]=");
    if (a.init_children != null && a.init_children[0] != null) {
      System.out.print(a.init_children[0]);
      for (int i = 0; i < a.init_children[0].getNumChildNoTransform(); i++) 
        System.out.print(", a.initial[0].child[" + i + "]=" + a.init_children[0].getChildNoTransform(i));
      System.out.println();
    } else System.out.println("null");
    a.dumpDependencies();
    a.getChildNoTransform(0).dumpDependencies();    
    a.getChildNoTransform(0).getChildNoTransform(0).dumpDependencies();
    a.getChildNoTransform(0).getChildNoTransform(1).dumpDependencies();
    a.getChildNoTransform(0).getChildNoTransform(2).dumpDependencies();

    System.out.println("======== Add of new C");
    C c2 = new C();
    a.addB(c2);


    System.out.print("## add of C: a=" + a + ", a.list=" + a.getChildNoTransform(0));
    for (int i = 0; i < a.getNumChildNoTransform(); i++) 
      System.out.print(", a.list.child[" + i + "]=" + a.getChildNoTransform(0).getChildNoTransform(i));
    System.out.print("\n\ta.initial[0]=");
    if (a.init_children != null && a.init_children[0] != null) {
      System.out.print(a.init_children[0]);
      for (int i = 0; i < a.init_children[0].getNumChildNoTransform(); i++) 
        System.out.print(", a.initial[0].child[" + i + "]=" + a.init_children[0].getChildNoTransform(i));
      System.out.println();
    } else System.out.println("null");
    a.dumpDependencies();
    a.getChildNoTransform(0).dumpDependencies();    
    a.getChildNoTransform(0).getChildNoTransform(0).dumpDependencies();
    a.getChildNoTransform(0).getChildNoTransform(1).dumpDependencies();
    a.getChildNoTransform(0).getChildNoTransform(2).dumpDependencies();

System.exit(1);

    B b2 = a.getB(1);
    C c = (C)a.getB(2);
    
    System.out.println("-- Dependencies/Cache after a.getB(0):");
    a.dumpDependencies();
    a.dumpCachedValues();
    b1.dumpDependencies();
    b1.dumpCachedValues();
    b2.dumpDependencies();
    b2.dumpCachedValues();
    c.dumpDependencies();
    c.dumpCachedValues();


    // Change
    C cNew = new C();
    System.out.println("cNew=" + cNew);
    a.addB(cNew);
    b1 = a.getB(0);
    b2 = a.getB(1);
    c = (C)a.getB(2);
    System.out.println("c=" + c + ", cNew=" + cNew);
  
    System.out.println("-- Dependencies/Cache after a.addChild(new C()):");
    a.dumpDependencies();
    a.dumpCachedValues();
    b1.dumpDependencies();
    b1.dumpCachedValues();
    b2.dumpDependencies();
    b2.dumpCachedValues();
    c.dumpDependencies();
    c.dumpCachedValues();

  }
}
