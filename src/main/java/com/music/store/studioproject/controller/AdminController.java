package com.music.store.studioproject.controller;

import com.music.store.studioproject.dto.AddMusicDto;
import com.music.store.studioproject.dto.MusicPageDto;
import com.music.store.studioproject.dto.UpdateMusicDto;
import com.music.store.studioproject.entity.MusicCategory;
import com.music.store.studioproject.entity.MusicInformation;
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
}
