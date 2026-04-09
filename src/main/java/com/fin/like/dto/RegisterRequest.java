package com.fin.like.dto;

public class RegisterRequest {

    private String userId;
    private String userName;
    private String email;
    private String account;
    private String username;
    private String password;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
