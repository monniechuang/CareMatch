package com.example.user.carematch.newPost;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.example.user.carematch.request.RequestOptions;
import com.example.user.carematch.BlogPostList;
import com.example.user.carematch.Fragment_Booking;
import com.example.user.carematch.MainActivity;
import com.example.user.carematch.ProductInformationActivity;
import com.example.user.carematch.R;
import com.example.user.carematch.Reservation;
import com.example.user.carematch.User;
import com.example.user.carematch.comment.CommentsActivity;
import com.example.user.carematch.model.BlogPost;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;


//import com.koushikdutta.ion.Ion;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stripe.net.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TimeZone;

import static com.facebook.GraphRequest.TAG;
import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.myViewHolder> {
    List<BlogPost> blogPosts;
    Context context;
    private @ServerTimestamp
    Date timestamp;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public BlogRecyclerAdapter(List<BlogPost> blogPosts, Context context) {
        this.blogPosts = blogPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_row, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String blog_post_id = blogPosts.get(position).blogPostId;

        final String currentUserID = firebaseAuth.getCurrentUser().getUid();

        String desc_data = blogPosts.get(position).getDesc();
        String blog_image_thumb = blogPosts.get(position).getImage_thumb();
        String blog_image = blogPosts.get(position).getImage_url();
        timestamp = blogPosts.get(position).getTimestamp();
        final String user_id = blogPosts.get(position).getUser_id();
//        if (firebaseAuth.getCurrentUser() != null) {
//            firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
//                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                        @Override
//                        public void onEvent(DocumentSnapshot documentSnapshot, final FirebaseFirestoreException e) {
//                            if(documentSnapshot.exists()){
//                                final User user = documentSnapshot.toObject(User.class);
//                                String Image=user.getImage();
//
//                                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//                                StorageReference picReference = storageReference.child("users_photos/"+Image);
//
//                                Glide.with(holder.userImage.getContext())
//                                        .using(new FirebaseImageLoader())
//                                        .load(picReference)
//                                        .into(holder.userImage);
//
//
//
//                            }else {
//
//                            }
//                        }
//                    });
//        }


        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    holder.setUserDescription(task.getResult().getString("name"), task.getResult().getString("image"));

                    String Image=task.getResult().getString("image");
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference picReference = storageReference.child("users_photos/"+Image);


                    Glide.with(holder.userImage.getContext())
                            .using(new FirebaseImageLoader())
                            .load(picReference)
                            .into(holder.userImage);
                } else {
                    Toast.makeText(context, "上傳圖片失敗" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });


        if (timestamp != null) {
            holder.setTimeStamp();
        }
        holder.setBlogImage(blog_image_thumb, blog_image);
        holder.setDescText(desc_data);

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore.collection("BlogPost/" + blog_post_id + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (documentSnapshots != null) {
                        if (!documentSnapshots.isEmpty()) {
                            int count = documentSnapshots.size();
                            holder.updateLikeCounts(count);
                        } else {
                            holder.updateLikeCounts(0);
                        }
                    }
                }
            });

            firebaseFirestore.collection("BlogPost/" + blog_post_id + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (documentSnapshots != null) {
                        if (!documentSnapshots.isEmpty()) {
                            int count = documentSnapshots.size();
                            holder.updateCommentCounts(count);
                        } else {
                            holder.updateCommentCounts(0);
                        }
                    }
                }
            });
        }
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore.collection("BlogPost/" + blog_post_id + "/Likes").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) {
                            holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.cm_logo));
                        } else {
                            holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like__gray));
                        }
                    }
                }
            });
        }
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUserID.equals(user_id)){
                    final String[] more= {"刪除","顯示使用者所有文章"};

                    AlertDialog.Builder dialog_list = new AlertDialog.Builder(context);
                    dialog_list.setItems(more, new DialogInterface.OnClickListener(){
                        @Override

                        //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            if(more[which]=="刪除")
                                firebaseFirestore.collection("BlogPost").document(blog_post_id)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                blogPosts.clear();
                                                firebaseFirestore.collection("BlogPost").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                        if (e != null) {

                                                            Log.d(TAG, "Error :" + e.getMessage());
                                                        } else {
                                                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {


                                                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                                                    String blogpost_id = doc.getDocument().getId();

                                                                    BlogPost blogpost = doc.getDocument().toObject(BlogPost.class).withId(blogpost_id);//抓ID
                                                                    blogPosts.add(blogpost);
                                                                    notifyDataSetChanged();

                                                                }

                                                            }


                                                        }
                                                    }
                                                });
                                            }
                                        });

                            else{
                                Intent intent = new Intent();
                                intent.setClass(context, BlogPostList.class);
                                intent.putExtra("user", user_id);
                                Log.d(TAG,"Id: "+user_id);
                                context.startActivity(intent);
                            }
                        }
                    });
                    dialog_list.show();
                }else{
                    final String[] more= {"顯示該用戶所有文章"};
                    AlertDialog.Builder dialog_list = new AlertDialog.Builder(context);
                    dialog_list.setItems(more, new DialogInterface.OnClickListener(){
                        @Override

                        //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            if(more[which]=="顯示使用者所有文章"){
                                Intent intent = new Intent();
                                intent.setClass(context, BlogPostList.class);
                                intent.putExtra("user", user_id);
                                Log.d(TAG,"Id: "+user_id);
                                context.startActivity(intent);
                            }
                        }
                    });
                    dialog_list.show();

                }

            }
        });
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("BlogPost/" + blog_post_id + "/Likes").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().exists()) {
                                Map<String, Object> likesMap = new HashMap<>();
                                likesMap.put("timestamp", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("BlogPost/" + blog_post_id + "/Likes").document(currentUserID).set(likesMap);
                            } else {
                                firebaseFirestore.collection("BlogPost/" + blog_post_id + "/Likes").document(currentUserID).delete();

                            }
                        } else {
                            Toast.makeText(context, "ERROR" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blog_post_id", blog_post_id);
                context.startActivity(commentIntent);
            }
        });
        holder.blogCommentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blog_post_id", blog_post_id);
                context.startActivity(commentIntent);
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "已顯示資訊", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogPosts.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        View mView;

        TextView name;
        TextView descView;
        ImageView userImage;
        ImageView blog_post_image;
        TextView timeStamp_object;
        ImageView blogLikeBtn;
        TextView blogLikeCount;
        ImageView blogCommentBtn;
        TextView blogCommentCount;
        ImageButton more;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            blogCommentCount = mView.findViewById(R.id.blog_comment_count);
            blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);
            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            more=mView.findViewById(R.id.more);
        }

        public void setDescText(String desc) {
            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(desc);
        }

        public void setUserDescription(String userName, String userImageString) {
            name = mView.findViewById(R.id.blog_user_name);
            userImage = mView.findViewById(R.id.blog_user_image);
            name.setText(userName);

//            Glide.with(context).load(userImageString).into(userImage);

        }

        public void setBlogImage(String blog_image_thumb, String blog_image) {
            blog_post_image = mView.findViewById(R.id.blog_image);
            scaleImage(blog_post_image);


            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference picReference = storageReference.child("post_images/" +blog_image);

            Glide.with(getApplicationContext())
                    .using(new FirebaseImageLoader())
                    .load(picReference)
                    .into(blog_post_image);

//            //先不用
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions.placeholder(R.drawable.image_placeholder);
//            Glide.with(context)
//                    .applyDefaultRequestOptions(requestOptions)
//                    .load(blog_image)
//                    .thumbnail(Glide.with(context).load(blog_image_thumb))
//                    .into(blog_post_image);
        }

        public void setTimeStamp() {

            timeStamp_object = mView.findViewById(R.id.blog_date);
            SimpleDateFormat date = new SimpleDateFormat("d LLLL yyyy", Locale.getDefault());
            date.setTimeZone(TimeZone.getDefault());

            if (timestamp != null) {

                String date_time = date.format(timestamp.getTime());
                timeStamp_object.setText(date_time + " (" + getTimeAgo(timestamp, context) + ") ");

            }

        }

        public void updateLikeCounts(int count) {
            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            if (count == 0) {
                blogLikeCount.setText(" " + count + "愛心");
            } else if (count == 1) {
                blogLikeCount.setText(" " + count + "愛心");
            } else {
                blogLikeCount.setText(" " + count + "愛心");

            }
        }

        public void updateCommentCounts(int count) {
            if (count == 0) {
                blogCommentCount.setText(" " + count + "留言");
            } else if (count == 1) {
                blogCommentCount.setText(" " + count + "留言");

            } else {
                blogCommentCount.setText(" " + count + "留言");
            }
        }


    }

    private void scaleImage(ImageView view) throws NoSuchElementException {
        // Get bitmap from the the ImageView.
        Bitmap bitmap = null;

        try {
            Drawable drawing = view.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No drawable on given view");
        } catch (ClassCastException e) {
            // Check bitmap is Ion drawable
//            bitmap = Ion.with(view).getBitmap();
        }

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(350);
        /*Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));*/

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
       /* Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));*/

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
       /* Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));*/

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        /*Log.i("Test", "done");*/
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getTimeAgo(Date date, Context ctx) {

        if (date == null) {
            return null;
        }

        long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        int dim = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (dim == 0) {
            timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_an) + " " + ctx.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 60)) + " " + ctx.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " + ctx.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " + ctx.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);

    }
}
