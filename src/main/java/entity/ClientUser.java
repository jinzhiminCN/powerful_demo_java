package entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author jinzhimin
 * @description: 客户
 */
@Data
public class ClientUser implements Serializable {
    private Integer id;
    private Integer age;
    private String createWho;
    private String email;
    private String gender;
    private String name;
    private String pw;
    private Date signtime;
}

