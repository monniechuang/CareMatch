package com.example.user.carematch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductInformationActivity extends AppCompatActivity {


    private static final String TAG ="Products";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public TextView productsName;
    public TextView productsStorename;
    public TextView productsDetails;
    public TextView productsTitle;
    public ImageView productsImage;
    String storename;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_products_information);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setCustomView(R.layout.actionbar_normal);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);


        Intent intent = this.getIntent();//取得傳遞過來的資料
        String productsId = intent.getStringExtra("ProductsId");
        final String x = intent.getStringExtra("lat");
        final String y = intent.getStringExtra("lon");
        productsName=(TextView)findViewById(R.id.products_name);
        productsStorename=(TextView)findViewById(R.id.products_storename);
//        productsTitle=(TextView) findViewById(R.id.products_title);
        productsDetails=(TextView) findViewById(R.id.products_details);
        productsImage=(ImageView) findViewById(R.id.products_image);

        ImageButton button = findViewById(R.id.Store_Button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("s",storename);
                bundle.putString("lat", x);
                bundle.putString("lon", y);
                Intent intent = new Intent(ProductInformationActivity.this, StorePage.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        Task<DocumentSnapshot> documentSnapshotTask = db.collection("Products").document(productsId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        storename = document.get("Products_storename").toString();
                        productsName.setText(document.get("Products_name").toString());
                                productsStorename.setText(document.get("Products_storename").toString());
                        productsDetails.setText(document.get("Products_details").toString());
//                                productsTitle.setText(document.get("Products_title").toString());
                        String image = document.get("Products_image").toString();
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        StorageReference picReference = storageReference.child("Products/"+image);

                        Glide.with(productsImage.getContext())
                                .using(new FirebaseImageLoader())
                                .load(picReference)
                                .into(productsImage);


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
