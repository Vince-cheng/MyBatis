/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.builder.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.JdbcType;

/**
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */
public class XMLConfigBuilder extends BaseBuilder {

  private boolean parsed;
  private final XPathParser parser;
  private String environment;
  private final ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();

  public XMLConfigBuilder(Reader reader) {
    this(reader, null, null);
  }

  public XMLConfigBuilder(Reader reader, String environment) {
    this(reader, environment, null);
  }

  public XMLConfigBuilder(Reader reader, String environment, Properties props) {
    this(new XPathParser(reader, true, props, new XMLMapperEntityResolver()), environment, props);
  }

  public XMLConfigBuilder(InputStream inputStream) {
    this(inputStream, null, null);
  }

  public XMLConfigBuilder(InputStream inputStream, String environment) {
    this(inputStream, environment, null);
  }

  public XMLConfigBuilder(InputStream inputStream, String environment, Properties props) {
    this(new XPathParser(inputStream, true, props, new XMLMapperEntityResolver()), environment, props);
  }

  private XMLConfigBuilder(XPathParser parser, String environment, Properties props) {
    super(new Configuration());
    ErrorContext.instance().resource("SQL Mapper Configuration");
    this.configuration.setVariables(props);
    this.parsed = false;
    this.environment = environment;
    this.parser = parser;
  }

  public Configuration parse() {
    if (parsed) {
      throw new BuilderException("Each XMLConfigBuilder can only be used once.");
    }
    parsed = true;
    // 解析配置
    parseConfiguration(parser.evalNode("/configuration"));
    return configuration;
  }

  private void parseConfiguration(XNode root) {
    try {
      //issue #117 read properties first
      // 解析 properties 配置
      propertiesElement(root.evalNode("properties"));

      // 解析 settings 配置，并将其转换为 Properties 对象
      Properties settings = settingsAsProperties(root.evalNode("settings"));

      // 加载 vfs
      loadCustomVfs(settings);

      loadCustomLogImpl(settings);

      // 解析 typeAliases 配置
      typeAliasesElement(root.evalNode("typeAliases"));

      // 解析 plugins 配置
      pluginElement(root.evalNode("plugins"));

      // 解析 objectFactory 配置
      objectFactoryElement(root.evalNode("objectFactory"));

      // 解析 objectWrapperFactory 配置
      objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));

      // 解析 reflectorFactory 配置
      reflectorFactoryElement(root.evalNode("reflectorFactory"));

      // settings 中的信息设置到 Configuration 对象中
      settingsElement(settings);

      // read it after objectFactory and objectWrapperFactory issue #631

      // 解析 environments 配置
      environmentsElement(root.evalNode("environments"));

      // 解析 databaseIdProvider，获取并设置 databaseId 到 Configuration 对象
      databaseIdProviderElement(root.evalNode("databaseIdProvider"));

      // 解析 typeHandlers 配置
      typeHandlerElement(root.evalNode("typeHandlers"));

