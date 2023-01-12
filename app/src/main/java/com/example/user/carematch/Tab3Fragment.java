package com.example.user.carematch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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


public class Tab3Fragment extends Fragment {
    private static final String TAG = "Tab3Fragment";

    private ImageButton imageButton_logo;
    private View view;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private PostListAdapter PostListAdapter;
    private List<Post> PostList;
    private FirebaseAuth auth;
    private String userId;
    private ArrayList<String> postList;


    //文章收藏
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment,container,false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();
        PostList = new ArrayList<>();
        PostListAdapter = new PostListAdapter(getApplicationContext(), PostList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) view.findViewById(R.id.post_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mMainList.setAdapter(PostListAdapter);

        db.collection("users/" + userId + "/favorites").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d(TAG, "Error :" + e.getMessage());
                } else {
                    postList = new ArrayList<>();
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String post_id = doc.getDocument().getId();

                            Post post = doc.getDocument().toObject(Post.class).withId(post_id);//抓ID
//                            ProductsList.add(products);
                            postList.add(post_id);
                            PostListAdapter.notifyDataSetChanged();
                            Log.d("TAG", postList.toString());

                        }

                    }
                    for (String productId : postList) {
                        db.collection("Post")
                                .document(productId)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                String post_id = document.getId();
                                                Log.d(TAG, post_id);

                                                Post post = document.toObject(Post.class).withId(post_id);//抓ID
                                                PostList.add(post);
                                                PostListAdapter.notifyDataSetChanged();

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
