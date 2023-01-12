package com.example.user.carematch;

import android.support.annotation.NonNull;

public class StoreId {
    public String StoreId;

    public <T extends StoreId>T withId(@NonNull final  String id){
        this.StoreId = id;
        return (T) this;
    }
}
