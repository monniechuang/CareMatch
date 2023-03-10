package com.example.user.carematch;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class HumanSearchList extends android.support.v4.app.Fragment {

    private SearchView mSearchView;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public Context context;

    ImageView gosearch;
    private View.OnClickListener Search = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == gosearch.getId()){
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.afterSearchPage, new HumanAfterFragment())
                        .commit();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View HumanSearchListView = inflater.inflate(R.layout.fragment_human_searchlist, container, false);
        //setContentView(R.layout.fragment_andrewsearchpage_searchpage);
        //??????????????????
        mSearchView=(SearchView) HumanSearchListView.findViewById(R.id.care_search);
        if(mSearchView == null){
            return null;
        }
        /*int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        TextView textView = mSearchView.findViewById(id);
        textView.setTextColor(Color.BLACK);  textView.setHintTextColor(Color.parseColor("#CCCCCC")); */

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.afterSearchPage, new HumanAfterFragment())
                .commit();

        gosearch = (ImageView) HumanSearchListView.findViewById(R.id.gosearch);
        gosearch.setOnClickListener(Search);



        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private String TAG = getClass().getSimpleName();

            @Override
            public boolean onQueryTextChange(String queryText) {
                Log.d(TAG, "onQueryTextChange = " + queryText);
                return true;
            }
            /*
             * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
             */
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                Log.d(TAG, "onQueryTextSubmit = " + queryText);

                if (mSearchView != null) {
                    // ????????????????????????
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0); // ?????????????????????????????????????????????????????????
                    }

                }
                if (queryText != null) {
                    mSearchView.clearFocus(); // ???????????????
                    fragmentManager = getFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    ((MainActivity)getActivity()).setNewSearchKey(queryText);
                    fragmentTransaction.replace(R.id.afterSearchPage, new HumanAfterFragment())
                            .commit();

                }


                return true;


            }
        });

        return HumanSearchListView;
    }
}