      // 解析 mappers 配置
      mapperElement(root.evalNode("mappers"));
    } catch (Exception e) {
      throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
    }
  }

  /**
   * 思路如下
   * 1. 解析 settings 子节点的内容，并将解析结果转成 Properties 对象
   * 2. 为 Configuration 创建元信息对象
   * 3. 通过 MetaClass 检测 Configuration 中是否存在某个属性的 setter 方法，
   * 不存在则抛异常
   * 4. 若通过 MetaClass 的检测，则返回 Properties 对象，方法逻辑结束
   */
  private Properties settingsAsProperties(XNode context) {
    if (context == null) {
      return new Properties();
    }

    // 处理 <settings> 标签的所有子标签，也就是 <setting> 标签，将其 name 属性和 value 属性整理到 Properties 对象中保存
    Properties props = context.getChildrenAsProperties();

    // 创建 Configuration 对应的 MetaClass 对象
    MetaClass metaConfig = MetaClass.forClass(Configuration.class, localReflectorFactory);

    for (Object key : props.keySet()) {
      // 检测 Configuration 对象中是否包含每个配置项的 setter 方法，不存在则抛出异常
      if (!metaConfig.hasSetter(String.valueOf(key))) {
        throw new BuilderException("The setting " + key + " is not known.  Make sure you spelled it correctly (case sensitive).");
      }
    }
    return props;
  }

  private void loadCustomVfs(Properties props) throws ClassNotFoundException {
    String value = props.getProperty("vfsImpl");
    if (value != null) {
      String[] clazzes = value.split(",");
      for (String clazz : clazzes) {
        if (!clazz.isEmpty()) {
          @SuppressWarnings("unchecked")
          Class<? extends VFS> vfsImpl = (Class<? extends VFS>)Resources.classForName(clazz);
          configuration.setVfsImpl(vfsImpl);
        }
      }
    }
  }

  private void loadCustomLogImpl(Properties props) {
    Class<? extends Log> logImpl = resolveClass(props.getProperty("logImpl"));
    configuration.setLogImpl(logImpl);
  }

  /**
   * 1. 通过 VFS（虚拟文件系统）获取指定包下的所有文件的路径名
   * 2. 比如 com/vince/demo1/User.class
   * 3. 筛选以.class 结尾的文件名
   * 4. 将路径名转成全限定的类名，通过类加载器加载类名
   * 5. 对类型进行匹配，若符合匹配规则，则将其放入内部集合中
   */
  private void typeAliasesElement(XNode parent) {
    if (parent != null) {
      for (XNode child : parent.getChildren()) {
        // 从指定的包中解析别名和类型的映射
        if ("package".equals(child.getName())) {
          String typeAliasPackage = child.getStringAttribute("name");
          configuration.getTypeAliasRegistry().registerAliases(typeAliasPackage);
        }
        // 从 typeAlias 节点中解析别名和类型的映射
        else {
          String alias = child.getStringAttribute("alias");
          String type = child.getStringAttribute("type");
          try {
            // 加载 type 对应的类型
            Class<?> clazz = Resources.classForName(type);
            // 注册别名到类型的映射
            if (alias == null) {
              typeAliasRegistry.registerAlias(clazz);
            } else {
              typeAliasRegistry.registerAlias(alias, clazz);
            }
          } catch (ClassNotFoundException e) {
            throw new BuilderException("Error registering typeAlias for '" + alias + "'. Cause: " + e, e);
          }
        }
      }
    }
  }

  private void pluginElement(XNode parent) throws Exception {
    if (parent != null) {
      // 遍历全部的 <plugin> 子标签
      for (XNode child : parent.getChildren()) {
        // 获取每个 <plugin> 标签中的 interceptor 属性
        String interceptor = child.getStringAttribute("interceptor");
        // 获取 <plugin> 标签下的其他配置信息
        Properties properties = child.getChildrenAsProperties();
        // 初始化 interceptor 属性指定的自定义插件
        Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).getDeclaredConstructor().newInstance();
        // 初始化插件的配置
        interceptorInstance.setProperties(properties);
        // 将 Interceptor 对象添加到 Configuration 的插件链中保存，等待后续使用
        configuration.addInterceptor(interceptorInstance);
      }
    }
  }

  private void objectFactoryElement(XNode context) throws Exception {
    if (context != null) {

      // 获取 <objectFactory> 标签的 type 属性
      String type = context.getStringAttribute("type");

      // 根据 type 属性值，初始化自定义的 ObjectFactory 实现
      Properties properties = context.getChildrenAsProperties();

      // 初始化 ObjectFactory 对象的配置
      ObjectFactory factory = (ObjectFactory) resolveClass(type).getDeclaredConstructor().newInstance();
      factory.setProperties(properties);

      // 将 ObjectFactory 对象记录到 Configuration 这个全局配置对象中
      configuration.setObjectFactory(factory);
    }
  }

  private void objectWrapperFactoryElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      ObjectWrapperFactory factory = (ObjectWrapperFactory) resolveClass(type).getDeclaredConstructor().newInstance();
      configuration.setObjectWrapperFactory(factory);
    }
  }

  private void reflectorFactoryElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      ReflectorFactory factory = (ReflectorFactory) resolveClass(type).getDeclaredConstructor().newInstance();
      configuration.setReflectorFactory(factory);
    }
  }

  private void propertiesElement(XNode context) throws Exception {
    if (context != null) {
      // 解析 properties 的子节点，并将这些节点内容转换为属性对象 Properties
      Properties defaults = context.getChildrenAsProperties();

      // 获取 properties 节点中的 resource 和 url 属性值
      String resource = context.getStringAttribute("resource");
      String url = context.getStringAttribute("url");

      // 两者都不用空，则抛出异常
      if (resource != null && url != null) {
        throw new BuilderException("The properties element cannot specify both a URL and a resource based property file reference.  Please specify one or the other.");
      }
      if (resource != null) {
        // 从文件系统中加载并解析属性文件
        defaults.putAll(Resources.getResourceAsProperties(resource));
      } else if (url != null) {
        // 通过 url 加载并解析属性文件
        defaults.putAll(Resources.getUrlAsProperties(url));
      }
      Properties vars = configuration.getVariables();
      if (vars != null) {
        defaults.putAll(vars);
      }
      parser.setVariables(defaults);

      // 将属性值设置到 configuration 中
      configuration.setVariables(defaults);
    }
  }

  private void settingsElement(Properties props) {
    // 设置 autoMappingBehavior 属性，默认值为 PARTIAL
    configuration.setAutoMappingBehavior(AutoMappingBehavior.valueOf(props.getProperty("autoMappingBehavior", "PARTIAL")));
    configuration.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.valueOf(props.getProperty("autoMappingUnknownColumnBehavior", "NONE")));
    // 设置 cacheEnabled 属性，默认值为 true
    configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
    configuration.setProxyFactory((ProxyFactory) createInstance(props.getProperty("proxyFactory")));
    configuration.setLazyLoadingEnabled(booleanValueOf(props.getProperty("lazyLoadingEnabled"), false));
    configuration.setAggressiveLazyLoading(booleanValueOf(props.getProperty("aggressiveLazyLoading"), false));
    configuration.setMultipleResultSetsEnabled(booleanValueOf(props.getProperty("multipleResultSetsEnabled"), true));
    configuration.setUseColumnLabel(booleanValueOf(props.getProperty("useColumnLabel"), true));
    configuration.setUseGeneratedKeys(booleanValueOf(props.getProperty("useGeneratedKeys"), false));
    configuration.setDefaultExecutorType(ExecutorType.valueOf(props.getProperty("defaultExecutorType", "SIMPLE")));
    configuration.setDefaultStatementTimeout(integerValueOf(props.getProperty("defaultStatementTimeout"), null));
    configuration.setDefaultFetchSize(integerValueOf(props.getProperty("defaultFetchSize"), null));
    configuration.setDefaultResultSetType(resolveResultSetType(props.getProperty("defaultResultSetType")));
    configuration.setMapUnderscoreToCamelCase(booleanValueOf(props.getProperty("mapUnderscoreToCamelCase"), false));
    configuration.setSafeRowBoundsEnabled(booleanValueOf(props.getProperty("safeRowBoundsEnabled"), false));
    configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope", "SESSION")));
    configuration.setJdbcTypeForNull(JdbcType.valueOf(props.getProperty("jdbcTypeForNull", "OTHER")));
    configuration.setLazyLoadTriggerMethods(stringSetValueOf(props.getProperty("lazyLoadTriggerMethods"), "equals,clone,hashCode,toString"));
    configuration.setSafeResultHandlerEnabled(booleanValueOf(props.getProperty("safeResultHandlerEnabled"), true));
    configuration.setDefaultScriptingLanguage(resolveClass(props.getProperty("defaultScriptingLanguage")));
    // 设置默认枚举处理器
    configuration.setDefaultEnumTypeHandler(resolveClass(props.getProperty("defaultEnumTypeHandler")));
    configuration.setCallSettersOnNulls(booleanValueOf(props.getProperty("callSettersOnNulls"), false));
    configuration.setUseActualParamName(booleanValueOf(props.getProperty("useActualParamName"), true));
    configuration.setReturnInstanceForEmptyRow(booleanValueOf(props.getProperty("returnInstanceForEmptyRow"), false));
    configuration.setLogPrefix(props.getProperty("logPrefix"));
    configuration.setConfigurationFactory(resolveClass(props.getProperty("configurationFactory")));
  }

  private void environmentsElement(XNode context) throws Exception {
    if (context != null) {
      // 未指定使用的环境 id，默认获取 default 值 
      if (environment == null) {
        // 获取 default 属性
        environment = context.getStringAttribute("default");
      }
      // 获取 <environment> 标签下的所有配置
      for (XNode child : context.getChildren()) {
        // 获取 id 属性
        String id = child.getStringAttribute("id");
        // 检测当前 environment 节点的 id 与其父节点 environments 的属性 default 内容是否一致，一致则返回 true，否则返回 false
        if (isSpecifiedEnvironment(id)) {
          // 获取 <transactionManager> 标签，并进行解析，根据配置信息初始化相应的 TransactionFactory 对象
          TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
          // 获取 <dataSource> 标签，并进行解析，根据配置信息初始化相应的 DataSource 对象
          DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
          // 创建 DataSource 对象
          DataSource dataSource = dsFactory.getDataSource();
          // 创建 Environment 对象，并关联创建好的 TransactionFactory 和 DataSource
          Environment.Builder environmentBuilder = new Environment.Builder(id)
              .transactionFactory(txFactory)
              .dataSource(dataSource);
          // 将 Environment 对象记录到 Configuration 中，等待后续使用
          configuration.setEnvironment(environmentBuilder.build());
        }
      }
    }
  }

  private void databaseIdProviderElement(XNode context) throws Exception {
    DatabaseIdProvider databaseIdProvider = null;
    if (context != null) {
      // 获取 type 属性值
      String type = context.getStringAttribute("type");
      // 兼容操作
      if ("VENDOR".equals(type)) {
        type = "DB_VENDOR";
      }
      // 初始化 DatabaseIdProvider
      Properties properties = context.getChildrenAsProperties();
      databaseIdProvider = (DatabaseIdProvider) resolveClass(type).getDeclaredConstructor().newInstance();
      databaseIdProvider.setProperties(properties);
    }
    Environment environment = configuration.getEnvironment();
    if (environment != null && databaseIdProvider != null) {
      // 通过 DataSource 获取 DatabaseId，并保存到 Configuration 中，等待后续使用
      String databaseId = databaseIdProvider.getDatabaseId(environment.getDataSource());
      configuration.setDatabaseId(databaseId);
    }
  }

  private TransactionFactory transactionManagerElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      Properties props = context.getChildrenAsProperties();
      TransactionFactory factory = (TransactionFactory) resolveClass(type).getDeclaredConstructor().newInstance();
      factory.setProperties(props);
      return factory;
    }
    throw new BuilderException("Environment declaration requires a TransactionFactory.");
  }

  private DataSourceFactory dataSourceElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      Properties props = context.getChildrenAsProperties();
      DataSourceFactory factory = (DataSourceFactory) resolveClass(type).getDeclaredConstructor().newInstance();
      factory.setProperties(props);
      return factory;
    }
    throw new BuilderException("Environment declaration requires a DataSourceFactory.");
  }

  private void typeHandlerElement(XNode parent) {
    if (parent != null) {
      // 处理全部 <typeHandler> 子标签
      for (XNode child : parent.getChildren()) {
        // 如果指定了 package 属性，则扫描指定包中所有的类，并解析 @MappedTypes 注解，完成 TypeHandler 的注册
        if ("package".equals(child.getName())) {
          String typeHandlerPackage = child.getStringAttribute("name");
          // 注册方法1
          typeHandlerRegistry.register(typeHandlerPackage);
        }
        // 从 typeHandler 节点中解析别名到类型的映射
        else {
          // 如果没有指定 package 属性，则尝试获取 javaType、jdbcType、handler 三个属性
          String javaTypeName = child.getStringAttribute("javaType");
          String jdbcTypeName = child.getStringAttribute("jdbcType");
          String handlerTypeName = child.getStringAttribute("handler");

          // 根据属性确定 TypeHandler 类型以及它能够处理的数据库类型和 Java 类型
          Class<?> javaTypeClass = resolveClass(javaTypeName);
          JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
          Class<?> typeHandlerClass = resolveClass(handlerTypeName);

          // 根据 javaTypeClass 和 jdbcType 值的情况进行不同的注册策略
          if (javaTypeClass != null) {
            if (jdbcType == null) {
              // 注册方法2
              typeHandlerRegistry.register(javaTypeClass, typeHandlerClass);
            } else {
              // 注册方法3
              typeHandlerRegistry.register(javaTypeClass, jdbcType, typeHandlerClass);
            }
          } else {
            // 注册方法4
            typeHandlerRegistry.register(typeHandlerClass);
          }
        }
      }
    }
  }

  private void mapperElement(XNode parent) throws Exception {
    if (parent != null) {
      // 遍历每个子标签
      for (XNode child : parent.getChildren()) {
        if ("package".equals(child.getName())) {
          // 获取 <package> 节点中的 name 属性
          String mapperPackage = child.getStringAttribute("name");
          // 从指定包中查找 mapper 接口，并根据 mapper 接口解析映射配置
          configuration.addMappers(mapperPackage);
        } else {
          // 解析 <mapper> 子标签，这里会获取 resource、url、class 三个属性，这三个属性互斥
          String resource = child.getStringAttribute("resource");
          String url = child.getStringAttribute("url");
          String mapperClass = child.getStringAttribute("class");

          // 如果 <mapper> 子标签指定了 resource 或是 url 属性，都会创建 XMLMapperBuilder 对象，
          // 然后使用这个 XMLMapperBuilder 实例解析指定的 Mapper.xml 配置文件
          if (resource != null && url == null && mapperClass == null) {
            ErrorContext.instance().resource(resource);
            InputStream inputStream = Resources.getResourceAsStream(resource);
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            // 解析映射文件
            mapperParser.parse();
          }
          // url 不为空，且其他两者为空，则通过 url 加载配置
          else if (resource == null && url != null && mapperClass == null) {
            ErrorContext.instance().resource(url);
            InputStream inputStream = Resources.getUrlAsStream(url);
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
            // 解析映射文件
            mapperParser.parse();
          }
          // 如果 <mapper> 子标签指定了 class 属性，则向 MapperRegistry 注册 class 属性指定的 Mapper 接口
          else if (resource == null && url == null && mapperClass != null) {
            Class<?> mapperInterface = Resources.classForName(mapperClass);
            configuration.addMapper(mapperInterface);
          }
          // 以上条件不满足，则抛出异常
          else {
            throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
          }
        }
      }
    }
  }

  private boolean isSpecifiedEnvironment(String id) {
    if (environment == null) {
      throw new BuilderException("No environment specified.");
    } else if (id == null) {
      throw new BuilderException("Environment requires an id attribute.");
    } else if (environment.equals(id)) {
      return true;
    }
    return false;
  }

}
