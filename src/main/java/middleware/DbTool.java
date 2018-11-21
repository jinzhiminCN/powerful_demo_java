package middleware;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;

/**
 * @author jinzhimin
 * @description: 数据库工具类
 */
public class DbTool {
    private static final Logger logger = LoggerFactory.getLogger(ActiveMqDemo.class);

    public static SqlSessionFactory sessionFactory;

    static{
        try {
            // 使用MyBatis提供的Resources类加载mybatis的配置文件
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            // 构建sqlSession的工厂
            sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (Exception e) {
            logger.info("生成Mybatis SqlSessionFactory出现异常！", e);
        }

    }

    /**
     * 创建能执行映射文件中sql的sqlSession
     */
    public static SqlSession getSession(){
        return sessionFactory.openSession();
    }
}
