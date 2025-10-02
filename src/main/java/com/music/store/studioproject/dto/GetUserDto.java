package com.music.store.studioproject.dto;

import com.music.store.studioproject.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class GetUserDto {
    private Integer total;
    private List<User> records;
}
