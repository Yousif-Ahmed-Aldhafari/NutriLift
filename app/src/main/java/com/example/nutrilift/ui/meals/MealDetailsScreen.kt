package com.example.nutrilift.ui.meals

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
import com.example.nutrilift.data.local.entity.FoodItemEntity
import com.example.nutrilift.ui.components.EmptyState
import com.example.nutrilift.ui.components.NutritionSummaryCard
import com.example.nutrilift.ui.components.SectionTitle
import com.example.nutrilift.util.DateUtils
import com.example.nutrilift.util.formatAmount
import com.example.nutrilift.viewmodel.MealDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsScreen(
    viewModel: MealDetailsViewModel,
    onBack: () -> Unit,
    onEditMeal: (Long) -> Unit,
    onAddFoodItem: (Long) -> Unit,
    onEditFoodItem: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val mealWithFoodItems = state.mealWithFoodItems
    val meal = mealWithFoodItems?.meal

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(meal?.name ?: "Meal details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    meal?.let {
                        IconButton(onClick = { onEditMeal(it.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit meal")
                        }
                        IconButton(onClick = { viewModel.deleteMeal(it, onBack) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete meal")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            meal?.let {
                FloatingActionButton(onClick = { onAddFoodItem(it.id) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add food item")
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
            if (mealWithFoodItems == null) {
                item { EmptyState("Meal not found") }
            } else {
                item {
                    Text(
                        text = "${DateUtils.displayDate(mealWithFoodItems.meal.date)} · ${mealWithFoodItems.meal.time}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (mealWithFoodItems.meal.notes.isNotBlank()) {
                        Text(
                            text = mealWithFoodItems.meal.notes,
                            modifier = Modifier.padding(top = 4.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item {
                    NutritionSummaryCard(
                        title = "Meal totals",
                        totals = mealWithFoodItems.totals
                    )
                }

                item { SectionTitle("Food items") }

                if (mealWithFoodItems.foodItems.isEmpty()) {
                    item { EmptyState("No food items in this meal") }
                } else {
                    items(mealWithFoodItems.foodItems, key = { it.id }) { foodItem ->
                        FoodItemCard(
                            foodItem = foodItem,
                            onEditFoodItem = onEditFoodItem,
                            onDeleteFoodItem = viewModel::deleteFoodItem
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodItemCard(
    foodItem: FoodItemEntity,
    onEditFoodItem: (Long) -> Unit,
    onDeleteFoodItem: (FoodItemEntity) -> Unit
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
                        text = foodItem.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${foodItem.quantity.formatAmount()} ${foodItem.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { onEditFoodItem(foodItem.id) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit food item")
                }
                IconButton(onClick = { onDeleteFoodItem(foodItem) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete food item")
                }
            }
            Spacer(Modifier.width(1.dp))
            Text(
                text = "${foodItem.calories.formatAmount()} cal · ${foodItem.protein.formatAmount("g")} protein · ${foodItem.carbs.formatAmount("g")} carbs · ${foodItem.fats.formatAmount("g")} fats",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
