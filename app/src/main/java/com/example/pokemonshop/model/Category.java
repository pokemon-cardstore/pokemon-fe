package com.example.pokemonshop.model;

import java.io.Serializable;

public class Category implements Serializable {
    private int categoryId;

    private String name;

    private String description;

    private int status;

    public Category() {

    }
    public Category(int categoryId, String name, String description, int status) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
