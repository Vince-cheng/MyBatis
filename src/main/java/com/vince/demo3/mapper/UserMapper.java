package com.vince.demo3.mapper;

import com.vince.demo3.dto.UserDto;
import com.vince.demo3.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ResultHandler;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author vince
 * @since v1.0.0
 */
public interface UserMapper {
  /**
   * 通过name查询
   *
   * @param name 名字
   * @return 用户对象
   */
  User getByName(String name);

  /**
   * 通过map查询
   *
   * @param map key为参数，value为传入的值
   * @return 对象列表
   */
  List<User> getByMap(Map<String, Object> map);

  /**
   * 通过UserDto进行查询
   *
   * @param userDto 封装好的bean
   * @return 对象列表
   */
  List<User> getListByUserDto(UserDto userDto);

  /**
   * 通过id或者name查询
   *
   * @param id   id
   * @param name 名字
   * @return 对象
   * <p>
   * User getByIdOrName(Long id, String name);
   */
  User getByIdOrName(@Param("userId") Long id, @Param("userName") String name);

  /**
   * 查询用户id列表
   *
   * @param id id
   * @return 用户列表
   */
  List<User> getListByIdCollection(Collection<Long> id);

  /**
   * 查询用户id列表
   *
   * @param id id
   * @return 用户列表
   */
  List<User> getListByIdList(List<Long> id);

  /**
   * 查询用户id列表
   *
   * @param idArray id数组
   * @return 用户列表
   */
  List<User> getListByIdArray(Long[] idArray);

  void getList(ResultHandler<User> resultHandler);
}
