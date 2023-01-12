package com.example.user.carematch;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ReviewID {
    @Exclude
    public
    String reviewId;

    public<T extends  ReviewID> T withId(@NonNull final String id) {
        this.reviewId = id;
        return (T) this;
    }
}
