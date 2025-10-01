package com.music.store.studioproject.controller;

import com.music.store.studioproject.dto.DeleteCartDto;
import com.music.store.studioproject.dto.GetOrdersDto;
import com.music.store.studioproject.dto.OrderDetailDto;
import com.music.store.studioproject.dto.TakeOrderDto;
import com.music.store.studioproject.service.OrderService;
import com.music.store.studioproject.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    /**
     * 获取用户订单列表
     * @Param page,size
     * @Return Response<GetOrdersDto>
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @GetMapping("/{page}/{size}")
    public Response<GetOrdersDto> getOrders(@PathVariable int page, @PathVariable int size) {
        return orderService.getOrders(page, size);
    }
    /**
     * 获取订单详情
     * @Param orderNo
     * @Return Response<OrderDetailDto>
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @GetMapping("/{orderNo}")
    public Response<OrderDetailDto> getOrderDetail(@PathVariable String orderNo) {
        return orderService.getOrderDetail(orderNo);
    }
}
