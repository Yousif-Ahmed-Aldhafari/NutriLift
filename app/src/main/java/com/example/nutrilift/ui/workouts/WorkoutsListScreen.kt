package com.example.nutrilift.ui.workouts

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutrilift.data.local.entity.WorkoutEntity
import com.example.nutrilift.ui.components.EmptyState
import com.example.nutrilift.util.DateUtils
import com.example.nutrilift.viewmodel.WorkoutsViewModel

@Composable
fun WorkoutsListScreen(
    contentPadding: PaddingValues,
    viewModel: WorkoutsViewModel,
    onAddWorkout: () -> Unit,
    onOpenWorkout: (Long) -> Unit,
    onEditWorkout: (Long) -> Unit
) {
    val workouts by viewModel.workouts.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 24.dp,
            end = 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Workouts",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${workouts.size} saved sessions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(onClick = onAddWorkout) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Workout")
                }
            }
        }

        if (workouts.isEmpty()) {
            item { EmptyState("No workouts recorded yet") }
        } else {
            items(workouts, key = { it.id }) { workout ->
                WorkoutListItem(
                    workout = workout,
                    onOpenWorkout = onOpenWorkout,
                    onEditWorkout = onEditWorkout,
                    onDeleteWorkout = viewModel::deleteWorkout
                )
            }
        }
    }
}

@Composable
private fun WorkoutListItem(
    workout: WorkoutEntity,
    onOpenWorkout: (Long) -> Unit,
    onEditWorkout: (Long) -> Unit,
    onDeleteWorkout: (WorkoutEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenWorkout(workout.id) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${DateUtils.displayDate(workout.date)} · ${workout.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (workout.notes.isNotBlank()) {
                    Text(
                        text = workout.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = { onEditWorkout(workout.id) }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit workout")
            }
            IconButton(onClick = { onDeleteWorkout(workout) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete workout")
            }
        }
    }
}
