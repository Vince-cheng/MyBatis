package com.vince.demo1.mapper;

import com.vince.demo1.pojo.User;

import java.util.List;

/**
 * mapper类
 *
 * @author vince
 * @since v1.0.0
 */
public interface UserMapper {
  /**
   * 插入用户信息
   *
   * @param user 用户
   * @return 插入行数
   */
  int insertUser(User user);

  /**
   * 更新用户信息
   *
   * @param user 用户
   * @return 插入行数
   */
  int updateUser(User user);

  /**
   * 删除用户信息
   *
   * @param id 用户id
   * @return 插入行数
   */
  int deleteUser(Long id);

  /**
   * 查询所有用户信息
   *
   * @return 用户列表
   */
  List<User> selectUser();
}

