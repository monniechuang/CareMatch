package com.example.user.carematch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import static com.facebook.GraphRequest.TAG;


public class CareListAdapter extends RecyclerView.Adapter<CareListAdapter.ViewHolder>{
    private static final String TAG = "TEST";
    public FirebaseAuth firebaseAuth;
    public FirebaseFirestore db;
    public List<Care>CareList;
    public Context context;
    public String com_date;
    public String date;
    private FirebaseAuth auth;
    private String userId,humanId;

    public List<Human> HumanList;
    Button btnLoadMore;
    Button reviewBtn;


    public CareListAdapter(List <Care> CareList,Context context){

        this.CareList=CareList;
        this.context =context;

    }
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_care1, parent, false);
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
        return new ViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.care_name.setText(CareList.get(position).getCare_name());
        holder.care_age.setText(CareList.get(position).getCare_age());
        holder.care_sex.setText(CareList.get(position).getCare_sex());
        holder.care_exp.setText(CareList.get(position).getCare_exp());
        holder.care_date.setText(CareList.get(position).getCare_date());
        holder.care_time.setText(CareList.get(position).getCare_time());




        final String careId =CareList.get(position).careId;//抓ID ,List可以替換



        db.collection("users/" + userId + "/Care")
                .document(careId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                humanId = document.get("Human_id").toString();

                                Log.d(TAG, humanId);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });




        String Image=CareList.get(position).getCare_image();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference picReference = storageReference.child("Human/"+Image);

        Glide.with(holder.care_image.getContext())
                .using(new FirebaseImageLoader())
                .load(picReference)
                .into(holder.care_image);


        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuider = new AlertDialog.Builder(view.getRootView().getContext());
                mBuider.setTitle("確認刪除？");
                mBuider.setMessage("刪除後即無法保證能在預約此時段預約此看護。");

                mBuider.setPositiveButton("是", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                            db.collection("users/" + userId + "/Care")
                                    .document(careId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            CareList.clear();
                                            DocumentReference okay = db.collection("Human").document(humanId);
                                            okay.update("H_ava", "可預約");
                                            db.collection("users/" + userId + "/Care")
                                                    .whereEqualTo("User_id", userId)
                                                    .orderBy("Care_date", Query.Direction.DESCENDING)
                                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                            if (e != null) {

                                                                Log.d(TAG, "Error :" + e.getMessage());
                                                            } else {
                                                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                                                        String care_id = doc.getDocument().getId();

                                                                        Care care = doc.getDocument().toObject(Care.class).withId(care_id);//抓ID
                                                                        CareList.add(care);
                                                                        notifyDataSetChanged();

                                                                    }

                                                                }


                                                            }
                                                        }
                                                    });
                                        }
                                    });
                    }

                }).setNegativeButton("取消",new DialogInterface.OnClickListener(){


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                mBuider.show();







            }
        });

    }
    @Override
    public int getItemCount() {
        return CareList.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        //    public TextView userName;
        public TextView care_name;
        public TextView care_age;
        public TextView care_sex;
        public TextView care_exp;
        public TextView care_date;
        public TextView care_time;
        public ImageView care_image;
//        public Button reviewBtn;

        public ImageView deleteBtn;




        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            care_name =(TextView)mView.findViewById(R.id.care_name);
            care_age = (TextView) mView.findViewById(R.id.care_age);
            care_sex=(TextView)mView.findViewById(R.id.care_sex);
            care_exp=(TextView)mView.findViewById(R.id.care_exp);
            care_date=(TextView)mView.findViewById(R.id.care_date);
            care_time=(TextView)mView.findViewById(R.id.care_time);
            care_image=(ImageView)mView.findViewById(R.id.care_image);
//            reviewBtn=(Button) mView.findViewById(R.id.review);


            deleteBtn=(ImageView)mView.findViewById(R.id.delete_button);

        }

    }
}