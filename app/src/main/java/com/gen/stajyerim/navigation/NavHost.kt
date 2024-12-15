package com.gen.stajyerim.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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

        // Control ekranı
        composable("control") {
            ControlScreen(navController, authViewModel, authRepository)
        }

        // Login ekranı
        composable("login") { LoginScreen(navController, authViewModel) }

        // Açılış ekranı
        composable("landing") {
            LandingPage(navController)
        }

        // Öğrenci veya şirket seçimi ekranı
        composable("comporstu") {
            CompOrStu(navController)
        }

        // Öğrenci kayıt ekranı
        composable("signup/student") {
            SignUpScreen(
                navController = navController,
                userType = "student",
                authRepository = authRepository
            )
        }

        // Şirket kayıt ekranı
        composable("signup/company") {
            SignUpScreen(
                navController = navController,
                userType = "company",
                authRepository = authRepository
            )
        }

        // Ana sayfa
        composable("home?userType={userType}") { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("userType") ?: "unknown"
            HomePage(userType = userType, navController = navController)
        }
        composable("home/student") {
            HomePage(navController, userType = "student")
        }
        composable("home/company") {
            HomePage(navController, userType = "company")
        }
        composable("home/unknown") {
            HomePage(navController, userType = "unknown")
        }

        // İlan oluşturma ekranı
        composable("createPost") {
            CreatePostScreen(navController = navController)
        }

        // Profil ekranı
        composable(
            route = "profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ProfileScreen(navController = navController, authViewModel = authViewModel, userId = userId)
        }

        // Mesajlar ekranı
        composable("messages") {
            MessageScreen(navController = navController)
        }

        // Başvurulan İlanlar
        composable("appliedPosts") {
            AppliedPostsScreen(navController = navController)
        }

        // Yayınlanan İlanlar
        composable("publishedPosts") {
            PublishedPostsScreen(navController = navController)
        }

        // İlan düzenleme ekranı
        composable("editPost/{jobId}") { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            EditPostScreen(navController = navController, jobId = jobId)
        }
    }
}
