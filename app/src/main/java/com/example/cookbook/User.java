package com.example.cookbook;

public class User {
    private int userId;
    private String email;
    private String password;
    private String fullName;
    private String avatar;

    public User() {}

    public User(int userId, String email, String password, String fullName, String avatar) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.avatar = avatar;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}