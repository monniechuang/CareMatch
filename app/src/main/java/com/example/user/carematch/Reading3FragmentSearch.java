package com.example.user.carematch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;

public class Reading3FragmentSearch extends Fragment {
    private SearchView mSearchView;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public Context context;

    ImageView returnToType;
    private View.OnClickListener listenReturnToType = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == returnToType.getId()){
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.afterSearchPage, new Reading3FragmentType())
                        .commit();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View andrewSearchPageView = inflater.inflate(R.layout.reading3_searchfragment, container, false);
        //setContentView(R.layout.reading3_searchfragment_xml);
        //以下是搜尋列
        mSearchView=(SearchView) andrewSearchPageView.findViewById(R.id.recipe_search);
        if(mSearchView == null){
            return null;
        }
        /*int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        TextView textView = mSearchView.findViewById(id);
        textView.setTextColor(Color.BLACK);  textView.setHintTextColor(Color.parseColor("#CCCCCC")); */

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.afterSearchPage, new Reading3FragmentType())
                .commit();

        returnToType = (ImageView) andrewSearchPageView.findViewById(R.id.returntodawnstarbutton);
        returnToType.setOnClickListener(listenReturnToType);



        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private String TAG = getClass().getSimpleName();

            @Override
            public boolean onQueryTextChange(String queryText) {
                Log.d(TAG, "onQueryTextChange = " + queryText);
                return true;
            }
            /*
             * 輸入完成後，提交時觸發的方法，一般情況是點擊輸入法中的搜索按鈕才會觸發。表示現在正式提交了
             */
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                Log.d(TAG, "onQueryTextSubmit = " + queryText);

                if (mSearchView != null) {
                    // 得到輸入管理對象
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        // 這將讓鍵盤在所有的情況下都被隱藏，但是一般我們在點擊搜索按鈕後，輸入法都會乖乖的自動隱藏的。
                        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0); // 輸入法如果是顯示狀態，那麼就隱藏輸入法
                    }

                }
                if (queryText != null) {
                    mSearchView.clearFocus(); // 不獲取焦點
                    fragmentManager = getFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    ((MainActivity)getActivity()).setNewSearchKey(queryText);
                    fragmentTransaction.replace(R.id.afterSearchPage, new Reading3FragmentAfterSearch())
                            .commit();
                    /*
                    Intent intent = new Intent();
                    intent.setClass(getApplication(), Reading3FragmentAfterSearch.class);
                    intent.putExtra("SearchKey",queryText);
                    startActivity(intent);
                    */
                }


                return true;


            }
        });

        return andrewSearchPageView;
    }
}
