package com.example.domo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.domo.ui.navigation.AppNavGraph
import com.example.domo.ui.theme.TocaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TocaTheme {
                AppNavGraph()
            }
        }
    }
}
