package com.music.store.studioproject.controller;

import com.music.store.studioproject.dto.*;
import com.music.store.studioproject.service.UserService;
import com.music.store.studioproject.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/me")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 修改密码
     * @Param changePasswordDto
     * @Return Response
     * @Author 阿亮
     * @Date 2025/10/1
     *
     * */
    @PutMapping("/password")
    public Response changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        if (changePasswordDto == null) {
            return Response.fail("请求参数不能为空");
        }
        if (changePasswordDto.getOldPassword().equals(changePasswordDto.getNewPassword())) {
            return Response.fail("新密码不能与旧密码相同");
        }
        return userService.changePassword(changePasswordDto);
    }

    /**
     * 获取用户收藏的音乐列表
     * @Param page,size
     * @Return Response<MusicCollectionDto>
     * @Author 郑鑫亮
     * @Date 2025/10/1
     *
     * */
    @GetMapping("/collections/{page}/{size}")
    public Response<MusicCollectionDto> collections(@PathVariable int page, @PathVariable int size) {
        int start = (page - 1) * size;
        return userService.getCollections(start, size, page);
    }
    /**
     * 添加音乐到用户收藏
     * @Param addCollectionDto
     * @Return Response
     * @Author 郑鑫亮
     * @Date 2025/10/1
     *
     * */
    @PostMapping("/collections")
    public Response addCollection(@RequestBody AddCollectionDto addCollectionDto) {
        return userService.addCollection(addCollectionDto.getMusicId());
    }
    /**
     * 从用户收藏中移除音乐
     * @Param musicId
     * @Return Response
     * @Author 郑鑫亮
     * @Date 2025/10/1
     *
     * */
    @DeleteMapping("/collections/{musicId}")
    public Response removeCollection(@PathVariable Integer musicId) {
        return userService.removeCollection(musicId);
    }
    /**
     * 获取用户购物车中的音乐列表
     * @Param
     * @Return Response<List<CartItemDto>>
     * @Author 郑鑫亮
     * @Date 2025/10/1
     *
     * */
    @GetMapping("/cart")
    public Response<List<CartItemDto>> getCart() {
        return userService.getCart();
    }
    /**
     * 添加音乐到用户购物车
     * @Param cartItemDto
     * @Return Response
     * @Author 郑鑫亮
     * @Date 2025/10/1
     *
     * */
    @PostMapping("/cart")
    public Response addCartItem(@RequestBody CartItemDto cartItemDto) {
        return userService.addCartItem(cartItemDto.getMusicId(), cartItemDto.getQuantity());
    }
    /**
     * 更新用户购物车中的音乐数量
     * @Param cartItemDto,cartItemId
     * @Return Response
     * @Author 郑鑫亮
     * @Date 2025/10/1
     *
     * */
    @PutMapping("/cart/items/{cartItemId}")
    public Response updateCartItem(@RequestBody CartItemDto cartItemDto,@PathVariable Integer cartItemId) {
        return userService.updateCartItem(cartItemId, cartItemDto.getQuantity());
    }
    /**
     * 删除用户购物车中的音乐
     * @Param deleteCartDto
     * @Return Response
     * @Author 郑鑫亮
     * @Date 2025/10/1
     *
     * */
    @DeleteMapping("cart/items")
    public Response deleteCartItems(@RequestBody DeleteCartDto deleteCartDto) {
        for (Integer cartItemId : deleteCartDto.getCartItemIds()) {
            userService.updateCartItem(cartItemId, 0);
        }
        return Response.success("删除成功");
    }
}
