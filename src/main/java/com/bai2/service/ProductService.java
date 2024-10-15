package com.bai2.service;

import com.bai2.exception.ResourceNotFoundException;
import com.bai2.model.Product;
import com.bai2.payload.request.ProductRequest;
import com.bai2.payload.response.MessageResponse;
import com.bai2.repository.CartItemRepository;
import com.bai2.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    private final FileStorageService fileStorageService;

    public Map<String, Object> getAllPageableProduct(String name, Integer page, Integer size) {
        try {
            if (page == null) page = 0;
            if (size == null) size = 10;
            List<Product> products = new ArrayList<Product>();
            Pageable pagination = PageRequest.of(page,size);
            Page<Product> productPage;
            if(name == null){
                productPage = productRepository.findAll(pagination);
                System.out.println(productPage);
            }else {
                productPage = productRepository.findByNameContaining(name, pagination);
            }
            products = productPage.getContent();

            Map<String, Object> response = new HashMap<String, Object>();
            response.put("products", products);
            response.put("totalPages", productPage.getTotalPages());
            return response;
        }catch (ResourceNotFoundException exc){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm",exc);
        }

    }

    public ResponseEntity<?> create(ProductRequest productRequest, MultipartFile file) {
        int status = fileStorageService.save(file);
        if(status != 0){
            Product product = new Product();
            product.setName(productRequest.getName());
            product.setPrice(productRequest.getPrice());
            product.setStock(productRequest.getStock());
            product.setPhoto(file.getOriginalFilename());
            productRepository.save(product);
        }else {
            throw new RuntimeException("Thêm sản phẩm thất bại");
        }
        return new ResponseEntity<>(new MessageResponse("Thêm sản phẩm thành công"), HttpStatus.OK);
    }

    public ResponseEntity<?> updateProductById(ProductRequest request, MultipartFile file, Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (file != null && !file.isEmpty()) {
            int status = fileStorageService.save(file);
            if (status != 0) {
                product.setPhoto(file.getOriginalFilename());
            } else {
                throw new RuntimeException("Tải ảnh thất bại");
            }
        } else {
            product.setPhoto(product.getPhoto());
        }


        if(!product.getName().equals(request.getName())){
            product.setName(request.getName());
        }
        if(!product.getPrice().equals(request.getPrice())){
            product.setPrice(request.getPrice());
        }
        if(!product.getStock().equals(request.getStock())){
            product.setStock(request.getStock());
        }
        productRepository.save(product);
        return new ResponseEntity<>(new MessageResponse("Cập nhật sản phẩm thành công"), HttpStatus.OK);
    }

    public Product findById(Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        return product;
    }
    @Transactional
    public ResponseEntity<?> deleteProductById(Long productId){
        if(!productRepository.existsById(productId)){
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
        cartItemRepository.deleteByProductId(productId);
        productRepository.deleteById(productId);
        return new ResponseEntity<>(new MessageResponse("Xóa sản phẩm thành công"), HttpStatus.OK);

    }
}
