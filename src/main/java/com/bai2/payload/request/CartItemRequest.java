package com.bai2.payload.request;

import lombok.Getter;

@Getter
public class CartItemRequest {
    private Long productId;
    private Long quantity;

}
