package com.music.store.studioproject.dto;

import com.music.store.studioproject.entity.MusicInformation;
import lombok.Data;

import java.util.List;

@Data
public class MusicPageDto {
    private Integer total;
    private Integer page;
    private List<MusicRecordDto> records;

}
