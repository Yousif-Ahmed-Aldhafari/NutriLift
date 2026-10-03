package com.example.nutrilift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nutrilift.data.local.entity.ExerciseEntity
import com.example.nutrilift.data.local.entity.WorkoutEntity
import com.example.nutrilift.data.repository.WorkoutRepository
import com.example.nutrilift.domain.model.ExerciseProgressPoint
import com.example.nutrilift.domain.model.WorkoutWithExercises
import com.example.nutrilift.util.DateUtils
import com.example.nutrilift.util.toCleanDouble
import com.example.nutrilift.util.toCleanInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutsViewModel(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    val workouts: StateFlow<List<WorkoutEntity>> = workoutRepository.observeWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deleteWorkout(workout: WorkoutEntity) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(workout)
        }
    }

    companion object {
        fun factory(workoutRepository: WorkoutRepository) = viewModelFactory {
            initializer { WorkoutsViewModel(workoutRepository) }
        }
    }
}

data class WorkoutFormState(
    val id: Long = 0,
    val name: String = "",
    val date: String = DateUtils.today(),
    val time: String = DateUtils.currentTime(),
    val notes: String = "",
    val isEdit: Boolean = false,
    val error: String? = null
)

class WorkoutFormViewModel(
    private val workoutRepository: WorkoutRepository,
    private val workoutId: Long? = null
) : ViewModel() {
    private val _state = MutableStateFlow(WorkoutFormState(isEdit = workoutId != null))
    val state: StateFlow<WorkoutFormState> = _state.asStateFlow()

    init {
        workoutId?.let { id ->
            viewModelScope.launch {
                val workout = workoutRepository.observeWorkout(id).filterNotNull().first()
                _state.value = WorkoutFormState(
                    id = workout.id,
                    name = workout.name,
                    date = workout.date,
                    time = workout.time,
                    notes = workout.notes,
                    isEdit = true
                )
            }
        }
    }

    fun updateName(value: String) = _state.update { it.copy(name = value, error = null) }
    fun updateDate(value: String) = _state.update { it.copy(date = value, error = null) }
    fun updateTime(value: String) = _state.update { it.copy(time = value, error = null) }
    fun updateNotes(value: String) = _state.update { it.copy(notes = value, error = null) }

    fun save(onSaved: (Long) -> Unit) {
        val current = state.value
        when {
            current.name.isBlank() -> {
                _state.update { it.copy(error = "Workout name is required") }
                return
            }
            !DateUtils.isValidDate(current.date) -> {
                _state.update { it.copy(error = "Use date format YYYY-MM-DD") }
                return
            }
            !DateUtils.isValidTime(current.time) -> {
                _state.update { it.copy(error = "Use time format HH:mm") }
                return
            }
        }

        viewModelScope.launch {
            val id = workoutRepository.saveWorkout(
                WorkoutEntity(
                    id = current.id,
                    name = current.name.trim(),
                    date = current.date.trim(),
                    time = current.time.trim(),
                    notes = current.notes.trim()
                )
            )
            onSaved(id)
        }
    }

    companion object {
        fun factory(
            workoutRepository: WorkoutRepository,
            workoutId: Long? = null
        ) = viewModelFactory {
            initializer { WorkoutFormViewModel(workoutRepository, workoutId) }
        }
    }
}

data class WorkoutDetailsUiState(
    val workoutWithExercises: WorkoutWithExercises? = null,
    val progress: List<ExerciseProgressPoint> = emptyList()
)

class WorkoutDetailsViewModel(
    private val workoutRepository: WorkoutRepository,
    workoutId: Long
) : ViewModel() {
    private val selectedExerciseName = MutableStateFlow<String?>(null)

    val uiState: StateFlow<WorkoutDetailsUiState> = workoutRepository.observeWorkoutWithExercises(workoutId)
        .map { workoutWithExercises ->
            WorkoutDetailsUiState(workoutWithExercises = workoutWithExercises)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkoutDetailsUiState())

    @OptIn(ExperimentalCoroutinesApi::class)
    val progress: StateFlow<List<ExerciseProgressPoint>> = selectedExerciseName
        .flatMapLatest { name ->
            if (name.isNullOrBlank()) {
                flowOf(emptyList())
            } else {
                workoutRepository.observeExerciseProgress(name)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectExerciseForProgress(name: String) {
        selectedExerciseName.value = name
    }

    fun deleteExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            workoutRepository.deleteExercise(exercise)
        }
    }

    fun deleteWorkout(workout: WorkoutEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(workout)
            onDeleted()
        }
    }

    companion object {
        fun factory(
            workoutRepository: WorkoutRepository,
            workoutId: Long
        ) = viewModelFactory {
            initializer { WorkoutDetailsViewModel(workoutRepository, workoutId) }
        }
    }
}

data class ExerciseFormState(
    val id: Long = 0,
    val workoutId: Long = 0,
    val name: String = "",
    val sets: String = "",
    val reps: String = "",
    val weight: String = "",
    val duration: String = "",
    val notes: String = "",
    val isEdit: Boolean = false,
    val error: String? = null
)

class ExerciseFormViewModel(
    private val workoutRepository: WorkoutRepository,
    private val workoutId: Long,
    private val exerciseId: Long? = null
) : ViewModel() {
    private val _state = MutableStateFlow(
        ExerciseFormState(workoutId = workoutId, isEdit = exerciseId != null)
    )
    val state: StateFlow<ExerciseFormState> = _state.asStateFlow()

    init {
        exerciseId?.let { id ->
            viewModelScope.launch {
                val exercise = workoutRepository.observeExercise(id).filterNotNull().first()
                _state.value = ExerciseFormState(
                    id = exercise.id,
                    workoutId = exercise.workoutId,
                    name = exercise.name,
                    sets = exercise.sets.toString(),
                    reps = exercise.reps.toString(),
                    weight = exercise.weight.toString(),
                    duration = exercise.duration.toString(),
                    notes = exercise.notes,
                    isEdit = true
                )
            }
        }
    }

    fun updateName(value: String) = _state.update { it.copy(name = value, error = null) }
    fun updateSets(value: String) = _state.update { it.copy(sets = value, error = null) }
    fun updateReps(value: String) = _state.update { it.copy(reps = value, error = null) }
    fun updateWeight(value: String) = _state.update { it.copy(weight = value, error = null) }
    fun updateDuration(value: String) = _state.update { it.copy(duration = value, error = null) }
    fun updateNotes(value: String) = _state.update { it.copy(notes = value, error = null) }

    fun save(onSaved: () -> Unit) {
        val current = state.value
        if (current.name.isBlank()) {
            _state.update { it.copy(error = "Exercise name is required") }
            return
        }

        viewModelScope.launch {
            workoutRepository.saveExercise(
                ExerciseEntity(
                    id = current.id,
                    workoutId = current.workoutId,
                    name = current.name.trim(),
                    sets = current.sets.toCleanInt(),
                    reps = current.reps.toCleanInt(),
                    weight = current.weight.toCleanDouble(),
                    duration = current.duration.toCleanDouble(),
                    notes = current.notes.trim()
                )
            )
            onSaved()
        }
    }

    companion object {
        fun factory(
            workoutRepository: WorkoutRepository,
            workoutId: Long,
            exerciseId: Long? = null
        ) = viewModelFactory {
            initializer { ExerciseFormViewModel(workoutRepository, workoutId, exerciseId) }
        }
    }
}
