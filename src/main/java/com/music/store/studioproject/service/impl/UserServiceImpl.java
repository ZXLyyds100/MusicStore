package com.music.store.studioproject.service.impl;

import com.music.store.studioproject.dao.UserDao;
import com.music.store.studioproject.entity.User;
import com.music.store.studioproject.exception.BusinessException;
import com.music.store.studioproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Override
    public void saveUser(User newUser) {
        User user = userDao.findByUsername(newUser.getUsername());
        if (user != null) {
            throw BusinessException.userAlreadyExists("用户已存在");
        }
        userDao.insert(newUser);

    }
}
