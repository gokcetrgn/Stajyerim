package com.gen.stajyerim.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gen.stajyerim.R
import com.gen.stajyerim.ui.components.BackButton

@Composable
fun CompOrStu(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize() ){
        Image(
            painter = painterResource(id = R.drawable.landing),
            contentDescription = "Landing Page Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Ne olarak devam etmek istiyorsunuz?", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = { navController.navigate("signup/company") },
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xffba68c8), // Özel bir mor renk
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp) ) {
                Text(text = "Şirket Hesabı")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { navController.navigate("signup/student") },
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xffba68c8),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp) ) {
                Text(text = "Kişisel Hesap")
            }
        }
        BackButton(navController = navController)
    }
}