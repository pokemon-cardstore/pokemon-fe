package com.example.pokemonshop.model;

import com.google.gson.annotations.SerializedName;

public class RegisterDto {

    @SerializedName("UserName")
    private String userName;

    @SerializedName("Email")
    private String email;

    @SerializedName("PassWord")
    private String passWord;

    @SerializedName("ConfirmPassword")
    private String confirmPassword;

    public RegisterDto(String userName, String email, String passWord, String confirmPassword) {
        this.userName = userName;
        this.email = email;
        this.passWord = passWord;
        this.confirmPassword = confirmPassword;
    }

    // Getter nếu cần
    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassWord() {
        return passWord;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
