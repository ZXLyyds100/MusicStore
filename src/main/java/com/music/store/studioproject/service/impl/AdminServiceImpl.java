package com.music.store.studioproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.music.store.studioproject.dao.MusicInformationDao;
import com.music.store.studioproject.dto.AddMusicDto;
import com.music.store.studioproject.entity.MusicInformation;
import com.music.store.studioproject.service.AdminService;
import com.music.store.studioproject.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private MusicInformationDao musicInformationDao;
    @Override
    public Response<MusicInformation> addMusic(AddMusicDto musicInformation) {
        LambdaQueryWrapper<MusicInformation> queryWrapper = new LambdaQueryWrapper<MusicInformation>();
        queryWrapper.eq(MusicInformation::getMusicName, musicInformation.getMusicName());

        MusicInformation musicInformation1 = musicInformationDao.selectOne(queryWrapper);
        if (musicInformation1 != null) {
            return Response.fail("音乐已存在");
        } else {

            MusicInformation music = new MusicInformation();
            music.setMusicName(musicInformation.getMusicName());
            music.setSinger(musicInformation.getSinger());
            music.setAlbumName(musicInformation.getAlbumName());
            music.setCategoryId(musicInformation.getCategoryId());
            music.setPrice(BigDecimal.valueOf(musicInformation.getPrice()));
            music.setCoverUrl(musicInformation.getCoverUrl());
            music.setMusicUrl(musicInformation.getMusicUrl());
            musicInformationDao.insert(music);
            music = musicInformationDao.selectOne(queryWrapper);
            return Response.success(music);
        }
    }

    @Override
    public Response<MusicInformation> updateMusic(Long id, String musicName, Double price, String coverUrl) {
        MusicInformation musicInformation = musicInformationDao.selectById(id);
        if (musicInformation == null) {
            return Response.fail("音乐不存在");
        }
        musicInformation.setMusicName(musicName);
        musicInformation.setPrice(BigDecimal.valueOf(price));
        musicInformation.setCoverUrl(coverUrl);
        musicInformationDao.updateById(musicInformation);
        return Response.success(musicInformation, "音乐信息更新成功");
    }

    @Override
    public Response deleteMusic(Long id) {
        MusicInformation musicInformation = musicInformationDao.selectById(id);
        if (musicInformation == null) {
            return Response.fail("音乐不存在");
        }
        musicInformationDao.deleteById(id);
        return Response.success("音乐删除成功");
    }
}
