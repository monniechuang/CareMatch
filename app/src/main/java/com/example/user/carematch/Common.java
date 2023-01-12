package com.example.user.carematch;

import com.example.user.carematch.Retrofit.CareMatchAPI;
import com.example.user.carematch.Retrofit.RetrofitClient;

public class Common {
    private static final String BASE_URL= "http://localhost/CareMatch/";

    public static CareMatchAPI getAPI()
    {
        return RetrofitClient.getClient(BASE_URL).create(CareMatchAPI.class);
    }
}
