package com.amoharib.bakingapp.util;

import com.amoharib.bakingapp.model.Ingredient;

import java.util.List;

public class StringUtils {
    public static String concatString(List<Ingredient> ingredients) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            stringBuilder.append("\u2022 ")
                    .append(ingredient.getQuantity())
                    .append(" ").append(ingredient.getIngredient())
                    .append(" ").append(ingredient.getMeasure())
                    .append("\n");
        }
        return stringBuilder.toString();
    }
}
