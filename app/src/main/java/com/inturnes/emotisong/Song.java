package com.inturnes.emotisong;

import android.content.Context;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Song {
    private String songName;
    private String artistName;
    private Emotion emotion;
    private double emotionCompatibility;
    private boolean successfullyRanked;
    private Context context;

    public Song(Context context, String songName, String artistName){
        this.context = context;
        this.songName = songName;
        this.artistName = artistName;
        this.emotion = new Emotion();
        this.emotionCompatibility = -1;
    }

    //given the emotion of the user, analyzes the song lyrics to set emotion to proper emotion for song
    //and updates emotion compatibility integer with a number that is larger if a closer fit to the user, and
    //smaller if it is not. also updates the successfully ranked boolean if the api's were able to categorize
    //the song lyrics appropriately
    public void setEmotion(Emotion faceEmotion){
        String apiKey = "910d266bf07add88c50e4b1acb84e0d6";
        MusixMatch musixMatch = new MusixMatch(apiKey);
        Track track;

        try {
            track = musixMatch.getMatchingTrack(songName, artistName);
        } catch (MusixMatchException e) {
            return;
        }

        TrackData data = track.getTrack();
        int trackID = data.getTrackId();
        Lyrics lyrics;

        try {
            lyrics = musixMatch.getLyrics(trackID);
        } catch (MusixMatchException e) {
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

        EmotionOptions emotionOptions = new EmotionOptions.Builder()
                .build();

        Features features = new Features.Builder()
                .emotion(emotionOptions)
                .build();

        AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                .text(lyricsBody)
                .features(features)
                .build();

        AnalysisResults response = service
                .analyze(parameters)
                .execute();

        EmotionResult emotionResult = response.getEmotion();

        String emotionString = emotionResult.toString();
        pattern = Pattern.compile("\"([a-z]+)\": ([0-9].[0-9]*),?");
        matcher = pattern.matcher(emotionString);
        int startingAt = 0;
        while(matcher.find(startingAt)){
            String tag = matcher.group(1);
            double strength = Double.parseDouble(matcher.group(2));

            switch(tag){
                case "anger":
                    emotion.setAnger(strength);
                    break;
                case "disgust":
                    emotion.setDisgust(strength);
                    break;
                case "fear":
                    emotion.setFear(strength);
                    break;
                case "joy":
                    emotion.setHappiness(strength);
                    break;
                case "sadness":
                    emotion.setSadness(strength);
                    break;
            }

            startingAt = matcher.end();
        }

        if(startingAt == 0){
            return;
        }

        emotionCompatibility = this.emotion.compatibility(faceEmotion);
        successfullyRanked = true;
    }

    public double getEmotionCompatibility(){
        return this.emotionCompatibility;
    }
}