package com.vince.demo.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单
 *
 * @author vince
 * @since v1.0.0
 */
@Data
public class Order {

  private long id;
  private Customer customer;
  private Address deliveryAddress;
  private List<OrderItem> orderItems = new ArrayList<>();
  private long createTime;
  private BigDecimal totalPrice;

}
