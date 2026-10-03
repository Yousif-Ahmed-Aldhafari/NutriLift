package com.example.nutrilift.domain.model

import com.example.nutrilift.data.local.entity.ExerciseEntity
import com.example.nutrilift.data.local.entity.WorkoutEntity

data class WorkoutWithExercises(
    val workout: WorkoutEntity,
    val exercises: List<ExerciseEntity>
)

data class WorkoutSummary(
    val workoutCount: Int = 0,
    val exerciseCount: Int = 0,
    val exerciseNames: List<String> = emptyList()
)

data class ExerciseProgressPoint(
    val date: String,
    val time: String,
    val exerciseName: String,
    val weight: Double,
    val reps: Int
)
