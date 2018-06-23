package com.inturnes.emotisong;

import android.os.AsyncTask;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Song implements Serializable {
    private static ArrayList<Song> database;
    private String songName, artistName, fileName;
    private Emotion emotion;

    public Song(){
        if(database == null){
            Song.initializeDatabase();
        }
    }

    public Song(String songName, String artistName, String fileName){
        if(database == null){
            Song.initializeDatabase();
        }

        this.songName = songName;
        this.artistName = artistName;
        this.fileName = fileName;
        ScrapeSentiment sentiment = new ScrapeSentiment();
        sentiment.execute();
        this.emotion = sentiment.getEmotion();
    }

    public static Song getTopSong(Emotion... faces){
        if(Song.database == null){
            Song.initializeDatabase();
        }

        Emotion overallEmotion = Emotion.averageEmotion(faces);
        System.out.println("Emotion of photo!");
        System.out.println(overallEmotion.toString());
        Song bestSong = new Song();
        double bestScore = Double.MIN_VALUE;

        for(Song song : Song.database) {
            double compatibility = song.getEmotionCompatibility(overallEmotion);
            if(compatibility > bestScore){
                bestScore = compatibility;
                bestSong = song;
            }
        }

        System.out.println("Emotion of song!");
        System.out.println(bestSong.getEmotion().toString());
        return bestSong;
    }

    public static void initializeDatabase(){
        Song.database = new ArrayList<>();
        database.add(new Song("Someone Like You","Adele", "someonelikeyou.mp3"));
        database.add(new Song("Lonely","Akon", "lonely.mp3"));
        database.add(new Song("Wouldn't it be nice","Beach Boys", "wouldntitbenice.mp3"));
        database.add(new Song("Nothin on you","BoB", "nothinonyou.mp3"));
        database.add(new Song("Don't Worry Be Happy","Bobby McFerrin", "dontworrybehappy.mp3"));
        database.add(new Song("Mr. Blue Sky","Electric Light Orchestra", "mrblueksy.mp3"));
        database.add(new Song("Beautiful Girls","Sean Kingston", "beautifulgirls.mp3"));
        database.add(new Song("Shut Up and Dance","Walk the Moon", "shutupanddance.mp3"));
        database.add(new Song("Single Ladies","Beyonce", "singleladies.mp3"));
        database.add(new Song("Stay With Me","Sam Smith", "staywithme.mp3"));
        database.add(new Song("Hey Jude","The Beatles", "heyjude.mp3"));
        database.add(new Song("Where is the love?","The Black Eyed Peas", "whereisthelove.mp3"));
        database.add(new Song("The Lazy Song","Bruno Mars", "thelazysong.mp3"));
        database.add(new Song("Walking on Sunshine","Katrina and The Waves", "walkingonsunshine.mp3"));
        database.add(new Song("Super Far","LANY", "superfar.mp3"));
    }

    public double getEmotionCompatibility(Emotion faceEmotion){
        return emotion.compatibility(faceEmotion);
    }

    public String getFileName() {
        return fileName;
    }

    public String getSongName() {
        return songName;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    private class ScrapeSentiment extends AsyncTask<Void, Void, String> implements Serializable{
        private Emotion emotion;

        public ScrapeSentiment(){
            emotion = new Emotion();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String apiKey = "910d266bf07add88c50e4b1acb84e0d6";
                MusixMatch musixMatch = new MusixMatch(apiKey);
                Track track;

                try {
                    track = musixMatch.getMatchingTrack(songName, artistName);
                } catch (MusixMatchException e) {
                    return null;
                }

                TrackData data = track.getTrack();
                int trackID = data.getTrackId();
                Lyrics lyrics;

                try {
                    lyrics = musixMatch.getLyrics(trackID);
                } catch (MusixMatchException e) {
                    return null;
                }

                String lyricsBody = lyrics.getLyricsBody();
                Pattern pattern = Pattern.compile("[.]{3}[\\s\\S]*[*]{7}");
                Matcher matcher = pattern.matcher(lyricsBody);
                if(matcher.find()){
                    lyricsBody = matcher.replaceAll("");
                }

                NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                        "2018-03-16",
                        "e418e6cb-741d-4b54-9aef-0d46e0658f7c",
                        "TVVSr6HbiN6o");
                service.setEndPoint("https://gateway.watsonplatform.net/natural-language-understanding/api");

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
                JSONObject emo = new JSONObject(emotionString).getJSONObject("document").getJSONObject("emotion");
                this.emotion.setDisgust(emo.getDouble("disgust"));
                this.emotion.setFear(emo.getDouble("fear"));
                this.emotion.setSadness(emo.getDouble("sadness"));
                this.emotion.setAnger(emo.getDouble("anger"));
                this.emotion.setHappiness(emo.getDouble("joy"));

                return null;
            } catch (Exception e) {
                return null;
            }
        }

        public Emotion getEmotion() {
            return this.emotion;
        }
    }
}