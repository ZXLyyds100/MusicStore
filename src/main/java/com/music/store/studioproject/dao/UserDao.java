package com.music.store.studioproject.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.store.studioproject.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao extends BaseMapper<User> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND is_deleted = 0")
    User findByUsername(@Param("username") String username);

}

