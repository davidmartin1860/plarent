package com.plarent.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.plarent.android.ui.AddPlantScreen
import com.plarent.android.ui.PlantDetailScreen
import com.plarent.android.ui.PlantListScreen
import com.plarent.shared.data.PlantRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 10.0.2.2 is the special alias the Android emulator uses to
            // reach "localhost" on the host machine running Docker.
            val repository = remember { PlantRepository(baseUrl = "http://10.0.2.2:8080/api") }
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "plants") {
                composable("plants") {
                    PlantListScreen(
                        repository = repository,
                        onPlantClick = { id -> navController.navigate("plants/$id") },
                        onAddClick = { navController.navigate("plants/new") },
                    )
                }
                composable(
                    route = "plants/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType }),
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id").orEmpty()
                    PlantDetailScreen(
                        repository = repository,
                        plantId = id,
                        onBack = { navController.popBackStack() },
                    )
                }
                composable("plants/new") {
                    AddPlantScreen(
                        repository = repository,
                        onSaved = { newId ->
                            navController.navigate("plants/$newId") {
                                popUpTo("plants")
                            }
                        },
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}
