package com.gen.stajyerim.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishedPostsScreen(
    navController: NavHostController,
    userType: String = "unknown",
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val publishedJobs = remember { mutableStateOf(listOf<JobPost>()) }

    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        if (currentUser == null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Lütfen giriş yapın.")
            }
        } else {
            db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereEqualTo("userId", currentUser.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("FirestoreError", "Error getting documents: ", e)
                        coroutineScope.launch {
                            //snackbarHostState.showSnackbar("İlanlar yüklenirken hata oluştu!")
                        }
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val jobs = snapshot.documents.mapNotNull { doc ->
                            try {
                                val jobPost = doc.toObject(JobPost::class.java)?.copy(id = doc.id)
                                jobPost
                            } catch (e: Exception) {
                                Log.e("MappingError", "Error mapping document: ${doc.id}", e)
                                null
                            }
                        }
                        publishedJobs.value = jobs
                    } else {
                        publishedJobs.value = emptyList()
                    }
                }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Yayınlanan İlanlar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (publishedJobs.value.isEmpty()) {
                Text(
                    text = "Henüz yayımlanan ilanınız yok.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(publishedJobs.value) { jobPost ->
                        PublishedJobItem(
                            jobPost = jobPost,
                            onProfileClick = { userId -> navController.navigate("profile/$userId") },
                            onDeleteClick = { jobId ->
                                db.collection("posts").document(jobId).delete()
                                    .addOnSuccessListener {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("İlan başarıyla silindi!")
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("DeleteError", "Silme işlemi başarısız: ", e)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("İlan silinirken bir hata oluştu.")
                                        }
                                    }
                            },
                            onEditClick = { jobId -> navController.navigate("editPost/$jobId") }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PublishedJobItem(
    jobPost: JobPost,
    onProfileClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit, // İlanı silmek için
    onEditClick: (String) -> Unit // İlanı düzenlemek için
) {
    var showApplicantsDialog by remember { mutableStateOf(false) }
    var showReactionsDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // İlan Başlığı
                Text(
                    text = jobPost.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                // Üç nokta menüsü
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menü"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Düzenle") },
                            onClick = {
                                showMenu = false
                                onEditClick(jobPost.id)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sil") },
                            onClick = {
                                showMenu = false
                                onDeleteClick(jobPost.id)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Başvuranlar Butonu
            TextButton(onClick = { showApplicantsDialog = true }) {
                Text(
                    text = "Başvuranlar: ${jobPost.applicants.size}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Tepkiler Butonu
            TextButton(onClick = { showReactionsDialog = true }) {
                Text(
                    text = "Tepkiler: ${jobPost.reactions.size}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }

    // Başvuranlar Dialog
    if (showApplicantsDialog) {
        AlertDialog(
            onDismissRequest = { showApplicantsDialog = false },
            title = { Text("Başvuranlar") },
            text = {
                Column {
                    if (jobPost.applicants.isEmpty()) {
                        Text("Henüz başvuru yapılmamış.")
                    } else {
                        jobPost.applicants.forEach { (key, applicant) ->
                            TextButton(onClick = {
                                onProfileClick(applicant.userId)
                                showApplicantsDialog = false
                            }) {
                                Text("- Kullanıcı ID: ${applicant.userId}, Kullanıcı Adı: ${applicant.userName}")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showApplicantsDialog = false }) {
                    Text("Kapat")
                }
            }
        )
    }

    // Tepkiler Dialog
    if (showReactionsDialog) {
        AlertDialog(
            onDismissRequest = { showReactionsDialog = false },
            title = { Text("Tepki Verenler") },
            text = {
                Column {
                    if (jobPost.reactions.isEmpty()) {
                        Text("Henüz tepki verilmemiş.")
                    } else {
                        jobPost.reactions.forEach { (key, reactions) ->
                            TextButton(onClick = {
                                onProfileClick(reactions.userId)
                                showReactionsDialog = false
                            }) {
                                Text("- Kullanıcı ID: ${reactions.userId}, Tepki: ${reactions.reaction}")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReactionsDialog = false }) {
                    Text("Kapat")
                }
            }
        )
    }
}



data class JobPost(
    val id: String = "",
    val title: String = "",
    val publisherId: String = "",
    val reactions: Map<String, JobReaction> = emptyMap(), // Tepkiler bir Map
    val applicants: Map<String, JobApplicant> = emptyMap(), // Başvuranlar bir Map
)

data class JobReaction(
    val reaction: String = "",
    val userId: String = "" // Tepki veren kullanıcı ID'si
)

data class JobApplicant(
    val userId: String = "",
    val userName: String = "" // Başvuran kullanıcı adı
)

