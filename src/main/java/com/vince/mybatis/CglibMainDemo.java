package com.vince.mybatis;

/**
 * @author vince
 * @since v1.0.0
 */
public class CglibMainDemo {


  /**
   * 被代理的目标方法
   */
  public String method(String str) {
    System.out.println(str);
    return "CglibMainDemo:" + str;
  }

  public static void main(String[] args) {
    CglibProxyDemo proxy = new CglibProxyDemo();

    // 获取 CglibMainDemo 的代理对象
    CglibMainDemo proxyImp = (CglibMainDemo) proxy.getProxy(CglibMainDemo.class);

    // 执行代理对象的 method() 方法
    String result = proxyImp.method("test");

    System.out.println(result);
  }
}
