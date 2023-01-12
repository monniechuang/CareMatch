package com.example.user.carematch;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class GoogleMapBtn extends AppCompatActivity  {
    private static final String TAG = "GoogleMap";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_btn);
        if(isServiceOK()){
            init();
        }
    }
    private void init(){
        Button btnMap =(Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (GoogleMapBtn.this,MapsFragment.class);
                startActivity(intent);
            }
        });
    }
    public boolean isServiceOK(){
        Log.d(TAG,"isServiceOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(GoogleMapBtn.this);
        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG,"isServiceOK: Google Play Services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we an resolve it
            Log.d(TAG,"isServiceOK: an error occured but we an fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(GoogleMapBtn.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"you cant make map request",Toast.LENGTH_SHORT).show();

        }
        return false;
    }

}
