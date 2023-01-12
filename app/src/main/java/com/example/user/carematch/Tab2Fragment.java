package com.example.user.carematch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";

    private View view;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private HumanListAdapter HumanListAdapter;
    private List<Human> HumanList;
    private FirebaseAuth auth;
    private String userId;
    private ArrayList<String> humanList;


    //看護收藏
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();
        HumanList = new ArrayList<>();
        HumanListAdapter = new HumanListAdapter(getApplicationContext(), HumanList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) view.findViewById(R.id.human_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mMainList.setAdapter(HumanListAdapter);

        db.collection("users/" + userId + "/favorites")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {

                            Log.d(TAG, "Error :" + e.getMessage());
                        } else {
                            humanList = new ArrayList<>();
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {




                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String human_id = doc.getDocument().getId();

                                    Human human = doc.getDocument().toObject(Human.class).withId(human_id);//抓ID
                                    humanList.add(human_id);
                                    HumanListAdapter.notifyDataSetChanged();
                                    Log.d("TAG", humanList.toString());

                                }

                            }
                            for (String productId : humanList) {
                                db.collection("Human")
                                        .document(productId)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        String human_id = document.getId();
                                                        Log.d(TAG, human_id);

                                                        Human human = document.toObject(Human.class).withId(human_id);//抓ID
                                                        HumanList.add(human);
                                                        HumanListAdapter.notifyDataSetChanged();

                                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
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
                    }
                });

        return view;
    }
}
