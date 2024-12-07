package com.gen.stajyerim.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gen.stajyerim.model.Job
import com.gen.stajyerim.ui.components.BackButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppliedPostsScreen(navController: NavHostController) {
    val appliedJobs = remember { mutableStateOf(listOf<Job>()) }

    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        currentUserId?.let { userId ->
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                val appliedPosts = document.get("appliedPosts") as? List<String> ?: emptyList()
                appliedJobs.value = appliedPosts.map { title ->
                    Job(title = title)
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(appliedJobs.value) { job ->
            JobItem(
                job = job,
                onApplyClick = { /* Başvuru butonu gizlenebilir */ },
                onReactClick = { reaction -> /* Reaksiyon işlemleri */ },
                onCommentClick = { /* Yorum ekleme işlemleri */ },
                onProfileClick = { /* Profil görüntüleme işlemleri */ },
                navController = navController,
                showApplyButton = false // Başvuru butonunu gizle
            )
        }
    }
    BackButton(navController = navController)
}