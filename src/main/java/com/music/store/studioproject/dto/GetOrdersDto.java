package com.music.store.studioproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.music.store.studioproject.entity.OrderInformation;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetOrdersDto {
    private Integer total;
    private Integer page;
    private List<OrderInformation> records;
}
