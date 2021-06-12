package com.vince.mybatis;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * @author vince
 * @since v1.0.0
 */
public class MethodHandleDemo {

  public String sayHello(String s) {
    return "Hello, " + s;
  }

  public static void main(String[] args) throws Throwable {
    // 初始化 MethodHandleDemo 实例
    MethodHandleDemo subMethodHandleDemo = new SubMethodHandleDemo();

    // 定义 sayHello() 方法的签名，第一个参数是方法的返回值类型，第二个参数是方法的参数列表
    MethodType methodType = MethodType.methodType(String.class, String.class);

    // 根据方法名和 MethodType 在 MethodHandleDemo 中查找对应的 MethodHandle
    MethodHandle methodHandle = MethodHandles.lookup()
      .findVirtual(MethodHandleDemo.class, "sayHello", methodType);

    // 将 MethodHandle 绑定到一个对象上，然后通过 invokeWithArguments() 方法传入实参并执行
    System.out.println(methodHandle.bindTo(subMethodHandleDemo)
      .invokeWithArguments("MethodHandleDemo"));

    // 下面是调用 MethodHandleDemo 对象(即父类)的方法
    MethodHandleDemo methodHandleDemo = new MethodHandleDemo();

    System.out.println(methodHandle.bindTo(methodHandleDemo)
      .invokeWithArguments("MethodHandleDemo"));

  }

  public static class SubMethodHandleDemo extends MethodHandleDemo {
    // 定义一个sayHello()方法
    @Override
    public String sayHello(String s) {
      return "Sub Hello, " + s;
    }
  }
}
