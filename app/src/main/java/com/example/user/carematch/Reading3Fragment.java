package com.example.user.carematch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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


public class Reading3Fragment extends Fragment {
    private static final String TAG = "Reading3Fragment";

    private View PostListview;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private PostListAdapter PostListAdapter;
    private List<Post> PostList;


    //CM推薦文章
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PostListview = inflater.inflate(R.layout.reading3_fragment, container, false);


        PostList = new ArrayList<>();
        PostListAdapter = new PostListAdapter(getApplicationContext(), PostList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) PostListview.findViewById(R.id.post_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMainList.setAdapter(PostListAdapter);

        db.collection("Post")
                .orderBy("Post_title").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d(TAG, "Error :" + e.getMessage());
                } else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String post_id = doc.getDocument().getId();

                            Post post = doc.getDocument().toObject(Post.class).withId(post_id);//抓ID
                            PostList.add(post);
                            PostListAdapter.notifyDataSetChanged();

                        }

                    }


                }

            }
        });

        return PostListview;
    }

}