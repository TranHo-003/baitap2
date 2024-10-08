package com.bai2.repository;

import com.bai2.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem>findAllByUserId(Long userId);
    CartItem findAllByUserIdAndProductId(Long userId, Long productId);
    void deleteByIdAndUserId(Long id, Long userId);
}
