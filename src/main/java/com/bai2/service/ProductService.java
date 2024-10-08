package com.bai2.service;

import com.bai2.exception.ResourceNotFoundException;
import com.bai2.model.Product;
import com.bai2.payload.request.ProductRequest;
import com.bai2.payload.response.MessageResponse;
import com.bai2.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private FileStorageService fileStorageService;

    public Map<String, Object> getAllPageableProduct(String name, int page, int size) {
        try {
            List<Product> products = new ArrayList<Product>();
            Pageable pagiantion = PageRequest.of(page,size);
            Page<Product> productPage;
            if(name == null){
                productPage = productRepository.findAll(pagiantion);
            }else {
                productPage = productRepository.findByNameContaining(name, pagiantion);
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

        int status = fileStorageService.save(file);
        if(status != 0){
            product.setPhoto(file.getOriginalFilename());
        }else{
            throw new RuntimeException("Tải ảnh thất bại");
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
    public ResponseEntity<?> deleteProductById(Long id){
        if(!productRepository.existsById(id)){
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
        productRepository.deleteById(id);
        return new ResponseEntity<>(new MessageResponse("Xóa sản phẩm thành công"), HttpStatus.OK);

    }
}
