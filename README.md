# NutriLift

NutriLift is a personal Android app for tracking meals, nutrition totals, workout sessions, exercises, and progress history. It is Android-only, offline-first, local-only, and has no login or backend.

## Recommended Stack

The requested stack is suitable for this app:

- Kotlin with AGP 9 built-in Kotlin support
- Jetpack Compose
- Material 3
- Room Database
- MVVM
- Repository pattern
- Navigation Compose
- Kotlin Coroutines
- Flow / StateFlow

Current core versions:

- Android Gradle Plugin: `8.13.2`
- Gradle wrapper: `8.14.5`
- Kotlin / Compose compiler plugin: `2.3.21`
- Compose BOM: `2026.06.01`
- Room: `2.8.4`
- Navigation Compose: `2.9.8`

## Project Structure

```text
app/
  src/
    main/
      java/com/example/nutrilift/
        data/
          local/
            dao/
            database/
            entity/
          repository/
        domain/
          model/
          usecase/
        ui/
          components/
          dashboard/
          history/
          meals/
          navigation/
          settings/
          theme/
          workouts/
        util/
        viewmodel/
      res/
        drawable/
        mipmap-anydpi-v26/
        values/
        xml/
    test/
      java/com/example/nutrilift/
        domain/usecase/
        util/
gradle/
  libs.versions.toml
  wrapper/
```

## Database Schema

`Meal`

- `id: Long`
- `name: String`
- `date: String` in `YYYY-MM-DD`
- `time: String` in `HH:mm`
- `notes: String`

`FoodItem`

- `id: Long`
- `mealId: Long`
- `name: String`
- `quantity: Double`
- `unit: String`
- `calories: Double`
- `protein: Double`
- `carbs: Double`
- `fats: Double`

`Workout`

- `id: Long`
- `name: String`
- `date: String` in `YYYY-MM-DD`
- `time: String` in `HH:mm`
- `notes: String`

`Exercise`

- `id: Long`
- `workoutId: Long`
- `name: String`
- `sets: Int`
- `reps: Int`
- `weight: Double`
- `duration: Double`
- `notes: String`

`FoodItem.mealId` cascades when a meal is deleted. `Exercise.workoutId` cascades when a workout is deleted.

## Implemented App Areas

- Dashboard Screen
- Meals List Screen
- Add/Edit Meal Screen
- Meal Details Screen
- Add/Edit Food Item Screen
- Workout List Screen
- Add/Edit Workout Screen
- Workout Details Screen
- Add/Edit Exercise Screen
- History Screen
- Settings Screen

## Calculations

The app calculates:

- Total calories, protein, carbs, and fats per meal
- Total calories, protein, carbs, and fats per day
- Number of meals per day
- Number of workouts per day
- Weekly and monthly meal summaries
- Weekly and monthly workout summaries
- Exercise progress points by exercise name

## Commands

Run this in PowerShell inside the NutriLift project folder `D:\Users\yyy\Desktop\NutriLift`:

```powershell
.\gradlew.bat -v
```

Run this in PowerShell inside the NutriLift project folder `D:\Users\yyy\Desktop\NutriLift` after Android SDK is installed/configured:

```powershell
.\gradlew.bat :app:assembleDebug
```

Run this in PowerShell inside the NutriLift project folder `D:\Users\yyy\Desktop\NutriLift` after Android SDK is installed/configured:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Run this in PowerShell inside the NutriLift project folder `D:\Users\yyy\Desktop\NutriLift` to install on a connected emulator/device:

```powershell
.\gradlew.bat :app:installDebug
```

Run this in PowerShell if your SDK exists at the default Windows path but Gradle cannot find it:

```powershell
$env:ANDROID_HOME="$env:LOCALAPPDATA\Android\Sdk"
```

Run this in PowerShell inside the NutriLift project folder `D:\Users\yyy\Desktop\NutriLift` if you prefer a project-local SDK pointer:

```powershell
"sdk.dir=$($env:LOCALAPPDATA.Replace('\','/'))/Android/Sdk" | Out-File -Encoding ASCII local.properties
```

Run this in PowerShell inside your Android SDK `platform-tools` folder, usually `%LOCALAPPDATA%\Android\Sdk\platform-tools`, to confirm a device is visible:

```powershell
.\adb.exe devices
```

Run this in PowerShell inside your Android SDK `emulator` folder, usually `%LOCALAPPDATA%\Android\Sdk\emulator`, to list emulators:

```powershell
.\emulator.exe -list-avds
```

Run this in PowerShell inside your Android SDK `emulator` folder, usually `%LOCALAPPDATA%\Android\Sdk\emulator`, replacing `<AVD_NAME>` with an emulator name:

```powershell
.\emulator.exe -avd <AVD_NAME>
```

## Android SDK Setup

This repository uses a project-local `local.properties` file to point Gradle at the Android SDK.

Recommended setup:

1. Install Android Studio.
2. Open Android Studio.
3. Open SDK Manager.
4. Install Android SDK Platform `36`, SDK Build Tools `36.0.0`, Platform Tools, and Android Emulator if they are not already installed.
5. Accept the Android SDK licenses in Android Studio.
6. Open this project folder in Android Studio: `D:\Users\yyy\Desktop\NutriLift`.
7. Let Gradle sync.

## Testing Checklist

- Add a meal with name, date, time, and notes.
- Edit that meal.
- Delete that meal.
- Add food items to a meal with quantity, unit, calories, protein, carbs, and fats.
- Edit and delete food items.
- Confirm meal totals update after food item changes.
- Confirm Dashboard daily nutrition totals update.
- Confirm weekly and monthly meal summaries update.
- Add a workout with name, date, time, and notes.
- Edit and delete workouts.
- Add exercises with sets, reps, weight, duration, and notes.
- Edit and delete exercises.
- Confirm Dashboard workout counts update.
- Confirm History lists recent meals and workouts.
- Close and reopen the app, then confirm local Room data persists.
- Turn off network access and confirm the app still works.

## Build Verification Status

Verified:

- Repository started with only `README.md`, `project_structure.txt`, and `.git`.
- Android command-line SDK tools were installed under `%LOCALAPPDATA%\Android\Sdk`.
- Gradle wrapper runs and reports Gradle `8.14.5`.
- Android SDK Platform `36`, Build Tools `36.0.0`, and Platform Tools were installed.

Blocked locally:

- `:app:assembleDebug` currently hangs in Gradle dependency transform/cache work on this Windows environment. Try running from Android Studio after opening the project, or clear `%USERPROFILE%\.gradle\caches\8.14.5\transforms` and rerun.
