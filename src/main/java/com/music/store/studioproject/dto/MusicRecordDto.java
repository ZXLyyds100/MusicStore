package com.music.store.studioproject.dto;

import lombok.Data;

@Data
public class MusicRecordDto {
    private Long id;
    private String title;
    private String artist;
    private String album;
    private Double price;
    private String coverUrl;
}