package com.music.store.studioproject.service;

import com.music.store.studioproject.entity.MusicInformation;
import com.music.store.studioproject.utils.Response;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {
    Response<MusicInformation> addMusic(MusicInformation musicInformation);
}
