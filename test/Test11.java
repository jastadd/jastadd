package test;

import test.ast.*;

public class Test11 {
	public static void main(String[] args) {
	    System.out.println("AST: list of children");
	    B first = new B();
	    B second = new B();
	    System.out.println("Creating list with two children");
	    A a = new A(new List().add(first).add(second));
	    System.out.println("size: " + a.getNumB());
	    System.out.println("First: " + (a.getB(0) == first));
	    System.out.println("Second: " + (a.getB(1) == second));
	}
}
