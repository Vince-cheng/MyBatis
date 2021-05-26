package com.vince.reflector;

import org.apache.ibatis.reflection.Reflector;

/**
 * @author vince
 * @since v1.0.0
 */
public class ReflectorDemo {

  public static void main(String[] args) {
    Reflector reflector = new Reflector(String.class);
    System.out.println(reflector.getType());
  }
}
