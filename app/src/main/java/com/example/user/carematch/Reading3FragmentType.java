package com.example.user.carematch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Reading3FragmentType extends Fragment {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    String[] str = {
            "樂活養生",
            "心靈慰藉",
            "長期照護",
            "病症知識",
            "新聞政策",
            "社會資源"};

    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
            Bundle bundle = new Bundle();
            bundle.putString("type", str[position]);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, TypePage.class);
            intent.putExtras(bundle);
            startActivity(intent);
            */
            ((MainActivity)getActivity()).setNewType(str[position]);
            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.afterSearchPage, new Reading3FragmentAfterType())
                    .commit();

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View dawnStarTypePageView = inflater.inflate(R.layout.reading3_typefragment, container, false);
        ListView typeList = (ListView) dawnStarTypePageView.findViewById(R.id.type_list);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.list_type, str);
        typeList.setAdapter(adapter);
        typeList.setOnItemClickListener(onClickListView);

        return dawnStarTypePageView;
    }
}