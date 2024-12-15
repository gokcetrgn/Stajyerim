package com.gen.stajyerim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.gen.stajyerim.model.Job
import kotlinx.coroutines.launch


@Composable
fun PostDetailPage(navController: NavHostController, title: String) {
    val db = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State to hold job data
    var job by remember { mutableStateOf<Job?>(null) }
    var fullName by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetching the job post from Firestore
    LaunchedEffect(title) {
        db.collection("posts")
            .whereEqualTo("title", title)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    job = querySnapshot.documents[0].toObject(Job::class.java)
                    val userId = job?.userId
                    // Fetch username from users collection using userId
                    userId?.let {
                        db.collection("users").document(it)
                            .get()
                            .addOnSuccessListener { userSnapshot ->
                                val name = userSnapshot.getString("name")
                                val surname = userSnapshot.getString("surname")
                                fullName = "${name ?: "Ad"} ${surname ?: "Soyad"}"
                            }
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("İlan bulunamadı!")
                    }
                }
                isLoading = false
            }
            .addOnFailureListener {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Veri çekilirken hata oluştu.")
                }
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
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                job?.let { post ->
                    // Başlık kısmı
                    Text(
                        text = post.title ?: "Başlık Yok",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6650a4),
                            fontSize = 28.sp,
                            letterSpacing = 1.5.sp,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(color = Color(0xFFEDE7F6), shape = MaterialTheme.shapes.medium)
                            .padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Açıklama kısmı
                    Text(
                        text = post.description ?: "Açıklama yok.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.DarkGray,
                            fontSize = 18.sp,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(color =Color( 0xFFEDE7F6), shape = MaterialTheme.shapes.small)
                            .padding(16.dp)
                    )

                    // İlanın sahipleri
                    Text(
                        text = "İlanı Sahibi: ${fullName ?: "Bilgi yok"}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.DarkGray,
                            fontSize = 18.sp,
                            lineHeight = 24.sp
                    ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(color = Color(0xFFEDE7F6), shape = MaterialTheme.shapes.small)
                            .padding(16.dp)
                            .clickable { navController.navigate("profile/${post.userId}") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                } ?: run {
                    Text(
                        text = "İlan bulunamadı.",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
