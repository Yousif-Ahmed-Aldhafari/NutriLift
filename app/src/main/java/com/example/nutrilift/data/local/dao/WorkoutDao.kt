package com.example.nutrilift.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nutrilift.data.local.entity.ExerciseEntity
import com.example.nutrilift.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY date DESC, time DESC, id DESC")
    fun observeWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE date = :date ORDER BY time DESC, id DESC")
    fun observeWorkoutsByDate(date: String): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, time DESC, id DESC")
    fun observeWorkoutsBetween(startDate: String, endDate: String): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun observeWorkout(id: Long): Flow<WorkoutEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    @Query("DELETE FROM workouts WHERE id = :workoutId")
    suspend fun deleteWorkoutById(workoutId: Long)

    @Query("SELECT * FROM exercises ORDER BY name ASC, id DESC")
    fun observeExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY id ASC")
    fun observeExercisesForWorkout(workoutId: Long): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun observeExercise(id: Long): Flow<ExerciseEntity?>

    @Query(
        """
        SELECT exercises.* FROM exercises
        INNER JOIN workouts ON workouts.id = exercises.workoutId
        WHERE workouts.date = :date
        ORDER BY workouts.time DESC, exercises.id ASC
        """
    )
    fun observeExercisesForDate(date: String): Flow<List<ExerciseEntity>>

    @Query(
        """
        SELECT exercises.* FROM exercises
        INNER JOIN workouts ON workouts.id = exercises.workoutId
        WHERE workouts.date BETWEEN :startDate AND :endDate
        ORDER BY workouts.date DESC, workouts.time DESC, exercises.id ASC
        """
    )
    fun observeExercisesBetween(startDate: String, endDate: String): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Query("DELETE FROM exercises WHERE id = :exerciseId")
    suspend fun deleteExerciseById(exerciseId: Long)
}
