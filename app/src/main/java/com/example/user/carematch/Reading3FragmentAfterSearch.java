package com.example.user.carematch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

public class Reading3FragmentAfterSearch extends Fragment {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private static final String TAG ="FireLog";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private Reading3SearchAdapter SearchPageListAdapter;
    private List<Post> SearchPageList;
    private String SearchKey;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View andrewAfterSearchPageView = inflater.inflate(R.layout.reading3_aftersearchfragment, container, false);
        //setContentView(R.layout.fragment_andrewsearchpage_main);
        //Intent intent = this.getIntent();//取得傳遞過來的資料
        SearchKey = ((MainActivity)getActivity()).getNewSearchKey();
        //SearchKey = intent.getStringExtra("SearchKey");
        Log.d(TAG, "search:" + SearchKey);
        SearchPageList = new ArrayList<>();
        SearchPageListAdapter = new Reading3SearchAdapter(getActivity().getApplicationContext(),SearchPageList);

        mMainList = (RecyclerView) andrewAfterSearchPageView.findViewById(R.id.search_list);
        mMainList.setHasFixedSize(true);
        mMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMainList.setAdapter(SearchPageListAdapter);

        if (SearchKey!=null){
            db.collection("Post").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (e != null) {

                        Log.d(TAG, "Error :" + e.getMessage());
                    }

                    for (DocumentChange doc1 : documentSnapshots.getDocumentChanges()) {
                        boolean Like =false;
                        String RestName =doc1.getDocument().get("Post_title").toString();
                        String RestName1 =doc1.getDocument().get("Post_desc").toString();
                        int key =RestName.indexOf(SearchKey);
                        int key1 =RestName1.indexOf(SearchKey);
                        if(key!=-1||key1!=-1){
                            Like=true;
                        }

                        if (doc1.getType() == DocumentChange.Type.ADDED && Like) {

                            String post_id=doc1.getDocument().getId();
                            Log.d(TAG,"id"+post_id);

                            Post post = doc1.getDocument().toObject(Post.class).withId(post_id);//抓ID
                            SearchPageList.add(post);
                            SearchPageListAdapter.notifyDataSetChanged();

                        }

                    }

                }
            });
        }

        return andrewAfterSearchPageView;
    }
}
