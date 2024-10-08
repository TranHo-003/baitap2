package com.bai2.payload.response;

import com.bai2.model.Product;
import lombok.Getter;


@Getter
public class CartItemResponse {
    private Long id;
    private ProductResponse product;
    private int quantity;

    public CartItemResponse(Long id, ProductResponse product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }
}
