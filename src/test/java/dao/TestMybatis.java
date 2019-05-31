package dao;


import entity.ClientUser;
import entity.ClientUserMapper;
import java.util.Date;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GsonUtil;

public class TestMybatis {
	private static Logger logger = LoggerFactory.getLogger(UserDaoTest.class);

	public static void main(String[] args) {
//		new TestMybatis().insertGetId();
		new TestMybatis().testSelectById();
	}

	public void insertGetId(){
		SqlSession sqlSession = MybatisUtil.getSqlSession();
		ClientUserMapper userMapper = sqlSession.getMapper(ClientUserMapper.class);

		ClientUser clientUser = new ClientUser();
		clientUser.setEmail("332768@163.com");
		clientUser.setName("zhangsan");
		clientUser.setSigntime(new Date());
		userMapper.insertUser2(clientUser);
		//默认事务关闭，需要手动提交事务
		sqlSession.commit();
		sqlSession.close();

		logger.info(clientUser.getId() + "");
	}

	public void testSelectById(){


		//简单工程log4j 属性文件 需要使用加载此属性文件
//		PropertyConfigurator.configure("src/main/resource/log4j.properties");
		
		/**使用接口方式 查询数据库*/
		SqlSession sqlSession = MybatisUtil.getSqlSession();
		ClientUserMapper userMapper = sqlSession.getMapper(ClientUserMapper.class);

		ClientUser user = userMapper.selectById(2);
		logger.info(GsonUtil.getGson().toJson(user));

		/** 使用一般的方式查询数据库  命名空间+方法名 ，参数*/
		Object one = sqlSession.selectOne("entity.ClientUserMapper.selectById", 3);
		logger.info(((ClientUser)one).toString());


	}
	
	
}
