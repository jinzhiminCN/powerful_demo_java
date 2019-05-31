package dao;

import entity.UserDO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GsonUtil;

/**
 * @author jinzhimin
 * @description: ${description}
 */
public class UserDaoTest {
    private static Logger logger = LoggerFactory.getLogger(UserDaoTest.class);

    public static void insertList(){
        int length = 10;
        List<UserDO> userList = new ArrayList<>(length);
        for(int i = 0; i < length; i++){
            UserDO user = new UserDO();

            user.setUserName("user" + i);
            user.setUserPasswd("passwd" + i);
            user.setAccount(new BigDecimal(i+""));

            userList.add(user);
        }

        UserDao.insertUserList(userList);
    }

    public static void insertUser(){
        UserDO user = new UserDO();

        user.setUserName("user A");
        user.setUserPasswd("passwd A");
        user.setAccount(new BigDecimal("12"));

        UserDao.insertUser(user);
    }

    public static void selectById(){
        UserDO user = UserDao.selectUserById(2);
        logger.info(GsonUtil.getGson().toJson(user));
    }

    public static void main(String[] args) {
//        PropertyConfigurator.configure("src/main/resource/log4j.properties");

        selectById();
    }
}
