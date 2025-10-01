package com.music.store.studioproject.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.store.studioproject.entity.MusicInformation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MusicInformationDao extends BaseMapper<MusicInformation> {
  /*  @Select("select * from music_info where is_deleted = 0 and id in #{ids}")
    List<MusicInformation> selectByIds(List<Long> ids);*/
}
