package com.music.store.studioproject.controller;

import com.music.store.studioproject.dto.DeleteCartDto;
import com.music.store.studioproject.dto.TakeOrderDto;
import com.music.store.studioproject.service.OrderService;
import com.music.store.studioproject.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    /**
     * 用户下单
     * @Param cartDto
     * @Return Response<TakeOrderDto>
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @PostMapping()
    public Response<TakeOrderDto> takeOrder(@RequestBody DeleteCartDto cartDto) {
        return orderService.takeOrder(cartDto.getCartItemIds());
    }

}
