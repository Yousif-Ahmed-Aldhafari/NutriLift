package com.example.nutrilift.ui.navigation

object Routes {
    const val Dashboard = "dashboard"
    const val Meals = "meals"
    const val AddMeal = "meals/add"
    const val EditMeal = "meals/edit/{mealId}"
    const val MealDetails = "meals/{mealId}"
    const val AddFoodItem = "meals/{mealId}/food/add"
    const val EditFoodItem = "meals/{mealId}/food/{foodItemId}/edit"

    const val Workouts = "workouts"
    const val AddWorkout = "workouts/add"
    const val EditWorkout = "workouts/edit/{workoutId}"
    const val WorkoutDetails = "workouts/{workoutId}"
    const val AddExercise = "workouts/{workoutId}/exercise/add"
    const val EditExercise = "workouts/{workoutId}/exercise/{exerciseId}/edit"

    const val History = "history"
    const val Settings = "settings"

    fun mealDetails(mealId: Long) = "meals/$mealId"
    fun editMeal(mealId: Long) = "meals/edit/$mealId"
    fun addFoodItem(mealId: Long) = "meals/$mealId/food/add"
    fun editFoodItem(mealId: Long, foodItemId: Long) = "meals/$mealId/food/$foodItemId/edit"

    fun workoutDetails(workoutId: Long) = "workouts/$workoutId"
    fun editWorkout(workoutId: Long) = "workouts/edit/$workoutId"
    fun addExercise(workoutId: Long) = "workouts/$workoutId/exercise/add"
    fun editExercise(workoutId: Long, exerciseId: Long) = "workouts/$workoutId/exercise/$exerciseId/edit"
}
