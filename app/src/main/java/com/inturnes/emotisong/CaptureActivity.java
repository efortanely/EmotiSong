package com.inturnes.emotisong;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class CaptureActivity extends AppCompatActivity {
    //private MasterPlaylist masterPlaylist;
    //credit: https://blogs.msdn.microsoft.com/uk_faculty_connection/2017/10/14/using-microsoft-cognitive-emotion-api-with-android-app-studio/
    private ImageView imageView; // variable to hold the image view in our activity_main.xml
    private TextView resultText; // variable to hold the text view in our activity_main.xml
    private static final int RESULT_LOAD_IMAGE  = 100;
    private static final int REQUEST_PERMISSION_CODE = 200;

    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;

    //TODO have a button that when you click it, will bring you to the playlist screen
    //TODO pass the master playlist to it, call the top songs method on it

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        // initiate our image view and text view
        imageView = (ImageView) findViewById(R.id.imageView);
        resultText = (TextView) findViewById(R.id.resultText);
        //masterPlaylist = new MasterPlaylist(CaptureActivity.this);
    }

    // when the "GET EMOTION" Button is clicked this function is called
    public void getEmotion(View view) {
        // run the GetEmotionCall class in the background
        GetEmotionCall emotionCall = new GetEmotionCall(imageView);
        emotionCall.execute();
    }

    // when the "GET IMAGE" Button is clicked this function is called
    public void getImage(View view) {
        // check if user has given us permission to access the gallery
        if(checkPermission()) {
            Intent choosePhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(choosePhotoIntent, RESULT_LOAD_IMAGE);
        }
        else {
            requestPermission();
        }
    }

    // This function gets the selected picture from the gallery and shows it on the image view
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // get the photo URI from the gallery, find the file path from URI and send the file path to ConfirmPhoto
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            // a string variable which will store the path to the image in the gallery
            String picturePath= cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(bitmap);


            if (resultCode == RESULT_OK) {
                if (requestCode == SELECT_PICTURE) {
                    Uri selectedImageUri = data.getData();
                    selectedImagePath = getPath(selectedImageUri);
                }
            }
        }

    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    // convert image to base 64 so that we can send the image to Emotion API
    public byte[] toBase64(ImageView imgPreview) {
        Bitmap bm = ((BitmapDrawable) imgPreview.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object

        return baos.toByteArray();

        /*Bitmap bm = BitmapFactory.decodeFile(selectedImagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        return b;*/
    }

    // if permission is not given we get permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(CaptureActivity.this,new String[]{READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    // asynchronous class which makes the API call in the background
    private class GetEmotionCall extends AsyncTask<Void, Void, String> implements Serializable{

        private final ImageView img;
        GetEmotionCall(ImageView img) {
            this.img = img;
        }

        // this function is called before the API call is made
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resultText.setText("Getting results...");
        }

        // this function is called when the API call is made
        @Override
        protected String doInBackground(Void... params) {

            HttpClient httpClient = HttpClients.createDefault();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try
            {
                // NOTE: You must use the same region in your REST call as you used to obtain your subscription keys.
                //   For example, if you obtained your subscription keys from westcentralus, replace "westus" in the
                //   URL below with "westcentralus".
                URIBuilder uriBuilder = new URIBuilder("https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize");

                URI uri = uriBuilder.build();
                HttpPost request = new HttpPost(uri);

                // Request headers. Replace the example key below with your valid subscription key.
                request.setHeader("Content-Type", "application/octet-stream");
                request.setHeader("Ocp-Apim-Subscription-Key", "c7d2f9108ed74bd8be4d0d38638400a4");

                // Request body. Replace the example URL below with the URL of the image you want to analyze.
                byte[] temp = toBase64(img);
                System.out.println("temp: " + temp);
                ByteArrayEntity reqEntity = new ByteArrayEntity(temp);
                String encodedImage = Base64.encodeToString(temp, Base64.DEFAULT);
                System.out.println("encoded: " + encodedImage);
                System.out.println("created byte array entity " + reqEntity);
                request.setEntity(reqEntity);
                System.out.println("img set");

                HttpResponse response = httpClient.execute(request);
                System.out.println("entity requested " + response);
                HttpEntity entity = response.getEntity();
                System.out.println("entity received " + entity);

                // Request body.The parameter of setEntity converts the image to base64
                //request.setEntity(new ByteArrayEntity(toBase64(img)));

                if (entity != null)
                {
                    String res = EntityUtils.toString(entity);
                    System.out.println("here: " + res);
                    return res;
                } else {
                    System.out.println("entity was null");
                    return "null";
                }
                // getting a response and assigning it to the string res
               /* HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                String res = EntityUtils.toString(entity);


                return res;*/

            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                return "null";
            }
        }


        // this function is called when we get a result from the API call
        @Override
        protected void onPostExecute(String result) {
            DecimalFormat df = new DecimalFormat("#.00");
            MasterPlaylist masterPlaylist = new MasterPlaylist(CaptureActivity.this);
            JSONArray jsonArray = null;
            try {
                // convert the string to JSONArray
                jsonArray = new JSONArray(result);
                String emotions = "";

                //store emotion mapped to sum of emotional scores from each person
                Map<String, Double> maxEmotions = new HashMap<>();
                maxEmotions.put("anger", 0.0);
                maxEmotions.put("contempt", 0.0);
                maxEmotions.put("disgust", 0.0);
                maxEmotions.put("fear", 0.0);
                maxEmotions.put("happiness", 0.0);
                maxEmotions.put("neutral", 0.0);
                maxEmotions.put("sadness", 0.0);
                maxEmotions.put("surprise", 0.0);
                // get the scores object from the results

                for(int i = 0; i < jsonArray.length();i++) {
                    Person newFace = new Person();
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    JSONObject scores = jsonObject.getJSONObject("scores");
                    double max = 0;
                    String emotion = "";
                    Emotion newEmotion = new Emotion();
                    for (int j = 0; j < scores.names().length(); j++) {

                        String currEmotion = scores.names().getString(j);
                        maxEmotions.put(currEmotion, maxEmotions.get(currEmotion) + scores.getDouble(currEmotion));

                        String tag = scores.names().getString(j);
                        double strength = scores.getDouble(scores.names().getString(j));
                        switch(tag){
                            case "anger":
                                newEmotion.setAnger(strength);
                                break;
                            case "disgust":
                                newEmotion.setDisgust(strength);
                                break;
                            case "fear":
                                newEmotion.setFear(strength);
                                break;
                            case "happiness":
                                newEmotion.setHappiness(strength);
                                break;
                            case "sadness":
                                newEmotion.setSadness(strength);
                                break;
                        }


                        if (scores.getDouble(scores.names().getString(j)) > max) {
                            max = strength;
                            emotion = tag;
                        }
                    }

                    //emotions += emotion.substring(0,1).toUpperCase() + emotion.substring(1) + " " + String.format( "%.2f", max * 100)  + "% \n";
                    newFace.addEmotion(newEmotion);
                    masterPlaylist.addFace(newFace);
                }

                Map.Entry<String, Double> maxEntry = null;
                for (Map.Entry<String, Double> entry : maxEmotions.entrySet())
                {
                    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                    {
                        maxEntry = entry;
                    }
                }

                final String maxEmotion = maxEntry.getKey();
                emotions += "Overall " + maxEmotion + " " + String.format( "%.2f", (maxEntry.getValue() / 8 * 100)) + "%\n";
                resultText.setText(emotions);

                //pass MasterPlaylist class to next class
                /*Song song = masterPlaylist.getTopSongs().get(0);
                String file = song.getFile();
                System.out.println(song.getSongName());*/


                Intent intent = new Intent(CaptureActivity.this, SongActivity.class);
                intent.putExtra("emotion", maxEmotion);
                System.out.println(maxEmotion);
                startActivity(intent);


            } catch (JSONException e) {
                System.out.println(e);
                resultText.setText("No emotion detected. Try again later");
            }
        }
    }

}