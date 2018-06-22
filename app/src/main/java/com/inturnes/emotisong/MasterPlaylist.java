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
        allSongs.add(new Song(this.context,"Someone Like You","Adele"));
        allSongs.add(new Song(this.context,"Lonely","Akon"));
        allSongs.add(new Song(this.context,"Wouldn't it be nice","Beach Boys"));
        allSongs.add(new Song(this.context,"Nothin on you","BoB"));
        allSongs.add(new Song(this.context,"Don't Worry Be Happy","Bobby McFerrin"));
        allSongs.add(new Song(this.context,"Nothin on you","BoB"));
        allSongs.add(new Song(this.context,"Mr. Blue Sky","Electric Light Orchestra"));
        allSongs.add(new Song(this.context,"Beautiful Girls","Sean Kingston"));
        allSongs.add(new Song(this.context,"Shut Up and Dance","Walk the Moon"));
        allSongs.add(new Song(this.context,"Single Ladies","Beyonce"));
        allSongs.add(new Song(this.context,"Stay With Me","Sam Smith"));
        allSongs.add(new Song(this.context,"The Beatles","Hey Jude"));
        allSongs.add(new Song(this.context,"Where is the love?","The Black Eyed Peas"));
        allSongs.add(new Song(this.context,"The Lazy Song","Bruno Mars"));
        allSongs.add(new Song(this.context,"Walking on Sunshine","Katrina and The Waves"));
    }

    public void addFace(Person face){
        this.faces.add(face);
    }
}
