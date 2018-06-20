package com.inturnes.emotisong;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SelectionActivity extends AppCompatActivity {
    MasterPlaylist masterPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
    }

    //TODO create method for searching and presenting a list of playlists with checkboxes, when the username is entered into the username box
}
