package com.example.user.carematch;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class CareID {
    @Exclude
    public
    String careId;

    public<T extends CareID> T withId(@NonNull final String id) {
        this.careId = id;
        return (T) this;
    }
}
