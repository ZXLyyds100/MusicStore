package com.music.store.studioproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.store.studioproject.dao.MusicCollectionDao;
import com.music.store.studioproject.dao.MusicInformationDao;
import com.music.store.studioproject.dao.UserDao;
import com.music.store.studioproject.dto.ChangePasswordDto;
import com.music.store.studioproject.dto.MusicCollectionDto;
import com.music.store.studioproject.dto.MusicRecordDto;
import com.music.store.studioproject.entity.MusicCollection;
import com.music.store.studioproject.entity.MusicInformation;
import com.music.store.studioproject.entity.User;
import com.music.store.studioproject.exception.BusinessException;
import com.music.store.studioproject.service.UserService;
import com.music.store.studioproject.utils.Response;
import com.music.store.studioproject.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private MusicCollectionDao musicCollectionDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MusicInformationDao musicInformationDao;

    @Override
    public void saveUser(User newUser) {
        User user = userDao.findByUsername(newUser.getUsername());
        if (user != null) {
            throw BusinessException.userAlreadyExists("用户已存在");
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userDao.insert(newUser);

    }

    @Override
    public Response changePassword(ChangePasswordDto changePasswordDto) {
        Long userId = UserContext.getUserId();
        User user = userDao.findById(userId);
        if (user == null) {
            return Response.fail("用户不存在");
        }
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            return Response.fail("旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId);
        int rows = userDao.update(user, updateWrapper);
        if (rows > 0) {
            return Response.success("密码修改成功");
        } else {
            return Response.fail("密码修改失败");
        }

    }

    @Override
    public Response<MusicCollectionDto> getCollections(int start, int size, int page) {

            Long userId = UserContext.getUserId();

            // 1. 使用MyBatis-Plus标准分页查询收藏表
            Page<MusicCollection> collectionPage = new Page<>(page, size);
            QueryWrapper<MusicCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).orderByDesc("create_time");
            musicCollectionDao.selectPage(collectionPage, queryWrapper);

            List<MusicCollection> musicCollections = collectionPage.getRecords();

            // 2. 检查收藏记录是否为空
            if (CollectionUtils.isEmpty(musicCollections)) {
                MusicCollectionDto emptyDto = new MusicCollectionDto();
                emptyDto.setTotal(0);
                emptyDto.setPage(page);
                emptyDto.setRecords(new ArrayList<>());
                return Response.success(emptyDto, "收藏列表为空");
            }

            // 3. 提取musicId，并使用可靠的自定义IN查询获取音乐信息
            List<Long> musicIds = musicCollections.stream()
                    .map(MusicCollection::getMusicId)
                    .distinct()
                    .toList();

            List<MusicInformation> musics = musicInformationDao.selectBatchIds(musicIds);

            // 4. 将音乐信息转换为DTO
            List<MusicRecordDto> list = musics.stream()
                    .map(music -> {
                        MusicRecordDto musicRecordDto = new MusicRecordDto();
                        musicRecordDto.setId(music.getId());
                        musicRecordDto.setArtist(music.getSinger());
                        musicRecordDto.setTitle(music.getMusicName());
                        musicRecordDto.setPrice(music.getPrice().doubleValue());
                        musicRecordDto.setAlbum(music.getAlbumName());
                        musicRecordDto.setCoverUrl(music.getCoverUrl());
                        return musicRecordDto;
                    }).collect(Collectors.toList());

            // 5. 组装最终的DTO并返回
            MusicCollectionDto musicCollectionDto = new MusicCollectionDto();
            musicCollectionDto.setTotal((int) collectionPage.getTotal());
            musicCollectionDto.setPage(page);
            musicCollectionDto.setRecords(list);

            return Response.success(musicCollectionDto, "获取收藏列表成功");
        }
}
