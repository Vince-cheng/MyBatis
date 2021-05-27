package com.vince.demo.domain;

import lombok.Data;

import java.math.BigDecimal;


/**
 * 产品
 *
 * @author vince
 * @since v1.0.0
 */
@Data
public class Product {
  private long id;
  private String name;
  private String description;
  private BigDecimal price;

}
