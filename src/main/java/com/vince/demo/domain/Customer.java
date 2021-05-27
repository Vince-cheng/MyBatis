package com.vince.demo.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户
 *
 * @author vince
 * @since v1.0.0
 */
@Data
public class Customer {

  private long id;

  private String name;

  private String phone;

  private List<Address> addresses = new ArrayList<>();

}
