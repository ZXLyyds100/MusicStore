package com.music.store.studioproject.service.impl;

import com.music.store.studioproject.dao.SysRoleDao;
import com.music.store.studioproject.dao.SysUserDao;
import com.music.store.studioproject.entity.SysRole;
import com.music.store.studioproject.entity.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserDao sysUserDao;
    private final SysRoleDao sysRoleDao;

    public UserDetailsServiceImpl(SysUserDao sysUserDao, SysRoleDao sysRoleDao) {
        this.sysUserDao = sysUserDao;
        this.sysRoleDao = sysRoleDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库查询用户
        SysUser sysUser = sysUserDao.findByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 2. 根据 role_id 查询角色信息
        SysRole sysRole = sysRoleDao.selectById(sysUser.getRoleId());
        if (sysRole == null) {
            throw new UsernameNotFoundException("用户角色配置错误");
        }

        // 3. 将角色名（如 "ROLE_ADMIN"）封装为 GrantedAuthority
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(sysRole.getRoleName())
        );

        // 4. 返回Spring Security的User对象
        return new User(sysUser.getUsername(), sysUser.getPassword(), authorities);
    }
}
