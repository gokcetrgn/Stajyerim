package com.gen.stajyerim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.gen.stajyerim.data.repository.AuthRepository
import com.gen.stajyerim.data.repository.AuthViewModelFactory
import com.gen.stajyerim.navigation.AppNavigation
import com.gen.stajyerim.viewmodel.AuthViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val authRepository = AuthRepository() // AuthRepository manuel olarak oluşturuluyor
            val authViewModel = ViewModelProvider(
                this,
                AuthViewModelFactory(authRepository)
            )[AuthViewModel::class.java] // ViewModel oluşturuluyor

            MaterialTheme {
                AppNavigation(
                    navController = navController,
                    authViewModel = authViewModel,
                    authRepository = authRepository
                )
            }
        }
    }
}