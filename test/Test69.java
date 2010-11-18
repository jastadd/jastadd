package test;
import test.ast.*;
public class Test69 {
  public static void main(String[] args) {
    // Create a non-terminal attribute with an argument
    System.out.println(new X().myY("hello").getID());
  }
}
