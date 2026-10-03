package com.example.nutrilift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nutrilift.data.local.entity.MealEntity
import com.example.nutrilift.data.local.entity.WorkoutEntity
import com.example.nutrilift.data.repository.MealRepository
import com.example.nutrilift.data.repository.WorkoutRepository
import com.example.nutrilift.domain.model.MealSummary
import com.example.nutrilift.domain.model.WorkoutSummary
import com.example.nutrilift.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HistoryUiState(
    val startDate: String = DateUtils.thirtyDaysAgo(),
    val endDate: String = DateUtils.today(),
    val mealSummary: MealSummary = MealSummary(),
    val workoutSummary: WorkoutSummary = WorkoutSummary(),
    val recentMeals: List<MealEntity> = emptyList(),
    val recentWorkouts: List<WorkoutEntity> = emptyList()
)

class HistoryViewModel(
    mealRepository: MealRepository,
    workoutRepository: WorkoutRepository
) : ViewModel() {
    private val startDate = DateUtils.thirtyDaysAgo()
    private val endDate = DateUtils.today()

    private val summaries = combine(
        mealRepository.observeSummaryBetween(startDate, endDate),
        workoutRepository.observeSummaryBetween(startDate, endDate)
    ) { meals, workouts ->
        meals to workouts
    }

    private val recentEntries = combine(
        mealRepository.observeMeals(),
        workoutRepository.observeWorkouts()
    ) { meals, workouts ->
        meals.take(20) to workouts.take(20)
    }

    val uiState: StateFlow<HistoryUiState> = combine(
        summaries,
        recentEntries
    ) { summaryPair, entriesPair ->
        HistoryUiState(
            startDate = startDate,
            endDate = endDate,
            mealSummary = summaryPair.first,
            workoutSummary = summaryPair.second,
            recentMeals = entriesPair.first,
            recentWorkouts = entriesPair.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState(startDate = startDate, endDate = endDate)
    )

    companion object {
        fun factory(
            mealRepository: MealRepository,
            workoutRepository: WorkoutRepository
        ) = viewModelFactory {
            initializer { HistoryViewModel(mealRepository, workoutRepository) }
        }
    }
}
