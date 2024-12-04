package com.gen.stajyerim.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.gen.stajyerim.data.repository.AuthRepository
import com.gen.stajyerim.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ControlScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    authRepository: AuthRepository
) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            // Kullanıcı oturumu varsa kullanıcı tipi alınır
            viewModel.fetchUserType { userType, error ->
                if (userType != null) {
                    navController.navigate("home/$userType") {
                        popUpTo("control") { inclusive = true }
                    }
                } else {
                    // Kullanıcı tipi alınamadıysa hata ekranı veya login ekranına yönlendirebilirsiniz
                    navController.navigate("login") {
                        popUpTo("control") { inclusive = true }
                    }
                }
            }
        } else {
            // Kullanıcı oturumu yoksa landing ekranına yönlendirme yapılır
            navController.navigate("landing") {
                popUpTo("control") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Yükleniyor...", style = MaterialTheme.typography.titleSmall)
    }
}