package com.example.nutrilift.ui.meals

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
import com.example.nutrilift.data.local.entity.MealEntity
import com.example.nutrilift.ui.components.EmptyState
import com.example.nutrilift.util.DateUtils
import com.example.nutrilift.viewmodel.MealsViewModel

@Composable
fun MealsListScreen(
    contentPadding: PaddingValues,
    viewModel: MealsViewModel,
    onAddMeal: () -> Unit,
    onOpenMeal: (Long) -> Unit,
    onEditMeal: (Long) -> Unit
) {
    val meals by viewModel.meals.collectAsStateWithLifecycle()

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
                        text = "Meals",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${meals.size} saved meals",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(onClick = onAddMeal) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Meal")
                }
            }
        }

        if (meals.isEmpty()) {
            item { EmptyState("No meals recorded yet") }
        } else {
            items(meals, key = { it.id }) { meal ->
                MealListItem(
                    meal = meal,
                    onOpenMeal = onOpenMeal,
                    onEditMeal = onEditMeal,
                    onDeleteMeal = viewModel::deleteMeal
                )
            }
        }
    }
}

@Composable
private fun MealListItem(
    meal: MealEntity,
    onOpenMeal: (Long) -> Unit,
    onEditMeal: (Long) -> Unit,
    onDeleteMeal: (MealEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenMeal(meal.id) },
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
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${DateUtils.displayDate(meal.date)} · ${meal.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (meal.notes.isNotBlank()) {
                    Text(
                        text = meal.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = { onEditMeal(meal.id) }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit meal")
            }
            IconButton(onClick = { onDeleteMeal(meal) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete meal")
            }
        }
    }
}
