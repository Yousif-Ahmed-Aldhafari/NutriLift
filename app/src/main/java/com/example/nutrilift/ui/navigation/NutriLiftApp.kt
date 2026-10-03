package com.example.nutrilift.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nutrilift.NutriLiftApplication
import com.example.nutrilift.ui.dashboard.DashboardScreen
import com.example.nutrilift.ui.history.HistoryScreen
import com.example.nutrilift.ui.meals.FoodItemFormScreen
import com.example.nutrilift.ui.meals.MealDetailsScreen
import com.example.nutrilift.ui.meals.MealFormScreen
import com.example.nutrilift.ui.meals.MealsListScreen
import com.example.nutrilift.ui.settings.SettingsScreen
import com.example.nutrilift.ui.workouts.ExerciseFormScreen
import com.example.nutrilift.ui.workouts.WorkoutDetailsScreen
import com.example.nutrilift.ui.workouts.WorkoutFormScreen
import com.example.nutrilift.ui.workouts.WorkoutsListScreen
import com.example.nutrilift.viewmodel.DashboardViewModel
import com.example.nutrilift.viewmodel.ExerciseFormViewModel
import com.example.nutrilift.viewmodel.FoodItemFormViewModel
import com.example.nutrilift.viewmodel.HistoryViewModel
import com.example.nutrilift.viewmodel.MealDetailsViewModel
import com.example.nutrilift.viewmodel.MealFormViewModel
import com.example.nutrilift.viewmodel.MealsViewModel
import com.example.nutrilift.viewmodel.SettingsViewModel
import com.example.nutrilift.viewmodel.WorkoutDetailsViewModel
import com.example.nutrilift.viewmodel.WorkoutFormViewModel
import com.example.nutrilift.viewmodel.WorkoutsViewModel

private data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val topLevelDestinations = listOf(
    TopLevelDestination(Routes.Dashboard, "Dashboard", Icons.Filled.Dashboard),
    TopLevelDestination(Routes.Meals, "Meals", Icons.Filled.Restaurant),
    TopLevelDestination(Routes.Workouts, "Workouts", Icons.Filled.FitnessCenter),
    TopLevelDestination(Routes.History, "History", Icons.Filled.History),
    TopLevelDestination(Routes.Settings, "Settings", Icons.Filled.Settings)
)

private val topLevelRoutes = topLevelDestinations.map { it.route }.toSet()

private fun isTopLevelTransition(initialRoute: String?, targetRoute: String?): Boolean {
    return initialRoute != null &&
        targetRoute != null &&
        initialRoute in topLevelRoutes &&
        targetRoute in topLevelRoutes
}

