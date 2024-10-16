package com.bai2.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String name;
    private String photo;
    private BigDecimal price;
    private Integer stock;

    public ProductResponse( String name, String photo, BigDecimal price, Integer stock) {
        this.name = name;
        this.photo = photo;
        this.price = price;
        this.stock = stock;
    }
}
