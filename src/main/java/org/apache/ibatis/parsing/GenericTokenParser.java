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
package org.apache.ibatis.parsing;

/**
 * @author Clinton Begin
 */
public class GenericTokenParser {

  /**
   * 开始标记
   */
  private final String openToken;
  /**
   * 结束标记
   */
  private final String closeToken;
  /**
   * 标记处理器
   */
  private final TokenHandler handler;

  public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
    this.openToken = openToken;
    this.closeToken = closeToken;
    this.handler = handler;
  }

  /**
   * 解析 ${} 和 #{}
   * 该方法主要实现了配置文件、脚本等片段中占位符的解析、处理工作，并返回最终需要的数据
   * 其中解析工作有该方法完成，处理工作是由处理器 handler 的 handlerToken 方式实现的
   */
  public String parse(String text) {
    // 验证参数问题，如果是 null，就返回空字符串
    if (text == null || text.isEmpty()) {
      return "";
    }


    // search open token
    // 继续验证是否包含开始标签，如果不包含，默认不是占位符，直接原样返回，否则继续执行
    int start = text.indexOf(openToken);
    if (start == -1) {
      return text;
    }

    // 把 text 转成字符数组 src ，并且定义默认偏移量 offset=0、存储最终需要返回字符串的变量 builder，text 变量中占位符对用的变量 expression。
    // 判断 start 是否大于 -1（即 text 中是否存在 openToken），如果不存在就执行下面代码
    char[] src = text.toCharArray();
    int offset = 0;
    final StringBuilder builder = new StringBuilder();
    StringBuilder expression = null;
    while (start > -1) {
      // 判断如果开始标记前有转义字符，就不作为 openToken 进行处理，否则继续处理
      if (start > 0 && src[start - 1] == '\\') {
        // this open token is escaped. remove the backslash and continue.
        builder.append(src, offset, start - offset - 1).append(openToken);
        offset = start + openToken.length();
      } else {
        // found open token. let's search close token.
        // 重置 expression 变量，避免空指针或者老数据干扰
        if (expression == null) {
          expression = new StringBuilder();
        } else {
          expression.setLength(0);
        }
        builder.append(src, offset, start - offset);
        offset = start + openToken.length();
        int end = text.indexOf(closeToken, offset);
        while (end > -1) {
          // 如果结束标记前面有转义字符时
          if (end > offset && src[end - 1] == '\\') {
            // this close token is escaped. remove the backslash and continue.
            expression.append(src, offset, end - offset - 1).append(closeToken);
            offset = end + closeToken.length();
            end = text.indexOf(closeToken, offset);
          } else {
            // 不存在转移字符，即需要作为参数直接处理
            expression.append(src, offset, end - offset);
            break;
          }
        }
        if (end == -1) {
          // close token was not found.
          builder.append(src, start, src.length - start);
          offset = src.length;
        } else {
          // 首先根据参数的 key （即 expression ）进行参数处理，返回 ? 作为占位符
          builder.append(handler.handleToken(expression.toString()));
          offset = end + closeToken.length();
        }
      }
      start = text.indexOf(openToken, offset);
    }
    if (offset < src.length) {
      builder.append(src, offset, src.length - offset);
    }
    return builder.toString();
  }
}
