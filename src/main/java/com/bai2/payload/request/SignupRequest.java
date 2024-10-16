package com.bai2.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Getter
public class SignupRequest {
    @NotBlank(message = "Username không được rỗng")
    @Size(min = 3, max = 10, message = "Username tối thiểu 3 và tối đa 10 ký tự")
    private String username;

    @NotBlank(message = "Email không được rỗng")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 50)
    private String email;

    @NotBlank(message = "Password không được rỗng")
    @Size(min = 6, max = 40, message = "Password tối thiểu 6  ký tự")
    private String password;




}
