package com.example.nutrilift.ui.workouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutrilift.data.local.entity.ExerciseEntity
import com.example.nutrilift.domain.model.ExerciseProgressPoint
import com.example.nutrilift.ui.components.EmptyState
import com.example.nutrilift.ui.components.SectionTitle
import com.example.nutrilift.util.DateUtils
import com.example.nutrilift.util.formatAmount
import com.example.nutrilift.viewmodel.WorkoutDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    viewModel: WorkoutDetailsViewModel,
    onBack: () -> Unit,
    onEditWorkout: (Long) -> Unit,
    onAddExercise: (Long) -> Unit,
    onEditExercise: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val workoutWithExercises = state.workoutWithExercises
    val workout = workoutWithExercises?.workout

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.name ?: "Workout details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    workout?.let {
                        IconButton(onClick = { onEditWorkout(it.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit workout")
                        }
                        IconButton(onClick = { viewModel.deleteWorkout(it, onBack) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete workout")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            workout?.let {
                FloatingActionButton(onClick = { onAddExercise(it.id) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add exercise")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                end = 16.dp,
                bottom = padding.calculateBottomPadding() + 88.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (workoutWithExercises == null) {
                item { EmptyState("Workout not found") }
            } else {
                item {
                    Text(
                        text = "${DateUtils.displayDate(workoutWithExercises.workout.date)} · ${workoutWithExercises.workout.time}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (workoutWithExercises.workout.notes.isNotBlank()) {
                        Text(
                            text = workoutWithExercises.workout.notes,
                            modifier = Modifier.padding(top = 4.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item { SectionTitle("Exercises") }

                if (workoutWithExercises.exercises.isEmpty()) {
                    item { EmptyState("No exercises in this workout") }
                } else {
                    items(workoutWithExercises.exercises, key = { it.id }) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onEditExercise = onEditExercise,
                            onDeleteExercise = viewModel::deleteExercise,
                            onShowProgress = viewModel::selectExerciseForProgress
                        )
                    }
                }

                if (progress.isNotEmpty()) {
                    item { SectionTitle("Progress") }
                    items(progress.takeLast(6)) { point ->
                        ProgressCard(point)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseEntity,
    onEditExercise: (Long) -> Unit,
    onDeleteExercise: (ExerciseEntity) -> Unit,
    onShowProgress: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${exercise.sets} sets · ${exercise.reps} reps · ${exercise.weight.formatAmount()} kg · ${exercise.duration.formatAmount()} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { onShowProgress(exercise.name) }) {
                    Icon(Icons.Filled.ShowChart, contentDescription = "Progress")
                }
                IconButton(onClick = { onEditExercise(exercise.id) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit exercise")
                }
                IconButton(onClick = { onDeleteExercise(exercise) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete exercise")
                }
            }
            if (exercise.notes.isNotBlank()) {
                Text(
                    text = exercise.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProgressCard(point: ExerciseProgressPoint) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = DateUtils.displayDate(point.date),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = point.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                Text(
                    text = "${point.weight.formatAmount()} kg",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "${point.reps} reps",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
