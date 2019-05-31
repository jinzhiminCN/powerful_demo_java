package config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

/**
 * @author jinzhimin
 * @description: 继承 UnpooledDataSourceFactory 并实现 dataSource，构建 Druid 数据库连接池
 */
public class DruidDataSourceFactory extends UnpooledDataSourceFactory{
  public DruidDataSourceFactory() {
    this.dataSource = new DruidDataSource();
  }
}
