package com.gen.stajyerim.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gen.stajyerim.model.Applicant
import com.gen.stajyerim.model.Job
import com.gen.stajyerim.model.Reaction
import com.gen.stajyerim.model.ReactionInfo
import com.gen.stajyerim.ui.search.SearchManager
import com.gen.stajyerim.ui.search.debounceSearch
import com.gen.stajyerim.ui.theme.PurpleGrey40
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    navController: NavHostController, userType: String = "unknown",
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val searchManager = remember { SearchManager(FirebaseFirestore.getInstance()) }

    var searchQuery by remember { mutableStateOf("") }
    val jobList = remember { mutableStateOf(listOf<Job>()) }

    val db = FirebaseFirestore.getInstance()

    debounceSearch(
        query = searchQuery,
        onSearchTriggered = { query ->
            searchManager.searchJobs(
                query = query,
                onResult = { jobs ->
                    jobList.value = jobs
                },
                onError = { exception ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Hata: ${exception.message}")
                    }
                }
            )
        }
    )

    LaunchedEffect(Unit) {
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("İlanlar yüklenirken hata oluştu!")
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val jobs = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Job::class.java)
                    }
                    jobList.value = jobs
                }
            }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (drawerState.isOpen) {
                DrawerContent(navController = navController)
            }
        },
        content = {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "StajYerim",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menü")
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            if (userType == "unknown") {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("İlan oluşturabilmek için giriş yapmalısınız!")
                                }
                            } else {
                                navController.navigate("createPost")
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Color(0xffba68c8),
                        contentColor = Color.White
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "İlan Oluştur")
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            searchQuery = query
                            searchManager.searchJobs(
                                query = query,
                                onResult = { jobs ->
                                    jobList.value = jobs
                                },
                                onError = { exception ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Hata: ${exception.message}")
                                    }
                                }
                            )
                        },
                        label = { Text("Ara (İlan, Kişi, Şirket)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Ara")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(jobList.value) { job ->
                            JobItem(
                                job = job,
                                onApplyClick = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("${job.title} ilanına başvuruldu!")
                                        addApplicant(job)
                                    }
                                },
                                onReactClick = { reaction ->
                                    coroutineScope.launch {
                                        addReaction(job, reaction)
                                    }
                                },
                                onCommentClick = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Yorum eklendi.")
                                    }
                                },
                                onProfileClick = {
                                    job.userId?.let { userId ->
                                        navController.navigate("profile/$userId")
                                    } ?: coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Geçersiz kullanıcı profili.")
                                    }
                                },
                                navController = navController
                            )
                        }
                    }

                    Button(
                        onClick = {
                            navController.navigate("messages")
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Message, contentDescription = "Mesajlar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mesajlar")
                    }
                }
            }
        }
    )
}

@Composable
fun DrawerContent(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.9f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Menü",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = Color.Gray, thickness = 1.dp)

        DrawerItem(
            title = "Profil",
            icon = Icons.Default.Person,
            onClick = {
                navController.navigate("profile")
            }
        )

        DrawerItem(
            title = "Başvurulan İlanlar",
            icon = Icons.Default.ThumbUp,
            onClick = {
                navController.navigate("appliedPosts")
            }
        )

        DrawerItem(
            title = "Yayınlanan İlanlar",
            icon = Icons.Default.Search,
            onClick = {
                navController.navigate("publishedPosts")
            }
        )

        DrawerItem(
            title = "Çıkış Yap",
            icon = Icons.Default.ExitToApp,
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo(0)
                }
            }
        )
    }
}

