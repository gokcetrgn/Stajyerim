package com.gen.stajyerim.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.MaterialTheme.colors
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(navController: NavHostController, jobId: String) {
    val db = Firebase.firestore
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var jobTitle by remember { mutableStateOf("") }
    var jobDescription by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // İlan bilgilerini getir
    LaunchedEffect(jobId) {
        db.collection("posts").document(jobId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    jobTitle = document.getString("title") ?: ""
                    jobDescription = document.getString("description") ?: ""
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                Log.e("EditError", "Hata: ", e)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("İlan yüklenirken bir hata oluştu.")
                }
                isLoading = false
            }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("İlanı Düzenle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            // Yüklenme ekranı
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Düzenleme ekranı
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // İlan başlığı giriş alanı
                TextField(
                    value = jobTitle,
                    onValueChange = { jobTitle = it },
                    label = { Text("İlan Başlığı") },
                    modifier = Modifier.fillMaxWidth()
                )

                // İlan içeriği giriş alanı
                TextField(
                    value = jobDescription,
                    onValueChange = { jobDescription = it },
                    label = { Text("İlan İçeriği") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )

                // Güncelleme butonu
                Button(
                    onClick = {
                        val updateData = mapOf("title" to jobTitle,
                            "description" to jobDescription,
                            )
                        db.collection("posts").document(jobId)
                            .update(updateData)
                            .addOnSuccessListener {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("İlan başarıyla güncellendi!")
                                }
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                Log.e("UpdateError", "Hata: ", e)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("İlan güncellenirken bir hata oluştu.")
                                }
                            }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xffba68c8),
                        contentColor = Color.White
                    ),
                ) {
                    Text("Güncelle")
                }
            }
        }
    }
}
