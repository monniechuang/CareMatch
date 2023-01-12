package com.example.user.carematch;

public class Review extends ReviewID {
    private String Human_id;
    private String User_id;
    private String User_name;

    private String Human_review;


    public String getHuman_review() {
        return Human_review;
    }

    public void setHuman_review(String human_review) {
        Human_review = human_review;
    }




    public Review() {
    }
    public Review(String human_id, String user_id, String user_name, String human_review) {
        Human_id = human_id;
        User_id = user_id;
        User_name = user_name;
        Human_review=human_review;
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



    public String getHuman_id() {
        return Human_id;
    }

    public void setHuman_id(String human_id) {
        Human_id = human_id;
    }
}
