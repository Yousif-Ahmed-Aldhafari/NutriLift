package com.example.nutrilift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.nutrilift.ui.navigation.NutriLiftApp
import com.example.nutrilift.ui.theme.NutriLiftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutriLiftTheme {
                NutriLiftApp()
            }
        }
    }
}
