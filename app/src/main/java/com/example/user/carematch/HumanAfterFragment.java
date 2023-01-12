package com.example.user.carematch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.accountkit.internal.AccountKitController;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class HumanAfterFragment extends Fragment {
    private static final String TAG = "HumanAfterFragment";

    private View HumanListview;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView mMainList;
    private com.example.user.carematch.HumanListAdapter HumanListAdapter;
    private List<Human> HumanList;
    private String SearchKey;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View HumanListview = inflater.inflate(R.layout.fragment_afterhumansearch, container, false);

        SearchKey = ((MainActivity)getActivity()).getNewSearchKey();

        //SearchKey = intent.getStringExtra("SearchKey");
        Log.d(TAG, "search:" + SearchKey);
        HumanList = new ArrayList<>();
        HumanListAdapter = new HumanListAdapter(getActivity().getApplicationContext(),HumanList);

        mMainList = (RecyclerView) HumanListview.findViewById(R.id.human_list);
        mMainList.setHasFixedSize(true);
//        mMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMainList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mMainList.setAdapter(HumanListAdapter);

        if (SearchKey!=null){
            db.collection("Human")
                    .whereEqualTo("H_ava","可預約")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.d(TAG, "Error :" + e.getMessage());
                    }

                    for (DocumentChange doc1 : documentSnapshots.getDocumentChanges()) {
                        boolean Like =false;
                        String HumantName =doc1.getDocument().get("H_exp").toString();
                        int key =HumantName.indexOf(SearchKey);
                        if(key!=-1){
                            Like=true;
                        }

                        if (doc1.getType() == DocumentChange.Type.ADDED && Like) {

                            String human_id=doc1.getDocument().getId();
                            Log.d(TAG,"id"+human_id);

                            Human human = doc1.getDocument().toObject(Human.class).withId(human_id);//抓ID
                            HumanList.add(human);
                            HumanListAdapter.notifyDataSetChanged();

                        }

                    }



                }
            });
        }

        return HumanListview;
    }
}