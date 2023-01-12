package com.example.user.carematch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.user.carematch.model.BlogPost;
import com.example.user.carematch.newPost.BlogRecyclerAdapter;
import com.example.user.carematch.newPost.PostActivity;
import com.facebook.share.internal.LikeButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class BlogFragment1 extends Fragment {

    private RecyclerView blog_list_Rv;
    private List<BlogPost> blog_list;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private BlogRecyclerAdapter blogRecyclerAdapter;


    DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;

    FloatingActionButton fab;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blog, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        blog_list = new ArrayList<>();
        blog_list_Rv = view.findViewById(R.id.blog_list_view);
        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list, view.getContext());

        blog_list_Rv.setHasFixedSize(true);
        blog_list_Rv.setLayoutManager(new LinearLayoutManager(view.getContext()));

        blog_list_Rv.setAdapter(blogRecyclerAdapter);


        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BlogFragment1.this.getActivity(), PostActivity.class));

            }
        });
        if (firebaseAuth.getCurrentUser() != null) {

            db = FirebaseFirestore.getInstance();
            blog_list_Rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                    if (reachedBottom) {

                        loadMorePosts();

                    }
                }
            });
            Query firstQuery =
                    db.collection("BlogPost")
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .limit(5);
            firstQuery
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (!documentSnapshots.isEmpty()) {
                                if (isFirstPageFirstLoad) {
                                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                                    blog_list.clear();
                                }
                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        String blogPostID = doc.getDocument().getId();
                                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostID);
                                        if (isFirstPageFirstLoad) {
                                            blog_list.add(blogPost);
                                        } else {
                                            blog_list.add(0, blogPost);
                                        }
                                        blogRecyclerAdapter.notifyDataSetChanged();


                                    }
                                }

                                isFirstPageFirstLoad = false;
                            }
                        }
                    });
        }
        return view;
    }

    private void loadMorePosts() {
        if (firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = db
                    .collection("BlogPost")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    ;
            nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (!documentSnapshots.isEmpty()) {
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
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


}
