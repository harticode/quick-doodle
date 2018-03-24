package com.quickdoodle.challengelearn.quickdoodle;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import  com.google.android.gms.ads.MobileAds;

import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {
Button mplayp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this, "ca-app-pub-9893102707172999~8288257940");

        mplayp = (Button) findViewById(R.id.playp);


        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            //do nothing

        } else {
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(Home.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        mplayp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent play = new Intent(Home.this, draw.class );
                startActivity(play);
            }
        });


    }
}
