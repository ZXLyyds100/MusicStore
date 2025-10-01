package com.music.store.studioproject.service;

import com.music.store.studioproject.dto.MusicPageDto;
import com.music.store.studioproject.entity.MusicCategory;
import com.music.store.studioproject.entity.MusicInformation;
import com.music.store.studioproject.utils.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GuestService {
    MusicPageDto searchMusic(String keyword, Integer categoryId, int page, int size);


    MusicInformation getMusicById(Long id);

    Response<List<MusicCategory>> getCategories();
}
