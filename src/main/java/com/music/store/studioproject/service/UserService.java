package com.music.store.studioproject.service;

import com.music.store.studioproject.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void saveUser(User newUser);
}
