package com.music.store.studioproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("website_config")
public class WebsiteConfigEntity {
    @TableId(type = IdType.AUTO, value = "id")
    private Integer id;
    private String configKey;
    private String ConfigValue;
    private String configDesc;
    private LocalDateTime updateTime;
}
