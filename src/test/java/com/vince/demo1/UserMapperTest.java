package com.vince.demo1;

import com.vince.demo1.mapper.UserMapper;
import com.vince.demo1.pojo.User;
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
public class UserMapperTest {


  private SqlSessionFactory factory;

  @Before
  public void before() throws IOException {
    // 指定mybatis全局配置文件
    String resource = "mybatis-config-demo1.xml";
    // 读取全局配置文件
    InputStream in = Resources.getResourceAsStream(resource);
    // 构建SqlSessionFactory对象
    this.factory = new SqlSessionFactoryBuilder().build(in);
  }

  @Test
  public void sqlSessionTest(){
    SqlSession sqlSession = this.factory.openSession();
    log.info("sqlSession ：{}", sqlSession);
  }


  @Test
  public void insertUser() {
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper mapper = sqlSession.getMapper(UserMapper.class);
      // 创建User对象
      User user = User.builder().id(1L).name("vince").age(25).salary(50000D).sex(1).build();
      // 执行插入操作
      int result = mapper.insertUser(user);
      log.info("影响行数：{}", result);
    }
  }

  @Test
  public void updateUser() {
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper mapper = sqlSession.getMapper(UserMapper.class);
      // 创建User对象
      User user = User.builder().id(2L).name("vince").age(18).salary(5000D).sex(0).build();
      // 执行更新操作
      int result = mapper.updateUser(user);
      log.info("影响行数：{}", result);
    }
  }

  @Test
  public void deleteUser() {
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper mapper = sqlSession.getMapper(UserMapper.class);
      // 定义需要删除的用户id
      Long id = 1L;
      // 执行删除操作
      int result = mapper.deleteUser(id);
      log.info("影响行数：{}", result);
    }
  }

  @Test
  public void getUserList() {
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper mapper = sqlSession.getMapper(UserMapper.class);
      // 执行查询操作
      List<User> users = mapper.selectUser();
      users.forEach(item -> log.info("{}", item));
    }
  }
}
