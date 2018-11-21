package dao;

import entity.UserDO;
import middleware.DbTool;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jinzhimin
 * @description: User用户Dao。
 */
public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    /**
     * 新增用户
     */
    public static void insertUser(UserDO user) {
        SqlSession session = DbTool.getSession();
        try {
            int insertResult = session.insert("insertUser", user);
            session.commit();
            logger.info("添加" + insertResult + "条数据。");
        } catch (Exception ex) {
            logger.error("添加数据出错", ex);
            if (session != null) {
                session.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * 新增用户列表
     */
    public static void insertUserList(List<UserDO> userList) {
        SqlSession session = DbTool.getSession();
        try {
            int insertResult = session.insert("insertUserList", userList);
            session.commit();
            logger.info("添加" + insertResult + "条数据。");
        } catch (Exception ex) {
            logger.error("添加数据出错", ex);
            if (session != null) {
                session.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public static UserDO selectUserById(int id){
        UserDO user = null;

        SqlSession session = DbTool.getSession();
        try {
            user = session.selectOne("selectUserById", id);
            session.commit();
        } catch (Exception ex) {
            logger.error("查询数据出错", ex);
            if (session != null) {
                session.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return user;
    }


}
