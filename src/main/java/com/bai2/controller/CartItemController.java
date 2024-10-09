package com.bai2.controller;

import com.bai2.model.CartItem;
import com.bai2.payload.request.CartItemRequest;
import com.bai2.service.CustomerCartService;
import com.bai2.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
@RestController
@RequestMapping("/api/cart-item")
public class CartItemController {
    @Autowired private CustomerCartService customerCartService;

    @GetMapping("/list")
    public ResponseEntity<?> getAllByUserId(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return customerCartService.getAllByUserId(userDetails.getId());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @Valid @RequestBody CartItemRequest request){
        return customerCartService.createCartItem(userDetails.getId(), request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return customerCartService.deleteById(id, userDetails.getId());
    }



}
