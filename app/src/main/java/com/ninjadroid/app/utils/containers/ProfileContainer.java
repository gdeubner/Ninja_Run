package com.ninjadroid.app.utils.containers;

import java.io.Serializable;

public class ProfileContainer implements Serializable {
    String userId;
    String username;
    String password;
    String name;
    String weight;
    String heightft;
    String heightin;
    String points;
    String calories;
    String distance;
    String isAdmin;

    public ProfileContainer(){
        this.userId = "";
    }

    public ProfileContainer(String userId,String username, String password, String weight,String heightft,
            String heightin, String points, String calories, String distance, String name, String isAdmin){
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.weight = weight;
        this.heightft = heightft;
        this.heightin = heightin;
        this.points = points;
        this.calories =calories;
        this.distance =distance;
        this.name = name;
        this.isAdmin =isAdmin;
    }

    public void setUserId(String uid){
        this.userId = uid;
    }

    public void setname(String name){
        this.name = name;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setWeight(String weight){
        this.weight = weight;
    }

    public void setHeightft(String heightft){
        this.heightft = heightft;
    }

    public void setHeightin(String heightin){
        this.heightin = heightin;
    }

    public void setPoints(String points){
        this.points = points;
    }

    public void setCalories(String calories){
        this.calories = calories;
    }

    public void setDistance(String distance){
        this.distance = distance;
    }

    public void setIsAdmin(String isAdmin){
        this.isAdmin = isAdmin;
    }


    public String getUserId(){
        return userId;
    }

    public String getName(){
        return name;
    }

    public String getUsername(){
        return this.username;
    }

    public String getWeight(){
        return weight;
    }

    public String getHeightft(){
        return heightft;
    }

    public String getHeightin(){
        return heightin;
    }

    public String getPoints(){
        return points;
    }

    public String getCalories(){
        return calories;
    }

    public String getDistance(){
        return distance;
    }

    public String getIsAdmin(){
        return isAdmin;
    }

}
