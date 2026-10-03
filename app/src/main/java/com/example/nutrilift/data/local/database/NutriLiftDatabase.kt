package com.example.nutrilift.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nutrilift.data.local.dao.MealDao
import com.example.nutrilift.data.local.dao.WorkoutDao
import com.example.nutrilift.data.local.entity.ExerciseEntity
import com.example.nutrilift.data.local.entity.FoodItemEntity
import com.example.nutrilift.data.local.entity.MealEntity
import com.example.nutrilift.data.local.entity.WorkoutEntity

@Database(
    entities = [
        MealEntity::class,
        FoodItemEntity::class,
        WorkoutEntity::class,
        ExerciseEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class NutriLiftDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        fun create(context: Context): NutriLiftDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                NutriLiftDatabase::class.java,
                "nutrilift.db"
            ).build()
        }
    }
}
