package com.example.missyou;


/*
 * Created by Sue on 07/10/2019
 * */

public class User {

    public String name, email, phone, havePet, image;

    public User(){

    }

    public User(String name, String email, String phone, String havePet, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.havePet = havePet;
        this.image = image;
    }
}
