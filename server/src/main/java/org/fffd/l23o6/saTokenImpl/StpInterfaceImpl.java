package org.fffd.l23o6.saTokenImpl;

import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {
    private final UserDao userDao;
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return null;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String usrName = loginId.toString();
        UserEntity entity = userDao.findByUsername(usrName);
        String trueType = entity.getRole();
        List<String> toRet = new ArrayList<>();
        toRet.add(trueType);
        return toRet;
    }
}
