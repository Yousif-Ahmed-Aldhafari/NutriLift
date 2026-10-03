package com.example.nutrilift.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nutrilift.data.local.entity.FoodItemEntity
import com.example.nutrilift.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY date DESC, time DESC, id DESC")
    fun observeMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE date = :date ORDER BY time DESC, id DESC")
    fun observeMealsByDate(date: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, time DESC, id DESC")
    fun observeMealsBetween(startDate: String, endDate: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :id")
    fun observeMeal(id: Long): Flow<MealEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteMealById(mealId: Long)

    @Query("SELECT * FROM food_items WHERE mealId = :mealId ORDER BY id ASC")
    fun observeFoodItemsForMeal(mealId: Long): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE id = :id")
    fun observeFoodItem(id: Long): Flow<FoodItemEntity?>

    @Query(
        """
        SELECT food_items.* FROM food_items
        INNER JOIN meals ON meals.id = food_items.mealId
        WHERE meals.date = :date
        ORDER BY meals.time DESC, food_items.id ASC
        """
    )
    fun observeFoodItemsForDate(date: String): Flow<List<FoodItemEntity>>

    @Query(
        """
        SELECT food_items.* FROM food_items
        INNER JOIN meals ON meals.id = food_items.mealId
        WHERE meals.date BETWEEN :startDate AND :endDate
        ORDER BY meals.date DESC, meals.time DESC, food_items.id ASC
        """
    )
    fun observeFoodItemsBetween(startDate: String, endDate: String): Flow<List<FoodItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(foodItem: FoodItemEntity): Long

    @Update
    suspend fun updateFoodItem(foodItem: FoodItemEntity)

    @Delete
    suspend fun deleteFoodItem(foodItem: FoodItemEntity)

    @Query("DELETE FROM food_items WHERE id = :foodItemId")
    suspend fun deleteFoodItemById(foodItemId: Long)
}
