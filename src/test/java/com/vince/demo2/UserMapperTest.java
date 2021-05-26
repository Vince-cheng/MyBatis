package com.vince.demo2;

import com.vince.demo2.mapper.UserMapper;
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
public class UserMapperTest {
  private SqlSessionFactory factory;

  @Before
  public void before() throws IOException {
    //指定mybatis全局配置文件
    String resource = "mybatis-config-demo2.xml";
    //读取全局配置文件
    InputStream inputStream = Resources.getResourceAsStream(resource);
    //构建SqlSessionFactory对象
    this.factory = new SqlSessionFactoryBuilder().build(inputStream);
  }

  @Test
  public void selectUser() {
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper mapper = sqlSession.getMapper(UserMapper.class);
      //执行查询操作
      List<User> UserList = mapper.selectUser();
      UserList.forEach(item ->
        log.info("{}", item)
      );
    }
  }
}
