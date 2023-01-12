//package com.example.user.carematch;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//
//public class FirstActivity extends AppCompatActivity {
//
//
//    ActionBar actionBar;
//    Button button;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_first);
//
//
//        actionBar = getSupportActionBar();
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
//        actionBar.setCustomView(R.layout.actionbar_normal);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowCustomEnabled(true);
//
//
//
//        button = findViewById(R.id.start_use);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View d) {
//                openActivity();
//
//            }
//
//        });
//
//}
//    public void openActivity(){
//        Intent intent = new Intent(FirstActivity.this,RegisterAndLoginActivity.class);
//        startActivity(intent);
//    }
//
//
//
//}
