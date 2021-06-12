package com.vince.mybatis;

import org.apache.ibatis.reflection.Reflector;

/**
 * @author vince
 * @since v1.0.0
 */
public class ReflectorDemo {

  public static void main(String[] args) {
    Reflector reflector = new Reflector(Reflector.class);
    System.out.println(reflector.getType());
    System.out.println(reflector.getType());
  }
}
