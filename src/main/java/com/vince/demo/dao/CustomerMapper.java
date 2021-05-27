package com.vince.demo.dao;

import com.vince.demo.domain.Customer;

/**
 * @author vince
 * @since v1.0.0
 */
public interface CustomerMapper {

  /**
   * 根据客户 id 查询 Customer(不查询 Address)
   *
   * @param id 客户id
   * @return 客户
   */
  Customer find(Long id);

  /**
   * 根据客户 id 查询 Customer(同时查询 Address)
   *
   * @param id 客户id
   * @return 客户
   */
  Customer findWithAddress(long id);

  /**
   * 根据 orderId 查询Customer
   *
   * @param orderId 订单 id
   * @return 客户
   */
  Customer findByOrderId(long orderId);

  /**
   * 保存
   *
   * @param customer 用户
   * @return 成功为1，失败为0
   */
  int save(Customer customer);
}
