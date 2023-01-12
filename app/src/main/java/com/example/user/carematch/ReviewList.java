package com.example.user.carematch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ReviewList extends AppCompatActivity {
    private static final String TAG ="FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private ReviewListAdapter ReviewListAdapter;
    private List<Review> ReviewList;
    private FirebaseAuth auth;
    private String androidId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        androidId = auth.getCurrentUser().getUid();
        ReviewList = new ArrayList<>();
        ReviewListAdapter = new ReviewListAdapter(getApplicationContext(),ReviewList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) findViewById(R.id.review_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(this));
        mMainList.setAdapter(ReviewListAdapter);

        db.collection("HumanReview").orderBy("Human_id").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d(TAG, "Error :" + e.getMessage());
                } else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String review_id = doc.getDocument().getId();

                            Review review = doc.getDocument().toObject(Review.class).withId(review_id);//抓ID
                            ReviewList.add(review);
                            ReviewListAdapter.notifyDataSetChanged();

                        }

                    }


                }
            }
        });
    }
}
