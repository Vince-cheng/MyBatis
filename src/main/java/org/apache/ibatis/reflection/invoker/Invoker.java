/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.reflection.invoker;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Clinton Begin
 */
public interface Invoker {

  /**
   * 调用底层封装的 Method 方法或是读写指定的字段
   *
   * @param target 目标
   * @param args   方法集合
   * @return 反射对象
   * @throws IllegalAccessException  没有访问权限异常
   * @throws InvocationTargetException 调用目标异常
   */
  Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException;

  /**
   * 返回属性的类型
   *
   * @return class 类型
   */
  Class<?> getType();
}
