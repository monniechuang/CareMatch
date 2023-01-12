package com.example.user.carematch;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;


public class ReservationListAdapter extends RecyclerView.Adapter<ReservationListAdapter.ViewHolder>{
private static final String TAG = "TEST";
public FirebaseAuth firebaseAuth;
public FirebaseFirestore db;
public List<Reservation>ReservationList;
public Context context;
public String com_date;
public String date;
private FirebaseAuth auth;
private String androidId;

public ReservationListAdapter(Context applicationContext, List<Reservation> ReservationList){

        this.ReservationList=ReservationList;
        this.context =context;
        }
@Override

public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reservation, parent, false);
        auth = FirebaseAuth.getInstance();
        androidId = auth.getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
        return new ViewHolder(view);
        }
@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.reservationDate.setText(ReservationList.get(position).getReservation_date());
        holder.reservationTime.setText(ReservationList.get(position).getReservation_time());
        holder.userName.setText(ReservationList.get(position).getUser_name());
        holder.reservationPlace.setText(ReservationList.get(position).getReservation_place());
        holder.reservationPlace2.setText(ReservationList.get(position).getReservation_place2());

    com_date=ReservationList.get(position).getReservation_date();


        final String reservationId =ReservationList.get(position).reservationId;//抓ID ,List可以替換


//    holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//
//            db.collection("Reservation").document(reservationId)
//                    .delete()
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//
//                            db.collection("Reservation").whereEqualTo("User_id",androidId).orderBy("Reservation_date").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                                    if (e != null) {
//
//                                        Log.d(TAG, "Error :" + e.getMessage());
//                                    } else {
//                                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
//                                            ReservationList.clear();
//
//                                            if (doc.getType() == DocumentChange.Type.ADDED) {
//
//                                                String reservation_id = doc.getDocument().getId();
//
//                                                Reservation reservation = doc.getDocument().toObject(Reservation.class).withId(reservation_id);//抓ID
//                                                ReservationList.add(reservation);
//                                                notifyDataSetChanged();
//
//                                            }
//
//                                        }
//
//
//                                    }
//                                }
//                            });
//                        }
//                    });
//
//        }
//    });



    holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder mBuider = new AlertDialog.Builder(view.getRootView().getContext());
            mBuider.setTitle("確認刪除交通訂單？");
            mBuider.setMessage("刪除後即無法保證能在預約此時段之復康巴士。");

            mBuider.setPositiveButton("是", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {


                    db.collection("Reservation").document(reservationId)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    db.collection("Reservation").whereEqualTo("User_id",androidId).orderBy("Reservation_date").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                            if (e != null) {

                                                Log.d(TAG, "Error :" + e.getMessage());
                                            } else {
                                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                    ReservationList.clear();

                                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                                        String reservation_id = doc.getDocument().getId();

                                                        Reservation reservation = doc.getDocument().toObject(Reservation.class).withId(reservation_id);//抓ID
                                                        ReservationList.add(reservation);
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
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        date = String.valueOf(year)  +"/"+ String.valueOf(month + 1) +"/"+ String.valueOf(day);
//        if (date.equals(com_date)) {
//
//            holder.statusButton.setImageResource(R.drawable.cm_logo);
//        } else {
//        }


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
        return ReservationList.size();
        }

public class  ViewHolder extends RecyclerView.ViewHolder{
    View mView;
    public TextView userName;
    public TextView reservationDate;
    public TextView reservationPlace;
    public TextView reservationPlace2;
    public TextView reservationTime;
    public ImageView deleteBtn;



    public ViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        userName =(TextView)mView.findViewById(R.id.user_name);
        reservationDate =(TextView)mView.findViewById(R.id.reservation_date);
        reservationTime = (TextView) mView.findViewById(R.id.reservation_time);
        deleteBtn=(ImageView)mView.findViewById(R.id.delete_button);
        reservationPlace=(TextView)mView.findViewById(R.id.reservation_place);
        reservationPlace2=(TextView)mView.findViewById(R.id.reservation_place2);
    }

}
}