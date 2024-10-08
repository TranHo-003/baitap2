package com.bai2.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Bạn chưa nhập tên tài khoản!")
    private String username;

    @NotBlank(message = "Bạn chưa nhập mật khẩu!")
    private String password;
}
