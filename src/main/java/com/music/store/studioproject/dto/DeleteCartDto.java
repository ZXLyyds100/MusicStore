package com.music.store.studioproject.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeleteCartDto {
    List<Integer> cartItemIds;
}
