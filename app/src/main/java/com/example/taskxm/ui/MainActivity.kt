package com.example.taskxm.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskxm.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurveyNavHost()
        }
    }
}

@Composable
fun SurveyNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController, startDestination = stringResource(R.string.screen_initial)) {
        composable("initial") {
            InitialScreen(onStartSurvey = { navController.navigate("questions") })
        }
        composable("questions") {
            QuestionsScreen()
        }
    }
}
