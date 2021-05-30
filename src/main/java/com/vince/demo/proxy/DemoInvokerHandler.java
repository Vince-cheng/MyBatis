package com.vince.demo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author vince
 * @since v1.0.0
 */
public class DemoInvokerHandler implements InvocationHandler {

  /**
   * 真正的业务对象，也就是RealSubject对象
   */
  private Object target;

  /**
   * DemoInvokerHandler 构造方法
   *
   * @param target 业务对象
   */
  public DemoInvokerHandler(Object target) {
    this.target = target;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // 在执行业务逻辑之前的预处理逻辑
    System.out.println("代理前的处理");

    Object result = method.invoke(target, args);

    // 在执行业务逻辑之后的后置处理逻辑
    System.out.println("代理后的处理");

    return result;
  }

  public Object getProxy() {
    // 创建代理对象
    return Proxy.newProxyInstance(Thread.currentThread()
        .getContextClassLoader(),
      target.getClass().getInterfaces(), this);
  }

}

