package com.example.nutrilift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nutrilift.data.local.entity.FoodItemEntity
import com.example.nutrilift.data.local.entity.MealEntity
import com.example.nutrilift.data.repository.MealRepository
import com.example.nutrilift.domain.model.MealWithFoodItems
import com.example.nutrilift.util.DateUtils
import com.example.nutrilift.util.toCleanDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MealsViewModel(
    private val mealRepository: MealRepository
) : ViewModel() {
    val meals: StateFlow<List<MealEntity>> = mealRepository.observeMeals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            mealRepository.deleteMeal(meal)
        }
    }

    companion object {
        fun factory(mealRepository: MealRepository) = viewModelFactory {
            initializer { MealsViewModel(mealRepository) }
        }
    }
}

data class MealFormState(
    val id: Long = 0,
    val name: String = "",
    val date: String = DateUtils.today(),
    val time: String = DateUtils.currentTime(),
    val notes: String = "",
    val isEdit: Boolean = false,
    val error: String? = null
)

class MealFormViewModel(
    private val mealRepository: MealRepository,
    private val mealId: Long? = null
) : ViewModel() {
    private val _state = MutableStateFlow(MealFormState(isEdit = mealId != null))
    val state: StateFlow<MealFormState> = _state.asStateFlow()

    init {
        mealId?.let { id ->
            viewModelScope.launch {
                val meal = mealRepository.observeMeal(id).filterNotNull().first()
                _state.value = MealFormState(
                    id = meal.id,
                    name = meal.name,
                    date = meal.date,
                    time = meal.time,
                    notes = meal.notes,
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
                _state.update { it.copy(error = "Meal name is required") }
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
            val id = mealRepository.saveMeal(
                MealEntity(
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
            mealRepository: MealRepository,
            mealId: Long? = null
        ) = viewModelFactory {
            initializer { MealFormViewModel(mealRepository, mealId) }
        }
    }
}

data class MealDetailsUiState(
    val mealWithFoodItems: MealWithFoodItems? = null
)

class MealDetailsViewModel(
    private val mealRepository: MealRepository,
    mealId: Long
) : ViewModel() {
    val uiState: StateFlow<MealDetailsUiState> = mealRepository.observeMealWithFoodItems(mealId)
        .map { MealDetailsUiState(mealWithFoodItems = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MealDetailsUiState())

    fun deleteFoodItem(foodItem: FoodItemEntity) {
        viewModelScope.launch {
            mealRepository.deleteFoodItem(foodItem)
        }
    }

    fun deleteMeal(meal: MealEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            mealRepository.deleteMeal(meal)
            onDeleted()
        }
    }

    companion object {
        fun factory(
            mealRepository: MealRepository,
            mealId: Long
        ) = viewModelFactory {
            initializer { MealDetailsViewModel(mealRepository, mealId) }
        }
    }
}

data class FoodItemFormState(
    val id: Long = 0,
    val mealId: Long = 0,
    val name: String = "",
    val quantity: String = "",
    val unit: String = "gram",
    val calories: String = "",
    val protein: String = "",
    val carbs: String = "",
    val fats: String = "",
    val isEdit: Boolean = false,
    val error: String? = null
)

class FoodItemFormViewModel(
    private val mealRepository: MealRepository,
    private val mealId: Long,
    private val foodItemId: Long? = null
) : ViewModel() {
    private val _state = MutableStateFlow(
        FoodItemFormState(mealId = mealId, isEdit = foodItemId != null)
    )
    val state: StateFlow<FoodItemFormState> = _state.asStateFlow()

    init {
        foodItemId?.let { id ->
            viewModelScope.launch {
                val foodItem = mealRepository.observeFoodItem(id).filterNotNull().first()
                _state.value = FoodItemFormState(
                    id = foodItem.id,
                    mealId = foodItem.mealId,
                    name = foodItem.name,
                    quantity = foodItem.quantity.toString(),
                    unit = foodItem.unit,
                    calories = foodItem.calories.toString(),
                    protein = foodItem.protein.toString(),
                    carbs = foodItem.carbs.toString(),
                    fats = foodItem.fats.toString(),
                    isEdit = true
                )
            }
        }
    }

    fun updateName(value: String) = _state.update { it.copy(name = value, error = null) }
    fun updateQuantity(value: String) = _state.update { it.copy(quantity = value, error = null) }
    fun updateUnit(value: String) = _state.update { it.copy(unit = value, error = null) }
    fun updateCalories(value: String) = _state.update { it.copy(calories = value, error = null) }
    fun updateProtein(value: String) = _state.update { it.copy(protein = value, error = null) }
    fun updateCarbs(value: String) = _state.update { it.copy(carbs = value, error = null) }
    fun updateFats(value: String) = _state.update { it.copy(fats = value, error = null) }

    fun save(onSaved: () -> Unit) {
        val current = state.value
        if (current.name.isBlank()) {
            _state.update { it.copy(error = "Food item name is required") }
            return
        }

        viewModelScope.launch {
            mealRepository.saveFoodItem(
                FoodItemEntity(
                    id = current.id,
                    mealId = current.mealId,
                    name = current.name.trim(),
                    quantity = current.quantity.toCleanDouble(),
                    unit = current.unit.ifBlank { "unit" }.trim(),
                    calories = current.calories.toCleanDouble(),
                    protein = current.protein.toCleanDouble(),
                    carbs = current.carbs.toCleanDouble(),
                    fats = current.fats.toCleanDouble()
                )
            )
            onSaved()
        }
    }

    companion object {
        fun factory(
            mealRepository: MealRepository,
            mealId: Long,
            foodItemId: Long? = null
        ) = viewModelFactory {
            initializer { FoodItemFormViewModel(mealRepository, mealId, foodItemId) }
        }
    }
}
