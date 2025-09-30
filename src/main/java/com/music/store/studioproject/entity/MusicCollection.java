package com.music.store.studioproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("music_collection")
public class MusicCollection {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long musicId;

    private LocalDateTime createTime;
}
