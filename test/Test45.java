package test;
import test.ast.*;
public class Test45 {

  public static void main(String[] args) {
    System.out.println("Rewrites: rewrite each C in A's B-list to two Ds");
    A a = new A(new List().add(new C()));
    for(int i = 0; i < a.getNumB(); i++)
      System.out.println(a.getB(i).getClass().getSimpleName());
  }
}
