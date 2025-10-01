package com.music.store.studioproject.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private Long cartItemId;
    private Long musicId;
    private String title;
    private String artist;
    private Double price;
    private Integer quantity;
}
