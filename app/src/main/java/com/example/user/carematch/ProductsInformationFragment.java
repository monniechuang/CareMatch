package com.example.user.carematch;
//jason
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductsInformationFragment extends android.support.v4.app.Fragment {

    private static final String TAG ="Products";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public TextView productsName;
    public TextView productsStorename;
    public TextView productsDetails;
    public TextView productsTitle;
    public ImageView productsImage;
    String storename;
    View ProductsInformationview;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        ProductsInformationview = inflater.inflate(R.layout.fragment_products_information, container, false);


        Intent intent = getActivity().getIntent();//取得傳遞過來的資料
        String productsId = intent.getStringExtra("ProductsId");
        productsName=(TextView)ProductsInformationview.findViewById(R.id.products_name);
        productsStorename=(TextView)ProductsInformationview.findViewById(R.id.products_storename);
        productsTitle=(TextView) ProductsInformationview.findViewById(R.id.products_title);
        productsDetails=(TextView) ProductsInformationview.findViewById(R.id.products_details);
        productsImage=(ImageView) ProductsInformationview.findViewById(R.id.products_image);

        ImageButton button = ProductsInformationview.findViewById(R.id.Store_Button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("s",storename);
                Intent intent = new Intent(getActivity(), ArticleActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        Task<DocumentSnapshot> documentSnapshotTask = db.collection("Products").document(productsId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        storename = document.get("Products_storename").toString();
                        productsName.setText(document.get("Products_name").toString());
//                                productsStorename.setText(document.get("Products_storename").toString());
                        productsDetails.setText(document.get("Products_details").toString());
//                                productsTitle.setText(document.get("Products_title").toString());
                        String image = document.get("Products_image").toString();
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        StorageReference picReference = storageReference.child("Products/"+image);

                        Glide.with(productsImage.getContext())
                                .using(new FirebaseImageLoader())
                                .load(picReference)
                                .into(productsImage);


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return  ProductsInformationview;
    }
}
