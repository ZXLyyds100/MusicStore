package com.music.store.studioproject.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.store.studioproject.entity.MusicCollection;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MusicCollectionDao extends BaseMapper<MusicCollection> {

    /**
     * 根据用户ID分页查询收藏列表
     * @param userId 用户ID
     * @param offset 查询起始位置
     * @param size   查询数量
     * @return 收藏列表
     */
    @Select("SELECT * FROM music_collection WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<MusicCollection> selectByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);
}
