package com.example.user.carematch.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class CommentsID {
    @Exclude
    public
    String commentsId;

    public<T extends CommentsID> T withId(@NonNull final String id) {
        this.commentsId = id;
        return (T) this;
    }
}

