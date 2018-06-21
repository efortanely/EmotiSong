package com.inturnes.emotisong;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class LoginActivity extends AppCompatActivity {
    MasterPlaylist masterPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO temp code
        new Song(this,"Rolling in the deep","Adele").execute("");
    }

    //TODO push info passed from user into masterPlaylist
}
