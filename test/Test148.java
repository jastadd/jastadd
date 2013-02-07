package test;

import test.ast.*;

public class Test148 {

  public static void main(String[] args) {


    System.out.println("======= Construct AST");

    B b1Init = new B();
    B b2Init = new B();
    test.ast.List listInit = new test.ast.List();

    listInit.add(b1Init);
    listInit.add(b2Init);
    A a = new A(listInit);

    assert(listInit == a.getChildNoTransform(0));
    assert(b1Init == a.getChildNoTransform(0).getChildNoTransform(0));
    assert(b2Init == a.getChildNoTransform(0).getChildNoTransform(1));
/*
    System.out.println("## start: ");
    System.out.println("\ta=" + a);
    System.out.println("\ta/list[0]=" + a.getChildNoTransform(0) + " [" + printHandlerNodes(a.getChildNoTransform(0)) + "]");
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
    C cAccess = (C)a.getB(2);

    assert(listAccess == a.getChildNoTransform(0));
    assert(b1Access == a.getChildNoTransform(0).getChildNoTransform(0));
    assert(b2Access == a.getChildNoTransform(0).getChildNoTransform(1));
    assert(cAccess == a.getChildNoTransform(0).getChildNoTransform(2));

/*
    System.out.println("## access: ");
    System.out.println("\ta=" + a);
    System.out.println("\ta/list[0]=" + a.getChildNoTransform(0) + " [" + printHandlerNodes(a.getChildNoTransform(0)) + "]");
    for (int i = 0; i < a.getChildNoTransform(0).getNumChildNoTransform(); i++) 
      System.out.println("\ta/list[0]/child[" + i + "]=" + a.getChildNoTransform(0).getChildNoTransform(i));
    System.out.print("\ta.initial[0]=");
    if (a.init_children != null && a.init_children[0] != null) {
      System.out.println(a.init_children[0] + " [" + printHandlerNodes(a.init_children[0]) + "]");
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
    cAccess.dumpDependencies();
    cAccess.dumpCachedValues();
    System.out.println("#### Dependencies/Cache in init after access:");
    listInit.dumpDependencies();
    listInit.dumpCachedValues();    
    b1Init.dumpDependencies();
    b1Init.dumpCachedValues();
    b2Init.dumpDependencies();
    b2Init.dumpCachedValues();


    System.out.println("======= Change AST");

    // Change
    C cNew = new C();
    a.addB(cNew);

    assert(cNew == a.getChildNoTransform(0).getChildNoTransform(2));
/*
    System.out.println("## change: ");
    System.out.println("\ta=" + a);
    System.out.println("\ta/list[0]=" + a.getChildNoTransform(0) + " [" + printHandlerNodes(a.getChildNoTransform(0)) + "]");
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
    a.getChildNoTransform(0).getChildNoTransform(2).dumpDependencies();
    a.getChildNoTransform(0).getChildNoTransform(2).dumpCachedValues();


    System.out.println("======= Access AST");

    test.ast.List listChange = (test.ast.List)a.getChild(0);
    B b1Change = a.getB(0);
    B b2Change = a.getB(1);
    C cChange = (C)a.getB(2);

    assert(listChange == a.getChildNoTransform(0));    
    assert(b1Change == a.getChildNoTransform(0).getChildNoTransform(0));
    assert(b2Change == a.getChildNoTransform(0).getChildNoTransform(1));
    assert(cChange == a.getChildNoTransform(0).getChildNoTransform(2));
    assert(cChange == cNew);
/*
    System.out.println("## access 2: ");
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
    System.out.println("## Dependencies/Cache in current after access 2:");
    a.dumpDependencies();
    a.dumpCachedValues();
    listChange.dumpDependencies();
    listChange.dumpCachedValues();
    b1Change.dumpDependencies();
    b1Change.dumpCachedValues();
    b2Change.dumpDependencies();
    b2Change.dumpCachedValues();
    cChange.dumpDependencies();
    cChange.dumpCachedValues();




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
