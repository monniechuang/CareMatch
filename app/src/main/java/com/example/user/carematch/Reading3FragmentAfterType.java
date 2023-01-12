package com.example.user.carematch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Reading3FragmentAfterType extends Fragment {
    private static final String TAG ="FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private TextView textView;
    private RecyclerView mMainList;
    private Reading3TypeAdapter TypePageListAdapter;
    private List<Post> TypeList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View dawnStarAfterTypePageView = inflater.inflate(R.layout.reading3_aftertypefragment, container, false);
        //setContentView(R.layout.activity_type_page);
        /*
        Intent myintent = getIntent();
        Bundle bundle = myintent.getExtras();
        String type=bundle.getString("type");
        */
        String type = ((MainActivity)getActivity()).getNewType();
        TypeList = new ArrayList<>();
        TypePageListAdapter = new Reading3TypeAdapter(getActivity(),TypeList);

        mMainList = (RecyclerView) dawnStarAfterTypePageView.findViewById(R.id.rest_type_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMainList.setAdapter(TypePageListAdapter);
        //use where() to query for all of the documents that meet a certain condition, then use get() to retrieve the results:
        db.collection("Post")
                .whereEqualTo("Post_type",type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String post_id = doc.getId();
                                Log.d(TAG, "TEST1:" + post_id);

                                Post post = doc.toObject(Post.class).withId(post_id);//抓ID
                                TypeList.add(post);
                                TypePageListAdapter.notifyDataSetChanged();
                            }

                        }

                    }
                });

        return dawnStarAfterTypePageView;
    }
}
