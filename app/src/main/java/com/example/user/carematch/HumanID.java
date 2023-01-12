package com.example.user.carematch;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class HumanID {
    @Exclude
    public
    String humanId;

    public<T extends  HumanID> T withId(@NonNull final String id) {
        this.humanId = id;
        return (T) this;
    }
}
