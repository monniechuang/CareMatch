package com.example.user.carematch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.user.carematch.R;
import com.example.user.carematch.model.BlogPost;
import com.example.user.carematch.newPost.BlogRecyclerAdapter;
import com.example.user.carematch.newPost.PostActivity;
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


public class BlogFragment extends Fragment {

    private View BlogListview;
    private static final String TAG ="FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private PostListAdapter PostListAdapter;
    private List<Post> PostList;

    //分類
    private TabLayout tabLayout;
    private ViewPager firstViewPager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        BlogListview = inflater.inflate(R.layout.fragment_blog_search, container, false);


        firstViewPager = (ViewPager) BlogListview.findViewById(R.id.viewpager_content);
        // Set Tabs inside Toolbar
        tabLayout = (TabLayout) BlogListview.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(firstViewPager);
        setupViewPager(firstViewPager);


        return BlogListview;
    }

    //收藏分類
    private void setupViewPager(ViewPager viewPager) {


        PostSearchFragment.Adapter adapter = new PostSearchFragment.Adapter(getChildFragmentManager());
        adapter.addFragment(new BlogFragment1(), "最新討論區");
        adapter.addFragment(new BlogFragment2(), "我的貼文");
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



