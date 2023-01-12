package com.example.user.carematch;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.facebook.GraphRequest.TAG;
import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class Fragment_Booking extends Fragment {


    private Spinner spinner_date;
    private Spinner spinner_time;


    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


//    //開始時間
//    TextView chooseTime;
//    TextView chooseEndTime;
//    TimePickerDialog timePickerDialog;
//    int currentHour;
//    int currentMinute;
//    String ampm;



    //Spinner
    Spinner chooseTimeSp;
    Spinner choosePlaceSp;

    //日期的
    TextView selectDate;
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayofMonth;
    Calendar calendar;

    //搜尋
    Button Button_search;
    Button Button_match;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private AppCompatDelegate delegate;

    private static final String TAG ="FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private com.example.user.carematch.HumanListAdapter HumanListAdapter;
    private List<Human> HumanList;

    public Fragment_Booking() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View bookingView = inflater.inflate(R.layout.fragment_booking, container, false);


        //進入
        Button_search = bookingView.findViewById(R.id.Button_search);
        Button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new HumanFragment())
                        .addToBackStack(null)
                        .commit();


            }
        });





        return bookingView;

    }


}




