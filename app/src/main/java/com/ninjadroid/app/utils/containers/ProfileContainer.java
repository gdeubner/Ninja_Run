package com.ninjadroid.app.utils.containers;

import java.io.Serializable;

public class ProfileContainer implements Serializable {
   private int user_id;
   private String username;
   private String password;
   private double weight;
   private int height_ft;
   private double height_in;
   private int points;
   private double calories;
   private double distance;
   private String Name;
   private int isAdmin;

   public int getUserId() {
      return user_id;
   }

   public String getUsername() {
      return username;
   }

   public String getPassword() {
      return password;
   }

   public double getWeight() {
      return weight;
   }

   public int getHeight_ft() {
      return height_ft;
   }

   public double getHeight_in() {
      return height_in;
   }

   public int getPoints() {
      return points;
   }

   public double getCalories() {
      return calories;
   }

   public double getDistance() {
      return distance;
   }

   public String getName() {
      return Name;
   }

   public int getIsAdmin() {
      return isAdmin;
   }
}