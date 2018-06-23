package com.inturnes.emotisong;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.IOException;

public class SongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        initializeButton();
    }

    //https://stackoverflow.com/questions/19464782/android-how-to-make-a-button-click-play-a-sound-file-every-time-it-been-presse
    private void initializeButton(){
        Bundle bundle = getIntent().getExtras();
        final String file = bundle.getString("fileName");

        final MediaPlayer mp = new MediaPlayer();
        Button b = (Button) findViewById(R.id.button1);

        AssetFileDescriptor afd;
        try{
            afd = getAssets().openFd(file);
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
        }catch (IOException e){
            e.printStackTrace();
        }

        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mp.isPlaying())
                    mp.pause();
                else
                    mp.start();
            }
        });
    }
}