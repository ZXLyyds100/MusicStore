package com.music.store.studioproject.service;

import com.music.store.studioproject.dto.AddMusicDto;
import com.music.store.studioproject.entity.MusicCategory;
import com.music.store.studioproject.entity.MusicInformation;
import com.music.store.studioproject.utils.Response;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {
    Response<MusicInformation> addMusic(AddMusicDto musicInformation);

    Response<MusicInformation> updateMusic(Long id, String musicName, Double price, String coverUrl);

    Response deleteMusic(Long id);

    Response<MusicCategory> addCategory(MusicCategory musicCategory);

    Response<MusicCategory> updateCategory(Long id, String categoryDesc, Integer sort);
}
