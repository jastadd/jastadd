package test;

import test.ast.*;

public class Test28 {

  public static void main(String[] args) {
    System.out.println("Larger example using the following features:");
    System.out.println("  syn paramterized attributes with/without initializer + overriding + declarative/imparative equations");
    System.out.println("  inh paramterized attributes using list child index");
    Decl d1 = new Decl("int", "d");
    Use u1 = new Use("d");
    Decl d2 = new Decl("string", "d");
    Use u2 = new Use("d");
    List list = new List();
    list.add(d1);
    list.add(u1);
    list.add(d2);
    list.add(u2);
    Program p = new Program(list);
    
    System.out.println(u1.decl().getType());
    System.out.println(u2.decl().getType());
  }
}
