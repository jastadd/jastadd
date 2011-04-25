package test;

import test.ast.*;

public class Test8 {
	public static void main(String[] args) {
		System.out.println("AST: single named child");
		B b = new B();
		A a = new A(b);
		System.out.println(a.getMyB() == b);
	}
}
