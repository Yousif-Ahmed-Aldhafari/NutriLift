package com.example.nutrilift.data.repository

import com.example.nutrilift.data.local.dao.WorkoutDao
import com.example.nutrilift.data.local.entity.ExerciseEntity
import com.example.nutrilift.data.local.entity.WorkoutEntity
import com.example.nutrilift.domain.model.ExerciseProgressPoint
import com.example.nutrilift.domain.model.WorkoutSummary
import com.example.nutrilift.domain.model.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class WorkoutRepository(
    private val workoutDao: WorkoutDao
) {
    fun observeWorkouts(): Flow<List<WorkoutEntity>> = workoutDao.observeWorkouts()

    fun observeWorkoutsByDate(date: String): Flow<List<WorkoutEntity>> {
        return workoutDao.observeWorkoutsByDate(date)
    }

    fun observeWorkout(id: Long): Flow<WorkoutEntity?> = workoutDao.observeWorkout(id)

    fun observeExercise(id: Long): Flow<ExerciseEntity?> = workoutDao.observeExercise(id)

    fun observeExercises(workoutId: Long): Flow<List<ExerciseEntity>> {
        return workoutDao.observeExercisesForWorkout(workoutId)
    }

    fun observeWorkoutWithExercises(workoutId: Long): Flow<WorkoutWithExercises?> {
        return combine(
            workoutDao.observeWorkout(workoutId),
            workoutDao.observeExercisesForWorkout(workoutId)
        ) { workout, exercises ->
            workout?.let { WorkoutWithExercises(it, exercises) }
        }
    }

    fun observeDailySummary(date: String): Flow<WorkoutSummary> {
        return combine(
            workoutDao.observeWorkoutsByDate(date),
            workoutDao.observeExercisesForDate(date)
        ) { workouts, exercises ->
            exercises.toWorkoutSummary(workoutCount = workouts.size)
        }
    }

    fun observeSummaryBetween(startDate: String, endDate: String): Flow<WorkoutSummary> {
        return combine(
            workoutDao.observeWorkoutsBetween(startDate, endDate),
            workoutDao.observeExercisesBetween(startDate, endDate)
        ) { workouts, exercises ->
            exercises.toWorkoutSummary(workoutCount = workouts.size)
        }
    }

    fun observeExerciseProgress(exerciseName: String): Flow<List<ExerciseProgressPoint>> {
        return combine(
            workoutDao.observeWorkouts(),
            workoutDao.observeExercises()
        ) { workouts, exercises ->
            val workoutsById = workouts.associateBy { it.id }
            exercises
                .filter { it.name.equals(exerciseName, ignoreCase = true) }
                .mapNotNull { exercise ->
                    val workout = workoutsById[exercise.workoutId] ?: return@mapNotNull null
                    ExerciseProgressPoint(
                        date = workout.date,
                        time = workout.time,
                        exerciseName = exercise.name,
                        weight = exercise.weight,
                        reps = exercise.reps
                    )
                }
                .sortedWith(compareBy<ExerciseProgressPoint> { it.date }.thenBy { it.time })
        }
    }

    suspend fun saveWorkout(workout: WorkoutEntity): Long {
        return if (workout.id == 0L) {
            workoutDao.insertWorkout(workout)
        } else {
            workoutDao.updateWorkout(workout)
            workout.id
        }
    }

    suspend fun deleteWorkout(workout: WorkoutEntity) {
        workoutDao.deleteWorkout(workout)
    }

    suspend fun deleteWorkoutById(workoutId: Long) {
        workoutDao.deleteWorkoutById(workoutId)
    }

    suspend fun saveExercise(exercise: ExerciseEntity): Long {
        return if (exercise.id == 0L) {
            workoutDao.insertExercise(exercise)
        } else {
            workoutDao.updateExercise(exercise)
            exercise.id
        }
    }

    suspend fun deleteExercise(exercise: ExerciseEntity) {
        workoutDao.deleteExercise(exercise)
    }

    suspend fun deleteExerciseById(exerciseId: Long) {
        workoutDao.deleteExerciseById(exerciseId)
    }
}

private fun List<ExerciseEntity>.toWorkoutSummary(workoutCount: Int): WorkoutSummary {
    return WorkoutSummary(
        workoutCount = workoutCount,
        exerciseCount = size,
        exerciseNames = map { it.name }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    )
}
