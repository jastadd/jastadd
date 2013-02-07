package test;
import java.io.*;
import test.ast.*;

public class Test141 {

  public static void main(String[] args) {

    System.out.println("======= Construct AST");

    B b1Init = new B("b", "a");
    B b2Init = new B("a", "b");    
    test.ast.List listInit = new test.ast.List();
    listInit.add(b1Init);
    listInit.add(b2Init);
    A a = new A(listInit);
/*
    System.out.println("## start: ");
    System.out.println("\ta=" + a);
    System.out.println("\ta/list[0]=" + a.getChildNoTransform(0));
    for (int i = 0; i < a.getChildNoTransform(0).getNumChildNoTransform(); i++) 
      System.out.println("\ta/list[0]/child[" + i + "]=" + a.getChildNoTransform(0).getChildNoTransform(i));
    System.out.print("\ta.initial[0]=");
    if (a.init_children != null && a.init_children[0] != null) {
      System.out.println(a.init_children[0]);
      for (int i = 0; i < a.init_children[0].getNumChildNoTransform(); i++) 
        System.out.println("\ta.initial[0]/child[" + i + "]=" + a.init_children[0].getChildNoTransform(i));
    } else System.out.println("null");
*/
    System.out.println("## Dependencies/Cache after construction:");
    a.dumpDependencies();
    a.dumpCachedValues();
    listInit.dumpDependencies();
    listInit.dumpCachedValues();    
    b1Init.dumpDependencies();
    b1Init.dumpCachedValues();
    b2Init.dumpDependencies();
    b2Init.dumpCachedValues();


    System.out.println("======= Access AST");

    test.ast.List listAccess = (test.ast.List)a.getChild(0);
    B b1Access = a.getB(0);
    B b2Access = a.getB(1);
/*
    System.out.println("## access: ");
    System.out.println("\ta=" + a);
    System.out.println("\ta/list[0]=" + a.getChildNoTransform(0));
    for (int i = 0; i < a.getChildNoTransform(0).getNumChildNoTransform(); i++) 
      System.out.println("\ta/list[0]/child[" + i + "]=" + a.getChildNoTransform(0).getChildNoTransform(i));
    System.out.print("\ta.initial[0]=");
    if (a.init_children != null && a.init_children[0] != null) {
      System.out.println(a.init_children[0]);
      for (int i = 0; i < a.init_children[0].getNumChildNoTransform(); i++) 
        System.out.println("\ta.initial[0]/child[" + i + "]=" + a.init_children[0].getChildNoTransform(i));
    } else System.out.println("null");
*/
    System.out.println("## Dependencies/Cache in current after access");
    a.dumpDependencies();
    a.dumpCachedValues();
    listAccess.dumpDependencies();
    listAccess.dumpCachedValues();
    b1Access.dumpDependencies();
    b1Access.dumpCachedValues();
    b2Access.dumpDependencies();
    b2Access.dumpCachedValues();

    System.out.println("======= Attribute calls");

    b1Access.decl();    
    b2Access.decl();
/*
    System.out.println("## calls: ");
    System.out.println("\ta=" + a);
    System.out.println("\ta/list[0]=" + a.getChildNoTransform(0));
    for (int i = 0; i < a.getChildNoTransform(0).getNumChildNoTransform(); i++) 
      System.out.println("\ta/list[0]/child[" + i + "]=" + a.getChildNoTransform(0).getChildNoTransform(i));
    System.out.print("\ta.initial[0]=");
    if (a.init_children != null && a.init_children[0] != null) {
      System.out.println(a.init_children[0]);
      for (int i = 0; i < a.init_children[0].getNumChildNoTransform(); i++) 
        System.out.println("\ta.initial[0]/child[" + i + "]=" + a.init_children[0].getChildNoTransform(i));
    } else System.out.println("null");
*/
    System.out.println("## Dependencies/Cache in current after calls:");
    a.dumpDependencies();
    a.dumpCachedValues();
    listAccess.dumpDependencies();
    listAccess.dumpCachedValues();
    b1Access.dumpDependencies();
    b1Access.dumpCachedValues();
    b2Access.dumpDependencies();
    b2Access.dumpCachedValues();

    System.out.println("======= Change AST with Remove");

    a.getChild(0).removeChild(0);
/*
    System.out.println("## remove: ");
    System.out.println("\ta=" + a);
    System.out.println("\ta/list[0]=" + a.getChildNoTransform(0));
    for (int i = 0; i < a.getChildNoTransform(0).getNumChildNoTransform(); i++) 
      System.out.println("\ta/list[0]/child[" + i + "]=" + a.getChildNoTransform(0).getChildNoTransform(i));
    System.out.print("\ta.initial[0]=");
    if (a.init_children != null && a.init_children[0] != null) {
      System.out.println(a.init_children[0]);
      for (int i = 0; i < a.init_children[0].getNumChildNoTransform(); i++) 
        System.out.println("\ta.initial[0]/child[" + i + "]=" + a.init_children[0].getChildNoTransform(i));
    } else System.out.println("null");
*/

    System.out.println("## Dependencies/Cache in current after remove:");
    a.dumpDependencies();
    a.dumpCachedValues();
    a.getChildNoTransform(0).dumpDependencies();
    a.getChildNoTransform(0).dumpCachedValues();
    a.getChildNoTransform(0).getChildNoTransform(0).dumpDependencies();
    a.getChildNoTransform(0).getChildNoTransform(0).dumpCachedValues();

    System.out.println("======= Second Access");

    test.ast.List listChange = (test.ast.List)a.getChild(0);
    B b1Change = a.getB(0);

/*
    System.out.println("## second access: ");
    System.out.println("\ta=" + a);
    System.out.println("\ta/list[0]=" + a.getChildNoTransform(0));
    for (int i = 0; i < a.getChildNoTransform(0).getNumChildNoTransform(); i++) 
      System.out.println("\ta/list[0]/child[" + i + "]=" + a.getChildNoTransform(0).getChildNoTransform(i));
    System.out.print("\ta.initial[0]=");
    if (a.init_children != null && a.init_children[0] != null) {
      System.out.println(a.init_children[0]);
      for (int i = 0; i < a.init_children[0].getNumChildNoTransform(); i++) 
        System.out.println("\ta.initial[0]/child[" + i + "]=" + a.init_children[0].getChildNoTransform(i));
    } else System.out.println("null");
*/
  
    System.out.println("## Dependencies/Cache after second access:");
    a.dumpDependencies();
    a.dumpCachedValues();
    listChange.dumpDependencies();
    listChange.dumpCachedValues();
    b1Change.dumpDependencies();
    b1Change.dumpCachedValues();

  
  }
}
