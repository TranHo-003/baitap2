package com.bai2.service;

import com.bai2.model.CartItem;
import com.bai2.model.Product;
import com.bai2.payload.request.CartItemRequest;
import com.bai2.payload.response.CartItemResponse;
import com.bai2.payload.response.MessageResponse;
import com.bai2.payload.response.ProductResponse;
import com.bai2.repository.CartItemRepository;
import com.bai2.repository.ProductRepository;
import com.bai2.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerCartService {
    @Autowired private CartItemRepository CartItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartItemRepository cartItemRepository;

    public ResponseEntity<?> getAllByUserId(Long id) {
        List<CartItem> cartItems = cartItemRepository.findAllByUserId(id);
        List<CartItemResponse> responses = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            ProductResponse productResponse = new ProductResponse(cartItem.getProduct().getName(),
                                                cartItem.getProduct().getPhoto(), cartItem.getProduct().getPrice(),
                                                cartItem.getProduct().getStock());

            CartItemResponse cartItemResponse = new CartItemResponse(cartItem.getId(), productResponse, cartItem.getQuantity());
            responses.add(cartItemResponse);
        }
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    public ResponseEntity<?> createCartItem(Long userId, CartItemRequest cartItemRequest) {
        CartItem availableCart = cartItemRepository.findAllByUserIdAndProductId(userId, cartItemRequest.getProductId());
        if (availableCart != null) {
            Integer quantity = availableCart.getQuantity();
            quantity = quantity + 1;
            availableCart.setQuantity(quantity);
            cartItemRepository.save(availableCart);
        }else {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(productRepository.findById(cartItemRequest.getProductId()).orElse(null));
            cartItem.setUser(userRepository.findById(userId).orElse(null));
            cartItem.setQuantity(cartItem.getQuantity());
            cartItemRepository.save(cartItem);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> deleteById(Long id, Long userId) {
        if(!CartItemRepository.existsById(id)) {
            throw new RuntimeException("Sản phẩm không tồn tại");
        }
        cartItemRepository.deleteByIdAndUserId(id,userId);
        return new ResponseEntity<>(new MessageResponse("Sản phẩm được xóa khỏi giỏ hàng"), HttpStatus.NO_CONTENT);
    }
}
