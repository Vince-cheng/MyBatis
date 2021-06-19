package com.vince.mybatis;

/**
 * @author vince
 * @since v1.0.0
 */
public class JavassistDemo {
  /**
   * 字段
   */
  private String demoProperty = "demo-value";

  /**
   * demoProperty 字段对应的 getter 方法
   *
   * @return 获取属性
   */
  public String getDemoProperty() {
    return demoProperty;
  }

  /**
   * demoProperty 字段对应的 setter 方法
   *
   * @param demoProperty 设置属性
   */
  public void setDemoProperty(String demoProperty) {
    this.demoProperty = demoProperty;
  }

  /**
   * JavassistDemo 的成员方法
   */
  public void operation() {
    System.out.println("operation():" + this.demoProperty);
  }
}
