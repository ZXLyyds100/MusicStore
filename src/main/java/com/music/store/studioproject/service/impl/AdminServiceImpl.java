package com.music.store.studioproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.store.studioproject.dao.MusicCategoryDao;
import com.music.store.studioproject.dao.MusicInformationDao;
import com.music.store.studioproject.dao.OrderInformationDao;
import com.music.store.studioproject.dao.OrderItemDao;
import com.music.store.studioproject.dto.AddMusicDto;
import com.music.store.studioproject.dto.GetOrderDetailsDto;
import com.music.store.studioproject.dto.GetOrdersDto;
import com.music.store.studioproject.entity.MusicCategory;
import com.music.store.studioproject.entity.MusicInformation;
import com.music.store.studioproject.entity.OrderInformation;
import com.music.store.studioproject.entity.OrderItem;
import com.music.store.studioproject.service.AdminService;
import com.music.store.studioproject.service.OrderService;
import com.music.store.studioproject.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private MusicInformationDao musicInformationDao;
    @Autowired
    private MusicCategoryDao musicCategoryDao;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderInformationDao orderInformationDao;
    @Autowired
    private OrderItemDao orderItemDao;
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

    @Override
    public Response<MusicCategory> addCategory(MusicCategory musicCategory) {
        LambdaQueryWrapper<MusicCategory> queryWrapper = new LambdaQueryWrapper<MusicCategory>();
        queryWrapper.eq(MusicCategory::getCategoryName, musicCategory.getCategoryName());
        MusicCategory category = musicCategoryDao.selectOne(queryWrapper);
        if (category != null) {
            return Response.fail("分类已存在");
        } else {
            musicCategoryDao.insert(musicCategory);
            musicCategory = musicCategoryDao.selectOne(queryWrapper);
            return Response.success(musicCategory, "分类添加成功");
        }
    }

    @Override
    public Response<MusicCategory> updateCategory(Long id, String categoryDesc, Integer sort) {
        MusicCategory musicCategory = musicCategoryDao.selectById(id);
        if (musicCategory == null) {
            return Response.fail("分类不存在");
        }
        musicCategory.setCategoryDesc(categoryDesc);
        musicCategory.setSort(sort);
        musicCategoryDao.updateById(musicCategory);
        return Response.success(musicCategory, "分类更新成功");
    }

    @Override
    public Response deleteCategory(Long id) {
        MusicCategory musicCategory = musicCategoryDao.selectById(id);
        if (musicCategory == null) {
            return Response.fail("分类不存在");
        }
        musicCategoryDao.deleteById(id);
        return Response.success("分类删除成功");
    }

    @Override
    public Response<GetOrdersDto> getOrders(Long userId, Integer orderStatus, String orderNo, int page, int size) {
        // 自己写一套，其中userId, orderStatus, orderNo都可以为空
        // 不调用orderService
        LambdaQueryWrapper<OrderInformation> queryWrapper = new LambdaQueryWrapper<OrderInformation>();
        if (userId != null) {
            queryWrapper.eq(OrderInformation::getUserId, userId);
        }
        if (orderStatus != null) {
            queryWrapper.eq(OrderInformation::getOrderStatus, orderStatus);
        }
        if (orderNo != null && !orderNo.isEmpty()) {
            queryWrapper.eq(OrderInformation::getOrderNo, orderNo);
        }
        Page<OrderInformation> orderPage = new Page<>(page, size);
        orderInformationDao.selectPage(orderPage, queryWrapper);
        List<OrderInformation> records = orderPage.getRecords();
        GetOrdersDto getOrdersDto = new GetOrdersDto();
        Integer total = (int) orderPage.getTotal();
        getOrdersDto.setPage(page);
        getOrdersDto.setTotal(total);
        getOrdersDto.setRecords(records);
        return Response.success(getOrdersDto, "获取订单列表成功");

    }

    @Override
    public Response<GetOrderDetailsDto> getOrderDetail(String orderNo) {
        QueryWrapper<OrderInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        OrderInformation orderInformation = orderInformationDao.selectOne(queryWrapper);
        if (orderInformation == null) {
            return Response.fail("订单不存在");
        } else {
            Long orderId = orderInformation.getId();
            QueryWrapper<OrderItem> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("order_id", orderId);
            List<OrderItem> orderItems = orderItemDao.selectList(queryWrapper1);
            GetOrderDetailsDto getOrderDetailsDto = new GetOrderDetailsDto();
            getOrderDetailsDto.setOrderItems(orderItems);
            getOrderDetailsDto.setOrderMain(orderInformation);
            return Response.success(getOrderDetailsDto);
        }
    }
}
