package com.music.store.studioproject.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("music_info")
public class MusicInformation {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String musicName;

    private String singer;

    private Integer categoryId;

    private String albumName;

    private Integer duration;

    private Integer playCount;

    private BigDecimal price;

    private String coverUrl;

    private String musicUrl;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
