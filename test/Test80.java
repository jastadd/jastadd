package test;
import test.ast.*;
public class Test80 {
  
  public static void main(String[] args) {
    Object o = new ASTNode();
    Object p = new C();
    Object q = new A();
    System.out.println("ASTNode is instanceof X: " + (o instanceof X));
    System.out.println("C is instanceof X: " + (p instanceof X));
    System.out.println("A is instanceof X: " + (q instanceof X));
  }
}
