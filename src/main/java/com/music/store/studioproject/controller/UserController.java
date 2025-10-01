package com.music.store.studioproject.controller;

import com.music.store.studioproject.dto.ChangePasswordDto;
import com.music.store.studioproject.dto.MusicCollectionDto;
import com.music.store.studioproject.service.UserService;
import com.music.store.studioproject.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/collections/{page}/{size}")
    public Response<MusicCollectionDto> collections(@PathVariable int page, @PathVariable int size) {
        int start = (page - 1) * size;
        return userService.getCollections(start, size, page);
    }
}
