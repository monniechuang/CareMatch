package com.example.user.carematch;

public class Store extends StoreId {
    private String Store_name;
    private String Store_image;
    private String Store_phone;
    private String Store_details;
    private String Store_time;
    private String Store_address;
    private String Store_service;
    public Store(){

    }
    public Store(String store_name, String store_image, String store_phone, String store_details, String store_time,String store_address,String store_service){
        Store_name=store_name;
        Store_image=store_image;
        Store_phone=store_phone;
        Store_details=store_details;
        Store_time=store_time;
        Store_address=store_address;
        Store_service=store_service;
    }
    public String getStore_name() {
        return Store_name;
    }

    public void setStore_name(String store_name) {
        Store_name=store_name;
    }
    public String getStore_image() {
        return Store_image;
    }

    public void setStore_image(String store_image) { Store_image=store_image;}
    public String getStore_phone() {
        return Store_phone;
    }

    public void setStore_phone(String store_phone) {Store_phone=store_phone;}
    public String getStore_details() {
        return Store_details;
    }

    public void setStore_details(String store_details) { Store_details=store_details;}
    public String getStore_time() {
        return Store_time;
    }

    public void setStore_address(String store_address) {Store_address=store_address;}
    public String getStore_address() {
        return Store_address;
    }

    public void setStore_time(String store_time) {Store_time=store_time;}
    public String getStore_service() {
        return Store_service;
    }
    public void setStore_service(String store_service) {
        Store_service = store_service;
    }
}
