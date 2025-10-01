package com.music.store.studioproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.store.studioproject.dao.MusicInformationDao;
import com.music.store.studioproject.dto.MusicPageDto;
import com.music.store.studioproject.dto.MusicRecordDto;
import com.music.store.studioproject.entity.MusicInformation;
import com.music.store.studioproject.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuestServiceImpl implements GuestService {
    @Autowired
    private MusicInformationDao musicInformationDao;
    @Override
    public MusicPageDto searchMusic(String keyword, Integer categoryId, int page, int size) {
        // 1. 创建MyBatis-Plus的分页对象
        Page<MusicInformation> pageRequest = new Page<>(page, size);

        // 2. 创建查询条件构造器
        QueryWrapper<MusicInformation> queryWrapper = new QueryWrapper<>();

        // 3. 动态构建查询条件
        // 如果传入了keyword，则进行模糊查询
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("music_name", keyword)
                    .or().like("singer", keyword)
                    .or().like("album_name", keyword);
        }

        // 如果传入了categoryId，则添加分类筛选条件
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        // 4. 执行分页查询
        Page<MusicInformation> resultPage = musicInformationDao.selectPage(pageRequest, queryWrapper);

        // 5. 将查询结果 (List<MusicInformation>) 转换为 DTO (List<MusicRecordDto>)
        List<MusicRecordDto> records = resultPage.getRecords().stream().map(music -> {
            MusicRecordDto dto = new MusicRecordDto();
            dto.setId(music.getId());
            dto.setTitle(music.getMusicName());
            dto.setArtist(music.getSinger());
            dto.setAlbum(music.getAlbumName());
            dto.setPrice(music.getPrice() != null ? music.getPrice().doubleValue() : 0.0);
            dto.setCoverUrl(music.getCoverUrl());
            return dto;
        }).collect(Collectors.toList());

        // 6. 组装最终的分页DTO对象
        MusicPageDto musicPageDto = new MusicPageDto();
        musicPageDto.setTotal((int) resultPage.getTotal());
        musicPageDto.setPage((int)resultPage.getPages());
        musicPageDto.setRecords(records);

        return musicPageDto;
    }
}
