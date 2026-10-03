package com.example.nutrilift.domain.model

data class NutritionTotals(
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fats: Double = 0.0
) {
    operator fun plus(other: NutritionTotals): NutritionTotals {
        return NutritionTotals(
            calories = calories + other.calories,
            protein = protein + other.protein,
            carbs = carbs + other.carbs,
            fats = fats + other.fats
        )
    }

    companion object {
        val Zero = NutritionTotals()
    }
}
