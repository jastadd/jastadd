package test;

import test.ast.*;

public class Test7 {
	public static void main(String[] args) {
		System.out.println("AST: single child");
		B b = new B();
		A a = new A(b);
		System.out.println(a.getB() == b);
	}
}
