package com.vince.demo.dao;

import java.util.List;

import com.vince.demo.domain.OrderItem;
import org.apache.ibatis.annotations.Param;

/**
 * @author vince
 * @since v1.0.0
 */
public interface OrderItemMapper {
  /**
   * 根据 id 查询 OrderItem 对象
   *
   * @param id 购物条目id
   * @return 购物条目
   */
  OrderItem find(long id);

  /**
   * 查询指定的订单中的全部OrderItem
   *
   * @param orderId 购物条目id
   * @return 所有的购物条目
   */
  List<OrderItem> findByOrderId(long orderId);

  /**
   * 保存一个 OrderItem 信息
   *
   * @param orderItem 购物条目
   * @param orderId   订单id
   * @return 成功为1，失败为0
   */
  long save(@Param("orderItem") OrderItem orderItem,
            @Param("orderId") long orderId);
}
