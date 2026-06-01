package com.fooddelivery.model;

public class Review {
    private String reviewId;
    private String customerId;
    private int rating;
    private String comment;

    public Review(String reviewId, String customerId, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be between 1 and 5");
        this.reviewId = reviewId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getReviewId() { return reviewId; }
    public String getCustomerId() { return customerId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public void setRating(int rating) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be between 1 and 5");
        this.rating = rating;
    }
    public void setComment(String comment) { this.comment = comment; }

    @Override
    public String toString() {
        return "Review{customerId='" + customerId + "', rating=" + rating + ", comment='" + comment + "'}";
    }
}
