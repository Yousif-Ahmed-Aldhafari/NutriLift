package com.example.nutrilift.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutrilift.data.local.entity.MealEntity
import com.example.nutrilift.data.local.entity.WorkoutEntity
import com.example.nutrilift.ui.components.EmptyState
import com.example.nutrilift.ui.components.MetricCard
import com.example.nutrilift.ui.components.NutritionSummaryCard
import com.example.nutrilift.ui.components.SectionTitle
import com.example.nutrilift.util.DateUtils
import com.example.nutrilift.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(
    contentPadding: PaddingValues,
    viewModel: HistoryViewModel,
    onOpenMeal: (Long) -> Unit,
    onOpenWorkout: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
            Text(
                text = "History",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${DateUtils.displayDate(state.startDate)} to ${DateUtils.displayDate(state.endDate)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            NutritionSummaryCard(
                title = "Meals, last 30 days",
                totals = state.mealSummary.totals
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    label = "Meals",
                    value = state.mealSummary.mealCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Workouts",
                    value = state.workoutSummary.workoutCount.toString(),
                    supportingText = "${state.workoutSummary.exerciseCount} exercises",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item { SectionTitle("Recent meals") }
        if (state.recentMeals.isEmpty()) {
            item { EmptyState("No meal history yet") }
        } else {
            items(state.recentMeals, key = { "meal-${it.id}" }) { meal ->
                HistoryMealItem(meal = meal, onOpenMeal = onOpenMeal)
            }
        }

        item { SectionTitle("Recent workouts") }
        if (state.recentWorkouts.isEmpty()) {
            item { EmptyState("No workout history yet") }
        } else {
            items(state.recentWorkouts, key = { "workout-${it.id}" }) { workout ->
                HistoryWorkoutItem(workout = workout, onOpenWorkout = onOpenWorkout)
            }
        }
    }
}

@Composable
private fun HistoryMealItem(
    meal: MealEntity,
    onOpenMeal: (Long) -> Unit
) {
    HistoryItemCard(
        title = meal.name,
        subtitle = "${DateUtils.displayDate(meal.date)} · ${meal.time}",
        onClick = { onOpenMeal(meal.id) }
    )
}

@Composable
private fun HistoryWorkoutItem(
    workout: WorkoutEntity,
    onOpenWorkout: (Long) -> Unit
) {
    HistoryItemCard(
        title = workout.name,
        subtitle = "${DateUtils.displayDate(workout.date)} · ${workout.time}",
        onClick = { onOpenWorkout(workout.id) }
    )
}

@Composable
private fun HistoryItemCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
