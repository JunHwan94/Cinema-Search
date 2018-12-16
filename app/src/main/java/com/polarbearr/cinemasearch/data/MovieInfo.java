package com.polarbearr.cinemasearch.data;

public class MovieInfo{
    String title;
    String link;
    String image;
    String pubDate;
    String director;
    String actor;
    float userRating;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getImageUrl() {
        return image;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDirector() {
        return director;
    }

    public String getActor() {
        return actor;
    }

    public float getUserRating() {
        return userRating;
    }
}
