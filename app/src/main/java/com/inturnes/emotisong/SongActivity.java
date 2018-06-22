package com.inturnes.emotisong;

import android.content.res.AssetFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.media.MediaPlayer;
import android.view.View.OnClickListener;

import java.io.IOException;

public class SongActivity extends AppCompatActivity {

    //https://stackoverflow.com/questions/19464782/android-how-to-make-a-button-click-play-a-sound-file-every-time-it-been-presse
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        Bundle bundle=getIntent().getExtras();
        int value=bundle.getInt("emotion");

        //start

        final MediaPlayer mp = new MediaPlayer();
        Button b = (Button) findViewById(R.id.button1);

        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mp.isPlaying()) {
                    mp.stop();
                }

                try {
                    mp.reset();
                    AssetFileDescriptor afd;
                    afd = getAssets().openFd("superfar.mp3");
                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mp.prepare();
                    mp.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }
}
