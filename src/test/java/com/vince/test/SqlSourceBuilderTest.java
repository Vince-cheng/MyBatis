package com.vince.test;

import lombok.Data;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.junit.Test;

import java.util.HashMap;

/**
 * @author vince
 * @since v1.0.0
 */
public class SqlSourceBuilderTest {
  @Test
  public void test() {
    // 带有复杂 #{} 占位符的参数，接下里会解析这个占位符
    String sql = "SELECT * FROM Author WHERE age ="
      + "#{age,javaType=int,jdbcType=NUMERIC}";
    SqlSourceBuilder sqlSourceBuilder =
      new SqlSourceBuilder(new Configuration());
    SqlSource sqlSource = sqlSourceBuilder.parse(
      sql, Author.class, new HashMap<>());
    BoundSql boundSql = sqlSource.getBoundSql(new Author());
    System.out.println(String.format("SQL: %s\n", boundSql.getSql()));
    System.out.println(String.format(
      "ParameterMappings: %s", boundSql.getParameterMappings()));
  }
}

@Data
class Author {
  private Integer id;
  private String name;
  private Integer age;

}
