package com.vince.mybatis;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author vince
 * @since v1.0.0
 */
public class CglibProxyDemo implements MethodInterceptor {

  /**
   * cglib 中的 Enhancer 对象
   */
  private Enhancer enhancer = new Enhancer();

  /**
   * intercept() 方法中实现了方法拦截
   *
   * @param obj    对象
   * @param method 方法
   * @param args   对象数组
   * @param proxy  拦截方法
   * @return 增强后的对象
   * @throws Throwable 异常
   */
  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    System.out.println("before operation...");
    // 调用父类中的方法
    Object result = proxy.invokeSuper(obj, args);
    System.out.println("after operation...");
    return result;
  }

  /**
   * 代理方法
   *
   * @param clazz 类
   * @return 增强后的类
   */
  public Object getProxy(Class clazz) {
    // 代理类的父类
    enhancer.setSuperclass(clazz);
    // 添加 Callback 对象
    enhancer.setCallback(this);
    // 通过 cglib 动态创建子类实例并返回
    return enhancer.create();
  }

}
