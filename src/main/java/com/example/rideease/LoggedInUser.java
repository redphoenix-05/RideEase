package com.example.rideease;

public class LoggedInUser {
    private static LoggedInUser instance;

    private String userId; // Added field for user ID
    private String name;
    private String email;
    private String phone;
    private String gender;

    // Constructor
    public LoggedInUser(String userId, String name, String email, String phone, String gender) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
    }

    // Singleton instance getter
    public static LoggedInUser getInstance() {
        return instance;
    }

    // Singleton instance setter
    public static void setInstance(LoggedInUser user) {
        instance = user;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
