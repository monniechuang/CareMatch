package com.example.user.carematch;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reading3TypeAdapter extends RecyclerView.Adapter<Reading3TypeAdapter.ViewHolder>{


    private static final String TAG = "TEST";
    public List<Post> TypePageList;
    public Context context;
    private FirebaseAuth firebaseAuth;
    private String androidId;
    private FirebaseFirestore db;
    Button like_button;
    int count;
    private String post_id;


    public Reading3TypeAdapter(Context applicationContext, List<Post> TypePageList){

        this.TypePageList=TypePageList;
        this.context = context;

    }

    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_post, parent, false);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String currentUserID = firebaseAuth.getCurrentUser().getUid();
        final String post_id =TypePageList.get(position).postId;//抓ID ,List可以替換


        holder.postTitle.setText(TypePageList.get(position).getPost_title());
        holder.postDesc.setText(TypePageList.get(position).getPost_desc());
        holder.postDate.setText(TypePageList.get(position).getPost_date());
        String Image=TypePageList.get(position).getPost_image();


        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference picReference = storageReference.child("Post/"+Image);

        Glide.with(holder.postImage.getContext())
                .using(new FirebaseImageLoader())
                .load(picReference)
                .into(holder.postImage);


        //以下為cardview按鈕監聽
        holder.mView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Context context=view.getContext();

                Intent intent = new Intent();
                intent.setClass(context, PostPageActivity.class);
                intent.putExtra("PostId", post_id);
                Log.d(TAG,"Id: "+post_id);
                context.startActivity(intent);


            }
        });

        //收藏
        if (firebaseAuth.getCurrentUser() != null) {
            db.collection("users/" + currentUserID + "/favorites").document(post_id)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                            if (documentSnapshot.exists() && e == null) {

                                holder.fav_button.setImageResource(R.drawable.cm_logo);
                            } else {
                                holder.fav_button.setImageResource(R.drawable.empty_heart);
                            }
                        }
                    });
        }
        if (firebaseAuth.getCurrentUser() != null) {
            holder.fav_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("users/" + currentUserID + "/favorites").document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().exists()) {
                                    Map<String,String> favorite = new HashMap<>();
                                    favorite.put("post_id",post_id);
                                    db.collection("users/" + currentUserID + "/favorites").document(post_id).set(favorite);

                                } else {
                                    db.collection("users/" + currentUserID + "/favorites").document(post_id).delete();


                                }
                            } else {
                                Toast.makeText(context, "ERROR" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }


        //按愛心
        if (firebaseAuth.getCurrentUser() != null) {
            db.collection("Post/" + post_id + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (documentSnapshots != null) {

                        if (!documentSnapshots.isEmpty()) {
                            count = documentSnapshots.size();
                            holder.updateLikeCounts(count);

                            DocumentReference locate  =db.collection("Post").document(post_id);
                            locate.update("Post_thumbs",count);
                        } else {
                            holder.updateLikeCounts(0);

                            DocumentReference locate  =db.collection("Post").document(post_id);
                            locate.update("Post_thumbs",count);

                        }
                    }
                    Log.d(TAG, String.valueOf(count));
                }
            });


        }




        if (firebaseAuth.getCurrentUser() != null) {
            db.collection("Post/" + post_id + "/Likes").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) {
                            holder.like_button.setImageResource(R.drawable.good);
                        } else {
                            holder.like_button.setImageResource(R.drawable.notgood);
                        }
                    }
                }
            });
        }

        holder.like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Post/" + post_id + "/Likes").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().exists()) {
                                Map<String, Object> likesMap = new HashMap<>();
                                likesMap.put("timestamp", FieldValue.serverTimestamp());
                                db.collection("Post/" + post_id + "/Likes").document(currentUserID).set(likesMap);
                            } else {
                                db.collection("Post/" + post_id + "/Likes").document(currentUserID).delete();

                            }
                        } else {
                            Toast.makeText(context, "錯誤" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

//        //以下為cardview按鈕監聽
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Context context = view.getContext();
//
//                FragmentManager fragmentManager;
//                final FragmentTransaction fragmentTransaction;
//
//                fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
//                fragmentTransaction = fragmentManager.beginTransaction();
//
//                ((Activity_Main)context).setNewRestId(rest_id);
//                Log.d(TAG,"Id: "+rest_id);
//                fragmentTransaction.replace(R.id.diarylayout, new Fragment_Diary_RestPage())
//                        .addToBackStack(null)
//                        .commit();
//                ((Activity_Main)context).onPageSelected(2);
//
//                /*
//                Intent intent = new Intent();
//                intent.setClass(context, RestPage.class);
//                intent.putExtra("RestId", rest_id);
//                Log.d(TAG, "Id: " + rest_id);
//                context.startActivity(intent);
//                */
//
//
//            }
//        });
    }
    @Override
    public int getItemCount() {
        return TypePageList.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public TextView postTitle;
        public TextView postDesc;
        public ImageView postImage;
        public TextView postDate;
        public ImageView like_button;
        TextView blogLikeCount;
        public ImageView fav_button;





        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            postTitle =(TextView)mView.findViewById(R.id.post_title);
            postDesc =(TextView)mView.findViewById(R.id.post_desc);
            postImage=(ImageView)mView.findViewById(R.id.post_image);
            postDate = (TextView) mView.findViewById(R.id.post_date);
            like_button = (ImageView) mView.findViewById(R.id.like_button);
            blogLikeCount = (TextView) mView.findViewById(R.id.post_like_count);
            fav_button = (ImageView) mView.findViewById(R.id.fav_button);

        }
        public void updateLikeCounts(int count) {
            blogLikeCount = mView.findViewById(R.id.post_like_count);
            if (count == 0) {
                blogLikeCount.setText(" " + count + "讚");
            } else if (count == 1) {
                blogLikeCount.setText(" " + count + "讚");
            } else {
                blogLikeCount.setText(" " + count + "讚");

            }
        }
    }
}