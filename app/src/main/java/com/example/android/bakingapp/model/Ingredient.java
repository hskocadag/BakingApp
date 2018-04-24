package com.example.android.bakingapp.model;

public class Ingredient {

    private float quantity;
    private String measure;
    private String ingredient;

    public Ingredient(float quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public Ingredient() {
    }

    public float getQuantity() {
        return quantity;
    }

    public String getQuantityStr()
    {
        if(quantity % 1 == 0){
            return Integer.toString((int)quantity);
        }
        return Float.toString(quantity);
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredientName() {
        return ingredient;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredient = ingredientName;
    }
}
