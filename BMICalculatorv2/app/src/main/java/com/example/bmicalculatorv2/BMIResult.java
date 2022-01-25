package com.example.bmicalculatorv2;

import static java.lang.Double.parseDouble;

public class BMIResult {
    private int id;
    private String weight,  height, datetime;
    public BMIResult(int id, String weight, String height, String datetime){
        this.id = id;
        this.weight = weight;
        this.height = height;
        this.datetime = datetime;
    }

    public BMIResult(String weight, String height){
        id = -1;
        datetime = "--";
        this.weight = weight;
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height){
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight){
        this.weight = weight;
    }

    public String getDateTime() {
        return datetime;
    }

    public String getBmi() {
        String bmiString;
        double weightD, heightD, bmiD;
        weightD = Double.parseDouble(weight);
        heightD = Double.parseDouble(height);

        heightD /= 100;

        bmiD = weightD / (heightD * heightD);
        bmiD = Math.round(bmiD * 10.0) / 10.0; // Force one decimal place

        bmiString = Double.toString(bmiD);
        return bmiString;
    }

    public String getCategory() {
        if(Double.parseDouble(getBmi()) < 18.5){
            return "Underweight";
        }else if(Double.parseDouble(getBmi()) < 25){
            return "Normal";
        }else if(Double.parseDouble(getBmi()) < 30) {
            return "Overweight";
        }else if(Double.parseDouble(getBmi()) < 35){
            return "Moderately obese";
        }else if(Double.parseDouble(getBmi()) < 40){
            return "Severely obese";
        }else{
            return "Very severely obese";
        }
    }

    public String getRange(){
        if(Double.parseDouble(getBmi()) < 18.5){
            return "18.4 and below";
        }else if(Double.parseDouble(getBmi()) < 25){
            return  "18.5 - 24.9";
        }else if(Double.parseDouble(getBmi()) < 30) {
            return  "25 - 29.9";
        }else if(Double.parseDouble(getBmi()) < 35){
            return  "30 - 34.9";
        }else if(Double.parseDouble(getBmi()) < 40){
            return  "35 - 39.9";
        }else{
            return "40 and above";
        }
    }

    public String getRisk(){
        if(Double.parseDouble(getBmi()) < 18.5){
            return "Malnutrition risk";
        }else if(Double.parseDouble(getBmi()) < 25){
            return "Low risk";
        }else if(Double.parseDouble(getBmi()) < 30) {
            return "Enhanced risk";
        }else if(Double.parseDouble(getBmi()) < 35){
            return "Medium risk";
        }else if(Double.parseDouble(getBmi()) < 40){
            return "High risk";
        }else{
            return "Very high risk";
        }
    }

}
