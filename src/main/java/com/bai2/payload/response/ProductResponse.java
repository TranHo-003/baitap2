package com.bai2.payload.response;

import java.math.BigDecimal;

public class ProductResponse {
    private Long id;
    private String name;
    private String photo;
    private BigDecimal price;
    private BigDecimal stock;

    public ProductResponse( String name, String photo, BigDecimal price, BigDecimal stock) {
        this.name = name;
        this.photo = photo;
        this.price = price;
        this.stock = stock;
    }
}
