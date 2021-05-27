package com.vince.demo.dao;

import com.vince.demo.domain.Product;

import java.util.List;

/**
 * @author vince
 * @since v1.0.0
 */
public interface ProductMapper {
  /**
   * 根据id查询商品信息
   *
   * @param id 产品id
   * @return 产品
   */
  Product find(long id);

  /**
   * 根据名称搜索商品信息
   *
   * @param name 产品名称
   * @return 同类产品列表
   */
  List<Product> findByName(String name);

  /**
   * 保存商品信息
   *
   * @param product 商品
   * @return 成功为1，失败为0
   */
  long save(Product product);
}
