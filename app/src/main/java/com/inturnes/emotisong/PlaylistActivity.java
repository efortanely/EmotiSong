package com.inturnes.emotisong;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PlaylistActivity extends AppCompatActivity {
    MasterPlaylist masterPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
    }

    //TODO update the master playlist object with the masterplaylist from the capture activity
    //display the songs in the masterplaylist object with the get top songs method
}
