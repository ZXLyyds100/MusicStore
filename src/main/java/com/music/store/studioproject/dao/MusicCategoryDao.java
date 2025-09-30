package com.music.store.studioproject.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.store.studioproject.entity.MusicCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MusicCategoryDao extends BaseMapper<MusicCategory> {
}
