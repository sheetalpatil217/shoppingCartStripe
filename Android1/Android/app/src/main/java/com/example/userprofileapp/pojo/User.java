package com.example.userprofileapp.pojo;

import java.io.Serializable;

public class User implements Serializable {
    String userId;
    String first_name;
    String last_name;
    Integer age;
    Double weight;
    String address;
    String email;
    String password;
    String Token;
    String status;
    String message;
    String StripeToken;


    public String getStripeToken() {
        return StripeToken;
    }

    public void setStripeToken(String stripeToken) {
        StripeToken = stripeToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFname() {
        return first_name;
    }

    public void setFname(String fname) {
        this.first_name = fname;
    }

    public String getLname() {
        return last_name;
    }

    public void setLname(String lname) {
        this.last_name = lname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public User() {
    }


    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", fname='" + first_name + '\'' +
                ", lname='" + last_name + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
