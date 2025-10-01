package com.music.store.studioproject.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddMusicDto {
    private String musicName;
    private String singer;
    private String albumName;
    private Integer categoryId;
    private Double price;
    private Integer duration; // Duration in seconds
    private String coverUrl;
    private String musicUrl;
}
