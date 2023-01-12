package com.example.user.carematch;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.androidadvance.topsnackbar.TSnackbar;
import com.bumptech.glide.Glide;
import com.example.user.carematch.User;
import com.example.user.carematch.model.BlogPost;
import com.example.user.carematch.newPost.BlogRecyclerAdapter;
import com.example.user.carematch.newPost.PostActivity;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore user;
    private StorageReference storageReference;



    private TextView name;
    private TextView surname;

    private TextView email;
    private TextView membership;
    private TextView phone_number;
    private TextView address;
    private ImageView changeProfile;
    private View view;
    private CircleImageView circleImageView;
    private Activity activity;


    private Context context;
    private ImageView confirm,set;
    ProgressBar mProgressBar;
    Bitmap compressedImageFile;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    private Uri filePath;
    ProgressBar mProgress;
    Boolean isChanged = false;
    private String user_id;
    Bitmap compressedProfileImageFile;
    boolean username_exists = false;

    public TextView FavCount,CareCounts,PostCounts,ReservationCounts;
    public LinearLayout myfav,myblog,mycare;


    private final int PICK_IMAGE_REQUEST = 71;


    private static final String TAG = "FireLog";
    List<BlogPost> blogPosts;



    public ProfileFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);


        auth = FirebaseAuth.getInstance();
        user = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        user_id = auth.getCurrentUser().getUid();

        name = view.findViewById(R.id.profile_name);
        surname = view.findViewById(R.id.oldname);
        email = view.findViewById(R.id.profile_email);
        phone_number = view.findViewById(R.id.profile_phone_number);
        address = view.findViewById(R.id.profile_address);
        membership = view.findViewById(R.id.profile_membership);
        changeProfile = view.findViewById(R.id.change_profile_image);
        circleImageView = view.findViewById(R.id.circleImageView);

        changeProfile = view.findViewById(R.id.change_profile_image);
        circleImageView = view.findViewById(R.id.circleImageView);
        confirm = view.findViewById(R.id.confirm);
        mProgressBar = view.findViewById(R.id.post_progress);

        FavCount = (TextView) view.findViewById(R.id.FavCount);


        activity = getActivity();

        getUserProfile();




        //連接到我的貼文
        myblog = (LinearLayout )view.findViewById(R.id.mybolg);
        myblog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new BlogFragment2())
                        .addToBackStack(null)
                        .commit();
            }
        });


        //連接到我的預約
        mycare = (LinearLayout )view.findViewById(R.id.mycare);
        mycare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new BookFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        //連接到收藏
        myfav=(LinearLayout) view.findViewById(R.id.myfav);
        myfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new FavoriteFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });





        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(ProfileFragment.this.getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            + ContextCompat.checkSelfPermission(ProfileFragment.this.getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(ProfileFragment.this.getActivity(), "Grant Storage Read & Write Permission", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(ProfileFragment.this.getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    } else {

                        imagePicker();

                    }

                } else {
                    imagePicker();
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Confirm();


            }


        });


        set = view.findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileFragment.this.getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });



        //我的收藏個數
        if (auth.getCurrentUser() != null) {
            user.collection("users/" + user_id + "/favorites").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (documentSnapshots != null) {
                        if (!documentSnapshots.isEmpty()) {
                            int count = documentSnapshots.size();
                            FavCounts(count);
                        } else {
                            FavCounts(0);
                        }
                    }
                }
            });
        }

        //我的貼文個數
        if (auth.getCurrentUser() != null) {
            user.collection("BlogPost/")
                    .whereEqualTo("user_id",user_id)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (documentSnapshots != null) {
                        if (!documentSnapshots.isEmpty()) {
                            int count = documentSnapshots.size();
                            PostCounts(count);
                        } else {
                            PostCounts(0);
                        }
                    }
                }
            });
        }



        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
        String date1 = String.valueOf(year)  + String.valueOf(month + 1) +String.valueOf(day);
        //交通預約個數
        if (auth.getCurrentUser() != null) {
            user.collection("Reservation")
                    .whereEqualTo("User_id",user_id)
                    .whereGreaterThanOrEqualTo("Reservation_date", date)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (documentSnapshots != null) {
                                if (!documentSnapshots.isEmpty()) {
                                    int count = documentSnapshots.size();
                                    ReservationCounts(count);
                                } else {
                                    ReservationCounts(0);
                                }
                            }
                        }
                    });
        }


        //我的預約個數
        if (auth.getCurrentUser() != null) {
            user.collection("users/" + user_id + "/Care").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (documentSnapshots != null) {
                        if (!documentSnapshots.isEmpty()) {
                            int count = documentSnapshots.size();
                            CareCounts(count);
                        } else {
                            CareCounts(0);
                        }
                    }
                }
            });
        }

        return view;
    }

    public void FavCounts(int count) {
        FavCount = view.findViewById(R.id.FavCount);

        String s = Integer.toString(count);
        if(count>=0){
            FavCount.setText(s);
        }else{
            FavCount.setText(s);
        }
    }

    public void CareCounts(int count) {
        CareCounts = view.findViewById(R.id.CareCounts);

        String s = Integer.toString(count);
        if(count>=0){
            CareCounts.setText(s);
        }else{
            CareCounts.setText(s);
        }
    }
    public void PostCounts(int count) {
        PostCounts = view.findViewById(R.id.PostCounts);

        String s = Integer.toString(count);
        if(count>=0){
            PostCounts.setText(s);
        }else{
            PostCounts.setText(s);
        }
    }


    public void ReservationCounts(int count) {
        ReservationCounts = view.findViewById(R.id.ReservationCounts);

        String s = Integer.toString(count);
        if(count>=0){
            ReservationCounts.setText(s);
        }else{
            ReservationCounts.setText(s);
        }
    }

    public void getUserProfile() {

        if (auth.getCurrentUser() != null) {
            user.collection("users").document(auth.getCurrentUser().getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot, final FirebaseFirestoreException e) {

                            if (e != null) {

                                Log.d(TAG, "Error :" + e.getMessage());

                            }
                            else {
                                final User user = documentSnapshot.toObject(User.class);
                                name.setText(String.format("%s", user.getName()));
                                surname.setText(String.format("%s", user.getSurname()));

                                phone_number.setText(user.getPhoneNumber());
                                address.setText(user.getAddress());
                                membership.setText(user.getMembership());
                                email.setText(auth.getCurrentUser().getEmail());

                                String Image = user.getImage();

                                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                StorageReference picReference = storageReference.child("users_photos/" + Image);

                                Glide.with(circleImageView.getContext())
                                        .using(new FirebaseImageLoader())
                                        .load(picReference)
                                        .into(circleImageView);


                            }
                        }
                    });
        }

    }


    private void imagePicker() {


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"SelectPicture"),PICK_IMAGE_REQUEST);
    }

    private void Confirm() {

        if (filePath != null) {
            mProgressBar.setVisibility(View.VISIBLE);

            final String randomName = UUID.randomUUID().toString();

            Toast.makeText(ProfileFragment.this.getActivity(), "圖片上傳中", Toast.LENGTH_SHORT).show();

            //final
            final StorageReference file_path = storageReference.child("users_photos").child(randomName + ".jpg");
            file_path.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.getResult() != null) {

                        if (task.isSuccessful()) {
                            File newImageFile = new File(filePath.getPath());
                            try {


                                compressedImageFile = new Compressor(ProfileFragment.this.getActivity())
                                        .setMaxHeight(100)
                                        .setMaxWidth(100)
                                        .setQuality(2)
                                        .compressToBitmap(newImageFile);

                            }
                            //原本是IOException
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();


                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setAspectRatio(1, 1)
                                    .setCropShape(CropImageView.CropShape.OVAL)
                                    .start(ProfileFragment.this.getActivity());


//                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] thumb_data = baos.toByteArray();

                            UploadTask uploadTask = storageReference.child("users_photos/raw:")
                                    .child(randomName + ".jpg").putBytes(thumb_data);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //原本的，getDownloadUrl不能跑，先刪掉
//                                    String downloathumbUri = taskSnapshot.getStorage().getDownloadUrl().toString();
//                                    String downloathumbUri = taskSnapshot.toString();
                                    final String currentUserID = auth.getCurrentUser().getUid();
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("image", randomName+ ".jpg");


                                    user.collection("users").document(currentUserID).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileFragment.this.getActivity(), "大頭照上傳成功！", Toast.LENGTH_SHORT).show();
                                                Intent mainIntent = new Intent(ProfileFragment.this.getActivity(), MainActivity.class);
                                                startActivity(mainIntent);
                                                activity.finish();
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(ProfileFragment.this.getActivity(), error, Toast.LENGTH_SHORT).show();
                                            }
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                                            StorageReference ImageRef = storageRef.child("users_photos").child(randomName + ".jpg");
                                            ImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.i("delImgPostnotAdded", randomName + ".jpg removed");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.i("delImgPostnotAdded", randomName + ".jpg not Found");

                                                }
                                            });
                                            StorageReference ThumbImageRef = storageRef.child("users_photos/raw:")
                                                    .child(randomName + ".jpg");
                                            ThumbImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Log.i("delThumbImgPostnotAdded", randomName + ".jpg removed");

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.i("delThumbImgPostnotAdded", randomName + ".jpg not Found");

                                                }
                                            });
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                    StorageReference ImageRef = storageRef.child("users_photos").child(randomName + ".jpg");
                                    ImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("delImgPostnotAdded", randomName + ".jpg removed");

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("delImgPostnotAdded", randomName + ".jpg not Found");

                                        }
                                    });
                                }
                            });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(ProfileFragment.this.getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(ProfileFragment.this.getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } else {
            Toast.makeText(ProfileFragment.this.getActivity(), "請選擇大頭照！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), filePath);
                circleImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

