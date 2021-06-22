package com.vince.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.tools.ant.types.resources.selectors.InstanceOf;

import javax.management.Query;
import java.util.Properties;

/**
 * @author vince
 * @since v1.0.0
 */
@Intercepts({
  @Signature(type = Executor.class, method = "query", args = {
    MappedStatement.class, Object.class, RowBounds.class,
    ResultHandler.class}),
  @Signature(type = Executor.class, method = "close", args = {boolean.class})
})
public class DemoPlugin implements Interceptor {
  private int logLevel;


  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object[] queryArgs = invocation.getArgs();
    // 对关闭操作进行处理
    if (queryArgs[0] instanceof Boolean) {
      return null;
    }
    MappedStatement mappedStatement = (MappedStatement) queryArgs[0];
    Object parameter = queryArgs[1];
    BoundSql boundSql = mappedStatement.getBoundSql(parameter);
    //获取到SQL ，可以进行调整
    String sql = boundSql.getSql();
    System.err.println(sql);
    String name = invocation.getMethod().getName();
    System.err.println("拦截的方法名是：" + name);
    return invocation.proceed();
  }

  @Override
  public Object plugin(Object target) {
    // 依赖 Plugin 工具类创建代理对象
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {

  }
}
