package com.example.user.carematch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CareList extends AppCompatActivity {

    private static final String TAG ="FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private CareListAdapter CareListAdapter;
    private List<Care> CareList;
    private FirebaseAuth auth;
    private String userId;
    ActionBar actionBar;
    private String humanId;
    public List<Human> HumanList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_list);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setCustomView(R.layout.actionbar_normal);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();

        CareList = new ArrayList<>();
        CareListAdapter = new CareListAdapter(CareList, getApplicationContext());
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) findViewById(R.id.care_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(this));
        mMainList.setAdapter(CareListAdapter);

        final String currentUserID = auth.getCurrentUser().getUid();
//        final String humanId =HumanList.get(position).humanId;//抓ID ,List可以替換

        //取得傳遞過來的資料
        Intent intent = this.getIntent();
        final String humanId = intent.getStringExtra("HumanId");




        db.collection("users/" + userId + "/Care")
                .whereEqualTo("User_id", userId)
                .orderBy("Care_name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d(TAG, "Error :" + e.getMessage());
                } else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String care_id = doc.getDocument().getId();

                            Care care = doc.getDocument().toObject(Care.class).withId(care_id);//抓ID
                            CareList.add(care);
                            CareListAdapter.notifyDataSetChanged();

                        }

                    }


                }
            }
        });

    }
}
