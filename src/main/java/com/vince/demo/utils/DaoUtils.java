package com.vince.demo.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * @author vince
 * @since v1.0.0
 */
public class DaoUtils {
  private static SqlSessionFactory factory;

  // 在静态代码块中直接读取MyBatis的mybatis-config.xml配置文件
  static {
    String resource = "mybatis-config.xml";
    InputStream inputStream = null;
    try {
      inputStream = Resources.getResourceAsStream(resource);
    } catch (IOException e) {
      System.err.println("read mybatis-config.xml fail");
      e.printStackTrace();
      System.exit(1);
    }

    // 加载完mybatis-config.xml配置文件之后，会根据其中的配置信息创建SqlSessionFactory对象
    factory = new SqlSessionFactoryBuilder().build(inputStream);
  }

  public static <R> R execute(Function<SqlSession, R> function) {
    // 创建SqlSession
    SqlSession session = factory.openSession();
    try {
      R apply = function.apply(session);
      // 提交事务
      session.commit();
      return apply;
    } catch (Throwable t) {
      // 出现异常的时候，回滚事务
      session.rollback();
      System.out.println("execute error");
      throw t;
    } finally {
      // 关闭SqlSession
      session.close();
    }
  }
}
