package com.example.user.carematch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class HumanList extends android.support.v4.app.Fragment {

    private static final String TAG ="FireLog";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private com.example.user.carematch.HumanListAdapter HumanListAdapter;
    private List<Human> HumanList;
    //????????????????????????
    Spinner DivisionSp,SexSp,SortSp,TimeSp;
    Button ButtonShowDialog;
    //?????????
    TextView selectDate;
    private String flag;
    private String care_date;
    private String care_time;

    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayofMonth;
    Calendar calendar;


    private Care care = new Care();

    private View HumanListview;


public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    HumanListview = inflater.inflate(R.layout.fragment_human_list, container, false);

    final String currentUserID = firebaseAuth.getCurrentUser().getUid();
//    final String human_id =HumanList.get(position).humanId;//???ID ,List????????????

        ButtonShowDialog = (Button) HumanListview.findViewById(R.id.ButtonShowDialog);
        ButtonShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder humanBuilder = new AlertDialog.Builder(HumanList.this.getActivity());
                View mView = getLayoutInflater().inflate(R.layout.dialog_human,null);
                humanBuilder.setTitle("????????????");


                //????????????
                selectDate = mView.findViewById(R.id.bookdate);
                selectDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calendar = Calendar.getInstance();
                        final int year1 = calendar.get(Calendar.YEAR);
                        final int month1 = calendar.get(Calendar.MONTH);
                        final int day1 = calendar.get(Calendar.DAY_OF_MONTH);
                        //Fragment_Booking
                        datePickerDialog = new DatePickerDialog(HumanList.this.getActivity(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @SuppressLint("WrongConstant")
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int day) {
                                        String date = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
                                        String date1 = String.valueOf(year)  + String.valueOf(month + 1) +String.valueOf(day);

                                        if(year1>year){
                                            flag= String.valueOf(1);
                                            Toast.makeText(HumanList.this.getActivity(), "????????????????????????????????????????????????", 5000).show();
                                        }else if(year1 == year){
                                            if((month1+1)>(month+1)){
                                                flag= String.valueOf(2);
                                                Toast.makeText(HumanList.this.getActivity(), "????????????????????????????????????????????????", 5000).show();
                                            }else if((month1+1)==(month+1)){
                                                if(day1>day){
                                                    flag= String.valueOf(3);
                                                    Toast.makeText(HumanList.this.getActivity(), "?????????????????????????????????????????????", 5000).show();
                                                }else{
                                                    flag= String.valueOf(4);
                                                }
                                            }
                                        }
                                        selectDate.setText(date);
                                        care_date=selectDate.getText().toString();


                                    }
                                }, year1, month1, day1);
                        datePickerDialog.show();
                    }
                });


                DivisionSp = mView.findViewById(R.id.spn_division);
                SexSp = mView.findViewById(R.id.spn_sex) ;
                SortSp = mView.findViewById(R.id.spn_sort) ;
                TimeSp = mView.findViewById(R.id.spn_time);

                selectDate = mView.findViewById(R.id.bookdate);



                String division[]  = {"--???????????????--","??????", "??????", "??????", "??????"};
                String sex[]  = {"--???????????????--","???", "???"};
                String sort[]  = {"--???????????????--", "??????????????????", "??????????????????", "??????????????????","??????????????????"};
                String time[]  = {"--???????????????--","06:00~12:00", "12:00~18:00", "18:00~24:00", "24:00~06:00"};



                ArrayAdapter<String> adapter = new ArrayAdapter<String>(HumanList.this.getActivity(), android.R.layout.simple_spinner_item,division);
                DivisionSp.setAdapter(adapter);

                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(HumanList.this.getActivity(), android.R.layout.simple_spinner_item,sex);
                SexSp.setAdapter(adapter1);

                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(HumanList.this.getActivity(), android.R.layout.simple_spinner_item,sort);
                SortSp.setAdapter(adapter2);

                ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(HumanList.this.getActivity(), android.R.layout.simple_spinner_item,time);
                TimeSp.setAdapter(adapter3);

                humanBuilder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (!DivisionSp.getSelectedItem().toString().equalsIgnoreCase("--???????????????--") ||
                                !SexSp.getSelectedItem().toString().equalsIgnoreCase("--???????????????--") ||
                                !SortSp.getSelectedItem().toString().equalsIgnoreCase("--???????????????--") ||
                                !TimeSp.getSelectedItem().toString().equalsIgnoreCase("--???????????????--")) {


                            //?????????????????????????????????06:00~12:00
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {



                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }

                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }


                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }

                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("???????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }

                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }

                            //????????????


                            //?????????????????????????????????18:00~24:00
                            //????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }

                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }


                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }

                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("???????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }

                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_years", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {
                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.ASCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                            //??????????????????????????????
                            if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("??????")
                                    & SexSp.getSelectedItem().toString().equalsIgnoreCase("???")
                                    & SortSp.getSelectedItem().toString().equalsIgnoreCase("??????????????????")
                                    & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                    || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                    ) {

                                HumanList.clear();
                                db.collection("Human")
                                        .whereEqualTo("H_region", "??????")
                                        .whereEqualTo("H_sex", "???")
                                        .whereEqualTo("H_time", "??????")
                                        .whereEqualTo("H_ava", "?????????")
                                        .orderBy("H_age", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.d(TAG, "Error :" + e.getMessage());
                                                } else {
                                                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                                            String human_id = doc.getDocument().getId();
                                                            Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
                                                            HumanList.add(human);
                                                            HumanListAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }

                            //????????????

                            dialog.dismiss();
                        }





                    }





                });

                //??????
                humanBuilder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                humanBuilder.setView(mView);
                AlertDialog dialog = humanBuilder.create();
                dialog.show();
            }
        });


//    Context context=getContext();
//
//
//    Intent intent = new Intent();
//        intent.setClass(context, AddHumanReview.class);
//        intent.putExtra("HumanId", humanId);
//        Log.d(TAG,"Id: "+humanId);
//        context.startActivity(intent);



    HumanList = new ArrayList<>();
        HumanListAdapter = new HumanListAdapter(getApplicationContext(), HumanList);
        //??????RecylerView????????????????????????adapter
        mMainList = (RecyclerView) HumanListview.findViewById(R.id.human_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mMainList.setAdapter(HumanListAdapter);


        //????????????
    HumanList.clear();
//    db.collection("Human")
//                .whereEqualTo("H_ava","?????????")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                        if (e != null) {
//                            Log.d(TAG, "Error :" + e.getMessage());
//                        } else {
//                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
//                                if (doc.getType() == DocumentChange.Type.ADDED) {
//                                    String human_id = doc.getDocument().getId();
//                                    Human human = doc.getDocument().toObject(Human.class).withId(human_id);//???ID
//                                    HumanList.add(human);
//                                    HumanListAdapter.notifyDataSetChanged();
//                                }
//                            }
//                        }
//                    }
//                });


        return HumanListview;
    }


}
