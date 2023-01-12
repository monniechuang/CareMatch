package com.example.user.carematch;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.user.carematch.App.CHANNEL_2_ID;

public class ReservationActivity extends android.support.v4.app.Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private NotificationManagerCompat notificationManager;

    private FirebaseAuth auth;
    private FirebaseFirestore user;
    private String final_date,final_time,comparedate;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private String flag;

    private Reservation reservation = new Reservation();

    private String TAG = "LeaveApplications_FLAG";


    private SimpleDateFormat myFmt2 = new SimpleDateFormat("yyyy-MM-dd");
    private StringBuffer date;
    private ArrayList<String> classList;
    private ImageView img_leave_photo;
    private Context context;
    private EditText edittext_place,edittext_place2;

    private final int PICK_IMAGE_REQUEST = 71;


    private Uri filePath;


    ActionBar actionBar;


    //交通
    private Spinner spinner_trans;
    private ArrayList<String> transList;
    private ArrayList<String> trans_idList;
    private String reservation_id;
    private static TextView textView_reverasation_content;
    private static TextView text_reservation_date;
    private static TextView text_reservation_time;
    private String user_id;
    private String user_name;
    private Button button_search;
    private Button button_cancel;
    private static TextView booking_date;
    private ImageView reservation_photo;
    private String reservation_content;


    //時間
    //開始時間
    TextView choose_time;
    TextView chooseEndTime;
    TimePickerDialog timePickerDialog;
    int currentHour;
    int currentMinute;
    String ampm;

    Calendar calendar;

    //日期
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View ResView = inflater.inflate(R.layout.activity_reservation, container, false);



        context = this.getActivity();
        auth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        notificationManager = NotificationManagerCompat.from(getActivity());
        final FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if (User != null) {
            // Name, email address, and profile photo Url
            user_id = User.getUid();

            Log.d(TAG, "userid" +user_id);
        }
        if ( auth.getCurrentUser()!= null) {
            db.collection("users").document(user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                user_name=task.getResult().getString("name");
                            } else {
                            }
                        }
                    });
        }

        edittext_place=(EditText)ResView.findViewById(R.id.booking_place);
        edittext_place2=(EditText)ResView.findViewById(R.id.booking_place2);


        text_reservation_date = (TextView) ResView.findViewById(R.id.booking_date);
        text_reservation_time = (TextView) ResView.findViewById(R.id.choose_time);
        button_search = (Button) ResView.findViewById(R.id.Button_search);
        button_cancel = (Button) ResView.findViewById(R.id.Button_cancel);
        text_reservation_time = ResView.findViewById(R.id.choose_time);






        setUser_name();

        Log.d(TAG, "reservation.setUser_name out get:" + reservation.getUser_name());

        initDate();
        date = new StringBuffer();

        //開啟日曆
        text_reservation_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePicker(v);
            }

        });

        //開啟時鐘
        text_reservation_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timePicker(v);

            }

        });


        Log.d(TAG, "Date:" + text_reservation_date.getText().toString());


        //預約
        button_search.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                if( "3".equals(flag) || "2".equals(flag) || "1".equals(flag)){
                    Toast.makeText(getActivity(), "请確認選取內容是否符合", 5000).show();

                }else if("".equals(edittext_place2.getText().toString().trim()) ||"".equals(edittext_place.getText().toString().trim())){
                    Toast.makeText(getActivity(), "请確認填寫", 5000).show();
                }else if("4".equals(flag)||"5".equals(flag)) {
//                sendEmail();
                    AlertDialog.Builder mBuider = new AlertDialog.Builder(getActivity());
                    mBuider.setTitle("乘車貴客:" + user_name);
                    mBuider.setIcon(R.drawable.logo);
                    mBuider.setMessage("更多服務規定請參閱「臺北市身心障礙者小型冷氣車乘客服務須知」");

                    mBuider.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        private DocumentSnapshot snapshot;

                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            String place = edittext_place.getText().toString();
                            String place2 = edittext_place2.getText().toString();

                            Map<String, Object> uploadMap = new HashMap<>();
                            uploadMap.put("Reservation_date", final_date);
                            uploadMap.put("Reservation_time", final_time);
                            uploadMap.put("Reservation_place", place);
                            uploadMap.put("Reservation_place2", place2);
                            uploadMap.put("User_id", user_id);
                            uploadMap.put("User_name", user_name);
                            sendOnChannel1();

                            db.collection("Reservation").add(uploadMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getActivity(), "預約成功！", Toast.LENGTH_SHORT).show();


//                                Intent intent = new Intent();
//                                intent.setClass(getApplicationContext(), ReservationList.class);
//                                intent.putExtra("HumanId",user_id);
//                                Log.d(TAG,"Id: "+user_id);
//                                startActivity(intent);
                                    fragmentManager = getFragmentManager();
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.framelayout, new Fragment_Transportation())
                                            .addToBackStack(null)
                                            .commit();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    String error = e.getMessage();
                                    Toast.makeText(getActivity(), "上傳失敗", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    }).show();
                }
                //打開預約清單頁面
