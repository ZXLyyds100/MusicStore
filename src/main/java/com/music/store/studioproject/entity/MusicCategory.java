package com.music.store.studioproject.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("music_category")
public class MusicCategory {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String categoryName;

    private String categoryDesc;

    private Integer sort;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
