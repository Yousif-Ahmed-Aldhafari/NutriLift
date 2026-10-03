package com.example.nutrilift

import android.app.Application
import com.example.nutrilift.data.local.database.NutriLiftDatabase
import com.example.nutrilift.data.repository.MealRepository
import com.example.nutrilift.data.repository.WorkoutRepository

class NutriLiftApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(application: Application) {
    private val database = NutriLiftDatabase.create(application)

    val mealRepository = MealRepository(database.mealDao())
    val workoutRepository = WorkoutRepository(database.workoutDao())
}
