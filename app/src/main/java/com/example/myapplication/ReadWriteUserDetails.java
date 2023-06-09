package com.example.myapplication;

public class ReadWriteUserDetails {
    public String fullname,doB,gender,mobile,email;

    public ReadWriteUserDetails(String textFullName,String textDoB,String textGender,String textmobile,String textEmail){
        this.fullname=textFullName;
        this.doB=textDoB;
        this.gender=textGender;
        this.mobile=textmobile;
        this.email=textEmail;
    }
}
