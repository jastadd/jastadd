package test;

import test.ast.*;

public class Test15 {
	public static void main(String[] args) {
	    System.out.println("AST: optional named child");
	    A a = new A();
	    System.out.println("HasB: " + a.hasMyB());
	    B b = new B();
	    a.setMyB(b);
	    System.out.println("Adding node");
	    System.out.println("HasB: " + a.hasMyB());
	    System.out.println("Correct node: " + (a.getMyB() == b));
	}
}
