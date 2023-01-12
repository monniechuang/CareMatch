package com.example.user.carematch;

import java.util.Date;

public class Post extends PostID {
    private String Post_image, Post_desc, Post_thumb, Post_title, Post_date,Post_type;
    public Post(){

    }
    public Post(String post_image, String post_desc, String post_thumb,String post_title,String post_date,String post_type){
        Post_image=post_image;
        Post_desc=post_desc;
        Post_thumb=post_thumb;
        Post_date=post_date;
        Post_type=post_type;
    }
    public String getPost_type() {
        return Post_type;
    }

    public void setPost_type(String post_type) {
        Post_type=post_type;
    }
    public String getPost_image() {
        return Post_image;
    }

    public void setPost_image(String post_image) {
        Post_image=post_image;
    }
    public String getPost_desc() {
        return Post_desc;
    }

    public void setPost_desc(String post_desc) { Post_desc=post_desc;}
    public String getPost_thumb() {
        return Post_thumb;
    }

    public void setPost_thumb(String post_thumb) { Post_thumb=post_thumb;}
    public String getPost_title() {
        return Post_title;
    }

    public void setPost_title(String post_title) { Post_title=post_title;}
    public String getPost_date() {
        return Post_date;
    }

    public void setPost_date(String post_date) {
        Post_date = post_date;
    }

}
