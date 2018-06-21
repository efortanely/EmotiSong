package com.inturnes.emotisong;

import android.os.AsyncTask;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Song extends AsyncTask<String, String, String>{
    private String songName;
    private String artistName;
    private Emotion emotion;
    private int emotionCompatibility;
    private boolean successfullyRanked;

    public Song(String songName, String artistName){
        this.songName = songName;
        this.artistName = artistName;
    }

    protected String doInBackground(String... urls){
        //TODO temp code
        setEmotion(new Emotion());
        return null;
    }

    private void setEmotion(Emotion faceEmotion){
        String apiKey = "910d266bf07add88c50e4b1acb84e0d6";
        MusixMatch musixMatch = new MusixMatch(apiKey);
        Track track;

        try {
            track = musixMatch.getMatchingTrack(songName, artistName);
        } catch (MusixMatchException e) {
            emotionCompatibility = -1;
            return;
        }

        TrackData data = track.getTrack();
        int trackID = data.getTrackId();
        Lyrics lyrics;

        try {
            lyrics = musixMatch.getLyrics(trackID);
        } catch (MusixMatchException e) {
            emotionCompatibility = -1;
            return;
        }

        String lyricsBody = lyrics.getLyricsBody();
        Pattern pattern = Pattern.compile("[.]{3}[\\s\\S]*[*]{7}");
        Matcher matcher = pattern.matcher(lyricsBody);
        if(matcher.find()){
            lyricsBody = matcher.replaceAll("");
        }


        //text analysis
        //https://natural-language-understanding-demo.ng.bluemix.net/
        //feed into ibm bluemix and set the emotion for the song

        emotionCompatibility = -1;
        successfullyRanked = true;
    }
}
