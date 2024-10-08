package com.bai2.controller;

import com.bai2.model.Product;
import com.bai2.payload.request.ProductRequest;
import com.bai2.service.FileStorageService;
import com.bai2.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired private FileStorageService fileStorageService;

    @GetMapping("/products")
    public Map<String, Object> fetchPaginationAllProducts(@RequestParam(value = "page", required = false) int page,
                                                          @RequestParam(value = "name", defaultValue = "0") String name,
                                                          @RequestParam(value = "size", defaultValue = "4") int size)
    {
        return productService.getAllPageableProduct(name,page,size);
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid  @RequestBody ProductRequest request, @RequestPart MultipartFile file){
        return productService.create(request,file);
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request,
                                        @RequestPart MultipartFile file) throws IOException {
        return productService.updateProductById(request,file,id);
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<?> getProductImage(@PathVariable String filename) {
        Resource resource = fileStorageService.load(filename);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
    }

    @DeleteMapping("/product/{id}")
    public boolean deleteProduct(@PathVariable Long id) {
         productService.deleteProductById(id);
         return true;
    }

    @GetMapping("/product/{id}")
    public Product getProductById(@PathVariable("id") Long id) {
        return productService.findById(id);
    }



}
