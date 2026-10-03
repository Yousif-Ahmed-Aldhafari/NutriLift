package com.example.nutrilift.data.repository

import com.example.nutrilift.data.local.dao.MealDao
import com.example.nutrilift.data.local.entity.FoodItemEntity
import com.example.nutrilift.data.local.entity.MealEntity
import com.example.nutrilift.domain.model.MealSummary
import com.example.nutrilift.domain.model.MealWithFoodItems
import com.example.nutrilift.domain.model.toNutritionTotals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class MealRepository(
    private val mealDao: MealDao
) {
    fun observeMeals(): Flow<List<MealEntity>> = mealDao.observeMeals()

    fun observeMealsByDate(date: String): Flow<List<MealEntity>> {
        return mealDao.observeMealsByDate(date)
    }

    fun observeMeal(id: Long): Flow<MealEntity?> = mealDao.observeMeal(id)

    fun observeFoodItem(id: Long): Flow<FoodItemEntity?> {
        return mealDao.observeFoodItem(id)
    }

    fun observeFoodItems(mealId: Long): Flow<List<FoodItemEntity>> {
        return mealDao.observeFoodItemsForMeal(mealId)
    }

    fun observeMealWithFoodItems(mealId: Long): Flow<MealWithFoodItems?> {
        return combine(
            mealDao.observeMeal(mealId),
            mealDao.observeFoodItemsForMeal(mealId)
        ) { meal, foodItems ->
            meal?.let { MealWithFoodItems(it, foodItems) }
        }
    }

    fun observeDailySummary(date: String): Flow<MealSummary> {
        return combine(
            mealDao.observeMealsByDate(date),
            mealDao.observeFoodItemsForDate(date)
        ) { meals, foodItems ->
            MealSummary(mealCount = meals.size, totals = foodItems.toNutritionTotals())
        }
    }

    fun observeSummaryBetween(startDate: String, endDate: String): Flow<MealSummary> {
        return combine(
            mealDao.observeMealsBetween(startDate, endDate),
            mealDao.observeFoodItemsBetween(startDate, endDate)
        ) { meals, foodItems ->
            MealSummary(mealCount = meals.size, totals = foodItems.toNutritionTotals())
        }
    }

    suspend fun saveMeal(meal: MealEntity): Long {
        return if (meal.id == 0L) {
            mealDao.insertMeal(meal)
        } else {
            mealDao.updateMeal(meal)
            meal.id
        }
    }

    suspend fun deleteMeal(meal: MealEntity) {
        mealDao.deleteMeal(meal)
    }

    suspend fun deleteMealById(mealId: Long) {
        mealDao.deleteMealById(mealId)
    }

    suspend fun saveFoodItem(foodItem: FoodItemEntity): Long {
        return if (foodItem.id == 0L) {
            mealDao.insertFoodItem(foodItem)
        } else {
            mealDao.updateFoodItem(foodItem)
            foodItem.id
        }
    }

    suspend fun deleteFoodItem(foodItem: FoodItemEntity) {
        mealDao.deleteFoodItem(foodItem)
    }

    suspend fun deleteFoodItemById(foodItemId: Long) {
        mealDao.deleteFoodItemById(foodItemId)
    }
}
