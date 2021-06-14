package com.vince.mybatis;

import com.vince.demo.domain.Address;
import com.vince.demo.domain.Customer;
import ognl.DefaultClassResolver;
import ognl.DefaultTypeConverter;
import ognl.Ognl;
import ognl.OgnlContext;
import org.apache.ibatis.scripting.xmltags.OgnlMemberAccess;

import java.util.ArrayList;

/**
 * OGNL 基本使用
 *
 * @author vince
 * @since v1.0.0
 */
public class OgnlDemo {
  private static Customer customer;
  private static OgnlContext context;

  private static Customer createCustomer() {
    customer = new Customer();
    customer.setId(1);
    customer.setName("Test Customer");
    customer.setPhone("1234567");
    Address address = new Address();
    address.setCity("city-001");
    address.setId(1);
    address.setCountry("country-001");
    address.setStreet("street-001");
    ArrayList<Address> addresses = new ArrayList<>();
    addresses.add(address);
    customer.setAddresses(addresses);
    return customer;
  }

  public static void main(String[] args) throws Exception {
    // 创建 Customer 对象以及 Address 对象
    customer = createCustomer();

    // 创建 OgnlContext 上下文对象
    context = new OgnlContext(new DefaultClassResolver(),
      new DefaultTypeConverter(), new OgnlMemberAccess());

    // 设置 root 以及 address 这个 key，默认从 root 开始查找属性或方法
    context.setRoot(customer);
    context.put("address", customer.getAddresses().get(0));

    // Ognl.parseExpression() 方法负责解析 OGNL 表达式，获取 Customer 的 addresses 属性
    Object obj = Ognl.getValue(Ognl.parseExpression("addresses"),
      context, context.getRoot());

    // 输出是 [Address{id=1, street='street-001', city='city-001', country='country-001'}]
    System.out.println(obj);

    // 获取 city 属性
    obj = Ognl.getValue(Ognl.parseExpression("addresses[0].city"),
      context, context.getRoot());
    // 输出是 city-001
    System.out.println(obj);
    // #address 表示访问的不是 root 对象，而是 OgnlContext 中 key 为 addresses 的对象
    obj = Ognl.getValue(Ognl.parseExpression("#address.city"), context,
      context.getRoot());
    // 输出是 city-001
    System.out.println(obj);
    // 执行 Customer 的 getName() 方法
    obj = Ognl.getValue(Ognl.parseExpression("getName()"), context,
      context.getRoot());
    // 输出是 Test Customer
    System.out.println(obj);
  }
}
