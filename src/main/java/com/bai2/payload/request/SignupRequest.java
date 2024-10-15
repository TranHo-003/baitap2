package com.bai2.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
@Data
public class SignupRequest {
    @NotBlank(message = "Username không được rỗng")
    @Size(min = 3, max = 20, message = "Bạn chưa nhập username")
    private String username;

    @NotBlank(message = "email không được rỗng")
    @Size(max = 50, message = "Bạn chưa nhập email")
    private String email;

    @NotBlank(message = "Password không được rỗng")
    @Size(min = 6, max = 40, message = "Bạn chưa nhập password")
    private String password;

    private Set<String> roles;


}
