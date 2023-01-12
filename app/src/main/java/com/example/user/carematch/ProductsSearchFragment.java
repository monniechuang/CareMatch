package com.example.user.carematch;
//jason
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.share.internal.LikeButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProductsSearchFragment extends android.support.v4.app.Fragment implements ViewPager.OnPageChangeListener,
        TabLayout.OnTabSelectedListener {
    ViewPager HomeViewPager;
    LinearLayout sliderDotspanel;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int dotscount;
    private ImageView[] dots;
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

    String[] type = {
            "請選擇商品類型",
            "醫療儀器",
            "護理床",
            "老人椅",
            "輪椅",
            "助行用品",
            "浴室安全",
            "護具",
            "營養食品",
            "其他"
    };
    View Medicalview;

    //分類
    private ViewPager firstViewPager;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Medicalview = inflater.inflate(R.layout.fragment_products_search, container, false);



        //分類
        firstViewPager = (ViewPager) Medicalview.findViewById(R.id.viewpager_content);
        // Set Tabs inside Toolbar
        tabLayout = (TabLayout) Medicalview.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(firstViewPager);
        setupViewPager(firstViewPager);



//        ProductsList = new ArrayList<>();
//        ProductsListAdapter = new ProductsListAdapter(getApplicationContext(),ProductsList);
//        //取得RecylerView物件，設定佈局及adapter
//        mMainList = (RecyclerView) Medicalview.findViewById(R.id.products_list);
//        mMainList.setHasFixedSize(true);
//        mMainList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        mMainList.setAdapter(ProductsListAdapter);
//        spnr = (Spinner)Medicalview.findViewById(R.id.spn);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                this.getActivity(), android.R.layout.simple_spinner_item, type);
//        spnr.setAdapter(adapter);
//        spnr.setOnItemSelectedListener(
//                new AdapterView.OnItemSelectedListener() {
//
//                    @Override
//                    public void onItemSelected(AdapterView<?> arg0, View arg1,
//                                               int arg2, long arg3) {
//
//
//                        position = spnr.getSelectedItemPosition();
//                        Toast.makeText(getApplicationContext(),"請選擇商品",Toast.LENGTH_LONG).show();
//
//                        // TODO Auto-generated method stub
//                        //
//                        String key= type[+position].toString();
//                        Log.d("aaaa",key);
//                        if(key=="請選擇商品類型"){
//                            db.collection("Products").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                                    if (e != null) {
//
//                                        Log.d(TAG, "Error :" + e.getMessage());
//
//                                    }
//                                    else {
//                                        ProductsList.clear();
//                                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
//
//                                            if (doc.getType() == DocumentChange.Type.ADDED) {
//
//                                                String products_id = doc.getDocument().getId();
//                                                Log.d(TAG, products_id);
//
//                                                Products products = doc.getDocument().toObject(Products.class).withId(products_id);//抓ID
//                                                ProductsList.add(products);
//                                                ProductsListAdapter.notifyDataSetChanged();
//                                                Toast.makeText(getApplicationContext(),"您已選擇 "+type[+position],Toast.LENGTH_LONG).show();
//
//
//                                            }
//
//                                        }
//                                    }
//
//                                }
//                            });
//                        }else {
//                            db.collection("Products").whereEqualTo("Products_type", key).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                                    if (e != null) {
//
//                                        Log.d(TAG, "Error :" + e.getMessage());
//                                    } else {
//                                        ProductsList.clear();
//                                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
//
//                                            if (doc.getType() == DocumentChange.Type.ADDED) {
//
//                                                String products_id = doc.getDocument().getId();
//                                                Log.d(TAG, products_id);
//
//                                                Products products = doc.getDocument().toObject(Products.class).withId(products_id);//抓ID
//                                                ProductsList.add(products);
//                                                ProductsListAdapter.notifyDataSetChanged();
//
//                                            }
//
//                                        }
//                                    }
//
//                                }
//                            });
//                        }
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> arg0) {
//                        // TODO Auto-generated method stub
//
//                    }
//
//                }
//        );
//




        return Medicalview;
    }



    //收藏分類
    private void setupViewPager(ViewPager viewPager) {


        PostSearchFragment.Adapter adapter = new PostSearchFragment.Adapter(getChildFragmentManager());
        adapter.addFragment(new Products1Fragment(), "最新商品");
        adapter.addFragment(new Products2Fragment(), "自選商品");
        adapter.addFragment(new Products3Fragment(), "CM推薦商品");
        viewPager.setAdapter(adapter);


    }







    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}





