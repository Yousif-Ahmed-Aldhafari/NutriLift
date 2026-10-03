package com.example.nutrilift.domain.usecase

import com.example.nutrilift.data.local.entity.FoodItemEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateNutritionTotalsUseCaseTest {
    private val useCase = CalculateNutritionTotalsUseCase()

    @Test
    fun sumsFoodItemNutrition() {
        val totals = useCase(
            listOf(
                FoodItemEntity(
                    mealId = 1,
                    name = "Rice",
                    quantity = 150.0,
                    unit = "gram",
                    calories = 190.0,
                    protein = 4.0,
                    carbs = 42.0,
                    fats = 0.5
                ),
                FoodItemEntity(
                    mealId = 1,
                    name = "Chicken",
                    quantity = 120.0,
                    unit = "gram",
                    calories = 210.0,
                    protein = 36.0,
                    carbs = 0.0,
                    fats = 6.0
                )
            )
        )

        assertEquals(400.0, totals.calories, 0.001)
        assertEquals(40.0, totals.protein, 0.001)
        assertEquals(42.0, totals.carbs, 0.001)
        assertEquals(6.5, totals.fats, 0.001)
    }
}
