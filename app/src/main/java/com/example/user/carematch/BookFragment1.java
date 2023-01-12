package com.example.user.carematch;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class BookFragment1 extends Fragment {

    private View BookFragmentview;
    private static final String TAG = "FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;



    private CareListAdapter CareListAdapter;
    private List<Care> CareList;
    private FirebaseAuth auth;
    private String androidId;
    ActionBar actionBar;
    private Button addButton;
    private String humanId;
    public List<Human> HumanList;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;





    //最新文章
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        BookFragmentview = inflater.inflate(R.layout.fragment_book1, container, false);


        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        androidId = auth.getCurrentUser().getUid();


        CareList = new ArrayList<>();
        CareListAdapter = new CareListAdapter(CareList, getApplicationContext());
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) BookFragmentview.findViewById(R.id.care_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mMainList.setAdapter(CareListAdapter);
        addButton=(Button) BookFragmentview.findViewById(R.id.book_care);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Context context=view.getContext();
                Intent intent = new Intent();
                intent.setClass(context, PastHumanBook.class);
//                intent.putExtra("HumanId", humanId);
//                Log.d(TAG,"Id: "+humanId);
                context.startActivity(intent);


            }
        });

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);


        db.collection("users/" + androidId + "/Care")
                .whereEqualTo("User_id",androidId)
                .whereGreaterThanOrEqualTo("Care_date", date)
                .orderBy("Care_date").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            CareListAdapter.notifyDataSetChanged();

                        }

                    }


                }
            }
        });

        return BookFragmentview;
    }
}
