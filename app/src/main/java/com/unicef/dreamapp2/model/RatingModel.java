package com.unicef.dreamapp2.model;

import com.google.firebase.database.Exclude;

/**
* @author Iman Augustine
 *
 * Rating model.
 *
 * */

public class RatingModel {

      private String volunteerName; // Volunteer name
      private int likes; // Number of likes

      // Constructor
      public RatingModel() {}

      public RatingModel(String name, int likes) {
          this.volunteerName = name;
          this.likes = likes;
      }

      public String getVolunteerName() {
          return volunteerName;
      }

      public void setVolunteerName(String volunteerName) {
          this.volunteerName = volunteerName;
      }

      public int getLikes() {
          return likes;
      }

      public void setLikes(int likes) {
          this.likes = likes;
      }
}
