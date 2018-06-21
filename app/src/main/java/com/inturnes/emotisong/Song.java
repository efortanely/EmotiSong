package com.inturnes.emotisong;

import android.content.Context;
import android.os.AsyncTask;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;

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
    private Context context;
    private Class<NaturalLanguageUnderstanding> naturalLanguageUnderstandingClass;

    public Song(Context context, String songName, String artistName){
        this.context = context;
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


        NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                "2018-03-16",
                context.getString(R.string.natural_language_understanding_username),
                context.getString(R.string.natural_language_understanding_password));
        service.setEndPoint(context.getString(R.string.natural_language_understanding_url));

        String text = "IBM is an American multinational technology " +
                "company headquartered in Armonk, New York, " +
                "United States, with operations in over 170 countries.";

        EntitiesOptions entitiesOptions = new EntitiesOptions.Builder()
                .emotion(true)
                .sentiment(true)
                .limit(2)
                .build();

        KeywordsOptions keywordsOptions = new KeywordsOptions.Builder()
                .emotion(true)
                .sentiment(true)
                .limit(2)
                .build();

        Features features = new Features.Builder()
                .entities(entitiesOptions)
                .keywords(keywordsOptions)
                .build();

        AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                .text(text)
                .features(features)
                .build();

        AnalysisResults response = service
                .analyze(parameters)
                .execute();
        System.out.println(response);


        emotionCompatibility = -1;
        successfullyRanked = true;
    }
}
