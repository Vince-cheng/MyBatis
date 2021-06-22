package com.vince.demo;

import com.google.common.collect.Lists;
import com.vince.demo.domain.Address;
import com.vince.demo.domain.Customer;
import com.vince.demo.domain.Order;
import com.vince.demo.domain.OrderItem;
import com.vince.demo.domain.Product;
import com.vince.demo.service.CustomerService;
import com.vince.demo.service.OrderService;
import com.vince.demo.service.ProductService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author vince
 * @since v1.0.0
 */
public class ServiceTest {

  private static CustomerService customerService;

  private static OrderService orderService;

  private static ProductService productService;

  @Before
  public void init() {
    customerService = new CustomerService();
    orderService = new OrderService();
    productService = new ProductService();
  }

  @Test
  public void testPlugin(){
    Customer customer = customerService.find(1L);
    System.out.println(customer);
  }


  @Test
  public void testShop() {
    // 创建一个用户
    long customerId = customerService.register("vince", "18827052946");
    // 为用户添加一个配送地址
    long addressId = customerService.addAddress(customerId, "江夏区", "武汉市", "湖北省");
    System.out.println(addressId);
    // 查询用户信息以及地址信息
    Customer customer = customerService.find(customerId);
    System.out.println(customer);
    Customer customer2 = customerService.findWithAddress(customerId);
    System.out.println(customer2);
    List<Address> addressList = customerService.findAllAddress(customerId);
    addressList.forEach(System.out::println);

    // 入库一些商品
    Product product = new Product();
    product.setName("手机");
    product.setDescription("华为手机");
    product.setPrice(new BigDecimal(3999));
    long productId = productService.createProduct(product);
    System.out.println("create productId:" + productId);

    // 创建一个订单
    Order order = new Order();
    // 买家
    order.setCustomer(customer);
    // 配送地址
    order.setDeliveryAddress(addressList.get(0));
    order.setCreateTime(new Date());
    // 生成购买条目
    OrderItem orderItem = new OrderItem();
    orderItem.setAmount(20);
    orderItem.setProduct(product);
    order.setOrderItems(Lists.newArrayList(orderItem));
    long orderId = orderService.createOrder(order);
    System.out.println("create orderId:" + orderId);
    Order order2 = orderService.find(orderId);
    System.out.println(order2);
  }
}
