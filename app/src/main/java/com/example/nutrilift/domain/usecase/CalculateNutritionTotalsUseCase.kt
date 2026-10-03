package com.example.nutrilift.domain.usecase

import com.example.nutrilift.data.local.entity.FoodItemEntity
import com.example.nutrilift.domain.model.NutritionTotals
import com.example.nutrilift.domain.model.toNutritionTotals

class CalculateNutritionTotalsUseCase {
    operator fun invoke(foodItems: List<FoodItemEntity>): NutritionTotals {
        return foodItems.toNutritionTotals()
    }
}
