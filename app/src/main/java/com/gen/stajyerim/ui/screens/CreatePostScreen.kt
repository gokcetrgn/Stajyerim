package com.gen.stajyerim.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.rememberNavController
import com.gen.stajyerim.ui.theme.Purple40
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun CreatePostScreen(
    navController: NavHostController
) {
    var jobTitle by remember { mutableStateOf("") }
    var jobDescription by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // İlan ekleme işlemi
    fun createPost() {
        if (jobTitle.isEmpty() || jobDescription.isEmpty()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Tüm alanları doldurmalısınız!")
            }
            return
        }

        isLoading = true

        try {
            // Firebase'e ilan ekleme işlemi
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid
            val db = FirebaseFirestore.getInstance()

            if (userId != null) {
                val post = mapOf(
                    "title" to jobTitle,
                    "description" to jobDescription,
                    "userId" to userId,
                    "timestamp" to System.currentTimeMillis()
                )

                db.collection("posts")
                    .add(post)
                    .addOnSuccessListener {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("İlan başarıyla paylaşıldı!")
                        }
                        navController.popBackStack()
                    }
                    .addOnFailureListener { exception ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("İlan paylaşılırken hata oluştu: ${exception.localizedMessage}")
                        }
                    }
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Kullanıcı girişi yapmanız lazım!")
                }
            }
        } catch (e: Exception) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Bilinmeyen bir hata oluştu: ${e.localizedMessage}")
            }
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "İlan Oluştur",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it },
                label = { Text("İlan Başlığı") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = jobDescription,
                onValueChange = { jobDescription = it },
                label = { Text("İlan Detayı") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { createPost() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xffba68c8), // Mor tonlarında arka plan rengi
                    contentColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("İlanı Paylaş")
            }
        }
    }
}
