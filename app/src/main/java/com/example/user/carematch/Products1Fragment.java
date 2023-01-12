package com.example.user.carematch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
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

import com.example.user.carematch.model.BlogPost;
import com.facebook.share.internal.LikeButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Products1Fragment extends Fragment {

    private static final String TAG ="FireLog";
    String ProductsName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private ProductsListAdapter ProductsListAdapter;
    private List<Products> ProductsList;
    LikeButton fav_button;
    private FirebaseAuth firebaseAuth;

    DocumentSnapshot lastVisible;
    ViewPager HomeViewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    View Medicalview;


    //最新商品
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Medicalview = inflater.inflate(R.layout.products1_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        ProductsList = new ArrayList<>();
        ProductsListAdapter = new ProductsListAdapter(getApplicationContext(),ProductsList);
        //取得RecylerView物件，設定佈局及adapter
        mMainList = (RecyclerView) Medicalview.findViewById(R.id.products_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mMainList.setAdapter(ProductsListAdapter);
        if (firebaseAuth.getCurrentUser() != null) {

            db = FirebaseFirestore.getInstance();
            mMainList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                    if (reachedBottom) {

                        loadMorePosts();

                    }
                }
            });


            db.collection("Products")
                    .orderBy("Products_time", Query.Direction.DESCENDING)
                    .limit(6)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (e != null) {

                                Log.d(TAG, "Error :" + e.getMessage());
                            } else {
                                ProductsList.clear();
                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
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
        }

            return Medicalview;

    }
    private void loadMorePosts() {
        if (firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = db.collection("Products")
                    .orderBy("Products_time", Query.Direction.DESCENDING)
                    .startAfter(lastVisible);
            nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (!documentSnapshots.isEmpty()) {
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
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
        }
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // ----ViewPager---- //

        HomeViewPager = (ViewPager) getView().findViewById(R.id.HomeViewPager);

        sliderDotspanel = (LinearLayout) getView().findViewById(R.id.SliderDots);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity());

        HomeViewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        HomeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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

    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if(getActivity()!=null) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (HomeViewPager.getCurrentItem() == 0) {
                            HomeViewPager.setCurrentItem(1);
                        } else if (HomeViewPager.getCurrentItem() == 1) {
                            HomeViewPager.setCurrentItem(2);
                        } else {
                            HomeViewPager.setCurrentItem(0);
                        }

                    }
                });
            }

        }
    }
}