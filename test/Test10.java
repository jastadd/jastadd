package test;

import test.ast.*;

public class Test10 {
	public static void main(String[] args) {
	    System.out.println("AST: multiple named children");
	    B left = new B();
	    B right = new B();
	    A n = new A(left, right);
	    System.out.println("Left: " + (n.getLeft() == left));
	    System.out.println("Right: " + (n.getRight() == right));
	}
}
