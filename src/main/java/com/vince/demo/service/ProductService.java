package com.vince.demo.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.vince.demo.dao.ProductMapper;
import com.vince.demo.domain.Product;
import com.vince.demo.utils.DaoUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author vince
 * @since v1.0.0
 */
public class ProductService {

  /**
   * 创建商品
   *
   * @param product 商品
   * @return 成功为1，失败为0
   */
  public long createProduct(Product product) {
    // 检查product中的各个字段是否合法
    Preconditions.checkArgument(product != null, "product is null");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(product.getName()), "product name is empty");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(product.getDescription()), "description name is empty");
    Preconditions.checkArgument(product.getPrice().compareTo(new BigDecimal(0)) > 0,
      "price<=0 error");
    return DaoUtils.execute(sqlSession -> {
      // 通过ProductMapper中的save()方法完成持久化
      ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
      return productMapper.save(product);
    });
  }

  /**
   * 根据商品id查找商品
   *
   * @param productId 商品id
   * @return 商品
   */
  public Product find(long productId) {
    // 检查productId参数是否合法
    Preconditions.checkArgument(productId > 0, "product id error");
    return DaoUtils.execute(sqlSession -> {
      // 通过ProductMapper中的find()方法精确查询Product
      ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
      return productMapper.find(productId);
    });
  }

  /**
   * 根据商品名称查询商品
   *
   * @param productName 商品名称
   * @return 商品列表
   */
  public List<Product> find(String productName) {
    // 检查productName参数是否合法
    Preconditions.checkArgument(Strings.isNullOrEmpty(productName), "product id error");
    return DaoUtils.execute(sqlSession -> {
      // 根据productName模糊查询Product
      ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
      return productMapper.findByName(productName);
    });
  }
}
