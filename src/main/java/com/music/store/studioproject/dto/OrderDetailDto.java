package com.music.store.studioproject.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.music.store.studioproject.entity.OrderItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailDto {
    private String orderNo;
    private Long userId;
    private Double totalAmount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private List<OrderItem> items;


}
