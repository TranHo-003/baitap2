package com.bai2.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
    private String name;
//    public String photo;
    private BigDecimal price;
    private Integer stock;
}
