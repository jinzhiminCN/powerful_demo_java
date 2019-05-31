package dao;

import java.io.IOException;
import java.io.Reader;
import javax.sql.DataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisUtil {
  private static Logger logger = LoggerFactory.getLogger(MybatisUtil.class);

  private static SqlSessionFactory sqlSessionFactory = null;

  private MybatisUtil() {}

  static {
    try {
      Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
      // 读取配置文件 获取sqlSessionFactory
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

      Configuration configuration = sqlSessionFactory.getConfiguration();
      //      logger.info(GsonUtil.getGson().toJson(configuration));
      System.out.println(configuration);

      Environment environment = configuration.getEnvironment();
      //      logger.info(GsonUtil.getGson().toJson(environment));
      System.out.println(environment);

      DataSource dataSource = environment.getDataSource();
      //      logger.info(GsonUtil.getGson().toJson(dataSource));
      System.out.println(dataSource);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 获取sqlSession
   *
   * @return
   */
  public static SqlSession getSqlSession() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    return sqlSession;
  }
}
