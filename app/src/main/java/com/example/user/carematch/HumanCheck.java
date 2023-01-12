package com.example.user.carematch;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HumanCheck extends AppCompatActivity {
    private static final String TAG ="FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private ReviewListAdapter ReviewListAdapter;
    private List<Review> ReviewList;
    private FirebaseAuth auth;
    private String androidId;
    public TextView humanName;
    public TextView human_id;
    public TextView humanCity;
    public TextView humanAge;
    public TextView humanEducate;
    public TextView humanScore;
    public TextView humanPrice;
    public TextView humanSex;
    public TextView humanYears;
    public TextView humanExp;
    public TextView humanTime;
    public TextView humanAva;
    public ImageView humanImage;
    private Button addButton;
    private Button bookButton,bookCancel;
    private String flag;


    private String care_date,final_time,care_image;



    private String user_id;
    private String user_name;
    private String url;
    private String name,age,exp,sex,price,time_time;




    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayofMonth;
    Calendar calendar;
    TextView selectDate;

    Spinner TimeSp;

    ActionBar actionBar;



    private RecyclerView mMainList1;
    private HumanListAdapter HumanListAdapter;
    private List<Human> HumanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human_check);


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setCustomView(R.layout.actionbar_normal);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        androidId = auth.getCurrentUser().getUid();

        final FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if (User != null) {
            user_id = User.getUid();
            Log.d(TAG, "userid" +user_id);
        }
        if ( auth.getCurrentUser()!= null) {
            db.collection("users").document(user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                user_name=task.getResult().getString("name");
                            } else {
                            }
                        }
                    });
        }



        HumanList = new ArrayList<>();
        HumanListAdapter = new HumanListAdapter(getApplicationContext(), HumanList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList1 = (RecyclerView) findViewById(R.id.human_list);
        mMainList1.setHasFixedSize(true);
        mMainList1.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mMainList1.setAdapter(HumanListAdapter);


        final String currentUserID = auth.getCurrentUser().getUid();

        //取得傳遞過來的資料
        Intent intent = this.getIntent();
        final String humanId = intent.getStringExtra("HumanId");

        humanName=(TextView)findViewById(R.id.human_name);
        humanCity=(TextView)findViewById(R.id.human_city);
        humanEducate=(TextView)findViewById(R.id.human_educate);
        humanPrice=(TextView) findViewById(R.id.human_price);
        humanExp=(TextView) findViewById(R.id.human_exp);
        humanSex=(TextView) findViewById(R.id.human_sex);
        humanAge=(TextView) findViewById(R.id.human_age);
        humanTime =(TextView) findViewById(R.id.human_time);
        humanAva =(TextView) findViewById(R.id.human_ava);
        humanImage=(ImageView) findViewById(R.id.human_image);



        //選擇日期
        selectDate = findViewById(R.id.bookdate);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                final int year1 = calendar.get(Calendar.YEAR);
                final int month1 = calendar.get(Calendar.MONTH);
                final int day1 = calendar.get(Calendar.DAY_OF_MONTH);
                //Fragment_Booking
                datePickerDialog = new DatePickerDialog(HumanCheck.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("WrongConstant")
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                String date = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
                                String date1 = String.valueOf(year)  + String.valueOf(month + 1) +String.valueOf(day);

                                if(year1>year){
                                    flag= String.valueOf(1);
                                    Toast.makeText(HumanCheck.this, "預約年分不得小於今年，請重新選擇", 5000).show();
                                }else if(year1 == year){
                                    if((month1+1)>(month+1)){
                                        flag= String.valueOf(2);
                                        Toast.makeText(HumanCheck.this, "預約月分不得小於本月，請重新選擇", 5000).show();
                                    }else if((month1+1)==(month+1)){
                                        if(day1>day){
                                            flag= String.valueOf(3);
                                            Toast.makeText(HumanCheck.this, "預約日不得小於本日，請重新選擇", 5000).show();
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
        Task<DocumentSnapshot> documentSnapshotTask = db.collection("Human").document(humanId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        humanName.setText(document.get("H_name").toString());
                        name = document.get("H_name").toString();

                        humanPrice.setText(document.get("H_price").toString());
                        price = document.get("H_price").toString();

                        humanExp.setText(document.get("H_exp").toString());
                        exp = document.get("H_exp").toString();

                        humanSex.setText(document.get("H_sex").toString());
                        sex = document.get("H_sex").toString();

                        humanAge.setText(document.get("H_age").toString());
                        age = document.get("H_age").toString();

                        humanTime.setText(document.get("H_time").toString());
                        time_time = document.get("H_time").toString();






                        String image = document.get("H_image").toString();
                        url=image;
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        StorageReference picReference = storageReference.child("Human/"+image);

                        Glide.with(humanImage.getContext())
                                .using(new FirebaseImageLoader())
                                .load(picReference)
                                .into(humanImage);


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        TimeSp = (Spinner) findViewById(R.id.spn_time);
        final String [] time = {"點選請確認時段","06:00~12:00", "12:00~18:00", "18:00~24:00", "24:00~06:00"};



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HumanCheck.this, android.R.layout.simple_spinner_item,time);
        TimeSp.setAdapter(adapter);




        TimeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final_time = TimeSp.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        //預約
        bookButton =(Button) findViewById(R.id.book_care);
        bookButton.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view){

                AlertDialog.Builder mBuider = new AlertDialog.Builder(HumanCheck.this);
                mBuider.setTitle("看護預約:"+ name);
                mBuider.setIcon(R.drawable.logo_blueword);
                mBuider.setMessage("請至我的預約確認！");

                mBuider.setPositiveButton("是", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                                        //加入看護預約時候的當下時間
                                        Map<String, Object> careMap = new HashMap<>();
                                        careMap.put("Care_currentTime", FieldValue.serverTimestamp());
                                        careMap.put("Care_time", final_time);
                                        careMap.put("Care_date", care_date);
                                        careMap.put("Care_image", url);
                                        careMap.put("Care_name", name);
                                        careMap.put("Care_age", age);
                                        careMap.put("Care_sex", sex);
                                        careMap.put("Care_exp", exp);
                                        careMap.put("User_id", user_id);
                                        careMap.put("User_name", user_name);
                                        careMap.put("Human_id", humanId);



                        DocumentReference okay  =db.collection("Human").document(humanId);
                        okay.update("H_ava","此時段已滿");


                        db.collection("users/" + user_id + "/Care")
                                .add(careMap)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                            @Override
                            public void onSuccess(DocumentReference documentReference) {



                                Toast.makeText(HumanCheck.this,"預約成功！請至「我的預約」確認",Toast.LENGTH_SHORT).show();


                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(), MainActivity.class);
                                intent.putExtra("HumanId",humanId);
                                Log.d(TAG,"Id: "+humanId);
                                startActivity(intent);



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String error = e.getMessage();
                                Toast.makeText(HumanCheck.this,"失敗",Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }).show();

            }
        });





    }
}
