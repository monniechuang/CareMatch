package com.example.user.carematch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class ReviewListAdapter  extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder>{
    private static final String TAG = "TEST";
    public FirebaseAuth firebaseAuth;
    public FirebaseFirestore db;
    public List<Review> ReviewList;
    public Context context;
    public ReviewListAdapter(Context applicationContext, List<Review> ReviewList){

        this.ReviewList=ReviewList;
        this.context =context;
    }
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_review, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.review.setText(ReviewList.get(position).getHuman_review());
//        holder.reviewDate.setText(ReviewList.get(position).getReview_Date());
        holder.userName.setText(ReviewList.get(position).getUser_name());

        final String reviewId =ReviewList.get(position).reviewId;//抓ID ,List可以替換
//
//        //以下為cardview按鈕監聽
//        holder.mView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                Context context=view.getContext();
//
//                Intent intent = new Intent();
//                intent.setClass(context, PostPage.class);
//                intent.putExtra("PostId", post_id);
//                Log.d(TAG,"Id: "+post_id);
//                context.startActivity(intent);
//
//
//            }
//        });
//        if (firebaseAuth != null) {
//            db.collection("users").document(reviewId).get()
//                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.isSuccessful()) {
//                                holder.setUserDescription(task.getResult().getString("name"), task.getResult().getString("image"));
//                            } else {
//                                Toast.makeText(context, "ERROR" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }
    }
    @Override
    public int getItemCount() {
        return ReviewList.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public TextView userName;
        public TextView reviewDate;
        public TextView review;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            userName =(TextView)mView.findViewById(R.id.user_name);
//            reviewDate =(TextView)mView.findViewById(R.id.review_date);
            review = (TextView) mView.findViewById(R.id.review);
        }

    }
}
