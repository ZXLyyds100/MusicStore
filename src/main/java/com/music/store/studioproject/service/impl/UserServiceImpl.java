package com.music.store.studioproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.store.studioproject.dao.MusicCollectionDao;
import com.music.store.studioproject.dao.MusicInformationDao;
import com.music.store.studioproject.dao.ShoppingCartDao;
import com.music.store.studioproject.dao.UserDao;
import com.music.store.studioproject.dto.*;
import com.music.store.studioproject.entity.MusicCollection;
import com.music.store.studioproject.entity.MusicInformation;
import com.music.store.studioproject.entity.ShoppingCart;
import com.music.store.studioproject.entity.User;
import com.music.store.studioproject.exception.BusinessException;
import com.music.store.studioproject.service.UserService;
import com.music.store.studioproject.utils.Response;
import com.music.store.studioproject.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    @Autowired
    private ShoppingCartDao shoppingCartDao;
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

    @Override
    public Response addCollection(Integer musicId) {
        Long userId = UserContext.getUserId();
        QueryWrapper<MusicCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("music_id", musicId);
        MusicCollection existingCollection = musicCollectionDao.selectOne(queryWrapper);
        if (existingCollection != null) {
            return Response.fail("音乐已收藏");
        } else {
            MusicCollection musicCollection = new MusicCollection();
            musicCollection.setUserId(userId);
            musicCollection.setMusicId(musicId.longValue());
            int rows = musicCollectionDao.insert(musicCollection);
            if (rows > 0) {
                return Response.success("收藏成功");
            } else {
                return Response.fail("收藏失败");
            }
        }
    }

    @Override
    public Response removeCollection(Integer musicId) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<MusicCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MusicCollection::getUserId, userId).eq(MusicCollection::getMusicId, musicId);
        MusicCollection existingCollection = musicCollectionDao.selectOne(queryWrapper);
        if (existingCollection == null) {
            return Response.fail("音乐未收藏");
        } else {
            int rows = musicCollectionDao.delete(queryWrapper);
            if (rows > 0) {
                return Response.success("取消收藏成功");
            } else {
                return Response.fail("取消收藏失败");
            }
        }
    }

    @Override
    public Response<List<CartItemDto>> getCart() {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> cartItems = shoppingCartDao.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(cartItems)) {
            return Response.success(new ArrayList<>(), "购物车为空");
        } else {
            List<CartItemDto> cartItemDtos = new ArrayList<>();
            cartItems.stream()
                    .forEach(item -> {
                        CartItemDto cartItemDto = new CartItemDto();
                        cartItemDto.setCartItemId(item.getId());
                        cartItemDto.setQuantity(item.getCount());
                        LambdaQueryWrapper<MusicInformation> musicQuery = new LambdaQueryWrapper<>();
                        musicQuery.eq(MusicInformation::getId, item.getMusicId());
                        MusicInformation music = musicInformationDao.selectOne(musicQuery);
                        if (music != null) {
                            cartItemDto.setTitle(music.getMusicName());
                            cartItemDto.setPrice(music.getPrice().doubleValue());
                            cartItemDto.setArtist(music.getSinger());
                            cartItemDto.setMusicId(music.getId());
                            cartItemDtos.add(cartItemDto);
                        }
                    });
            return Response.success(cartItemDtos, "获取购物车成功");
        }
    }

    @Override
    public Response addCartItem(Long musicId, Integer quantity) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId).eq(ShoppingCart::getMusicId, musicId);
        ShoppingCart existingItem = shoppingCartDao.selectOne(queryWrapper);
        if (existingItem != null) {
            existingItem.setCount(existingItem.getCount() + quantity);
            int rows = shoppingCartDao.updateById(existingItem);
            if (rows > 0) {
                return Response.success("购物车更新成功");
            } else {
                return Response.fail("购物车更新失败");
            }
        } else {
            ShoppingCart newItem = new ShoppingCart();
            newItem.setUserId(userId);
            newItem.setMusicId(musicId);
            newItem.setCount(quantity);
            int rows = shoppingCartDao.insert(newItem);
            if (rows > 0) {
                return Response.success("添加到购物车成功");
            } else {
                return Response.fail("添加到购物车失败");
            }
        }
    }

    @Override
    public Response updateCartItem(Integer cartItemId, Integer quantity) {
        Long userId = UserContext.getUserId();
        ShoppingCart cartItem = shoppingCartDao.selectById(cartItemId);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            return Response.fail("购物车项不存在");
        }
        cartItem.setCount(quantity);
        int rows = shoppingCartDao.updateById(cartItem);
        if (rows > 0) {
            return Response.success("购物车项更新成功");
        } else {
            return Response.fail("购物车项更新失败");
        }
    }

    @Override
    public Response<User> addUser(User user) {
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            return Response.fail("用户名和密码不能为空");
        }
        User existingUser = userDao.findByUsername(user.getUsername());
        if (existingUser != null) {
            return Response.fail("用户已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        int rows = userDao.insert(user);
        if (rows > 0) {
            return Response.success(user, "用户注册成功");
        } else {
            return Response.fail("用户注册失败");
        }
    }

}
