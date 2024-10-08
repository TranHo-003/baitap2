package com.bai2.payload.request;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductRequest {
    private String name;
    public String photo;
    private BigDecimal price;
    private BigDecimal stock;
}
