package com.gen.stajyerim.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gen.stajyerim.R
import com.gen.stajyerim.ui.components.BackButton
import com.gen.stajyerim.ui.components.CustomTextField
import com.gen.stajyerim.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel? = null
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val userType = remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(id = R.drawable.login), // Resim dosyasını belirtin
            contentDescription = "Landing Page Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit // Resmi alanı dolduracak şekilde ölçeklendirir
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))


            Text("Giriş Yap", style = MaterialTheme.typography.headlineLarge)

            CustomTextField(value = email.value, onValueChange = { email.value = it }, label = "Email")
            CustomTextField(value = password.value, onValueChange = { password.value = it }, label = "Şifre", isPassword = true)
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {

                    navController.navigate("home/${userType.value}")
                },
                enabled = userType.value.isNotEmpty()
            ) {
                Text("Giriş Yap")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Hesabın yok mu? Kayıt ol",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary, // Vurgulamak için farklı bir renk
                modifier = Modifier.clickable {
                    navController.navigate("comporstu") // SignUp sayfasına yönlendirme
                }
            )
        }
        BackButton(navController = navController)
    }
}