package com.example.canteenchecker.canteenmanager.domainobjects;

import java.util.Collection;

public class Canteen {
    private int canteenId;
    private String name;
    private String phone;
    private String website;
    private String meal;
    private float mealPrice;
    private float averageRating;
    private int averageWaitingTime;
    private String address;
    private Collection<CanteenRating> ratings;

    public Canteen(int canteenId, String name, String phone, String website, String meal, float mealPrice, float averageRating, int averageWaitingTime, String address, Collection<CanteenRating> ratings) {
        this.canteenId = canteenId;
        this.name = name;
        this.phone = phone;
        this.website = website;
        this.meal = meal;
        this.mealPrice = mealPrice;
        this.averageRating = averageRating;
        this.averageWaitingTime = averageWaitingTime;
        this.address = address;
        this.ratings = ratings;
    }

    public int getCanteenId() {
        return canteenId;
    }

    public void setCanteenId(int canteenId) {
        this.canteenId = canteenId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public float getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(float mealPrice) {
        this.mealPrice = mealPrice;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public int getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public void setAverageWaitingTime(int averageWaitingTime) {
        this.averageWaitingTime = averageWaitingTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Collection<CanteenRating> getRatings() {
        return ratings;
    }

    public void setRatings(Collection<CanteenRating> ratings) {
        this.ratings = ratings;
    }
}
