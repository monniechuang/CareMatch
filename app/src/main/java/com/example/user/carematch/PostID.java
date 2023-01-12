package com.example.user.carematch;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class PostID {
    @Exclude
    public
    String postId;

    public<T extends  PostID> T withId(@NonNull final String id) {
        this.postId = id;
        return (T) this;
    }
}
