package test;

import test.ast.*;

public class Test14 {
	public static void main(String[] args) {
	    System.out.println("AST: optional child");
	    A a = new A();
	    System.out.println("HasB: " + a.hasB());
	    B b = new B();
	    a.setB(b);
	    System.out.println("Adding node");
	    System.out.println("HasB: " + a.hasB());
	    System.out.println("Correct node: " + (a.getB() == b));
	}
}
