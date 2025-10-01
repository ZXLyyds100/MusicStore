package com.music.store.studioproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.music.store.studioproject.dao.UserDao;
import com.music.store.studioproject.dto.ChangePasswordDto;
import com.music.store.studioproject.entity.User;
import com.music.store.studioproject.exception.BusinessException;
import com.music.store.studioproject.service.UserService;
import com.music.store.studioproject.utils.Response;
import com.music.store.studioproject.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public void saveUser(User newUser) {
        User user = userDao.findByUsername(newUser.getUsername());
        if (user != null) {
            throw BusinessException.userAlreadyExists("用户已存在");
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userDao.insert(newUser);

    }

    @Override
    public Response changePassword(ChangePasswordDto changePasswordDto) {
        Long userId = UserContext.getUserId();
        User user = userDao.findById(userId);
        if (user == null) {
            return Response.fail("用户不存在");
        }
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            return Response.fail("旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId);
        int rows = userDao.update(user, updateWrapper);
        if (rows > 0) {
            return Response.success("密码修改成功");
        } else {
            return Response.fail("密码修改失败");
        }

    }
}
