package com.vince.demo.service;

import java.util.List;


import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.vince.demo.dao.AddressMapper;
import com.vince.demo.dao.CustomerMapper;
import com.vince.demo.domain.Address;
import com.vince.demo.domain.Customer;
import com.vince.demo.utils.DaoUtils;

/**
 * @author vince
 * @since v1.0.0
 */
public class CustomerService {

  /**
   * 创建一个新用户
   *
   * @param name  客户名
   * @param phone 手机号
   * @return 客户id
   */
  public long register(String name, String phone) {
    // 检查传入的name参数以及phone参数是否合法
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "name is empty");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(phone), "phone is empty");
    // 还可以完成其他业务逻辑，例如检查用户名是否重复，手机号是否重复等等
    return DaoUtils.execute(sqlSession -> {
      // 创建Customer对象，并通过CustomerMapper.save()方法完成持久化
      CustomerMapper mapper = sqlSession.getMapper(CustomerMapper.class);
      Customer customer = new Customer();
      customer.setName(name);
      customer.setPhone(phone);
      int affected = mapper.save(customer);
      if (affected <= 0) {
        throw new RuntimeException("Save Customer fail...");
      }
      return customer.getId();
    });
  }

  /**
   * 用户添加一个新的送货地址
   *
   * @param customerId 客户id
   * @param street     街道
   * @param city       城市
   * @param country    国家
   * @return 地址id
   */
  public long addAddress(long customerId, String street, String city, String country) {
    // 检查传入的name参数以及phone参数是否合法
    Preconditions.checkArgument(customerId > 0, "customerId is empty");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(street), "street is empty");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(city), "city is empty");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(country), "country is empty");
    // 还可以完成其他业务逻辑，例如检查该地址是否超出了送货范围等，这里不再展示
    return DaoUtils.execute(sqlSession -> {
      // 创建Address对象并调用AddressMapper.save()方法完成持久化
      AddressMapper mapper = sqlSession.getMapper(AddressMapper.class);
      Address address = new Address();
      address.setStreet(street);
      address.setCity(city);
      address.setCountry(country);
      int affected = mapper.save(address, customerId);
      if (affected <= 0) {
        throw new RuntimeException("Save Customer fail...");
      }
      return address.getId();
    });
  }

  /**
   * 查询所有地址
   *
   * @param customerId 客户id
   * @return 客户所有地址
   */
  public List<Address> findAllAddress(long customerId) {
    // 检查用户id参数是否合法
    Preconditions.checkArgument(customerId > 0, "id error");
    return DaoUtils.execute(sqlSession -> {
      // 执行AddressMapper.find()方法完成查询
      AddressMapper mapper = sqlSession.getMapper(AddressMapper.class);
      return mapper.findAll(customerId);
    });
  }

  /**
   * 通过客户id查找客户
   *
   * @param id 客户id
   * @return 客户
   */
  public Customer find(long id) {
    // 检查用户id参数是否合法
    Preconditions.checkArgument(id > 0, "id error");
    return DaoUtils.execute(sqlSession -> {
      // 执行CustomerMapper.find()方法完成查询
      CustomerMapper mapper = sqlSession.getMapper(CustomerMapper.class);
      return mapper.find(id);
    });
  }

  /**
   * 根据客户id查找客户并查找客户地址
   *
   * @param id 客户id
   * @return 客户信息包括地址
   */
  public Customer findWithAddress(long id) {
    // 检查用户id参数是否合法
    Preconditions.checkArgument(id > 0, "id error");
    return DaoUtils.execute(sqlSession -> {
      // 执行CustomerMapper.findWithAddress()方法完成查询
      CustomerMapper mapper = sqlSession.getMapper(CustomerMapper.class);
      return mapper.findWithAddress(id);
    });
  }
}
