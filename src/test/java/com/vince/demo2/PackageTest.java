package com.vince.demo2;

import com.vince.demo2.mapper.OrderMapper;
import com.vince.demo2.mapper.UserMapper;
import com.vince.demo2.pojo.Order;
import com.vince.demo2.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author vince
 * @since v1.0.0
 */
@Slf4j
public class PackageTest {
  private SqlSessionFactory sqlSessionFactory;

  @Before
  public void before() throws IOException {
    //指定mybatis全局配置文件
    String resource = "mybatis-config-demo2.xml";
    //读取全局配置文件
    InputStream inputStream = Resources.getResourceAsStream(resource);
    //构建SqlSessionFactory对象
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    this.sqlSessionFactory = sqlSessionFactory;
  }

  @Test
  public void test() {
    try (SqlSession sqlSession = this.sqlSessionFactory.openSession(true);) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      //执行查询操作
      List<User> userList = userMapper.selectUser();
      userList.forEach(item ->
        log.info("{}", item)
      );

      log.info("----------------------------------");
      OrderMapper orderMapper = sqlSession.getMapper(OrderMapper.class);
      //执行查询操作
      List<Order> orderList = orderMapper.selectOrder();
      orderList.forEach(item ->
        log.info("{}", item)
      );
    }
  }
}
