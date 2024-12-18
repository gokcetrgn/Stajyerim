package com.gen.stajyerim.ui.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gen.stajyerim.model.Job
import com.gen.stajyerim.model.JobApplicant
import com.gen.stajyerim.model.JobPost
import com.gen.stajyerim.model.JobReaction
import com.gen.stajyerim.model.PostInfo
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
            db.collection("postInfo")
                .get()
                .addOnSuccessListener { snapshot ->
                    val jobs = snapshot.documents.mapNotNull { doc ->
                        val postInfo = doc.toObject(PostInfo::class.java)
                        postInfo?.takeIf { post ->
                            post.applicants.values.any { it.userId == userId }
                        }?.let { jobPost ->
                            JobPost(title = postInfo.title, userId = postInfo.userId) // Başvurduğumuz ilanları alıyoruz
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
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
                jobPost = job,
                onDeleteClick = { job.title?.let { it1 -> cancelApplication(it1) } }, // Başvuruyu iptal etme
                showApplyButton = true
            )
        }
    }
    BackButton(navController = navController)
}
@Composable
fun JobItem(
    jobPost: Job,
    onDeleteClick: (String) -> Unit,
    showApplyButton: Boolean
) {
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
                jobPost.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (!showApplyButton) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Başvuruyu Geri Çek",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable {
                                jobPost.title?.let { onDeleteClick(it) }
                            }
                    )
                }
            }
        }
    }
}

fun cancelApplication(jobTitle: String) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    currentUserId?.let { userId ->
        FirebaseFirestore.getInstance().collection("postInfo")
            .whereEqualTo("title", jobTitle)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { doc ->
                    val postInfo = doc.toObject(PostInfo::class.java)
                    postInfo?.let {
                        val updatedApplicants = it.applicants.toMutableMap()
                        updatedApplicants.remove(userId)

                        FirebaseFirestore.getInstance().collection("postInfo")
                            .document(doc.id)
                            .update("applicants", updatedApplicants)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { exception ->
                                exception.printStackTrace()
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }
}

