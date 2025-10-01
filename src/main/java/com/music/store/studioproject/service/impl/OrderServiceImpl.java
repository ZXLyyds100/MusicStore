package com.music.store.studioproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.music.store.studioproject.dao.MusicInformationDao;
import com.music.store.studioproject.dao.OrderInformationDao;
import com.music.store.studioproject.dao.OrderItemDao;
import com.music.store.studioproject.dao.ShoppingCartDao;
import com.music.store.studioproject.dto.TakeOrderDto;
import com.music.store.studioproject.entity.MusicInformation;
import com.music.store.studioproject.entity.OrderInformation;
import com.music.store.studioproject.entity.OrderItem;
import com.music.store.studioproject.entity.ShoppingCart;
import com.music.store.studioproject.exception.BusinessException;
import com.music.store.studioproject.service.OrderService;
import com.music.store.studioproject.utils.Response;
import com.music.store.studioproject.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ShoppingCartDao shoppingCartDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private MusicInformationDao musicInformationDao;
    @Autowired
    private OrderInformationDao orderInformationDao;
    @Override
    @Transactional
    public Response<TakeOrderDto> takeOrder(List<Integer> cartItemIds) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ShoppingCart::getId, cartItemIds);
        List<ShoppingCart> cartItems = shoppingCartDao.selectList(queryWrapper);
        if (cartItems.isEmpty()) {
            return Response.fail("购物车项不存在");
        }
        if (cartItems.size() != cartItemIds.size()) {
            return Response.fail("部分购物车项不存在");
        }
        OrderInformation orderInformation = new OrderInformation();
        String orderNumber = "ORD" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        orderInformation.setOrderNo(orderNumber);
        orderInformation.setUserId(UserContext.getUserId());
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (ShoppingCart shoppingCart : cartItems) {
            Long musicId = shoppingCart.getMusicId();
            Integer count = shoppingCart.getCount();
            MusicInformation musicInformation = musicInformationDao.selectById(musicId);
            if (musicInformation == null) {
                throw BusinessException.resourceNotFound("音乐不存在，ID：" + musicId);
            }
            BigDecimal price = musicInformation.getPrice();
            totalPrice = totalPrice.add(price.multiply(BigDecimal.valueOf(count)));
            OrderItem orderItem = new OrderItem();
            orderItem.setCount(count);
            orderItem.setMusicId(musicId);
            orderItem.setMusicName(musicInformation.getMusicName());
            orderItem.setSinger(musicInformation.getSinger());
            orderItem.setPrice(price);
            orderItems.add(orderItem);
        }
        orderInformation.setTotalAmount(totalPrice);
        orderInformationDao.insert(orderInformation);
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(orderInformation.getId());
            orderItemDao.insert(orderItem);
        }
        return Response.success(new TakeOrderDto(orderNumber));
    }
}
