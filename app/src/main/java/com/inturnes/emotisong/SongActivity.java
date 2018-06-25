package com.inturnes.emotisong;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class SongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        initializeArt();
        initialSongStrings();
        initializeButton();
    }

    //update album art based on selected top song
    private void initializeArt() {
        Bundle bundle = getIntent().getExtras();
        final int artResource = bundle.getInt("artResource");

        ImageView img = (ImageView) findViewById(R.id.songArt);
        img.setImageResource(artResource);
    }

    //update song title and artist name based on selected top song
    private void initialSongStrings() {
        Bundle bundle = getIntent().getExtras();
        final String songName = bundle.getString("songName");
        final String artistName = bundle.getString("artistName");

        TextView songNameLabel = (TextView) findViewById(R.id.songNameLabel);
        songNameLabel.setText(songName);

        TextView songArtistLabel = (TextView) findViewById(R.id.songArtistLabel);
        songArtistLabel.setText(artistName);
    }

    //song button pauses/plays the mp3 file for the top song
    //https://stackoverflow.com/questions/19464782/android-how-to-make-a-button-click-play-a-sound-file-every-time-it-been-presse
    private void initializeButton() {
        Bundle bundle = getIntent().getExtras();
        final String songPath = bundle.getString("songPath");

        final MediaPlayer mp = new MediaPlayer();
        final ImageButton b = (ImageButton) findViewById(R.id.playButton);

        AssetFileDescriptor afd;
        try {
            afd = getAssets().openFd(songPath);
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    mp.pause();
                    b.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mp.start();
                    b.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
    }
}