package com.bubbletrouble.cookingapp.model;

import java.util.ArrayList;

public class FoodCategory {

    public ArrayList<Meal> meals;

    public FoodCategory(ArrayList<Meal> meals) {
        this.meals = meals;
    }

    public ArrayList<Meal> getMeals() {
        return meals;
    }

    public void setMeals(ArrayList<Meal> meals) {
        this.meals = meals;
    }
}