@Composable
fun NutriLiftApp() {
    val context = LocalContext.current
    val container = remember(context) {
        (context.applicationContext as NutriLiftApplication).container
    }
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = topLevelDestinations.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label
                                )
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Dashboard,
            enterTransition = {
                if (isTopLevelTransition(initialState.destination.route, targetState.destination.route)) {
                    EnterTransition.None
                } else {
                    fadeIn(animationSpec = tween(700))
                }
            },
            exitTransition = {
                if (isTopLevelTransition(initialState.destination.route, targetState.destination.route)) {
                    ExitTransition.None
                } else {
                    fadeOut(animationSpec = tween(700))
                }
            },
            popEnterTransition = {
                if (isTopLevelTransition(initialState.destination.route, targetState.destination.route)) {
                    EnterTransition.None
                } else {
                    fadeIn(animationSpec = tween(700))
                }
            },
            popExitTransition = {
                if (isTopLevelTransition(initialState.destination.route, targetState.destination.route)) {
                    ExitTransition.None
                } else {
                    fadeOut(animationSpec = tween(700))
                }
            }
        ) {
            composable(Routes.Dashboard) {
                val viewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModel.factory(
                        container.mealRepository,
                        container.workoutRepository
                    )
                )
                DashboardScreen(
                    contentPadding = innerPadding,
                    viewModel = viewModel,
                    onAddMeal = { navController.navigate(Routes.AddMeal) },
                    onAddWorkout = { navController.navigate(Routes.AddWorkout) }
                )
            }

            composable(Routes.Meals) {
                val viewModel: MealsViewModel = viewModel(
                    factory = MealsViewModel.factory(container.mealRepository)
                )
                MealsListScreen(
                    contentPadding = innerPadding,
                    viewModel = viewModel,
                    onAddMeal = { navController.navigate(Routes.AddMeal) },
                    onOpenMeal = { navController.navigate(Routes.mealDetails(it)) },
                    onEditMeal = { navController.navigate(Routes.editMeal(it)) }
                )
            }

            composable(Routes.AddMeal) {
                val viewModel: MealFormViewModel = viewModel(
                    factory = MealFormViewModel.factory(container.mealRepository)
                )
                MealFormScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { mealId ->
                        navController.navigate(Routes.mealDetails(mealId)) {
                            popUpTo(Routes.AddMeal) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Routes.EditMeal,
                arguments = listOf(navArgument("mealId") { type = NavType.LongType })
            ) { entry ->
                val mealId = entry.arguments?.getLong("mealId") ?: return@composable
                val viewModel: MealFormViewModel = viewModel(
                    factory = MealFormViewModel.factory(container.mealRepository, mealId)
                )
                MealFormScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Routes.MealDetails,
                arguments = listOf(navArgument("mealId") { type = NavType.LongType })
            ) { entry ->
                val mealId = entry.arguments?.getLong("mealId") ?: return@composable
                val viewModel: MealDetailsViewModel = viewModel(
                    factory = MealDetailsViewModel.factory(container.mealRepository, mealId)
                )
                MealDetailsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEditMeal = { navController.navigate(Routes.editMeal(it)) },
                    onAddFoodItem = { navController.navigate(Routes.addFoodItem(it)) },
                    onEditFoodItem = { id -> navController.navigate(Routes.editFoodItem(mealId, id)) }
                )
            }

            composable(
                route = Routes.AddFoodItem,
                arguments = listOf(navArgument("mealId") { type = NavType.LongType })
            ) { entry ->
                val mealId = entry.arguments?.getLong("mealId") ?: return@composable
                val viewModel: FoodItemFormViewModel = viewModel(
                    factory = FoodItemFormViewModel.factory(container.mealRepository, mealId)
                )
                FoodItemFormScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EditFoodItem,
                arguments = listOf(
                    navArgument("mealId") { type = NavType.LongType },
                    navArgument("foodItemId") { type = NavType.LongType }
                )
            ) { entry ->
                val mealId = entry.arguments?.getLong("mealId") ?: return@composable
                val foodItemId = entry.arguments?.getLong("foodItemId") ?: return@composable
                val viewModel: FoodItemFormViewModel = viewModel(
                    factory = FoodItemFormViewModel.factory(
                        container.mealRepository,
                        mealId,
                        foodItemId
                    )
                )
                FoodItemFormScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(Routes.Workouts) {
                val viewModel: WorkoutsViewModel = viewModel(
                    factory = WorkoutsViewModel.factory(container.workoutRepository)
                )
                WorkoutsListScreen(
                    contentPadding = innerPadding,
                    viewModel = viewModel,
                    onAddWorkout = { navController.navigate(Routes.AddWorkout) },
                    onOpenWorkout = { navController.navigate(Routes.workoutDetails(it)) },
                    onEditWorkout = { navController.navigate(Routes.editWorkout(it)) }
                )
            }

            composable(Routes.AddWorkout) {
                val viewModel: WorkoutFormViewModel = viewModel(
                    factory = WorkoutFormViewModel.factory(container.workoutRepository)
                )
                WorkoutFormScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { workoutId ->
                        navController.navigate(Routes.workoutDetails(workoutId)) {
                            popUpTo(Routes.AddWorkout) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Routes.EditWorkout,
                arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
            ) { entry ->
                val workoutId = entry.arguments?.getLong("workoutId") ?: return@composable
                val viewModel: WorkoutFormViewModel = viewModel(
                    factory = WorkoutFormViewModel.factory(container.workoutRepository, workoutId)
                )
                WorkoutFormScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Routes.WorkoutDetails,
                arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
            ) { entry ->
                val workoutId = entry.arguments?.getLong("workoutId") ?: return@composable
                val viewModel: WorkoutDetailsViewModel = viewModel(
                    factory = WorkoutDetailsViewModel.factory(container.workoutRepository, workoutId)
                )
                WorkoutDetailsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEditWorkout = { navController.navigate(Routes.editWorkout(it)) },
                    onAddExercise = { navController.navigate(Routes.addExercise(it)) },
                    onEditExercise = { id -> navController.navigate(Routes.editExercise(workoutId, id)) }
                )
            }

            composable(
                route = Routes.AddExercise,
                arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
            ) { entry ->
                val workoutId = entry.arguments?.getLong("workoutId") ?: return@composable
                val viewModel: ExerciseFormViewModel = viewModel(
                    factory = ExerciseFormViewModel.factory(container.workoutRepository, workoutId)
                )
                ExerciseFormScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EditExercise,
                arguments = listOf(
                    navArgument("workoutId") { type = NavType.LongType },
                    navArgument("exerciseId") { type = NavType.LongType }
                )
            ) { entry ->
                val workoutId = entry.arguments?.getLong("workoutId") ?: return@composable
                val exerciseId = entry.arguments?.getLong("exerciseId") ?: return@composable
                val viewModel: ExerciseFormViewModel = viewModel(
                    factory = ExerciseFormViewModel.factory(
                        container.workoutRepository,
                        workoutId,
                        exerciseId
                    )
                )
                ExerciseFormScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(Routes.History) {
                val viewModel: HistoryViewModel = viewModel(
                    factory = HistoryViewModel.factory(
                        container.mealRepository,
                        container.workoutRepository
                    )
                )
                HistoryScreen(
                    contentPadding = innerPadding,
                    viewModel = viewModel,
                    onOpenMeal = { navController.navigate(Routes.mealDetails(it)) },
                    onOpenWorkout = { navController.navigate(Routes.workoutDetails(it)) }
                )
            }

            composable(Routes.Settings) {
                val viewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModel.factory()
                )
                SettingsScreen(
                    contentPadding = innerPadding,
                    viewModel = viewModel
                )
            }
        }
    }
}
