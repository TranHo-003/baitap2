package com.bai2.controller;

import com.bai2.exception.NotEnoughProductsInStockException;
import com.bai2.model.Product;
import com.bai2.payload.request.CartItemRequest;
import com.bai2.payload.response.CartItemResponse;
import com.bai2.payload.response.ProductResponse;
import com.bai2.service.CustomerCartService;
import com.bai2.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
@RestController
@RequestMapping("/api/cart-item")
public class CartItemController {
    @Autowired private CustomerCartService customerCartService;

//    @GetMapping("/list")
//    public ResponseEntity<?> getAllByUserId(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return customerCartService.getAllByUserId(userDetails.getId());
//    }
@GetMapping("/list")
public ResponseEntity<?> getShoppingCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    Long userId = (userDetails != null) ? userDetails.getId() : null;
    List<CartItemResponse> cartItems = new ArrayList<>();

    if (userId == null) {
        Map<Product, Integer> cartProducts = customerCartService.getProductsInCart();

        for (Map.Entry<Product, Integer> entry : cartProducts.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            ProductResponse productResponse = new ProductResponse(product.getName(), product.getPhoto(), product.getPrice(), quantity);
            CartItemResponse item = new CartItemResponse(
                    product.getId(),
                    productResponse,
                    quantity
            );
            cartItems.add(item);
        }
    } else {
        return customerCartService.getAllByUserId(userId);
    }

    return ResponseEntity.ok(cartItems);
}


    @PostMapping("/create")
    public ResponseEntity<?> create(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @Valid @RequestBody CartItemRequest request){
        Long userId = (userDetails != null) ? userDetails.getId() : null;
        return customerCartService.createCartItem(userId, request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = (userDetails != null) ? userDetails.getId() : null;
        return customerCartService.deleteById(id, userId);
    }



    @PostMapping("/checkout/{userId}")
    public ResponseEntity<?> checkout(@PathVariable("userId") Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws NotEnoughProductsInStockException {
        return customerCartService.checkout(userId);
    }

}
