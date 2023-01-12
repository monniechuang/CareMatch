package com.example.user.carematch;

//jason
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.user.carematch.newPost.PostActivity;


public class HomeFragment extends android.support.v4.app.Fragment {

    private ImageButton imageButton_booking;
    private ImageButton imageButton_medical;
    private ImageButton imageButton_trans;
    private ImageButton imageButton_post;
    private ImageButton imageButton_read;

    public Context context;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;



    public HomeFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View homefragmentview = inflater.inflate(R.layout.fragment_home, container, false);




        //打開預約booking
        imageButton_booking=(ImageButton) homefragmentview.findViewById(R.id.imageButton_booking);
        imageButton_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new Fragment_Booking())
                        .addToBackStack(null)
                        .commit();
            }
        });
        //交通預約
        imageButton_trans=(ImageButton) homefragmentview.findViewById(R.id.imageButton_trans);
        imageButton_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    fragmentManager = getFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, new Fragment_Transportation())
                            .addToBackStack(null)
                            .commit();
                }
        });
        //醫療
        imageButton_medical=(ImageButton) homefragmentview.findViewById(R.id.imageButton_medical);
        imageButton_medical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new ProductsSearchFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        //連接到Post
        imageButton_read=(ImageButton) homefragmentview.findViewById(R.id.imageButton_read);
        imageButton_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new PostSearchFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        //連接到Blog
        imageButton_post=(ImageButton) homefragmentview.findViewById(R.id.imageButton_post);
        imageButton_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new BlogFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });


        return homefragmentview;
    }



}

