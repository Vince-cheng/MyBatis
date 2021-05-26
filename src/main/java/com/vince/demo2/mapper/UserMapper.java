package com.vince.demo2.mapper;

import com.vince.demo2.pojo.User;

import java.util.List;

/**
 * mapper类
 *
 * @author vince
 * @since v1.0.0
 */
public interface UserMapper {

  /**
   * 查询所有用户信息
   *
   * @return 用户列表
   */
  List<User> selectUser();
}
