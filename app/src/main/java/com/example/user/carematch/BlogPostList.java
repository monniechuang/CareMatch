package com.example.user.carematch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;

import com.example.user.carematch.model.BlogPost;
import com.example.user.carematch.newPost.BlogRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BlogPostList extends AppCompatActivity {
    private RecyclerView blog_list_Rv;
    private List<BlogPost> blog_list;
    private static final String TAG ="FireLog";
    private ImageButton test;
    ActionBar actionBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setCustomView(R.layout.actionbar_normal);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        setContentView(R.layout.activity_blog_post_list);
        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();//取得傳遞過來的資料
        String user = intent.getStringExtra("user");
        blog_list = new ArrayList<>();
        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list, getApplicationContext());
        //取得RecylerView物件，設定佈局及adapter
        blog_list_Rv = (RecyclerView) findViewById(R.id.blog_list);
        blog_list_Rv.setHasFixedSize(true);
        blog_list_Rv.setLayoutManager(new LinearLayoutManager(this));
        blog_list_Rv.setAdapter(blogRecyclerAdapter);
        db.collection("BlogPost").whereEqualTo("user_id",user).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d(TAG,"Error :" + e.getMessage());
                } else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostID = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostID);
                            blog_list.add(blogPost);
                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }


                }
            }
        });

    }
}
