package com.example.canteenchecker.canteenmanager.domainobjects;

public class CanteenRating {

    private int ratingId;
    private int ratingPoints;
    private String username;
    private long timestamp;
    private String remark;

    public CanteenRating(int ratingId, int ratingPoints, String username, long timestamp, String remark) {
        this.ratingId = ratingId;
        this.ratingPoints = ratingPoints;
        this.username = username;
        this.timestamp = timestamp;
        this.remark = remark;
    }

    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public int getRatingPoints() {
        return ratingPoints;
    }

    public void setRatingPoints(int ratingPoints) {
        this.ratingPoints = ratingPoints;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
