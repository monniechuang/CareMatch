package com.example.user.carematch;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

public class AddHumanReview extends AppCompatActivity {
    private static final String TAG ="FireLog";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnSubmit;
    private EditText EditText_Review;
    public TextView humanName;
    public String userName;
    String uid,name;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private FirebaseFirestore user;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_human_review);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setCustomView(R.layout.actionbar_normal);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        Intent intent = this.getIntent();//取得傳遞過來的資料
        final String humanId = intent.getStringExtra("HumanId");
        final String currentUserID = auth.getCurrentUser().getUid();
        Log.d(TAG,"HumanId"+humanId);

        humanName=(TextView)findViewById(R.id.human_name);
        mFirestore = FirebaseFirestore.getInstance();
        btnSubmit = (Button)findViewById(R.id.btn_submit);
        EditText_Review = (EditText)findViewById(R.id.human_review) ;
        final FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if (User != null) {
            // Name, email address, and profile photo Url
            uid = User.getUid();

            Log.d(TAG, "userid" +uid);
        }
        if ( auth.getCurrentUser()!= null) {
            db.collection("users").document(currentUserID).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                userName=task.getResult().getString("name");
                            } else {
                            }
                        }
                    });
        }
        Task<DocumentSnapshot> documentSnapshotTask1 = db.collection("Human").document(humanId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        humanName.setText(document.get("H_name").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        btnSubmit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final AlertDialog.Builder mBuider = new AlertDialog.Builder(AddHumanReview.this);
//                mBuider.setTitle("");
//                mBuider.setIcon(R.drawable.logo);
//                mBuider.setMessage("確認評論");
//                mBuider.setPositiveButton("確認", new DialogInterface.OnClickListener() {
//                    private DocumentSnapshot snapshot;
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {

                        String review = EditText_Review.getText().toString();

                        Map<String, Object> uploadMap = new HashMap<>();
                        uploadMap.put("Human_id", humanId);
                        uploadMap.put("Human_review",review);
                        uploadMap.put("User_id",uid);
                        uploadMap.put("User_name",userName);

                        mFirestore.collection("HumanReview").add(uploadMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AddHumanReview.this,"上傳成功！",Toast.LENGTH_SHORT).show();



                                Intent mainIntent = new Intent(AddHumanReview.this, HumanPage.class);
                                mainIntent.putExtra("HumanId", humanId);
                                Log.d(TAG,"Id: "+humanId);
                                startActivity(mainIntent);
                                finish();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String error = e.getMessage();
                                Toast.makeText(AddHumanReview.this,"上傳失敗",Toast.LENGTH_SHORT).show();
                            }
                        });

//                }).show();

            }


        });
    }
}
