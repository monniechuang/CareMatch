package com.example.user.carematch;

import android.support.annotation.NonNull;

public class ProductsId {
    public String productsId;

    public <T extends ProductsId>T withId(@NonNull final  String id){
        this.productsId = id;
        return (T) this;
    }

}