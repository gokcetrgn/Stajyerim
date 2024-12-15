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
import com.gen.stajyerim.model.JobApplicant
import com.gen.stajyerim.model.JobPost
import com.gen.stajyerim.model.JobReaction
import com.gen.stajyerim.model.PostInfo
import com.gen.stajyerim.ui.components.BackButton
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishedPostsScreen(
    navController: NavHostController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Yayımlanan iş ilanları
    val publishedJobs = remember { mutableStateOf(listOf<JobPost>()) }

    // Başvuranlar ve Tepkiler
    val applicants = remember { mutableStateOf<Map<String, JobApplicant>>(emptyMap()) }
    val reactions = remember { mutableStateOf<Map<String, JobReaction>>(emptyMap()) }

    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser

    // İlanları ve başvuranları almak
    LaunchedEffect(Unit) {
        if (currentUser == null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Lütfen giriş yapın.")
            }
        } else {
            // "posts" koleksiyonundan verileri alıyoruz
            db.collection("posts")
                .whereEqualTo("userId", currentUser.uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("FirestoreError", "Error getting documents: ", e)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("İlanlar yüklenirken hata oluştu!")
                        }
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {
                            val jobs = snapshot.documents.mapNotNull { doc ->
                                try {
                                    val jobPost =
                                        doc.toObject(JobPost::class.java)?.copy(id = doc.id)
                                    jobPost
                                } catch (e: Exception) {
                                    Log.e("MappingError", "Error mapping document: ${doc.id}", e)
                                    null
                                }
                            }
                            // Yayımlanan ilanları kaydediyoruz
                            publishedJobs.value = jobs
                            Log.d("FirestoreData", "Published jobs: ${jobs.size} jobs found")
                        } else {
                            publishedJobs.value = emptyList()
                            Log.d("FirestoreData", "No jobs found")
                        }
                    }
                }

            // "postInfo" koleksiyonundan başvuranları ve tepki verenleri alıyoruz
            if (publishedJobs.value.isNotEmpty()) {
                db.collection("postInfo")
                    .whereIn("title", publishedJobs.value.map { it.title }) // İlanlara göre filtreleme
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.e("FirestoreError", "Error getting documents: ", e)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Başvuranlar ve tepkiler yüklenirken hata oluştu!")
                            }
                            return@addSnapshotListener
                        }

                        if (snapshot != null) {
                            if (!snapshot.isEmpty) {
                                // Başvuranlar ve Tepkiler verisini işliyoruz
                                snapshot.documents.forEach { doc ->
                                    Log.d("FirestoreData", "Document data: ${doc.data}")
                                    val postInfo = doc.toObject(PostInfo::class.java)
                                    postInfo?.let { info ->
                                        applicants.value = info.applicants
                                        reactions.value = info.reactions
                                        // Başvuranları ve Tepkileri saklıyoruz
                                        Log.d("FirestoreData", "PostInfo: Applicants count = ${info.applicants.size}, Reactions count = ${info.reactions.size}")
                                    }
                                }
                            }
                        }
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
                            applicants = applicants.value, // Başvuranlar verisini gönderiyoruz
                            reactions = reactions.value,  // Tepkiler verisini gönderiyoruz
                            onProfileClick = { userId ->
                                // Profil ekranına yönlendirme
                                navController.navigate("profile/$userId")
                            },

                            onDeleteClick = { jobId ->
                                // Silme işlemi
                                coroutineScope.launch {
                                    try {
                                        // "posts" koleksiyonundan ilanı sil
                                        db.collection("posts").document(jobId).delete()

                                        // "postInfo" koleksiyonundan ilişkili post bilgilerini sil
                                        db.collection("postInfo")
                                            .whereEqualTo("title", jobPost.title) // İlanın başlığına göre filtreleme
                                            .get()
                                            .addOnSuccessListener { querySnapshot ->
                                                querySnapshot.documents.forEach { doc ->
                                                    // İlgili postInfo dokümanını sil
                                                    db.collection("postInfo").document(doc.id).delete()
                                                }
                                                Log.d("Firestore", "Related postInfo documents deleted")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("Firestore", "Error deleting related postInfo documents", e)
                                            }

                                        snackbarHostState.showSnackbar("İlan başarıyla silindi.")
                                    } catch (e: Exception) {
                                        Log.e("DeleteError", "Error deleting post: ", e)
                                        snackbarHostState.showSnackbar("İlan silinirken bir hata oluştu.")
                                    }
                                }
                            },

                            onEditClick = { jobId ->
                                // Düzenleme ekranına yönlendirme
                                navController.navigate("editPost/$jobId")
                            }
                        )
                    }
                }
            }
        }
    }
    BackButton(navController = navController)
}


@Composable
fun PublishedJobItem(
    jobPost: JobPost,
    applicants: Map<String, JobApplicant>,
    reactions: Map<String, JobReaction>,
    onProfileClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onEditClick: (String) -> Unit
) {
    var showApplicantsDialog by remember { mutableStateOf(false) }
    var showReactionsDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Beğeni ekleme
    fun addReaction(jobId: String, reaction: String) {
        if (currentUser != null) {
            val reactionData = mapOf(
                "userId" to currentUser.uid,
                "reaction" to reaction
            )
            db.collection("posts")
                .document(jobId)
                .collection("reactions")
                .add(reactionData)
                .addOnSuccessListener {
                    db.collection("posts").document(jobId)
                        .update("reactionsCount", FieldValue.increment(1))
                }
                .addOnFailureListener { e ->
                    Log.e("AddReactionError", "Error adding reaction: ", e)
                }
        }
    }

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
                Text(
                    text = jobPost.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

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
                    text = "Başvuranlar: ${applicants.size}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Tepkiler Butonu
            TextButton(onClick = { showReactionsDialog = true }) {
                Text(
                    text = "Tepkiler: ${reactions.size}",
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
                    if (applicants.isEmpty()) {
                        Text("Henüz başvuru yapılmamış.")
                    } else {
                        applicants.values.forEach { applicant ->
                            TextButton(onClick = {
                                onProfileClick(applicant.userId)
                                showApplicantsDialog = false
                            }) {

                                Text("- Kullanıcı Adı: ${applicant.userName}")
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
                    if (reactions.isEmpty()) {
                        Text("Henüz tepki verilmemiş.")
                    } else {
                        reactions.values.forEach { reaction ->
                            TextButton(onClick = {
                                onProfileClick(reaction.userId)
                                showReactionsDialog = false
                            }) {
                                Text("- Kullanıcı Adı: ${reaction.userName}, Tepki: ${reaction.reaction}")
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

