package com.vince.demo2.mapper;

import com.vince.demo2.pojo.Order;

import java.util.List;

/**
 * @author vince
 * @since v1.0.0
 */
public interface OrderMapper {

  /**
   * 查询所有订单
   * @return 订单列表
   */
  List<Order> selectOrder();
}
