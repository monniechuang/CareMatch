package com.example.user.carematch;

import android.app.Activity;
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
import com.example.user.carematch.Utils;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.like.LikeButton;
//import com.like.OnLikeListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static android.widget.Toast.LENGTH_SHORT;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.ViewHolder> {
    private static final String TAG = "TEST";
    public List<Products> ProductsList;
    public Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    Button fav_button;
    private String androidId, kW3pzilDzJbi23vbnwMQ;
    double x,y,x1,y1;
    public double lat,lon;
    private String dist1;

    public ProductsListAdapter(Context applicationContext, List<Products> ProductsList){

        this.ProductsList=ProductsList;
        this.context = context;
    }
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_products, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String currentUserID = firebaseAuth.getCurrentUser().getUid();
        holder.productsName.setText(ProductsList.get(position).getProducts_name());
        holder.productsStorename.setText(ProductsList.get(position).getProducts_storename());
        holder.products_time.setText(ProductsList.get(position).getProducts_time());
        holder.lon.setText(ProductsList.get(position).getLongitude());
        holder.lat.setText(ProductsList.get(position).getLatitude());
        x=Double.parseDouble(ProductsList.get(position).getLatitude());
        y=Double.parseDouble(ProductsList.get(position).getLongitude());

        Task<DocumentSnapshot> documentSnapshotTask = db.collection("users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        lat=Double.parseDouble(document.get("Latitude").toString());
                        lon=Double.parseDouble(document.get("Longitude").toString());


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        if ((x == lat) && (y == lon)) {
            dist1= String.valueOf(0);
        }
        else {
            double theta = y - lon;
            double dist = Math.sin(Math.toRadians(x)) * Math.sin(Math.toRadians(lat)) + Math.cos(Math.toRadians(x)) * Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = ((dist * 60 * 1.1515)* 1.609344)/1000;
            DecimalFormat df=new DecimalFormat("#.##");
            dist1=df.format(dist);
            Log.d(TAG,dist1);
        }

        holder.km.setText(dist1);


//        holder.productsDetails.setText(ProductsList.get(position).getProducts_details());
        String Image=ProductsList.get(position).getProducts_image();
        Log.d(TAG,""+ProductsList.get(position).getProducts_name());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference picReference = storageReference.child("Products/"+Image);

        Glide.with(holder.productsImage.getContext())
                .using(new FirebaseImageLoader())
                .load(picReference)
                .into(holder.productsImage);

        final String products_id =ProductsList.get(position).productsId;//抓ID ,List可以替換

        //以下為cardview按鈕監聽
        holder.mView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Context context=view.getContext();

                Intent intent = new Intent();
                intent.setClass(context, ProductInformationActivity.class);
                intent.putExtra("ProductsId", products_id);
                Log.d(TAG,"Id: "+products_id);
                context.startActivity(intent);


            }
        });
        if (firebaseAuth.getCurrentUser() != null) {
            db.collection("users/" + currentUserID + "/favorites").document(products_id)
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
                    db.collection("users/" + currentUserID + "/favorites").document(products_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().exists()) {
                                    Map<String,String> favorite = new HashMap<>();
                                    favorite.put("product_id",products_id);
                                    db.collection("users/" + currentUserID + "/favorites").document(products_id).set(favorite);




                                } else {
                                    db.collection("users/" + currentUserID + "/favorites").document(products_id).delete();


                                }
                            } else {
                                Toast.makeText(context, "ERROR" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }



    @Override
    public int getItemCount() {
        return ProductsList.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public TextView productsName;
        public TextView productsStorename;
        public TextView products_time;
        public ImageView productsImage;
        public ImageView fav_button;
        //        Button fav_button;
        public TextView productsDetails;
        public TextView lon;
        public TextView lat;
        public TextView km;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            productsName =(TextView)mView.findViewById(R.id.products_name);
            productsStorename=(TextView)mView.findViewById(R.id.products_storename);
            productsImage=(ImageView)mView.findViewById(R.id.products_image);
            products_time = (TextView)mView.findViewById(R.id.products_time);
            fav_button = (ImageView) mView.findViewById(R.id.fav_button);
            lon =(TextView)mView.findViewById(R.id.lon);
            lat=(TextView)mView.findViewById(R.id.lat);
            km=(TextView)mView.findViewById(R.id.km);
        }
    }
}

