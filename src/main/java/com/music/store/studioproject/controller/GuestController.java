package com.music.store.studioproject.controller;

import com.music.store.studioproject.dto.MusicPageDto;
import com.music.store.studioproject.entity.MusicCategory;
import com.music.store.studioproject.entity.MusicInformation;
import com.music.store.studioproject.service.GuestService;
import com.music.store.studioproject.service.UserService;
import com.music.store.studioproject.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GuestController {
    @Autowired
    private GuestService guestService;

    /**
     * 音乐浏览与搜索接口
     * @param keyword    搜索关键字（可选）
     * @param categoryId 分类ID（可选）
     * @param page       当前页码（可选，默认为1）
     * @param size       每页数量（可选，默认为10）
     * @return 分页后的音乐列表
     */
    @GetMapping("/music/{keyword}/{categoryId}/{page}/{size}")
    public Response<MusicPageDto> searchMusic(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        MusicPageDto musicPage = guestService.searchMusic(keyword, categoryId, page, size);
        return Response.success(musicPage);
    }
    /**
     * 根据ID获取音乐详情
     * @param id 音乐ID
     * @return 音乐详情
     */
    @GetMapping("/music/{id}")
    public Response<MusicInformation> getMusicById(@PathVariable Integer id) {
        MusicInformation music = guestService.getMusicById((long)id);
        if (music != null) {
            return Response.success(music);
        } else {
            return Response.fail("音乐未找到");
        }
    }
    /**
     * 获取所有音乐分类
     * @return 音乐分类列表
     */
    @GetMapping("/categories")
    public Response<List<MusicCategory>> getCategories() {
        return guestService.getCategories();
    }
}
