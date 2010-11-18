package test;

import test.ast.*;

public class Test18 {

	public static void main(String[] args) {
		System.out.println("Syn: synthesized attribute with initializing equation, overriden in subclass");
		Node node = new Node();
		System.out.println("Attribute attr: " + node.attr());
		System.out.println("Attribute value: " + node.value());
		node = new SubNode();
		System.out.println("Attribute attr: " + node.attr());
		System.out.println("Attribute value: " + node.value());
	}
}
