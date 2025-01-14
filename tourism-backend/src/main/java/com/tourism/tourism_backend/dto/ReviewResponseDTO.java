package com.tourism.tourism_backend.dto;

import java.time.LocalDateTime;

public class ReviewResponseDTO {
    private String user;
    private int rating;
    private String comment;
    private LocalDateTime date;

    public ReviewResponseDTO(String user, int rating, String comment, LocalDateTime date) {
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    // Getters and Setters

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}


