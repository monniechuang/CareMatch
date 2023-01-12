package com.example.user.carematch;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProductsList extends android.support.v4.app.Fragment {

    private View ProductsListview;
    private static final String TAG ="FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private ProductsListAdapter ProductsListAdapter;
    private List<Products> ProductsList;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_products_list);

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ProductsListview = inflater.inflate(R.layout.list_products, container, false);


        ProductsList = new ArrayList<>();
        ProductsListAdapter = new ProductsListAdapter(getApplicationContext(),ProductsList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) ProductsListview.findViewById(R.id.products_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMainList.setAdapter(ProductsListAdapter);

        db.collection("Products").orderBy("Products_name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d(TAG, "Error :" + e.getMessage());
                } else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String products_id = doc.getDocument().getId();

                            Products products = doc.getDocument().toObject(Products.class).withId(products_id);//抓ID
                            ProductsList.add(products);
                            ProductsListAdapter.notifyDataSetChanged();

                        }

                    }


                }
            }
        });

        return ProductsListview;
    }
}
