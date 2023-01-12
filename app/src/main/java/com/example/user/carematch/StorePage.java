package com.example.user.carematch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stripe.model.Product;

import java.util.ArrayList;
import java.util.List;

public class StorePage extends AppCompatActivity {
    //    private static final String TAG ="Dairy";
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    public TextView storeName;
//    public TextView storeAddress;
//    public TextView storeTime;
//    public TextView storePhone;
//    public TextView storeDetails;
//    public ImageView storeImage;
    private static final String TAG = "FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private RecyclerView mMainList1;
    private StoreListAdapter StoreListAdapter;
    private ProductsListAdapter ProductsListAdapter;
    private List<Store> StoreList;
    private List<Products> ProductsList;
    //    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public TextView storeName;
    public TextView storeAddress;
    public TextView storeTime;
    public TextView storePhone;
    public TextView storeDetails;
    public ImageView storeImage;
    public ImageView position;

    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_page);


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setCustomView(R.layout.actionbar_normal);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        Intent intent = this.getIntent();//取得傳遞過來的資料
        String storeId = intent.getStringExtra("StoreId");
        String storename = intent.getStringExtra("s");
        final String x = intent.getStringExtra("lat");
        final String y = intent.getStringExtra("lon");
        storeName = (TextView) findViewById(R.id.store_name);
        storeAddress = (TextView) findViewById(R.id.store_address);
        storeTime = (TextView) findViewById(R.id.store_time);
        storePhone = (TextView) findViewById(R.id.store_phone);
        storeDetails = (TextView) findViewById(R.id.store_details);
        storeImage = (ImageView) findViewById(R.id.store_image);

        StoreList = new ArrayList<>();
        StoreListAdapter = new StoreListAdapter(getApplicationContext(), StoreList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) findViewById(R.id.store_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(this));
        mMainList.setAdapter(StoreListAdapter);
        ProductsList = new ArrayList<>();
        ProductsListAdapter = new ProductsListAdapter(getApplicationContext(), ProductsList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList1 = (RecyclerView) findViewById(R.id.products_list);
        mMainList1.setHasFixedSize(true);
        mMainList1.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mMainList1.setAdapter(ProductsListAdapter);

        db.collection("Store")
                .whereEqualTo("Store_name", storename)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d(TAG, "Error :" + e.getMessage());
                } else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String store_id = doc.getDocument().getId();

                            Store store = doc.getDocument().toObject(Store.class).withId(store_id);//抓ID
                            StoreList.add(store);
                            StoreListAdapter.notifyDataSetChanged();

                        }

                    }


                }
            }
        });

        db.collection("Products")
                .whereEqualTo("Products_storename", storename)
                .orderBy("Products_name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d(TAG, "Error :" + e.getMessage());
                } else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String products_id = doc.getDocument().getId();

                            Products products = doc.getDocument().toObject(Products.class).withId(products_id);//抓ID
                            ProductsList.add(products);
                            ProductsListAdapter.notifyDataSetChanged();

                        }

                    }


                }
            }
        });


    }
}