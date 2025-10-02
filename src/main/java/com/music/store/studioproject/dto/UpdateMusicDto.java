package com.music.store.studioproject.dto;

import lombok.Data;

@Data
public class UpdateMusicDto {
    private String MusicName;
    private Double Price;
    private String CoverUrl;
}
