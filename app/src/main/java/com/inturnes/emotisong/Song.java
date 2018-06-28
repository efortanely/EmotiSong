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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Song implements Serializable {
    private static ArrayList<Song> database;
    private String songName, artistName, songPath;
    private Emotion emotion;
    private int artResource;

    public Song() {
        if (database == null) {
            Song.initializeDatabase();
        }
    }

    public Song(String songName, String artistName, String songPath, int artResource) {
        if (database == null) {
            Song.initializeDatabase();
        }

        this.songName = songName;
        this.artistName = artistName;
        this.songPath = songPath;
        this.artResource = artResource;

        //the emotion correlated to the song lyrics must be run on a separate thread
        //to retrieve data from internet while current thread runs
        ScrapeSentiment sentiment = new ScrapeSentiment();
        sentiment.execute();
        this.emotion = sentiment.getEmotion();
    }

    //returns the song from the database with the greatest compatibility
    //with the average emotion of the faces passed to the method
    //this is done by finding the average vector of the emotion vector,
    //then returning the song from the database with the largest
    //dot product
    public static Song getTopSong(Emotion... faces) {
        if (Song.database == null) {
            Song.initializeDatabase();
        }

        Emotion overallEmotion = Emotion.averageEmotion(faces);
        Song bestSong = new Song();
        double bestScore = Double.MIN_VALUE;

        for (Song song : Song.database) {
            double compatibility = song.getEmotionCompatibility(overallEmotion);
            if (compatibility > bestScore) {
                bestScore = compatibility;
                bestSong = song;
            }
        }

        return bestSong;
    }

    //add songs with mp3 and art resources to the database arraylist
    public static void initializeDatabase() {
        Song.database = new ArrayList<>();
        database.add(new Song("Someone Like You", "Adele", "someonelikeyou.mp3", R.drawable.someonelikeyou));
        database.add(new Song("Lonely", "Akon", "lonely.mp3", R.drawable.lonely));
        database.add(new Song("Wouldn't It Be Nice", "The Beach Boys", "wouldntitbenice.mp3", R.drawable.wouldntitbenice));
        database.add(new Song("Nothin' On You", "B.o.B", "nothinonyou.mp3", R.drawable.nothinonyou));
        database.add(new Song("Don't Worry Be Happy", "Bobby McFerrin", "dontworrybehappy.mp3", R.drawable.dontworrybehappy));
        database.add(new Song("Mr. Blue Sky", "Electric Light Orchestra", "mrbluesky.mp3", R.drawable.mrbluesky));
        database.add(new Song("Beautiful Girls", "Sean Kingston", "beautifulgirls.mp3", R.drawable.beautifulgirls));
        database.add(new Song("Shut Up and Dance", "Walk the Moon", "shutupanddance.mp3", R.drawable.shutupanddance));
        database.add(new Song("Single Ladies", "Beyonce", "singleladies.mp3", R.drawable.singleladies));
        database.add(new Song("Stay With Me", "Sam Smith", "staywithme.mp3", R.drawable.staywithme));
        database.add(new Song("Hey Jude", "The Beatles", "heyjude.mp3", R.drawable.heyjude));
        database.add(new Song("Where Is The Love?", "The Black Eyed Peas", "whereisthelove.mp3", R.drawable.whereisthelove));
        database.add(new Song("The Lazy Song", "Bruno Mars", "thelazysong.mp3", R.drawable.thelazysong));
        database.add(new Song("Walking on Sunshine", "Katrina and The Waves", "walkingonsunshine.mp3", R.drawable.walkingonsunshine));
        database.add(new Song("Super Far", "LANY", "superfar.mp3", R.drawable.superfar));
    }

    //returns the mood of the song correlated with its strongest emotion
    public String getSongMood() {
        String emotionMood = this.emotion.getEmotionForFlavorText().first;
        switch (emotionMood) {
            case "disgusted":
                return "disgusting";
            case "fearful":
                return "scary";
            default:
                return emotionMood;
        }
    }

    public double getEmotionCompatibility(Emotion faceEmotion) {
        return emotion.compatibility(faceEmotion);
    }

    public String getSongPath() {
        return songPath;
    }

    public int getArtResource() {
        return this.artResource;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return this.artistName;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    //an asynchronous class that retrieves the song lyrics for a song, given its title and singer,
    //from a database, then determines the emotional sentiment of the lyrics
    private class ScrapeSentiment extends AsyncTask<Void, Void, String> implements Serializable {
        private Emotion emotion;

        public ScrapeSentiment() {
            emotion = new Emotion();
        }

        @Override
        protected String doInBackground(Void... params) {
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

            //remove copyright string at bottom to avoid analyzing the sentiment
            //of the copyright string...
            String lyricsBody = lyrics.getLyricsBody();
            Pattern pattern = Pattern.compile("[.]{3}[\\s\\S]*[*]{7}");
            Matcher matcher = pattern.matcher(lyricsBody);
            if (matcher.find()) {
                lyricsBody = matcher.replaceAll("");
            }

            //determine sentiment of song lyrics
            try {
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

                //parse JSON and store in emotion object

                String emotionString = emotionResult.toString();
                JSONObject emo;

                emo = new JSONObject(emotionString).getJSONObject("document").getJSONObject("emotion");
                this.emotion.setDisgust(emo.getDouble("disgust"));
                this.emotion.setFear(emo.getDouble("fear"));
                this.emotion.setSadness(emo.getDouble("sadness"));
                this.emotion.setAnger(emo.getDouble("anger"));
                this.emotion.setHappiness(emo.getDouble("joy"));
            }catch(Exception e){
                System.out.println("Failed to retrieve " + songName + " by " + artistName);
                System.out.println("Emotion vector for " + songName + " set to 0.");
                return null;
            }

            return null;
        }

        public Emotion getEmotion() {
            return this.emotion;
        }
    }
}