package org.fffd.l23o6.UnitTests;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * 这个单元测试用于测试UserService接口类的功能
 * 为了验证数据是否入库，必须引入UserDao类用于拉取数据库数据
 * */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Test
    public void registerUser(){
        String pwd = "Abc123456";
        String name = "测试";
        String idn = "320683200404235723";
        String phone = "15372778964";
        String type = "身份证";
        userService.register("testa", pwd, name,
                idn, phone, type);
        UserEntity getUserEntity = userDao.findByUsername("testa");

        assertEquals(getUserEntity.getIdn(), idn);
        assertEquals( getUserEntity.getName() ,name);
        assertEquals( getUserEntity.getPhone() , phone);
        assertEquals(getUserEntity.getType() , type);

        //这里是为了验证密码密文存储
        assertNotEquals(getUserEntity.getPassword(), pwd);

    }
}
