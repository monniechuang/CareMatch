package com.example.user.carematch;

import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Reservation extends ReservationID {


    private String User_id;
    private String User_name;

    private String Reservation_name;
    private String Reservation_date;
    private String Reservation_time;
    private String Trans_id;
    private String Reservation_place,Reservation_place2;



    public Reservation() {

    }

    public Reservation(String user_id, String user_name, String reservation_name, String reservation_date, String reservation_content,
                       String reservation_time, String trans_id,String reservation_place, String reservation_place2) {
        User_id = user_id;
        User_name = user_name;
        Reservation_name = reservation_name;
        Reservation_date = reservation_date;
        Reservation_time = reservation_time;
        Reservation_place = reservation_place;
        Reservation_place2 = reservation_place2;
        Trans_id = trans_id;

    }




    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    public String getUser_name() {
        return User_name;
    }

    public void setUser_name(String user_name) {
        User_name = user_name;
    }

    public String getTrans_id() {
        return Trans_id;
    }

    public void setTrans_id(String trans_id) {
        Trans_id = trans_id;
    }

    public String getReservation_name() {
        return Reservation_name;
    }

    public void setReservation_name(String reservation_name) {
        Reservation_name = reservation_name;
    }

    public String getReservation_date() {
        return Reservation_date;
    }

    public void setReservation_date(String reservation_date) {
        Reservation_date = reservation_date;
    }



    public String getReservation_time() {
        return Reservation_time;
    }

    public void setReservation_time(String reservation_time) {
        Reservation_time = reservation_time;
    }
    public String getReservation_place() {
        return Reservation_place;
    }

    public void setReservation_place(String reservation_place) {
        Reservation_place = reservation_place;
    }

    public String getReservation_place2() {
        return Reservation_place2;
    }

    public void setReservation_place2(String reservation_place2) {
        Reservation_place2 = reservation_place2;
    }



}
