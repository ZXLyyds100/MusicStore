package com.music.store.studioproject.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("shopping_cart")
public class ShoppingCart {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long musicId;

    private Integer count;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
