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
import com.gen.stajyerim.data.repository.AuthRepository
import com.gen.stajyerim.ui.backgrounds.SignUpBackground
import com.gen.stajyerim.ui.components.BackButton
import com.gen.stajyerim.ui.components.CustomTextField


@Composable
fun SignUpScreen(
    navController: NavHostController,
    userType: String,
    authRepository: AuthRepository
) {
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val companyName = remember { mutableStateOf("") }
    val companyNumber = remember { mutableStateOf("") }
    val profession = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        SignUpBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Kayıt Ol", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(30.dp))
            CustomTextField(value = name.value, onValueChange = { name.value = it }, label = "Ad")
            CustomTextField(value = surname.value, onValueChange = { surname.value = it }, label = "Soyad")

            if (userType == "company") {
                CustomTextField(value = profession.value, onValueChange = { profession.value = it }, label = "Meslek")
                CustomTextField(value = companyName.value, onValueChange = { companyName.value = it }, label = "Şirket Adı")
                CustomTextField(value = companyNumber.value, onValueChange = { companyNumber.value = it }, label = "Şirket No")
            }

            CustomTextField(value = email.value, onValueChange = { email.value = it }, label = "Email")
            CustomTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = "Şifre",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                val userData = mutableMapOf<String, Any>(
                    "email" to email.value,
                    "name" to name.value,
                    "surname" to surname.value,
                    "userType" to userType
                )
                println("A")

                if (userType == "company") {
                    userData["profession"] = profession.value
                    userData["companyName"] = companyName.value
                    userData["companyNumber"] = companyNumber.value
                }
                println("B")

                authRepository.registerUser(
                    email = email.value,
                    password = password.value,
                    userData = userData,
                    onComplete = { result ->
                        result.onSuccess {
                            println("Kullanıcı başarıyla oluşturuldu!")
                            navController.navigate("login") // Başarı sonrası yönlendirme
                        }.onFailure { exception ->
                            println("Hata: ${exception.message}")
                        }
                    }
                )
            }) {
                Text("Kayıt Ol")
            }
            Text(
                text = "Hesabın var mı? Giriş Yap",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )
        }
        BackButton(navController = navController)
    }
}