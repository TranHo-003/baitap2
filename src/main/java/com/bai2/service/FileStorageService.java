package com.bai2.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    // Constructor để chỉ định thư mục lưu trữ
    public FileStorageService() {
        // Đường dẫn đến thư mục static/images/product trong resources
        this.fileStorageLocation = Paths.get("src/main/resources/static/images/product").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ file", ex);
        }
    }

    public int save(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Tên file không hợp lệ: " + fileName);
            }

            // Đường dẫn tới file đích
            Path targetLocation = this.fileStorageLocation.resolve(fileName);

            // Lưu file tại đường dẫn chỉ định
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Trả về 1 nếu lưu thành công
            return 1;

        } catch (IOException ex) {
            // Trả về 0 nếu có lỗi trong quá trình lưu file
            return 0;
        }
    }
    public Resource load(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Không thể tải file: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Không thể tải file: " + fileName, ex);
        }
    }
}
