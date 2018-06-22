package com.inturnes.emotisong;

public class Person {
    //TODO store face for showing in selectionActivity
    private Emotion emotion;
    private String username;
    private Playlist[] allPlaylists;
    private int numSongsToContribute;

    //TODO create method to analyze songs in selected songs
    public Person(){

    }

    public void addEmotion(Emotion emotion){
        this.emotion = emotion;
    }

    public Emotion getEmotion(){
        return this.emotion;
    }
}
