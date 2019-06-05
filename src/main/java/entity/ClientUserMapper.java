package entity;

import org.apache.ibatis.annotations.Param;

/**
 * @author jinzhimin
 * @description: ClientUserMapper
 */
public interface ClientUserMapper {
  ClientUser selectById(Integer id);

  int insertUser(ClientUser user);

  void insertUser2(ClientUser user);

  /**
   * 多个参数可以使用集合的 方式传递，map等
   * @param id
   * @param email
   * @return
   */
  ClientUser findUser(@Param("id")Integer id,@Param("email") String email);
}
