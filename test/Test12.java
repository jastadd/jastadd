package test;

import test.ast.*;

public class Test12 {
	public static void main(String[] args) {
	    System.out.println("AST: adding nodes to a list of children");
	    B first = new B();
	    B second = new B();
	    System.out.println("Creating list with two children");
	    A a = new A(new List().add(first).add(second));
	    System.out.println("size: " + a.getNumB());
	    System.out.println("First: " + (a.getB(0) == first));
	    System.out.println("Second: " + (a.getB(1) == second));
	    System.out.println("Adding one child");
	    B third = new B();
	    a.addB(third);
	    System.out.println("size: " + a.getNumB());
	    System.out.println("First: " + (a.getB(0) == first));
	    System.out.println("Second: " + (a.getB(1) == second));
	    System.out.println("Third: " + (a.getB(2) == third));
	    System.out.println("Adding another child");
	    B fourth = new B();
	    a.addB(fourth);
	    System.out.println("size: " + a.getNumB());
	    System.out.println("First: " + (a.getB(0) == first));
	    System.out.println("Second: " + (a.getB(1) == second));
	    System.out.println("Third: " + (a.getB(2) == third));
	    System.out.println("Fourth: " + (a.getB(3) == fourth));
	}
}
