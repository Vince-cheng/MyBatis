/**
 *    Copyright 2009-2019 the original author or authors.
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
package org.apache.ibatis.executor.resultset;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.cursor.Cursor;

/**
 * @author Clinton Begin
 */
public interface ResultSetHandler {

  /**
   * 将 ResultSet 映射成 Java 对象
   *
   * @param stmt Statement 接口
   * @param <E>  泛型对象
   * @return 泛型对象
   * @throws SQLException 异常
   */
  <E> List<E> handleResultSets(Statement stmt) throws SQLException;

  /**
   * 将 ResultSet 映射成游标对象
   *
   * @param stmt Statement 接口
   * @param <E>  泛型对象
   * @return 泛型对象
   * @throws SQLException 异常
   */
  <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException;

  /**
   * 处理存储过程的输出参数
   *
   * @param cs 带存储过程的 Statement 接口
   * @throws SQLException 异常
   */
  void handleOutputParameters(CallableStatement cs) throws SQLException;

}
