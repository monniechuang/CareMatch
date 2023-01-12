package com.example.user.carematch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.facebook.share.internal.LikeButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Products3Fragment extends Fragment {

    private static final String TAG ="FireLog";
    String ProductsName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private ProductsListAdapter ProductsListAdapter;
    private List<Products> ProductsList;
    LikeButton fav_button;
    private Button c;
    String type1;
    Spinner spnr;
    private int position;

    View Medicalview;


    //CM推薦商品
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Medicalview = inflater.inflate(R.layout.products3_fragment, container, false);



        ProductsList = new ArrayList<>();
        ProductsListAdapter = new ProductsListAdapter(getApplicationContext(),ProductsList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) Medicalview.findViewById(R.id.products_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mMainList.setAdapter(ProductsListAdapter);


                            db.collection("Products")
                                    .orderBy("Products_name", Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                    if (e != null) {

                                        Log.d(TAG, "Error :" + e.getMessage());
                                    } else {
                                        ProductsList.clear();
                                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                                String products_id = doc.getDocument().getId();
                                                Log.d(TAG, products_id);

                                                Products products = doc.getDocument().toObject(Products.class).withId(products_id);//抓ID
                                                ProductsList.add(products);
                                                ProductsListAdapter.notifyDataSetChanged();

                                            }

                                        }
                                    }

                                }
                            });




        return Medicalview;
    }
}