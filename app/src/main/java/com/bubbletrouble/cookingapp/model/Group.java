package com.bubbletrouble.cookingapp.model;

import java.util.ArrayList;
import java.util.List;

public class Group {

    List<User> members;
    List<FoodCategory>categories;

    public Group(List<User> members, List<FoodCategory> categories) {
        this.members = members;
        this.categories = categories;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }

    public List<FoodCategory> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<FoodCategory> categories) {
        this.categories = categories;
    }
}
