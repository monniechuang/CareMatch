package com.example.user.carematch;

import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Care extends CareID {


    private String User_id;
    private String User_name;
    private String Care_name;
    private String Care_age;
    private String Care_sex;
    private String Care_exp;
    private String Care_date;
    private String Care_time;
    private String Care_image;


    //firebase日期
    public static final Parcelable.Creator<Timestamp> CREATOR = null;



    public Care() {

    }

    public Care(String user_id, String user_name,String care_age,String care_date, String care_exp,String care_name
    ,String care_time,String care_sex , String care_image) {
        User_id = user_id;
        User_name = user_name;
        Care_name = care_name;
        Care_age = care_age;
        Care_sex = care_sex;
        Care_exp = care_exp;
        Care_date = care_date;
        Care_time = care_time;
        Care_image = care_image;
    }


    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    public String getUser_name() {
        return User_name;
    }

    public void setUser_name(String user_name) {
        User_name = user_name;
    }

    public String getCare_name() {
        return Care_name;
    }

    public void setCare_name(String care_name) {
        Care_name = care_name;
    }

    public String getCare_age() {
        return Care_age;
    }

    public void setCare_age(String care_age) {
        Care_age = care_age;
    }

    public String getCare_date() {
        return Care_date;
    }

    public void setCare_date(String care_date) {
        Care_date = care_date;
    }

    public String getCare_sex() {
        return Care_sex;
    }

    public void setCare_sex(String care_sex) {
        Care_sex = care_sex;
    }

    public String getCare_exp() {
        return Care_exp;
    }

    public void setCare_exp(String care_exp) {
        Care_exp = care_exp;
    }

    public String getCare_time() {
        return Care_time;
    }

    public void setCare_time(String care_time) {
        Care_time = care_time;
    }

    public String getCare_image() {
        return Care_image;
    }

    public void setCare_image(String care_image) {
        Care_time = care_image;
    }


}
