package com.inturnes.emotisong;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MasterPlaylist {
    //TODO add information queried from the user in the login page, if these credentials are needed to push the created playlist to their spotify
    private Song[] matchedSongs;
    private ArrayList<Person> faces;
    private ArrayList<Song> allSongs;
    private Context context;

    public MasterPlaylist(Context context){
        this.context = context;
        allSongs = new ArrayList<>();
        faces = new ArrayList<>();
        this.addTopSongs();
    }

    //sorts songs arraylist based on average emotion of faces in person arraylist, returns top 10 songs
    //with most similar emotions to the average emotion
    public ArrayList<Song> getTopSongs(){
        Emotion[] emotions = new Emotion[faces.size()];
        for(int i = 0; i < emotions.length; i++){
            emotions[i] = faces.get(i).getEmotion();
        }

        Emotion overallEmotion = Emotion.averageEmotion(emotions);
        for(Song song : this.allSongs) {
            song.setEmotion(overallEmotion);
        }

        Collections.sort(allSongs, new Comparator<Song>() {
            @Override public int compare(Song a, Song b) {
                return ((Double)a.getEmotionCompatibility()).compareTo(b.getEmotionCompatibility());
            }

        });

        return (ArrayList<Song>) allSongs.subList(0,10);
    }

    private void addTopSongs(){
        allSongs.add(new Song(this.context,"Sober","Demi Lovato"));
        allSongs.add(new Song(this.context,"Girls Like You (feat. Cardi B)","Maroon 5"));
        allSongs.add(new Song(this.context,"I Like It","Cardi B, Bad Bunny & J Balvin"));
        allSongs.add(new Song(this.context,"the light is coming (feat. Nicki Minaj)","Ariana Grande"));
        allSongs.add(new Song(this.context,"Whatever It Takes","Imagine Dragons"));
        allSongs.add(new Song(this.context,"Born to Be Yours","Kygo & Imagine Dragons"));
        allSongs.add(new Song(this.context,"Meant to Be","Bebe Rexha & Florida Georgia Line"));
        allSongs.add(new Song(this.context,"Psycho (feat. Ty Dolla $ign","Post Malone"));
        allSongs.add(new Song(this.context,"no tears left to cry","Ariana Grande"));
        allSongs.add(new Song(this.context,"Simple","Florida Georgia Line"));
        allSongs.add(new Song(this.context,"In My Blood","Shawn Mendes"));
        allSongs.add(new Song(this.context,"Better Now","Post Malone"));
        allSongs.add(new Song(this.context,"The Middle","Zedd, Maren Morris & Grey"));
        allSongs.add(new Song(this.context,"Nice For What","Drake"));
        allSongs.add(new Song(this.context,"Delicate","Taylor Swift"));
        allSongs.add(new Song(this.context,"Lucid Dreams","Juice WRLD"));
        allSongs.add(new Song(this.context,"Youngblood","5 Seconds of Summer"));
        allSongs.add(new Song(this.context,"Everything's Gonna Be Alright","David Lee Murphy & Kenny Chesney"));
        allSongs.add(new Song(this.context,"Back to You","Selena Gomez"));
        allSongs.add(new Song(this.context,"Mercy","Brett Young"));
        allSongs.add(new Song(this.context,"One Kiss","Calvin Harris, Dua Lipa"));
        allSongs.add(new Song(this.context,"Thunder","Imagine Dragons"));
        allSongs.add(new Song(this.context,"Mine","Bazzi"));
        allSongs.add(new Song(this.context,"Drowns the Whiskey (feat. Miranda Lambert)","Jason Aldean"));
        allSongs.add(new Song(this.context,"Heaven","Kane Brown"));
    }

    public void addFace(Person face){
        this.faces.add(face);
    }
}
