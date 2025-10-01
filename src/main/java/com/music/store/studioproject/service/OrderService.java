package com.music.store.studioproject.service;

import com.music.store.studioproject.dto.TakeOrderDto;
import com.music.store.studioproject.utils.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    Response<TakeOrderDto> takeOrder(List<Integer> cartItemIds);
}
