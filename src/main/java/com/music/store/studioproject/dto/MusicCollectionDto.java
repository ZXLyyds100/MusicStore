package com.music.store.studioproject.dto;

import lombok.Data;

import java.util.List;

@Data
public class MusicCollectionDto {
    private Integer total;
    private Integer page;
    private List<MusicRecordDto> records;
}
