package com.example.nutrilift.domain.model

import com.example.nutrilift.data.local.entity.FoodItemEntity
import com.example.nutrilift.data.local.entity.MealEntity

data class MealWithFoodItems(
    val meal: MealEntity,
    val foodItems: List<FoodItemEntity>
) {
    val totals: NutritionTotals = foodItems.toNutritionTotals()
}

data class MealSummary(
    val mealCount: Int = 0,
    val totals: NutritionTotals = NutritionTotals.Zero
)

fun List<FoodItemEntity>.toNutritionTotals(): NutritionTotals {
    return fold(NutritionTotals.Zero) { total, item ->
        total + NutritionTotals(
            calories = item.calories,
            protein = item.protein,
            carbs = item.carbs,
            fats = item.fats
        )
    }
}
