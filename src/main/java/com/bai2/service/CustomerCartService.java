package com.bai2.service;

import com.bai2.exception.NotEnoughProductsInStockException;
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

import java.util.*;

@Service
public class CustomerCartService {
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartItemRepository cartItemRepository;
    private Map<Product, Integer> products = new HashMap<>();

    public void addProduct(Product product) {
        if (products.containsKey(product)) {
            products.replace(product, products.get(product) + 1);
        }else {
            products.put(product, 1);
        }
    }
    public void removeProduct(Product product) {
        System.out.println("Sản phẩm cần xóa: " + product); // Kiểm tra sản phẩm cần xóa
        System.out.println("Các sản phẩm trong giỏ hàng: " + products.keySet()); // Kiểm tra sản phẩm trong giỏ hàng
        if (products.containsKey(product)) {
            if (products.get(product) > 1) {
                products.replace(product, products.get(product) - 1);
                System.out.println("Giảm số lượng sản phẩm: " + product.getName());
            } else if (products.get(product) == 1) {
                products.remove(product);
                System.out.println("Đã xóa sản phẩm: " + product.getName());
            }
        } else {
            System.out.println("Sản phẩm không tồn tại trong giỏ hàng: " + product.getName());
        }
    }

    public Map<Product, Integer> getProductsInCart() {
        return Collections.unmodifiableMap(products);
    }
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
        if (userId == null) {
            addProduct(productRepository.findById(cartItemRequest.getProductId()).orElse(null));
        } else {
            CartItem availableCart = cartItemRepository.findAllByUserIdAndProductId(userId, cartItemRequest.getProductId());
            if (availableCart != null) {
                Integer quantity = availableCart.getQuantity();
                quantity = quantity + 1;
                availableCart.setQuantity(quantity);
                cartItemRepository.save(availableCart);
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setProduct(productRepository.findById(cartItemRequest.getProductId()).orElse(null));
                cartItem.setUser(userRepository.findById(userId).orElse(null));
                cartItem.setQuantity(cartItemRequest.getQuantity());
                cartItemRepository.save(cartItem);
            }
        }
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @Transactional
    public ResponseEntity<?> deleteById(Long id, Long userId) {
        if(userId == null){
            Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
            System.out.println("A" +product);
            removeProduct(product);
            return new ResponseEntity<>(new MessageResponse("Sản phẩm đã được xóa"), HttpStatus.NO_CONTENT);
        }else {
            if(!cartItemRepository.existsById(id)) {
                throw new RuntimeException("Sản phẩm không tồn tại");
            }
            cartItemRepository.deleteByIdAndUserId(id,userId);
            return new ResponseEntity<>(new MessageResponse("Sản phẩm được xóa khỏi giỏ hàng"), HttpStatus.NO_CONTENT);
        }

    }

    @Transactional
    public ResponseEntity<?> checkout(Long userId) throws NotEnoughProductsInStockException {

        List<CartItem> cartItems = cartItemRepository.findAllByUserId(userId);
        if (cartItems.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Giỏ hàng trống, không thể đặt hàng"), HttpStatus.BAD_REQUEST);
        }

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.getReferenceById(cartItem.getProduct().getId());

            if (product.getStock() < cartItem.getQuantity()) {
                throw new NotEnoughProductsInStockException();
            }
            product.setStock(product.getStock() - cartItem.getQuantity());
        }

        productRepository.saveAll(cartItems.stream().map(CartItem::getProduct).distinct().toList());
        cartItemRepository.deleteAll(cartItems);

        return new ResponseEntity<>(new MessageResponse("Đặt hàng thành công!"), HttpStatus.OK);
    }



}
