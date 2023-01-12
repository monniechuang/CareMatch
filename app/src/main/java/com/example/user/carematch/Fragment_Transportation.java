package com.example.user.carematch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class Fragment_Transportation extends android.support.v4.app.Fragment {


    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    Button Button_search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View feedBackView = inflater.inflate(R.layout.fragment_transportation, container, false);




        Button_search=(Button) feedBackView.findViewById(R.id.Button_search);
        Button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View d) {

                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new ReservationActivity())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return feedBackView;
    }


}
