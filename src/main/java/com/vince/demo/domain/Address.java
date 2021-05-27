package com.vince.demo.domain;

import lombok.Data;

/**
 * 地址
 *
 * @author vince
 * @since v1.0.0
 */
@Data
public class Address {
  private long id;
  private String street;
  private String city;
  private String country;

}
