package test;

import test.ast.*;

public class Test164 {

  public static void main(String[] args) {


    System.out.println("======= Construct AST");

    B b1Init = new B("a");
    test.ast.List listInit = new test.ast.List();
    listInit.add(b1Init);
    A a = new A(listInit);

    assert(listInit == a.getChildNoTransform(0));
    assert(b1Init == a.getChildNoTransform(0).getChildNoTransform(0));

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


    System.out.println("======= Access AST");

    test.ast.List listAccess = (test.ast.List)a.getChild(0);
    B b1Access = a.getB(0);

    assert(listAccess == a.getChildNoTransform(0));
    assert(b1Access == a.getChildNoTransform(0).getChildNoTransform(0));
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
    System.out.println("#### Dependencies/Cache in init after access:");
    listInit.dumpDependencies();
    listInit.dumpCachedValues();    
    b1Init.dumpDependencies();
    b1Init.dumpCachedValues();

    System.out.println("======= Access attribute/Change AST");

    C c = b1Access.decl();
/*
    System.out.println("## call/change: ");
    System.out.println("\tc=" + c);
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
