package com.example.user.carematch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class BookFragment2 extends Fragment {
    private View BookFragmentview;
    private static final String TAG = "FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;


    private FirebaseAuth auth;
    private String androidId;
    ActionBar actionBar;
    private String humanId;
    public List<Human> HumanList;

    private ReservationListAdapter ReservationListAdapter;
    private List<Reservation> ReservationList;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;





    //最新文章
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        BookFragmentview = inflater.inflate(R.layout.fragment_book2, container, false);


        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        androidId = auth.getCurrentUser().getUid();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        androidId = auth.getCurrentUser().getUid();
        ReservationList = new ArrayList<>();
        ReservationListAdapter = new ReservationListAdapter(getApplicationContext(),ReservationList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) BookFragmentview.findViewById(R.id.reservation_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mMainList.setAdapter(ReservationListAdapter);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
        String date1 = String.valueOf(year)  + String.valueOf(month + 1) +String.valueOf(day);

        db.collection("Reservation")
                .whereEqualTo("User_id",androidId)
                .whereGreaterThanOrEqualTo("Reservation_date", date)
                .orderBy("Reservation_date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d(TAG, "Error :" + e.getMessage());
                } else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String reservation_id = doc.getDocument().getId();

                            Reservation reservation = doc.getDocument().toObject(Reservation.class).withId(reservation_id);//抓ID
                            ReservationList.add(reservation);
                            ReservationListAdapter.notifyDataSetChanged();

                        }

                    }


                }
            }
        });


        return BookFragmentview;
    }
}
