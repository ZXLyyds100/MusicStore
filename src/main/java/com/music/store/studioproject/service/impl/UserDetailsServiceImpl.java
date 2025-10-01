package com.music.store.studioproject.service.impl;

import com.music.store.studioproject.dao.RoleDao;
import com.music.store.studioproject.dao.UserDao;
import com.music.store.studioproject.entity.Role;
import com.music.store.studioproject.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;
    private final RoleDao roleDao;


    public UserDetailsServiceImpl(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库查询用户
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 2. 根据 role_id 查询角色信息
        Role role = roleDao.selectById(user.getRoleId());
        if (role == null) {
            throw new UsernameNotFoundException("用户角色配置错误");
        }

        // 3. 将角色名（如 "ROLE_ADMIN"）封装为 GrantedAuthority
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(role.getRoleName())
        );

        // 4. 返回Spring Security的User对象
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    /**
     * 根据用户名加载自定义的User实体
     * @param username 用户名
     * @return User实体
     */
    public User loadUserEntityByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
