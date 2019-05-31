package config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

/**
 * @author jinzhimin
 * @description: 继承 UnpooledDataSourceFactory 并实现 dataSource，构建 c3p0 数据库连接池
 */
public class C3p0DataSourceFactory extends UnpooledDataSourceFactory {
  public C3p0DataSourceFactory() {
    this.dataSource = new ComboPooledDataSource();
  }
}
