package com.example.nutrilift.util

import java.util.Locale
import kotlin.math.abs

fun Double.formatAmount(unit: String = ""): String {
    val number = if (abs(this % 1.0) < 0.0001) {
        String.format(Locale.US, "%.0f", this)
    } else {
        String.format(Locale.US, "%.1f", this)
    }
    return if (unit.isBlank()) number else "$number $unit"
}

fun String.toCleanDouble(): Double {
    return trim().toDoubleOrNull() ?: 0.0
}

fun String.toCleanInt(): Int {
    return trim().toIntOrNull() ?: 0
}
