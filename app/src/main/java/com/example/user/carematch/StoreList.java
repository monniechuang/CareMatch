package com.example.user.carematch;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StoreList extends AppCompatActivity {
    private static final String TAG ="FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList1;
    private com.example.user.carematch.StoreListAdapter StoreListAdapter;
    private List<Store> StoreList;


    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setCustomView(R.layout.actionbar_normal);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        StoreList = new ArrayList<Store>();
        StoreListAdapter = new StoreListAdapter(getApplicationContext(),StoreList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList1 = (RecyclerView) findViewById(R.id.store_list);
        mMainList1.setHasFixedSize(true);
        mMainList1.setLayoutManager(new LinearLayoutManager(this));
        mMainList1.setAdapter(StoreListAdapter);

        db.collection("Store").orderBy("Store_name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d(TAG, "Error :" + e.getMessage());
                } else {
                    for (DocumentChange doc: documentSnapshots.getDocumentChanges()) {

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
    }
}
