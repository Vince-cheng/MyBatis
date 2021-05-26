package com.vince.demo3;

import com.vince.demo3.dto.UserDto;
import com.vince.demo3.mapper.UserMapper;
import com.vince.demo3.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.result.DefaultResultContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String resource = "mybatis-config-demo3.xml";
    //读取全局配置文件
    InputStream inputStream = Resources.getResourceAsStream(resource);
    //构建SqlSessionFactory对象
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
    this.factory = factory;
  }

  @Test
  public void getByName() {
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper mapper = sqlSession.getMapper(UserMapper.class);
      //执行查询操作
      User user = mapper.getByName("vince");
      log.info("{}", user);
    }
  }

  /**
   * 通过map给Mapper接口的方法传递参数
   */
  @Test
  public void getByMap() {
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      Map<String, Object> map = new HashMap<>();
      map.put("id", "3L");
      map.put("name", "vince");
      List<User> userList = userMapper.getByMap(map);
      userList.forEach(item ->
        log.info("{}", item)
      );
    }
  }

  @Test
  public void getListByUserFindDto() {
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      UserDto userDto = UserDto.builder().userId(1L).userName("张学友").build();
      List<User> userList = userMapper.getListByUserDto(userDto);
      userList.forEach(item ->
        log.info("{}", item)
      );
    }
  }

  /**
   * 通过map给Mapper接口的方法传递参数
   */
  @Test
  public void getByIdOrName() {
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      User user = userMapper.getByIdOrName(1L, "vince");
      log.info("{}", user);
    }
  }

  @Test
  public void getListByIdCollection() {
    log.info("----------");
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      List<Long> userIdList = Arrays.asList(1L, 3L);
      List<User> list = userMapper.getListByIdCollection(userIdList);
      list.forEach(item ->
        log.info("{}", item)
      );
    }
  }

  @Test
  public void getListByIdList() {
    log.info("----------");
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      List<Long> userIdList = Arrays.asList(1L, 3L);
      List<User> list = userMapper.getListByIdList(userIdList);
      list.forEach(item ->
        log.info("{}", item)
      );
    }
  }

  @Test
  public void getListByIdArray() {
    log.info("----------");
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      Long[] id = {1L, 4L};
      List<User> idArray = userMapper.getListByIdArray(id);
      idArray.forEach(
        item -> log.info("{}", item)
      );
    }
  }

  @Test
  public void getList() {
    log.info("----------");
    try (SqlSession sqlSession = this.factory.openSession(true)) {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      userMapper.getList(context -> {
        //将context参数转换为DefaultResultContext对象
        DefaultResultContext<User> defaultResultContext = (DefaultResultContext<User>) context;
        log.info("{}", defaultResultContext.getResultObject());
        //遍历到第二条之后停止
        if (defaultResultContext.getResultCount() == 2) {
          //调用stop方法停止遍历，stop方法会更新内部的一个标志，置为停止遍历
          defaultResultContext.stop();
        }
      });
    }
  }
}
