package com.music.store.studioproject.service;

import com.music.store.studioproject.dto.AddMusicDto;
import com.music.store.studioproject.dto.GetOrderDetailsDto;
import com.music.store.studioproject.dto.GetOrdersDto;
import com.music.store.studioproject.dto.GetUserDto;
import com.music.store.studioproject.entity.*;
import com.music.store.studioproject.utils.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminService {
    Response<MusicInformation> addMusic(AddMusicDto musicInformation);

    Response<MusicInformation> updateMusic(Long id, String musicName, Double price, String coverUrl);

    Response deleteMusic(Long id);

    Response<MusicCategory> addCategory(MusicCategory musicCategory);

    Response<MusicCategory> updateCategory(Long id, String categoryDesc, Integer sort);

    Response deleteCategory(Long id);

    Response<GetOrdersDto> getOrders(Long userId, Integer orderStatus, String orderNo, int page, int size);

    Response<GetOrderDetailsDto> getOrderDetail(String orderNo);

    Response<OrderInformation> updateOrderStatus(String orderNo, Integer orderStatus);

    Response<GetUserDto> getUsers(String username, int roleId, int status, int page, int size);

    Response<List<WebsiteConfigEntity>> getWebsiteConfig();

    Response<List<WebsiteConfigEntity>> updateWebsiteConfig(List<WebsiteConfigEntity> configs);
}
