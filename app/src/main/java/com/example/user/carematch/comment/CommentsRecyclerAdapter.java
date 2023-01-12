package com.example.user.carematch.comment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.carematch.BlogPostList;
import com.example.user.carematch.Fragment_Transportation;
import com.example.user.carematch.Human;
import com.example.user.carematch.ProductInformationActivity;
import com.example.user.carematch.R;
//import com.example.carematch.myblogapp.model.Comments;
import com.example.user.carematch.model.BlogPost;
import com.example.user.carematch.model.Comments;
import com.bumptech.glide.Glide;
import com.example.user.carematch.model.BlogPost;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.GraphRequest.TAG;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {
    public List<Comments> CommentsList;
    public Context context;
    private String blog_post_id,currentUserID;
    public FirebaseAuth firebaseAuth;
    public FirebaseFirestore db;
    private PreferenceScreen onItemClickListener;
    public List<BlogPost> blogPostList;

    public CommentsRecyclerAdapter(List<Comments> CommentsList, Context context) {
        this.CommentsList = CommentsList;
        this.context = context;
    }

//    public CommentsRecyclerAdapter(List<Comments> commentsList) {
//        this.commentsList = commentsList;
//    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final String user_id = CommentsList.get(position).getUser_id();


        if (firebaseAuth != null) {
            db.collection("users").document(user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                holder.setUserDescription(task.getResult().getString("name"), task.getResult().getString("image"));
                            } else {
                                Toast.makeText(context, "ERROR" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        final String blog_post_id = CommentsList.get(position).getBlog_post_id();

        String commentMessage = CommentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);
        final String commentsId = CommentsList.get(position).commentsId;
        holder.mView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view){
                if(currentUserID.equals(user_id)){

                    android.support.v7.app.AlertDialog.Builder mBuider = new android.support.v7.app.AlertDialog.Builder(context);
                    mBuider.setMessage("確認刪除你在此篇貼文留言?");

                    mBuider.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        private DocumentSnapshot snapshot;

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.collection("BlogPost/" + blog_post_id + "/Comments").document(commentsId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            CommentsList.clear();
                                            db.collection("BlogPost/" + blog_post_id + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                    if (e != null) {

                                                        Log.d(TAG, "Error :" + e.getMessage());
                                                    } else {
                                                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {


                                                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                                                String comments_id = doc.getDocument().getId();

                                                                Comments comments = doc.getDocument().toObject(Comments.class).withId(comments_id);//抓ID
                                                                CommentsList.add(comments);
                                                                notifyDataSetChanged();

                                                            }

                                                        }


                                                    }
                                                }
                                            });
                                        }
                                    });




                        }
                    }).show();
                }else{
                    Toast.makeText(context, "不是你的留言無法刪除喔", Toast.LENGTH_SHORT).show();


                }


                return true;
            }
        });



        db.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    holder.setUserDescription(task.getResult().getString("name"), task.getResult().getString("image"));

                    String Image=task.getResult().getString("image");
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference picReference = storageReference.child("users_photos/"+Image);
//
                    Glide.with(holder.userImage.getContext())
                            .using(new FirebaseImageLoader())
                            .load(picReference)
                            .into(holder.userImage);
                } else {
                }
            }

        });
    }

    @Override
    public int getItemCount() {

            return CommentsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public TextView name;
        public CircleImageView userImage;
        public TextView comment_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setComment_message(String message) {

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);

        }

        public void setUserDescription(String nameText, String userImageComment) {

            name = mView.findViewById(R.id.comment_username);
            userImage = mView.findViewById(R.id.comment_image);
            name.setText(nameText);

//            Glide.with(context)
//                    .load(userImageComment)
//                    .into(userImage);

        }
    }
}
