package com.gen.stajyerim.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomePage(navController: NavHostController, userType: String? = null) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (userType) {
                "company" -> "Hoşgeldiniz, Şirket Profili!"
                "student" -> "Hoşgeldiniz, Öğrenci Profili!"
                else -> "Hoş Geldiniz!"
            },
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("landing") {
                popUpTo("home/{userType}") { inclusive = true } // HomePage'i temizler
            }
        }) {
            Text("Çıkış Yap")
        }
    }
}