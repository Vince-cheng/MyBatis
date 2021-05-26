package com.vince.demo1;

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
public class UserTest {

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
  public void sqlSessionTest() {
    SqlSession sqlSession = this.factory.openSession();
    log.info("sqlSession {}", sqlSession);
  }

  @Test
  public void insertUserTest() {
    try (SqlSession sqlSession = this.factory.openSession(false)) {
      User user = new User();
      user.setId(2L);
      user.setName("peter");
      user.setAge(18);
      user.setSalary(20000.0);
      user.setSex(1);
      int result = sqlSession.insert("com.vince.demo1.mapper.UserMapper.insertUser", user);
      log.info("插入影响行数：{}", result);
      // 提交事务
      sqlSession.commit();
    }
  }

  @Test
  public void updateUserTest(){
    try(SqlSession sqlSession = this.factory.openSession(true)){
      User user = new User();
      user.setId(2L);
      user.setName("peter");
      user.setAge(25);
      user.setSalary(10000.0);
      user.setSex(1);
      int result = sqlSession.update("com.vince.demo1.mapper.UserMapper.updateUser", user);
      log.info("更新影响行数：{}",result);
    }
  }

  @Test
  public void deleteUserTest(){
    try(SqlSession sqlSession =this.factory.openSession(true)){
      Long userId = 2L;
      int result = sqlSession.delete("com.vince.demo1.mapper.UserMapper.deleteUser", userId);
      log.info("影响行数：{}", result);
    }
  }

  @Test
  public void selectUserTest(){
    try(SqlSession sqlSession =this.factory.openSession(true)){
      List<User> result = sqlSession.selectList("com.vince.demo1.mapper.UserMapper.selectUser");
      log.info("结果：{}", result);
    }
  }
}
