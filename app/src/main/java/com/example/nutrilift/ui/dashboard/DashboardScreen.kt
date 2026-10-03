package com.example.nutrilift.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutrilift.ui.components.MetricCard
import com.example.nutrilift.ui.components.NutritionSummaryCard
import com.example.nutrilift.ui.components.SectionTitle
import com.example.nutrilift.util.DateUtils
import com.example.nutrilift.util.formatAmount
import com.example.nutrilift.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    contentPadding: PaddingValues,
    viewModel: DashboardViewModel,
    onAddMeal: () -> Unit,
    onAddWorkout: () -> Unit
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "NutriLift",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = DateUtils.displayDate(state.today),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            NutritionSummaryCard(
                title = "Today's nutrition",
                totals = state.dailyMealSummary.totals
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    label = "Meals today",
                    value = state.dailyMealSummary.mealCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Workouts today",
                    value = state.dailyWorkoutSummary.workoutCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = onAddMeal,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Restaurant, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Meal")
                }
                Spacer(Modifier.width(12.dp))
                OutlinedButton(
                    onClick = onAddWorkout,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.FitnessCenter, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Workout")
                }
            }
        }

        item { SectionTitle("Meals summary") }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    label = "This week",
                    value = state.weeklyMealSummary.mealCount.toString(),
                    supportingText = "${state.weeklyMealSummary.totals.calories.formatAmount()} cal",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "This month",
                    value = state.monthlyMealSummary.mealCount.toString(),
                    supportingText = "${state.monthlyMealSummary.totals.calories.formatAmount()} cal",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item { SectionTitle("Workout summary") }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    label = "This week",
                    value = state.weeklyWorkoutSummary.workoutCount.toString(),
                    supportingText = "${state.weeklyWorkoutSummary.exerciseCount} exercises",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "This month",
                    value = state.monthlyWorkoutSummary.workoutCount.toString(),
                    supportingText = "${state.monthlyWorkoutSummary.exerciseCount} exercises",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            OutlinedButton(
                onClick = onAddMeal,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add food or training data")
            }
        }
    }
}
