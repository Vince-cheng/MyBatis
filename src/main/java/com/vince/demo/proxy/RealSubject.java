package com.vince.demo.proxy;

/**
 * @author vince
 * @since v1.0.0
 */
public class RealSubject implements Subject {

  @Override
  public void operation() {
    System.out.println("操作");
  }
}
