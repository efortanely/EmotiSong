package com.inturnes.emotisong;

import java.io.Serializable;

public class Emotion implements Serializable {
    private double disgust, fear, sadness, anger, happiness;

    public Emotion() {
    }

    public Emotion(double disgust, double fear, double sadness, double anger, double happiness) {
        this.disgust = disgust;
        this.fear = fear;
        this.sadness = sadness;
        this.anger = anger;
        this.happiness = happiness;
    }

    //find the average emotion vector
    public static Emotion averageEmotion(Emotion... emotions) {
        double[] feelings = new double[5];
        int numFaces = emotions.length;

        for (Emotion emotion : emotions) {
            feelings[0] += emotion.disgust;
            feelings[1] += emotion.fear;
            feelings[2] += emotion.sadness;
            feelings[3] += emotion.anger;
            feelings[4] += emotion.happiness;
        }

        return new Emotion(feelings[0] / numFaces, feelings[1] / numFaces, feelings[2] / numFaces, feelings[3] / numFaces, feelings[4] / numFaces);
    }

    public void setDisgust(double disgust) {
        this.disgust = disgust;
    }

    public void setFear(double fear) {
        this.fear = fear;
    }

    public void setSadness(double sadness) {
        this.sadness = sadness;
    }

    public void setAnger(double anger) {
        this.anger = anger;
    }

    public void setHappiness(double happiness) {
        this.happiness = happiness;
    }

    //returns the dot product between two emotions
    //a larger number corresponds to a stronger fit between the 2 emotion vectors
    //a smaller number corresponds to a worse match
    public double compatibility(Emotion other) {
        return this.disgust * other.disgust + this.fear * other.fear +
                this.sadness * other.sadness + this.anger * other.anger + this.happiness * other.happiness;
    }

    public double getVal(String emotion) {
        if (emotion.equalsIgnoreCase("fear")) {
            return fear;
        } else if (emotion.equalsIgnoreCase("disgust")) {
            return disgust;
        } else if (emotion.equalsIgnoreCase("anger")) {
            return anger;
        } else if (emotion.equalsIgnoreCase("happy")) {
            return happiness;
        } else if (emotion.equalsIgnoreCase("sad")) {
            return sadness;
        }
        return 0;
    }

    //returns the string associated with the strongest emotion
    public String getEmotionConveyed() {
        double maxVal = disgust;
        String maxEmotion = "disgusted";
        if (fear > maxVal)
            maxEmotion = "fearful";
        if (sadness > maxVal)
            maxEmotion = "sad";
        if (anger > maxVal)
            maxEmotion = "angry";
        if (happiness > maxVal)
            maxEmotion = "happy";
        return maxEmotion;
    }

    @Override
    public String toString() {
        StringBuilder emotion = new StringBuilder();
        emotion.append("Disgust " + disgust);
        emotion.append("\nFear " + fear);
        emotion.append("\nSadness " + sadness);
        emotion.append("\nAnger " + anger);
        emotion.append("\nHappiness " + happiness + "\n");
        return emotion.toString();
    }
}
