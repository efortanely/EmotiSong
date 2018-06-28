package com.inturnes.emotisong;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SongActivity extends AppCompatActivity {
    private final MediaPlayer mp = new MediaPlayer();
    private boolean mediaPlayerInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        Bundle bundle = getIntent().getExtras();
        initializeArt(bundle);
        initialSongStrings(bundle);
        initializeButton(bundle);
    }

    @Override
    public void onBackPressed() {
        if(mediaPlayerInitialized)
            mp.stop();

        Intent intent = new Intent(SongActivity.this, CaptureActivity.class);
        startActivity(intent);
    }

    //update album art based on selected top song
    private void initializeArt(Bundle bundle) {
        final int artResource = bundle.getInt("artResource");
        ImageView img = (ImageView) findViewById(R.id.songArt);
        img.setImageResource(artResource);
    }

    //update song title and artist name based on selected top song
    private void initialSongStrings(Bundle bundle) {
        final String songName = bundle.getString("songName");
        final String artistName = bundle.getString("artistName");

        TextView songNameLabel = (TextView) findViewById(R.id.songNameLabel);
        songNameLabel.setText(songName);

        TextView songArtistLabel = (TextView) findViewById(R.id.songArtistLabel);
        songArtistLabel.setText(artistName);
    }

    //song button pauses/plays the mp3 file for the top song
    //https://stackoverflow.com/questions/19464782/android-how-to-make-a-button-click-play-a-sound-file-every-time-it-been-presse
    private void initializeButton(Bundle bundle) {
        final String songPath = bundle.getString("songPath");

        final ImageButton b = (ImageButton) findViewById(R.id.playButton);

        AssetFileDescriptor afd;
        try {
            afd = getAssets().openFd(songPath);
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
            mediaPlayerInitialized = true;
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