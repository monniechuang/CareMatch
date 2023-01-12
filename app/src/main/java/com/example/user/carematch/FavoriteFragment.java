package com.example.user.carematch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FavoriteFragment extends android.support.v4.app.Fragment {

    private ImageButton imageButton_logo;
    private View FavoriteView;


    private static final String TAG = "FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private ProductsListAdapter ProductsListAdapter;
    private List<Products> ProductsList;
    private FirebaseAuth auth;
    private String userId;
    private ArrayList<String> productList;
    private TabLayout tabLayout;
    private ViewPager firstViewPager;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FavoriteView = inflater.inflate(R.layout.fragment_favorite, container, false);


        firstViewPager = (ViewPager) FavoriteView.findViewById(R.id.viewpager_content);
        // Set Tabs inside Toolbar
        tabLayout = (TabLayout) FavoriteView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(firstViewPager);
        setupViewPager(firstViewPager);


//        auth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        userId = auth.getCurrentUser().getUid();
//        ProductsList = new ArrayList<>();
//        ProductsListAdapter = new ProductsListAdapter(getApplicationContext(), ProductsList);
//        //取得RecylerView物件，設定佈局及adapter
//        mMainList = (RecyclerView) FavoriteView.findViewById(R.id.products_list);
//        mMainList.setHasFixedSize(true);
//        mMainList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        mMainList.setAdapter(ProductsListAdapter);
//
//        db.collection("users/" + userId + "/favorites").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                if (e != null) {
//
//                    Log.d(TAG, "Error :" + e.getMessage());
//                } else {
//                    productList = new ArrayList<>();
//                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
//
//                        if (doc.getType() == DocumentChange.Type.ADDED) {
//
//                            String products_id = doc.getDocument().getId();
//
//                            Products products = doc.getDocument().toObject(Products.class).withId(products_id);//抓ID
////                            ProductsList.add(products);
//                            productList.add(products_id);
//                            ProductsListAdapter.notifyDataSetChanged();
//                            Log.d("TAG", productList.toString());
//
//                        }
//
//                    }
//                    for (String productId : productList) {
//                        db.collection("Products")
//                                .document(productId)
//                                .get()
//                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                        if (task.isSuccessful()) {
//                                            DocumentSnapshot document = task.getResult();
//                                            if (document.exists()) {
//                                                String products_id = document.getId();
//                                                Log.d(TAG, products_id);
//
//                                                Products products = document.toObject(Products.class).withId(products_id);//抓ID
//                                                ProductsList.add(products);
//                                                ProductsListAdapter.notifyDataSetChanged();
//
//                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                                            } else {
//                                                Log.d(TAG, "No such document");
//                                            }
//                                        } else {
//                                            Log.d(TAG, "get failed with ", task.getException());
//                                        }
//                                    }
//
//
//                                });
//
//
//                    }
//
//
//                }
//            }
//        });

        return FavoriteView;

    }


    //收藏分類
    private void setupViewPager(ViewPager viewPager) {


        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "商品收藏");
        adapter.addFragment(new Tab2Fragment(), "看護收藏");
        adapter.addFragment(new Tab3Fragment(), "文章收藏");
        viewPager.setAdapter(adapter);


    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}



