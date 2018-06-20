package com.inturnes.emotisong;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BufferingActivity extends AppCompatActivity {
    //TODO add resources for the "buffering" stage of the playlist creation
    //TODO add resources for the "created" stage of the playlist creation
    MasterPlaylist masterPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffering);
    }

    //TODO create methods to use ibm watson bluemix to rank songs selected by user in the masterPlaylist object, and add them to the masterPlaylist song array, then push this playlist to masterPlaylist user spotify
}
