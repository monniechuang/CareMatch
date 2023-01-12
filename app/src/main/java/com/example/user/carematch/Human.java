package com.example.user.carematch;


public class Human extends HumanID {
    private String H_age;
    private String H_city;
    private String H_educate;
    private String H_exp;
    private String H_image;
    private String H_name;
    private String H_score;
    private String H_sex;
    private String H_years;
    private String H_price;
    private String H_time;
    private String H_ava;
    private String H_region;


    public Human() {
    }

    public Human(String h_years, String h_sex, String h_exp,String h_age,
                 String h_city, String h_name, String h_educate, String h_score,
                 String h_image,String h_price,String h_time, String h_ava,String h_region) {
        H_years = h_years;
        H_region = h_region;
        H_image=h_image;
        H_sex = h_sex;
        H_exp = h_exp;
        H_age = h_age;
        H_city = h_city;
        H_name = h_name;
        H_educate = h_educate;
        H_score = h_score;
        H_price=h_price;
        H_time=h_time;
        H_ava=h_ava;

    }

    public String getH_ava() {
        return H_ava;
    }

    public void setH_ava(String h_ava) {
        H_ava = h_ava;
    }

    public String getH_image() {
        return H_image;
    }

    public void seth_image(String h_image) {
        H_image = h_image;
    }

    public String getH_price() {
        return H_price;
    }

    public void setH_price( String h_price) {
        H_price = h_price;
    }

    public String getH_sex() {
        return H_sex;
    }

    public void setH_sex( String h_sex) {
        H_sex = h_sex;
    }

    public String getH_exp() {
        return H_exp;
    }

    public void setH_exp(String h_exp) {
        H_exp = h_exp;
    }

    public String getH_age() {
        return H_age;
    }

    public void setH_age(String h_age) {
        H_age = h_age;
    }

    public String getH_city() {
        return H_city;
    }

    public void setH_city(String h_city) {
        H_city = h_city;
    }

    public String getH_name() {
        return H_name;
    }

    public void setH_name(String h_name) {
        H_name = h_name;
    }

    public String getH_educate() {
        return H_educate;
    }

    public void setH_educate(String h_educate) {
        H_educate = h_educate;
    }

    public String getH_score() {
        return H_score;
    }

    public void setH_score(String h_score) {
        H_score = h_score;
    }

    public String getH_time() {
        return H_time;
    }

    public void setH_years(String h_years) {
        H_years = h_years;
    }

    public String getH_years() {
        return H_years;
    }

    public void setH_time(String h_time) {
        H_time = h_time;
    }

    public String getH_region() {
        return H_region;
    }

    public void setH_region(String h_region) {
        H_region = h_region;
    }

}
