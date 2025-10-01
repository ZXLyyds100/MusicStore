package com.music.store.studioproject.service;

import com.music.store.studioproject.dto.MusicPageDto;
import org.springframework.stereotype.Service;

@Service
public interface GuestService {
    MusicPageDto searchMusic(String keyword, Integer categoryId, int page, int size);


}
