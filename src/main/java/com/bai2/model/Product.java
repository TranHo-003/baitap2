package com.bai2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Length(min = 5, message = "Tên sản phẩm phải ít nhất 5 ký tự")
    private String name;

    @Column(nullable = false)
    @DecimalMin(value = "0.00", message = "Giá phải là số dương")
    private BigDecimal price;

    @Column(name = "photo")
    private String photo;

    @Column(name = "stock", nullable = false)
    @DecimalMin(value = "0", message = "Số lượng tồn kho phải là số dương")
    private Integer stock;


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return id.equals(product.id); // So sánh theo ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Sử dụng ID để tính toán hash
    }

}
