package com.vince.demo.dao;

import com.vince.demo.domain.Order;

import java.util.List;


/**
 * Created on 2020-10-29
 */
public interface OrderMapper {
  /**
   * 根据订单Id查询
   *
   * @param id 订单id
   * @return 订单
   */
  Order find(long id);

  /**
   * 查询一个用户一段时间段内的订单列表
   *
   * @param customerId 客户id
   * @param startTime  开始时间
   * @param endTime    结束时间
   * @return 订单列表
   */
  List<Order> findByCustomerId(long customerId, long startTime, long endTime);

  /**
   * 保存订单
   *
   * @param order 订单
   * @return 成功为1，失败为0
   */
  long save(Order order);
}
