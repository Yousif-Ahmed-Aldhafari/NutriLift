package com.example.nutrilift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nutrilift.data.repository.MealRepository
import com.example.nutrilift.data.repository.WorkoutRepository
import com.example.nutrilift.domain.model.MealSummary
import com.example.nutrilift.domain.model.WorkoutSummary
import com.example.nutrilift.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val today: String = DateUtils.today(),
    val dailyMealSummary: MealSummary = MealSummary(),
    val dailyWorkoutSummary: WorkoutSummary = WorkoutSummary(),
    val weeklyMealSummary: MealSummary = MealSummary(),
    val monthlyMealSummary: MealSummary = MealSummary(),
    val weeklyWorkoutSummary: WorkoutSummary = WorkoutSummary(),
    val monthlyWorkoutSummary: WorkoutSummary = WorkoutSummary()
)

private data class MealDashboardSummaries(
    val daily: MealSummary,
    val weekly: MealSummary,
    val monthly: MealSummary
)

private data class WorkoutDashboardSummaries(
    val daily: WorkoutSummary,
    val weekly: WorkoutSummary,
    val monthly: WorkoutSummary
)

class DashboardViewModel(
    mealRepository: MealRepository,
    workoutRepository: WorkoutRepository
) : ViewModel() {
    private val today = DateUtils.today()
    private val weekStart = DateUtils.weekStart()
    private val monthStart = DateUtils.monthStart()

    private val mealSummaries = combine(
        mealRepository.observeDailySummary(today),
        mealRepository.observeSummaryBetween(weekStart, today),
        mealRepository.observeSummaryBetween(monthStart, today)
    ) { daily, weekly, monthly ->
        MealDashboardSummaries(daily, weekly, monthly)
    }

    private val workoutSummaries = combine(
        workoutRepository.observeDailySummary(today),
        workoutRepository.observeSummaryBetween(weekStart, today),
        workoutRepository.observeSummaryBetween(monthStart, today)
    ) { daily, weekly, monthly ->
        WorkoutDashboardSummaries(daily, weekly, monthly)
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        mealSummaries,
        workoutSummaries
    ) { meals, workouts ->
        DashboardUiState(
            today = today,
            dailyMealSummary = meals.daily,
            dailyWorkoutSummary = workouts.daily,
            weeklyMealSummary = meals.weekly,
            monthlyMealSummary = meals.monthly,
            weeklyWorkoutSummary = workouts.weekly,
            monthlyWorkoutSummary = workouts.monthly
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState(today = today)
    )

    companion object {
        fun factory(
            mealRepository: MealRepository,
            workoutRepository: WorkoutRepository
        ) = viewModelFactory {
            initializer { DashboardViewModel(mealRepository, workoutRepository) }
        }
    }
}
