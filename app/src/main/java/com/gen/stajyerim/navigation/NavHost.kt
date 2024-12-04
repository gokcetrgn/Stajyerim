package com.gen.stajyerim.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gen.stajyerim.data.repository.AuthRepository
import com.gen.stajyerim.ui.screens.*
import com.gen.stajyerim.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    authRepository: AuthRepository
) {
    NavHost(navController = navController, startDestination = "control") {

        composable("control") {
            ControlScreen(navController, authViewModel, authRepository)
        }

        composable("landing") { LandingPage(navController) }

        // Login Screen
        composable("login") { LoginScreen(navController, authViewModel) }

        composable("comporstu") { CompOrStu(navController) }

        composable("signup/student") {
            SignUpScreen(
                navController = navController,
                userType = "student",
                authRepository = authRepository
            )
        }
        composable("signup/company") {
            SignUpScreen(
                navController = navController,
                userType = "company",
                authRepository = authRepository
            )
        }

        composable("home/student") {
            HomePage(navController,userType = "student")
        }
        composable("home/company") {
            HomePage(navController,userType = "company")
        }

        composable("home/{userType}") { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("userType")
            if (userType != null) {
                HomePage(navController,userType)
            }
        }
    }
}