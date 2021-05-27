package com.vince.demo.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物条目
 *
 * @author vince
 * @since v1.0.0
 */
@Data
public class OrderItem {
  private long id;
  private Product product;
  private int amount;
  private BigDecimal price;
  private long orderId;

}
