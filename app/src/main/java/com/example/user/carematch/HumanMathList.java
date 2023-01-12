package com.example.user.carematch;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class HumanMathList extends android.support.v4.app.Fragment {

    private static final String TAG ="FireLog";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private com.example.user.carematch.HumanListAdapter HumanListAdapter;
    private List<Human> HumanList;
    Button ButtonShowDialog;
    Spinner DivisionSp,TimeSp;
    //日期的
    TextView selectDate;
    private String care_date;
    private String flag;
    private String care_time;

    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayofMonth;
    Calendar calendar;


    private FirebaseAuth auth;
    private String androidId;


    private Care care = new Care();

    private View HumanListview;


public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    HumanListview = inflater.inflate(R.layout.fragment_human_math, container, false);

    auth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    androidId = auth.getCurrentUser().getUid();



    ButtonShowDialog = (Button) HumanListview.findViewById(R.id.ButtonShowDialog);
    ButtonShowDialog.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder humanBuilder = new AlertDialog.Builder(HumanMathList.this.getActivity());
            View mView = getLayoutInflater().inflate(R.layout.dialog_human_math,null);
            humanBuilder.setTitle("看護搜尋");

            //選擇日期
            selectDate = mView.findViewById(R.id.bookdate);
            selectDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar = Calendar.getInstance();
                    final int year1 = calendar.get(Calendar.YEAR);
                    final int month1 = calendar.get(Calendar.MONTH);
                    final int day1 = calendar.get(Calendar.DAY_OF_MONTH);
                    //Fragment_Booking
                    datePickerDialog = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                @SuppressLint("WrongConstant")
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int day) {
                                    String date = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
                                    String date1 = String.valueOf(year)  + String.valueOf(month + 1) +String.valueOf(day);

                                    if(year1>year){
                                        flag= String.valueOf(1);
                                        Toast.makeText(getActivity(), "預約年分不得小於今年，請重新選擇", 5000).show();
                                    }else if(year1 == year){
                                        if((month1+1)>(month+1)){
                                            flag= String.valueOf(2);
                                            Toast.makeText(getActivity(), "預約月分不得小於本月，請重新選擇", 5000).show();
                                        }else if((month1+1)==(month+1)){
                                            if(day1>day){
                                                flag= String.valueOf(3);
                                                Toast.makeText(getActivity(), "預約日不得小於本日，請重新選擇", 5000).show();
                                            }else{
                                                flag= String.valueOf(4);
                                            }
                                        }
                                    }
                                    selectDate.setText(date);
                                    care_date=selectDate.getText().toString();


                                }
                            }, year1, month1, day1);
                    datePickerDialog.show();
                }
            });


            DivisionSp = mView.findViewById(R.id.spn_division);
            TimeSp = mView.findViewById(R.id.spn_time);

            selectDate = mView.findViewById(R.id.bookdate);



            String division[]  = {"--請選擇地區--","北部", "中部", "南部", "東部"};
            String time[]  = {"--請選擇時段--","06:00~12:00", "12:00~18:00", "18:00~24:00", "24:00~06:00"};



            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HumanMathList.this.getActivity(), android.R.layout.simple_spinner_item,division);
            DivisionSp.setAdapter(adapter);


            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(HumanMathList.this.getActivity(), android.R.layout.simple_spinner_item,time);
            TimeSp.setAdapter(adapter3);

            humanBuilder.setPositiveButton("搜尋", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    if (!DivisionSp.getSelectedItem().toString().equalsIgnoreCase("--請選擇地區--") ||
                            !TimeSp.getSelectedItem().toString().equalsIgnoreCase("--請選擇時段--")) {


                        if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("北部")
                                & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                ) {



                            HumanList.clear();
                            db.collection("Human")
                                    .whereEqualTo("H_region", "北部")
                                    .whereEqualTo("H_time", "早班")
                                    .whereEqualTo("H_ava", "可預約")
                                    .orderBy("H_score", Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.d(TAG, "Error :" + e.getMessage());
                                            } else {
                                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                                        String human_id = doc.getDocument().getId();
                                                        Human human = doc.getDocument().toObject(Human.class).withId(human_id);//抓ID
                                                        HumanList.add(human);
                                                        HumanListAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }


                        if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("中部")
                                & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                ) {
                            HumanList.clear();
                            db.collection("Human")
                                    .whereEqualTo("H_region", "中部")
                                    .whereEqualTo("H_time", "早班")
                                    .whereEqualTo("H_ava", "可預約")
                                    .orderBy("H_score", Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.d(TAG, "Error :" + e.getMessage());
                                            } else {
                                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                                        String human_id = doc.getDocument().getId();
                                                        Human human = doc.getDocument().toObject(Human.class).withId(human_id);//抓ID
                                                        HumanList.add(human);
                                                        HumanListAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }

                        if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("南部")
                                & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                ) {
                            HumanList.clear();
                            db.collection("Human")
                                    .whereEqualTo("H_region", "南部")
                                    .whereEqualTo("H_time", "早班")
                                    .whereEqualTo("H_ava", "可預約")
                                    .orderBy("H_score", Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.d(TAG, "Error :" + e.getMessage());
                                            } else {
                                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                                        String human_id = doc.getDocument().getId();
                                                        Human human = doc.getDocument().toObject(Human.class).withId(human_id);//抓ID
                                                        HumanList.add(human);
                                                        HumanListAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }

                        if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("東部")
                                & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("06:00~12:00")
                                || TimeSp.getSelectedItem().toString().equalsIgnoreCase("12:00~18:00"))
                                ) {
                            HumanList.clear();
                            db.collection("Human")
                                    .whereEqualTo("H_region", "東部")
                                    .whereEqualTo("H_time", "早班")
                                    .whereEqualTo("H_ava", "可預約")
                                    .orderBy("H_score", Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.d(TAG, "Error :" + e.getMessage());
                                            } else {
                                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                                        String human_id = doc.getDocument().getId();
                                                        Human human = doc.getDocument().toObject(Human.class).withId(human_id);//抓ID
                                                        HumanList.add(human);
                                                        HumanListAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }



                        //晚班開始
                        if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("北部")
                                & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                ) {
                            HumanList.clear();
                            db.collection("Human")
                                    .whereEqualTo("H_region", "北部")
                                    .whereEqualTo("H_time", "晚班")
                                    .whereEqualTo("H_ava", "可預約")
                                    .orderBy("H_score", Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.d(TAG, "Error :" + e.getMessage());
                                            } else {
                                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                                        String human_id = doc.getDocument().getId();
                                                        Human human = doc.getDocument().toObject(Human.class).withId(human_id);//抓ID
                                                        HumanList.add(human);
                                                        HumanListAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }

                        if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("中部")
                                & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                ) {
                            HumanList.clear();
                            db.collection("Human")
                                    .whereEqualTo("H_region", "中部")
                                    .whereEqualTo("H_time", "晚班")
                                    .whereEqualTo("H_ava", "可預約")
                                    .orderBy("H_score", Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.d(TAG, "Error :" + e.getMessage());
                                            } else {
                                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                                        String human_id = doc.getDocument().getId();
                                                        Human human = doc.getDocument().toObject(Human.class).withId(human_id);//抓ID
                                                        HumanList.add(human);
                                                        HumanListAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }

                        if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("南部")
                                & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                ) {
                            HumanList.clear();
                            db.collection("Human")
                                    .whereEqualTo("H_region", "南部")
                                    .whereEqualTo("H_time", "晚班")
                                    .whereEqualTo("H_ava", "可預約")
                                    .orderBy("H_score", Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.d(TAG, "Error :" + e.getMessage());
                                            } else {
                                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                                        String human_id = doc.getDocument().getId();
                                                        Human human = doc.getDocument().toObject(Human.class).withId(human_id);//抓ID
                                                        HumanList.add(human);
                                                        HumanListAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }

                        if (DivisionSp.getSelectedItem().toString().equalsIgnoreCase("東部")
                                & (TimeSp.getSelectedItem().toString().equalsIgnoreCase("18:00~24:00")
                                || TimeSp.getSelectedItem().toString().equalsIgnoreCase("24:00~06:00"))
                                ) {
                            HumanList.clear();
                            db.collection("Human")
                                    .whereEqualTo("H_region", "東部")
                                    .whereEqualTo("H_time", "晚班")
                                    .whereEqualTo("H_ava", "可預約")
                                    .orderBy("H_score", Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.d(TAG, "Error :" + e.getMessage());
                                            } else {
                                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                                        String human_id = doc.getDocument().getId();
                                                        Human human = doc.getDocument().toObject(Human.class).withId(human_id);//抓ID
                                                        HumanList.add(human);
                                                        HumanListAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });


                        }


                        //晚班結束

                        dialog.dismiss();
                    }



                }





            });

            //取消
            humanBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            humanBuilder.setView(mView);
            AlertDialog dialog = humanBuilder.create();
            dialog.show();
        }
    });




    HumanList = new ArrayList<>();
        HumanListAdapter = new HumanListAdapter(getApplicationContext(), HumanList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) HumanListview.findViewById(R.id.human_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mMainList.setAdapter(HumanListAdapter);


//    HumanList.clear();
//    db.collection("Human")
//                .whereEqualTo("H_ava","可預約")
//            .orderBy("H_score", Query.Direction.DESCENDING)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                        if (e != null) {
//                            Log.d(TAG, "Error :" + e.getMessage());
//                        } else {
//                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
//                                if (doc.getType() == DocumentChange.Type.ADDED) {
//                                    String human_id = doc.getDocument().getId();
//                                    Human human = doc.getDocument().toObject(Human.class).withId(human_id);//抓ID
//                                    HumanList.add(human);
//                                    HumanListAdapter.notifyDataSetChanged();
//                                }
//                            }
//                        }
//                    }
//                });



        return HumanListview;
    }


}
