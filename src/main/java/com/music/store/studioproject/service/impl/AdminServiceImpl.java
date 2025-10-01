package com.music.store.studioproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.music.store.studioproject.dao.MusicInformationDao;
import com.music.store.studioproject.entity.MusicInformation;
import com.music.store.studioproject.service.AdminService;
import com.music.store.studioproject.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private MusicInformationDao musicInformationDao;
    @Override
    public Response<MusicInformation> addMusic(MusicInformation musicInformation) {
        LambdaQueryWrapper<MusicInformation> queryWrapper = new LambdaQueryWrapper<MusicInformation>();
        queryWrapper.eq(MusicInformation::getMusicName, musicInformation.getMusicName());

        MusicInformation musicInformation1 = musicInformationDao.selectOne(queryWrapper);
        if (musicInformation1 != null) {
            musicInformationDao.update(musicInformation, queryWrapper);
            return Response.success(musicInformation, "修改音乐成功");
        } else {
            musicInformationDao.insert(musicInformation);
            return Response.success(musicInformation, "新增音乐成功");
        }
    }
}
