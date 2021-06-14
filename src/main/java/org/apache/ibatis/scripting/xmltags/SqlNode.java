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
package org.apache.ibatis.scripting.xmltags;

/**
 * @author Clinton Begin
 */
public interface SqlNode {

  /**
   * apply() 方法会根据用户传入的实参，解析该 SqlNode 所表示的动态SQL内容并将解析之后的
   * SQL 片段追加到将解析之后的 SQL 片段追加到 DynamicContext.sqlBuilder 字段中暂存
   * 当 SQL 语句中全部的动态 SQL 片段都解析完成之后，就可以从 DynamicContext.sqlBuilder
   * 字段中得到一条完整的、可用的 SQL 语句
   *
   * @param context 中间结果
   * @return 执行是否成功
   */
  boolean apply(DynamicContext context);
}
