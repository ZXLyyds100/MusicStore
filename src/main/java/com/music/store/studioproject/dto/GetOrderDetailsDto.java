package com.music.store.studioproject.dto;

import com.music.store.studioproject.entity.OrderInformation;
import com.music.store.studioproject.entity.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class GetOrderDetailsDto {
    private OrderInformation orderMain;
    private List<OrderItem> orderItems;
}
