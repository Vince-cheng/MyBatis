package com.vince.demo.dao;


import com.vince.demo.domain.Address;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author vince
 * @since v1.0.0
 */
public interface AddressMapper {
  /**
   * 根据 id 查询Address对象
   *
   * @param id 地址id
   * @return 地址信息
   */
  Address find(long id);

  /**
   * 查询一个用户的全部地址信息
   *
   * @param customerId 客户id
   * @return 客户的所有地址
   */
  List<Address> findAll(long customerId);

  /**
   * 查询指定订单的送货地址
   *
   * @param orderId 订单id
   * @return 地址
   */
  Address findByOrderId(long orderId);

  /**
   * 保存 Address，同时会记录关联的 Customer
   *
   * @param address    地址信息
   * @param customerId 客户id
   * @return 成功返回1，失败返回0
   */
  int save(@Param("address") Address address,
           @Param("customerId") long customerId);
}
