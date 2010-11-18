package test;
import test.ast.*;
public class Test41 {

  public static void main(String[] args) {
    System.out.println("Intertype declarations: introduce constructor");
    A a1 = new A();
    A a2 = new A("Intertype constructor with extra String argument");
  }
}
