package com.inturnes.emotisong;

public class Emotion {
    //TODO from microsoft- "digust", "fear", "sadness", "anger", "happiness"
    //TODO from ibm- "digust", "fear", "sadness", "anger", "joy"
    private double disgust;
    private double fear;
    private double sadness;
    private double anger;
    private double happiness;

    public Emotion(){
    }

    public Emotion(double disgust, double fear, double sadness, double anger, double happiness){
        this.disgust = disgust;
        this.fear = fear;
        this.sadness = sadness;
        this.anger = anger;
        this.happiness = happiness;
    }

    public void setDisgust(double disgust){
        this.disgust = disgust;
    }

    public void setFear(double fear){
        this.fear = fear;
    }

    public void setSadness(double sadness){
        this.sadness = sadness;
    }

    public void setAnger(double anger){
        this.anger = anger;
    }

    public void setHappiness(double happiness){
        this.happiness = happiness;
    }

    public double compatibility(Emotion other){
        return this.disgust * other.disgust + this.fear * other.fear +
                this.sadness * other.sadness + this.anger * other.anger + this.happiness * other.happiness;
    }
}
