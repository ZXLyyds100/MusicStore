package com.music.store.studioproject.controller;

import com.music.store.studioproject.dto.*;
import com.music.store.studioproject.entity.MusicCategory;
import com.music.store.studioproject.entity.MusicInformation;
import com.music.store.studioproject.entity.OrderInformation;
import com.music.store.studioproject.service.AdminService;
import com.music.store.studioproject.service.GuestService;
import com.music.store.studioproject.service.UserService;
import com.music.store.studioproject.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private GuestService guestService;
    @Autowired
    private AdminService adminService;

    /**
     * 音乐浏览与搜索接口
     * @param keyword    搜索关键字（可选）
     * @param categoryId 分类ID（可选）
     * @param page       当前页码（可选，默认为1）
     * @param size       每页数量（可选，默认为10）
     * @return 分页后的音乐列表
     */
    @GetMapping("/music")
    public Response<MusicPageDto> searchMusic(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        MusicPageDto musicPage = guestService.searchMusic(keyword, categoryId, page, size);
        return Response.success(musicPage);
    }
    /**
     * 添加音乐
     * @Param addMusicDto
     * @Return Response<MusicInformation>
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @PostMapping("/music")
    public Response<MusicInformation> addMusic(@RequestBody AddMusicDto addMusicDto) {
        return adminService.addMusic(addMusicDto);
    }
    /**
     * 更新音乐信息
     * @Param id,updateMusicDto
     * @Return Response<MusicInformation>
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @PutMapping("/music/{id}")
    public Response<MusicInformation> updateMusic(@PathVariable Long id, @RequestBody UpdateMusicDto addMusicDto) {
        return adminService.updateMusic(id, addMusicDto.getMusicName(), addMusicDto.getPrice(), addMusicDto.getCoverUrl());
    }
    /**
     * 删除音乐
     * @Param id
     * @Return Response
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @DeleteMapping("/music/{id}")
    public Response deleteMusic(@PathVariable Long id) {
        return adminService.deleteMusic(id);
    }
    /**
     * 获取所有音乐分类
     * @return 音乐分类列表
     */
    @GetMapping("/music/categories")
    public Response<List<MusicCategory>> getCategories() {
        return guestService.getCategories();
    }
    /**
     * 添加音乐分类
     * @Param musicCategory
     * @Return Response<MusicCategory>
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @PostMapping("/music/categories")
    public Response<MusicCategory> addCategory(@RequestBody MusicCategory musicCategory) {
        return adminService.addCategory(musicCategory);
    }
    /**
     * 更新音乐分类
     * @Param id,musicCategory
     * @Return Response<MusicCategory>
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @PutMapping("/music/categories/{id}")
    public Response<MusicCategory> updateCategory(@PathVariable Long id, @RequestBody MusicCategory musicCategory) {
        return adminService.updateCategory(id, musicCategory.getCategoryDesc(), musicCategory.getSort());
    }
    /**
     * 删除音乐分类
     * @Param id
     * @Return Response
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @DeleteMapping("/music/categories/{id}")
    public Response deleteCategory(@PathVariable Long id) {
        return adminService.deleteCategory(id);
    }
    /**
     * 获取订单列表
     * @Param userId,orderStatus,orderNo,page,size
     * @Return Response<GetOrdersDto>
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @GetMapping("/orders")
    public Response<GetOrdersDto> getOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer orderStatus,
            @RequestParam(required = false) String orderNo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return adminService.getOrders(userId, orderStatus, orderNo, page, size);
    }
    /**
     * 获取订单详情
     * @Param orderNo
     * @Return Response<GetOrderDetailsDto>
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @GetMapping("orders/{orderNo}")
    public Response<GetOrderDetailsDto> getOrderDetail(@PathVariable String orderNo) {
        return adminService.getOrderDetail(orderNo);
    }
    /**
     * 更新订单状态
     * @Param orderNo,orderStatusDto
     * @Return Response<OrderInformation>
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @PutMapping("/orders/{orderNo}/status")
    public Response<OrderInformation> updateOrderStatus(@PathVariable String orderNo, @RequestBody OrderStatusDto orderStatusDto) {
        // 这里可以添加对 orderStatusDto.getOrderStatus() 的验证
        return adminService.updateOrderStatus(orderNo, orderStatusDto.getOrderStatus());
    }
    /**
     * 取消订单
     * @Param orderNo
     * @Return Response
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @DeleteMapping("/orders/{orderNo}")
    public Response deleteOrder(@PathVariable String orderNo) {
        return adminService.updateOrderStatus(orderNo, 4); // 假设4表示已取消状态
    }
}
