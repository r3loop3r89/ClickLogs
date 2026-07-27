package com.clicklogs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.clicklogs.ui.navigation.AppNavGraph
import com.clicklogs.ui.theme.ClickLogsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as ClickLogsApp).repository

        setContent {
            ClickLogsTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    repository = repository
                )
            }
        }
    }
}
