package com.example.user.carematch;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener{


    //設定搜尋id
    private String newSearchKey;
    public String getNewSearchKey() {
        return newSearchKey;
    }
    public void setNewSearchKey(String searchKey){
        this.newSearchKey = searchKey;
    }

    //搜尋文章type
    private String newType;
    public String getNewType() {
        return newType;
    }
    public void setNewType(String type) {
        this.newType = type;
    }


    private BottomNavigationView bottomNavigationView;
    private int fragment_status;
    private Button test;


    ActionBar actionBar;

    //設定主頁fragment
    private HomeFragment homeFragment;
    private BookFragment bookFragment;
    private FavoriteFragment favoriteFragment;
    private ProfileFragment profileFragment;
    private MapsFragment mapsFragment;

    private FirebaseAuth mAuth;
    private FirebaseFirestore user;
    FirebaseFirestore firebaseFirestore;

    String current_user_id;





    FragmentManager getChildFragmentManager;
    FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setCustomView(R.layout.actionbar);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);




        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        homeFragment = new HomeFragment();
        bookFragment = new BookFragment();
        favoriteFragment = new FavoriteFragment();
        profileFragment = new ProfileFragment();
        mapsFragment = new MapsFragment();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();



        //預設
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout, new HomeFragment())
                .commit();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent i = getIntent();
        if (i != null) {
            String string = i.getStringExtra("fromWhere");
            if (string != null) {
                if (string.equals("fromProfile")) {
                    setfragment(profileFragment);
                    bottomNavigationView.setSelectedItemId(R.id.profile);
                } else if (string.equals("fromBook")) {
                    setfragment(bookFragment);
                    bottomNavigationView.setSelectedItemId(R.id.book);
                } else if (string.equals("fromFavorite")) {
                    setfragment(favoriteFragment);
                    bottomNavigationView.setSelectedItemId(R.id.favorite);
                }
                else if (string.equals("fromHome")) {
                    setfragment(homeFragment);
                    bottomNavigationView.setSelectedItemId(R.id.home);
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.favorites: {
                setfragment(favoriteFragment);
                return true;
            }
            case R.id.home: {
                setfragment(homeFragment);
                return true;
            }
            case R.id.profile: {
                setfragment(profileFragment);

                return true;
            }
            case R.id.book: {
                setfragment(bookFragment);
                return true;
            }

            default:
                setfragment(homeFragment);
                return true;
        }
    }


    public void setfragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.framelayout, fragment)
                .commit();
    }




    private void callInstagram() {
        String apppackage = "com.instagram.android";
        Context cx = this;
        try {
            Uri uri = Uri.parse("http://instagram.com/_u/care_match");


            Intent i = new Intent(Intent.ACTION_VIEW, uri);

            i.setPackage("com.instagram.android");


                startActivity(i);
            } catch (ActivityNotFoundException e) {

                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/care_match")));
            }

        }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, RegisterAndLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_maps:
                   setfragment(mapsFragment);
                   return true;
            case R.id.action_opencv:
                Intent browserIntent =
                        new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.50.104:5001/stream"));
                startActivity(browserIntent);
                return true;
            case R.id.action_change_pass:
                startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
                return true;
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_ig:
                callInstagram();
                return true;
            case R.id.action_CM:
                startActivity(new Intent(MainActivity.this, CmActivity.class));
                return true;

            default:
                return false;
        }
    }


}







