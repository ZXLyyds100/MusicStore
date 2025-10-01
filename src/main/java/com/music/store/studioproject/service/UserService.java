package com.music.store.studioproject.service;

import com.music.store.studioproject.dto.CartItemDto;
import com.music.store.studioproject.dto.ChangePasswordDto;
import com.music.store.studioproject.dto.MusicCollectionDto;
import com.music.store.studioproject.entity.User;
import com.music.store.studioproject.utils.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    void saveUser(User newUser);

    Response changePassword(ChangePasswordDto changePasswordDto);

    Response<MusicCollectionDto> getCollections(int start, int size, int page);

    Response addCollection(Integer musicId);

    Response removeCollection(Integer musicId);

    Response<List<CartItemDto>> getCart();

    Response addCartItem(Long musicId, Integer quantity);

}
