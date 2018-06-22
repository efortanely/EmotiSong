package com.inturnes.emotisong;

import android.content.Intent;
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

        //Intent i = getIntent();
        //final MasterPlaylist playlist = (MasterPlaylist) i.getSerializableExtra("playlist");

        Bundle bundle = getIntent().getExtras();
        final String emotion = bundle.getString("emotion");
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

                    //Song song = playlist.getTopSongs().get(0);
                    //String file = song.getFile();
                    String[] happiness = {"beautifulgirls.mp3", "dontworrybehappy.mp3", "shutupanddance.mp3", "mrbluesky.mp3", "walkingonsunshine.mp3"};
                    String[] sadness = {"staywithme.mp3", "someonelikeyou.mp3", "lonely.mp3", "wouldntitbenice.mp3"};
                    String[] anger = {"singleladies.mp3", "whereisthelove.mp3"};
                    String[] neutral = {"heyjude.mp3", "superfar.mp3", "mrbluesky.mp3", "nothinonyou.mp3", "thelazysong.mp3"};
                    String file = "";
                    if (emotion.equalsIgnoreCase("happiness")) {
                        file = happiness[(int) (Math.random() * happiness.length)];
                    } else if (emotion.equalsIgnoreCase("sadness")) {
                        file = sadness[(int) (Math.random() * sadness.length)];
                    } else if (emotion.equalsIgnoreCase("anger") || emotion.equalsIgnoreCase("contempt") || emotion.equalsIgnoreCase("disgust")) {
                        file = anger[(int) (Math.random() * anger.length)];
                    } else if (emotion.equalsIgnoreCase("neutral") || emotion.equalsIgnoreCase("surprise") || emotion.equalsIgnoreCase("fear")) {
                        file = neutral[(int) (Math.random() * neutral.length)];
                    }
                    afd = getAssets().openFd(file);

                    //System.out.println(song.getSongName());

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
