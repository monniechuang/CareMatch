package com.example.user.carematch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.example.user.carematch.R;
import com.example.user.carematch.User;
import com.example.user.carematch.Utils;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.example.user.carematch.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth auth;
    private FirebaseFirestore user;
    private EditText name , phoneNumber ,address , surname;
    private Button update;
    private CircleImageView profileImg;
    private ArcProgress arcProgress;
    private ProgressBar progressBar;

    ActionBar actionBar;

    android.app.FragmentManager fragmentManager;
    android.app.FragmentTransaction fragmentTransaction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setCustomView(R.layout.actionbar_normal);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);


        auth = FirebaseAuth.getInstance();
        user = FirebaseFirestore.getInstance();

        name = findViewById(R.id.tv_name);
        surname = findViewById(R.id.tv_surname);
        phoneNumber = findViewById(R.id.tv_phone_number);
        address = findViewById(R.id.tv_address);
        profileImg = findViewById(R.id.userProfileImgHeader);
        arcProgress = findViewById(R.id.arc_progress);
        update = findViewById(R.id.update_button);
        update.setOnClickListener(this);
        progressBar = findViewById(R.id.edit_profile_progress);
        getUserProfile();

    }

    public void getUserProfile() {

        if (auth.getCurrentUser() != null) {
            user.collection("users").document(auth.getCurrentUser().getUid())
                    .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot, final FirebaseFirestoreException e) {
                            if(documentSnapshot.exists()){
                                final User user = documentSnapshot.toObject(User.class);
                                name.setText(user.getName());
                                surname.setText(user.getSurname());
                                phoneNumber.setText(user.getPhoneNumber());
                                address.setText(user.getAddress());

                            }else {
                                showError(e.getMessage());
                            }
                        }
                    });
        }

    }

//    public void chooseImage(){
//        Intent gallery_intent = new Intent();
//        gallery_intent.setType("image/*");
//        gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
//        if(gallery_intent.resolveActivity(getPackageManager()) != null){
//            startActivityForResult(Intent.createChooser(gallery_intent,"請選擇圖片"),1);
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.uploadImage(data,requestCode,resultCode,EditProfileActivity.this,auth,arcProgress);
    }

    public void updateUser(String nm ,String sur ,String ad , String phone ){
        if(TextUtils.isEmpty(nm) || TextUtils.isEmpty(sur) || TextUtils.isEmpty(ad) || TextUtils.isEmpty(phone)){
            showError("請填寫正確！");
        }else{
            update.setClickable(false);
            update.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            if(auth.getCurrentUser()!= null){
                Map<String,String> map = new HashMap<>();
                map.put("name",nm);
                map.put("phoneNumber",phone);
                map.put("surname",sur);
                map.put("address",ad);

                user.collection("users").document(auth.getCurrentUser().getUid())
                        .set(map, SetOptions.merge())
                        .addOnCompleteListener(EditProfileActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    showSuccess("我的專區資料已更新！");
                                    update.setClickable(true);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    update.setVisibility(View.VISIBLE);
                                }else {
                                    if(task.getException() != null)
                                        showError(task.getException().getMessage());
                                    update.setClickable(true);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    update.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.userProfileImgHeader : {
//                chooseImage();
//            }
            case R.id.update_button : {
                updateUser(name.getText().toString(),surname.getText().toString(),address.getText().toString(),phoneNumber.getText().toString());
                finish();
            }
        }

    }

    public void showError(String s) {

    }

    public void showSuccess(String s) {

    }
}