//                openReservationConfirmedList();

            }
        });


        //取消
        //OK
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new Fragment_Transportation())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return ResView;
    }
    public void sendOnChannel1() {
        String place = edittext_place.getText().toString();
        String time = text_reservation_time.getText().toString();

        Intent activityIntent = new Intent(getActivity(),ReservationList.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(),
                0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_2_ID)
                .setSmallIcon(R.drawable.logo)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(user_name+"您好，感謝您預約復康巴士")
                        .addLine("時間："+time)
                        .addLine("地點："+place)
                        .setBigContentTitle("預約成功")
                        .setSummaryText("Care&Match"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                reservation_photo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //OK
    private void initDate() {


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
        String date1 = String.valueOf(year)  + String.valueOf(month + 1) +String.valueOf(day);
        text_reservation_date.setText(date);
        final_date=text_reservation_date.getText().toString();
        flag= String.valueOf(5);

    }

    //選擇日期
    //
    public void datePicker (final View v){

        Calendar calendar = Calendar.getInstance();
        final int year1 = calendar.get(Calendar.YEAR);
        final int month1 = calendar.get(Calendar.MONTH);
        final int day1 = calendar.get(Calendar.DAY_OF_MONTH);
        final String initdate = String.valueOf(year1) + "/" + String.valueOf(month1+ 1) + "/" + String.valueOf(day1);
        new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String date = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
                String date1 = String.valueOf(year)  + String.valueOf(month + 1) +String.valueOf(day);
                if(year1>year){
                    flag= String.valueOf(1);
                    Toast.makeText(getActivity(), "預約年分不得小於今年，请重新選擇", 5000).show();
                }else if(year1 == year){
                    if((month1+1)>(month+1)){
                        flag= String.valueOf(2);
                        Toast.makeText(getActivity(), "預約月分不得小於本月，请重新選擇", 5000).show();
                    }else if((month1+1)==(month+1)){
                        if(day1>day){
                            flag= String.valueOf(3);
                            Toast.makeText(getActivity(), "預約日不得小於本日，请重新選擇", 5000).show();
                        }else{
                            flag= String.valueOf(4);
                        }
                    }
                }
                Log.d("eeeee",date1);
                text_reservation_date.setText(date);
                final_date=text_reservation_date.getText().toString();

                Log.d(TAG, "Date inin:" +final_date);
            }


        }, year1, month1, day1).show();

        Log.d(TAG, "Date in:" + text_reservation_date.getText().toString());
    }


    //開啟時鐘
    public void timePicker(View v){

        //選擇開始時間
//        text_reservation_time = ResView.findViewById(R.id.choose_time);


        //加上上面的calender
        Calendar calendar = Calendar.getInstance();

        final int currentHour1 = calendar.get(Calendar.HOUR_OF_DAY);
        final int currentMinute1 = calendar.get(Calendar.MINUTE);

        //設定內容
        new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int currentHour, int currentMinute) {

                if(currentHour>=12){
                    ampm = "PM";
                }else{
                    ampm = "AM";
                }

                String time = String.format("%02d:%02d",currentHour,currentMinute)+ ampm;
                text_reservation_time.setText(time);
                final_time=text_reservation_time.getText().toString();
                Log.d(TAG, "Time in:" + text_reservation_time.getText().toString());


            }
        },currentHour,currentMinute,false).show();



    }


    //OK
    //設定user
    private void setUser_name() {


        db = FirebaseFirestore.getInstance();
        db.collection("User").whereEqualTo("user_id", user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user_name = document.get("user_name").toString();
                                reservation.setUser_name(user_name);
                                Log.d(TAG, "leave.setUser_name in get:" + reservation.getUser_name());
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }




}