@Composable
fun DrawerItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xffba68c8),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}@Composable
fun JobItem(
    job: Job,
    onApplyClick: () -> Unit,
    onReactClick: (Reaction) -> Unit,
    onCommentClick: () -> Unit,
    onProfileClick: () -> Unit,
    navController: NavHostController
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    var reactionState by remember { mutableStateOf<Reaction?>(null) }
    var applicantState by remember { mutableStateOf(false) }  // Başvuru durumu
    val jobTitle = job.title ?: ""

    LaunchedEffect(jobTitle) {
        val postRef = db.collection("posts").document(jobTitle)
        val snapshot = postRef.get().await()

        if (snapshot.exists()) {
            val reactions = snapshot.get("reactions") as? List<Map<String, Any>>
            reactions?.forEach { reaction ->
                val userId = reaction["userId"] as? String
                if (userId == currentUser?.uid) {
                    reactionState = Reaction.valueOf(reaction["reaction"] as? String ?: "")
                }
            }

            val applicants = snapshot.get("applicants") as? List<Map<String, Any>>
            applicants?.forEach { applicant ->
                val userId = applicant["userId"] as? String
                if (userId == currentUser?.uid) {
                    applicantState = true  // Kullanıcı başvuru yapmış
                }
            }
        }
    }

    var showAlertDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = job.title ?: "İlan Başlığı",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (currentUser == null) {
                        showAlertDialog = true
                    } else {
                        if (applicantState) {
                            showAlertDialog = true
                        } else {
                            addApplicant(job)
                            applicantState = true
                            onApplyClick()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xffba68c8),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Başvur")
            }

            Spacer(modifier = Modifier.height(12.dp))


            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(
                    onClick = {
                        reactionState = if (reactionState == Reaction.Like) null else Reaction.Like
                        reactionState?.let {
                            addReaction(job, it)
                            onReactClick(it)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Beğen",
                        tint = if (reactionState == Reaction.Like) Color(0xffba68c8) else PurpleGrey40
                    )
                }

                IconButton(
                    onClick = {
                        reactionState = if (reactionState == Reaction.Dislike) null else Reaction.Dislike
                        reactionState?.let {
                            addReaction(job, it)
                            onReactClick(it)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = "Beğenme",
                        tint = if (reactionState == Reaction.Dislike) Color(0xffba68c8) else PurpleGrey40
                    )
                }
            }
        }
    }

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = {
                Text("Giriş Yapılmadı")
            },
            text = {
                Text("Başvuru yapabilmek için giriş yapmalısınız.")
            },
            confirmButton = {
                TextButton(onClick = {
                    navController.navigate("login") // Giriş sayfasına yönlendir
                    showAlertDialog = false
                }) {
                    Text("Giriş Yap")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAlertDialog = false // AlertDialog'u kapat
                }) {
                    Text("Tamam")
                }
            }
        )
    }
}

fun addReaction(job: Job, reaction: Reaction) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    if (currentUserId != null) {
        val reactionInfo = ReactionInfo(userId = currentUserId, reaction = reaction.name)
        val postRef = FirebaseFirestore.getInstance().collection("posts").document(job.title ?: "")

        postRef.set(
            mapOf(
                "title" to job.title,
                "reactions" to listOf(reactionInfo)
            ), SetOptions.merge()
        )
            .addOnSuccessListener {
                Log.d("Firestore", "Yeni belge oluşturuldu ve reaksiyon eklendi!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Belge oluşturulamadı: ${e.localizedMessage}", e)
            }
    }
}


fun addApplicant(job: Job) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val currentUserName = FirebaseAuth.getInstance().currentUser?.displayName

    if (currentUserId != null && currentUserName != null) {
        val applicant = Applicant(userId = currentUserId, userName = currentUserName)
        val postRef = FirebaseFirestore.getInstance().collection("posts").document(job.title ?: "")

        postRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                postRef.update("applicants", FieldValue.arrayUnion(applicant))
                    .addOnSuccessListener {
                        Log.d("Firestore", "Başvuru başarıyla eklendi!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Başvuru eklenemedi: ${e.localizedMessage}", e)
                    }
            } else {
                postRef.set(
                    mapOf(
                        "title" to job.title,
                        "applicants" to listOf(applicant)
                    ), SetOptions.merge()
                )
                    .addOnSuccessListener {
                        Log.d("Firestore", "Yeni belge oluşturuldu ve başvuru eklendi!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Belge oluşturulamadı: ${e.localizedMessage}", e)
                    }
            }
        }
    }
}