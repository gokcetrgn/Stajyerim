package com.gen.stajyerim.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.gen.stajyerim.R
import com.gen.stajyerim.data.repository.AuthRepository
import com.gen.stajyerim.ui.components.BackButton
import com.gen.stajyerim.ui.components.CustomTextField
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@Composable
fun SignUpScreen(
    navController: NavHostController,
    userType: String, // "student" veya "company"
    authRepository: AuthRepository
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity ?: run {
        println("Hata: Bu ekran bir FragmentActivity üzerinde çalıştırılmalı.")
        return
    }



    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val companyName = remember { mutableStateOf("") }
    val companyNumber = remember { mutableStateOf("") }
    val profession = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val verificationCode = remember { mutableStateOf("") }

    var isCodeSent by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf("") }
    var isVerificationCompleted by remember { mutableStateOf(false) }

    val callbacks = remember {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                isVerificationCompleted = true
                println("Doğrulama başarılı: $credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                println("Doğrulama hatası: ${e.message}")
            }

            override fun onCodeSent(verId: String, token: PhoneAuthProvider.ForceResendingToken) {
                verificationId = verId
                isCodeSent = true
                println("Kod gönderildi: $verId")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.signup),
            contentDescription = "Landing Page Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Kayıt Ol", style = MaterialTheme.typography.headlineSmall)
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

            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(value = phoneNumber.value, onValueChange = { phoneNumber.value = it }, label = "Telefon Numarası")

            if (isCodeSent) {
                CustomTextField(value = verificationCode.value, onValueChange = { verificationCode.value = it }, label = "Doğrulama Kodu")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                if (isCodeSent && verificationCode.value.isNotEmpty()) {
                    val credential = PhoneAuthProvider.getCredential(
                        verificationId,
                        verificationCode.value // Doğrulama kodu
                    )

                    authRepository.firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                // Kayıt işlemi başarıyla tamamlandığında
                                val userData = mutableMapOf<String, Any>(
                                    "email" to email.value,
                                    "name" to name.value,
                                    "surname" to surname.value,
                                    "userType" to userType
                                )

                                if (userType == "company") {
                                    userData["profession"] = profession.value
                                    userData["companyName"] = companyName.value
                                    userData["companyNumber"] = companyNumber.value
                                }

                                authRepository.registerUser(
                                    email = email.value,
                                    password = password.value,
                                    userData = userData,
                                    onComplete = { result ->
                                        result.onSuccess {
                                            // Kayıt sonrası yönlendirme
                                            navController.navigate("home?userType=$userType")
                                        }.onFailure { exception ->
                                            println("Hata: ${exception.message}")
                                        }
                                    }
                                )
                            } else {
                                println("Telefon numarası doğrulaması başarısız.")
                            }
                        }
                } else {
                    // Telefon doğrulama işlemi yapılmamışsa, kullanıcı kaydını başlat
                    val userData = mutableMapOf<String, Any>(
                        "email" to email.value,
                        "name" to name.value,
                        "surname" to surname.value,
                        "userType" to userType
                    )

                    if (userType == "company") {
                        userData["profession"] = profession.value
                        userData["companyName"] = companyName.value
                        userData["companyNumber"] = companyNumber.value
                    }

                    authRepository.registerUser(
                        email = email.value,
                        password = password.value,
                        userData = userData,
                        onComplete = { result ->
                            result.onSuccess {
                                // Doğrulama kodu gönderme işlemi başlat
                                PhoneAuthProvider.verifyPhoneNumber(
                                    PhoneAuthOptions.newBuilder(authRepository.firebaseAuth)
                                        .setPhoneNumber(phoneNumber.value) // Telefon numarası
                                        .setTimeout(60L, TimeUnit.SECONDS) // Zaman aşımı
                                        .setActivity(activity) // Activity bağlamı
                                        .setCallbacks(callbacks) // Callbacks
                                        .build()
                                )
                            }.onFailure { exception ->
                                println("Hata: ${exception.message}")
                            }
                        }
                    )
                }
            }) {
                Text(if (isCodeSent) "Kodu Doğrula" else "Kayıt Ol")
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
