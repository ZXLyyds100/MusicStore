package com.music.store.studioproject.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.store.studioproject.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserDao extends BaseMapper<SysUser> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND is_deleted = 0")
    SysUser findByUsername(@Param("username") String username);
}

