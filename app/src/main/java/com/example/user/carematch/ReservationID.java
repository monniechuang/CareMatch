package com.example.user.carematch;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ReservationID {
    @Exclude
    public
    String reservationId;

    public<T extends ReservationID> T withId(@NonNull final String id) {
        this.reservationId = id;
        return (T) this;
    }
}
