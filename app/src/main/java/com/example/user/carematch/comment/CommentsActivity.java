package com.example.user.carematch.comment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.carematch.R;
import com.example.user.carematch.model.Comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {
    Toolbar toolbar_comment;

    EditText comment_field;
    ImageView comment_post_btn;
    ImageView comment_current_user;
    List<Comments> commentsList;
    RecyclerView comment_list_RV;
    //
    CommentsRecyclerAdapter commentsRecyclerAdapter;
    private String blog_post_id;
    private String current_user_id;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


//        toolbar_comment = findViewById(R.id.comment_toolbar);
//        setSupportActionBar(toolbar_comment);
//        getSupportActionBar().setTitle("Comments");
//        initViews();


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setCustomView(R.layout.actionbar_normal);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        comment_field = (EditText) findViewById(R.id.comment_field);
        comment_post_btn = (ImageView) findViewById(R.id.comment_post_btn);
        comment_current_user = (ImageView) findViewById(R.id.comment_current_user);
        commentsList = new ArrayList<>();
        comment_list_RV = (RecyclerView) findViewById(R.id.comment_list);
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList, CommentsActivity.this);
        current_user_id = auth.getCurrentUser().getUid();
        blog_post_id = getIntent().getStringExtra("blog_post_id");


        comment_list_RV.setHasFixedSize(true);
        comment_list_RV.setLayoutManager(new LinearLayoutManager(this));
        comment_list_RV.setAdapter(commentsRecyclerAdapter);

//        if (auth.getCurrentUser() != null) {

            db.collection("BlogPost/" + blog_post_id + "/Comments").orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (documentSnapshots != null) {
                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        String commentsId = doc.getDocument().getId();
                                        Comments comments = doc.getDocument().toObject(Comments.class).withId(commentsId);;
                                        commentsList.add(comments);
                                        commentsRecyclerAdapter.notifyDataSetChanged();


                                    }

                                }
                            }
                        }
                    });

            db.collection("users").document(auth.getCurrentUser().getUid()).get()
                    .addOnCompleteListener(CommentsActivity.this, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                System.out.println(task.getResult().getString("image"));
                                Glide.with(CommentsActivity.this)
                                        .load(task.getResult().getString("image"))
                                        .into(comment_current_user);

                            } else {

                                Toast.makeText(CommentsActivity.this, "ERROR" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
            comment_post_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String comment_text = comment_field.getText().toString();
                    if (!TextUtils.isEmpty(comment_text)) {
                        Map<String, Object> commentMap = new HashMap<>();
                        commentMap.put("message", comment_text);
                        commentMap.put("user_id", current_user_id);
                        commentMap.put("timestamp", FieldValue.serverTimestamp());
                        commentMap.put("blog_post_id", blog_post_id);


                        db.collection("BlogPost/" + blog_post_id + "/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    comment_field.setText("");
                                    Toast.makeText(CommentsActivity.this, "已留言", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CommentsActivity.this, "留言出現問題 :", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CommentsActivity.this, "留言出現問題 :", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(CommentsActivity.this, "請留言", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }








}

