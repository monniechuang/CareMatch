package com.example.user.carematch;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PostList extends android.support.v4.app.Fragment {

    private View PostListview;
    private static final String TAG ="FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private PostListAdapter PostListAdapter;
    private List<Post> PostList;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_post_list);


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        PostListview = inflater.inflate(R.layout.list_post, container, false);

        PostList = new ArrayList<>();
        PostListAdapter = new PostListAdapter(getApplicationContext(),PostList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) PostListview.findViewById(R.id.post_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMainList.setAdapter(PostListAdapter);

        db.collection("Post").orderBy("Post_title").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
