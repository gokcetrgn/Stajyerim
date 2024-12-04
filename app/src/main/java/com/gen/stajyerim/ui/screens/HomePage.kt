package com.gen.stajyerim.ui.screens

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
import com.gen.stajyerim.model.Job
import com.gen.stajyerim.model.Reaction
import com.gen.stajyerim.ui.theme.Purple40
import com.gen.stajyerim.ui.theme.PurpleGrey40
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    navController: NavHostController,
    userType: String? = "unknown"
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }

    val jobList = remember { mutableStateOf(listOf<Job>()) }

    val db = Firebase.firestore

    LaunchedEffect(Unit) {
        db.collection("jobs")
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
                            navController.navigate("createPost")
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
                        onValueChange = { query -> searchQuery = query },
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
                                        snackbarHostState.showSnackbar("${job.title} ilanına başvuruldu!") // job nesnesine doğrudan erişim
                                    }
                                },
                                onReactClick = { reaction ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Tepki: $reaction")
                                    }
                                },
                                onCommentClick = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Yorum eklendi.")
                                    }
                                },
                                onProfileClick = {
                                    navController.navigate("profile/${job.user}")
                                }
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
            .background(Color.White.copy(alpha = 0.9f)) // Arka plan şeffaflığını düzelttik
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Başlık
        Text(
            text = "Menü",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = Color.Gray, thickness = 1.dp)

        // Profil
        DrawerItem(
            title = "Profil",
            icon = Icons.Default.Person,
            onClick = {
                navController.navigate("profile")
            }
        )

        // Başvurulan İlanlar
        DrawerItem(
            title = "Başvurulan İlanlar",
            icon = Icons.Default.ThumbUp,
            onClick = {
                navController.navigate("appliedPosts")
            }
        )

        // Yayınlanan İlanlar
        DrawerItem(
            title = "Yayınlanan İlanlar",
            icon = Icons.Default.Search,
            onClick = {
                navController.navigate("publishedPosts")
            }
        )

        // Çıkış Yap
        DrawerItem(
            title = "Çıkış Yap",
            icon = Icons.Default.ExitToApp,
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("landing")
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
            tint = Purple40,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black  // Metin rengini siyah yaparak okunabilirliği artırdık
        )
    }
}


@Composable
fun JobItem(
    job: Job,
    onApplyClick: () -> Unit,
    onReactClick: (Reaction) -> Unit,
    onCommentClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var reactionState by remember { mutableStateOf(job.reaction) }  // Buton tıklama durumunu tutacak state

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = job.title, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Başvur butonu
                Button(
                    onClick = onApplyClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xffba68c8), // Mor tonlarında arka plan rengi
                        contentColor = Color.White // Beyaz metin rengi
                    )
                ) {
                    Text("Başvur")
                }

                // Like butonu
                Button(
                    onClick = {
                        // Eğer already liked ise, dislike'a dön
                        reactionState = if (reactionState == Reaction.Like) null else Reaction.Like
                        reactionState?.let { onReactClick(it) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (reactionState == Reaction.Like) Color(0xffba68c8) else PurpleGrey40,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.ThumbUp, contentDescription = "Like")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Like")
                }

                // Dislike butonu
                Button(
                    onClick = {
                        // Eğer already disliked ise, like'a dön
                        reactionState = if (reactionState == Reaction.Dislike) null else Reaction.Dislike
                        reactionState?.let { onReactClick(it) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (reactionState == Reaction.Dislike) Color(0xffba68c8) else PurpleGrey40,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.ThumbDown, contentDescription = "Dislike")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dislike")
                }
            }
        }
    }
}

