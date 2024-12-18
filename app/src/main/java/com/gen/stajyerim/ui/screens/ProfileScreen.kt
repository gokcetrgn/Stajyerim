package com.gen.stajyerim.ui.screens

import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.Column
import android.os.Build
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.gen.stajyerim.model.User
import com.gen.stajyerim.ui.components.BackButton
import com.gen.stajyerim.viewmodel.AuthViewModel


@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userId: String
) {
    var userState by remember { mutableStateOf<User?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }


    authViewModel.fetchUserProfile(userId) { user, error ->
        if (error != null) {
            Log.e("ProfileScreen", "Kullanıcı bilgisi alınırken hata: $error")
        } else if (user == null) {
            Log.e("ProfileScreen", "Kullanıcı bilgisi bulunamadı")
        } else {
            Log.d("ProfileScreen", "Kullanıcı bilgisi: $user")
        }
        userState = user
        isLoading.value = false
    }

    if (isLoading.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bilgiler yükleniyor...")
        }
    } else if (userState == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Kullanıcı bulunamadı.")
        }
    } else {
        val user = userState!!
        var name by remember { mutableStateOf(user.name) }
        var surname by remember { mutableStateOf(user.surname ?: "") }
        var email by remember { mutableStateOf(user.email) }
        var companyName by remember { mutableStateOf(user.companyName ?: "") }
        var profession by remember { mutableStateOf(user.profession ?: "") }
        var summary by remember { mutableStateOf(user.summary ?: "") }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            item {
                Text(text = "Profil", style = MaterialTheme.typography.headlineMedium)
            }

            item {
                if (user.userType == "student") {
                    ProfileImagePicker(isEditing) { uri -> /* Fotoğraf işlemleri */ }
                    Spacer(modifier = Modifier.height(20.dp))

                    UserInfoCard(isEditing, name, surname, email,summary) { newName, newSurname, newEmail,newSummary ->
                        name = newName
                        surname = newSurname
                        email = newEmail
                        summary = newSummary
                    }


                    Spacer(modifier = Modifier.height(12.dp))

                    EducationSection(isEditing)
                    Spacer(modifier = Modifier.height(12.dp))
                    CertificateSection(isEditing)
                    Spacer(modifier = Modifier.height(12.dp))
                    CvSection(isEditing)

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isEditing) {
                        Button(
                            modifier = Modifier.width(200.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFBF89D4)
                            ),
                            onClick = {
                                // "Kaydet" butonuna basıldığında düzenlemeyi kaydediyoruz
                                isEditing = false
                                authViewModel.updateUserProfile(
                                    user.copy(
                                        name = name,
                                        surname = surname,
                                        companyName = companyName,
                                        profession = profession,
                                        summary = summary
                                    )
                                ) { success, error ->
                                    if (success) {
                                        Log.d("ProfileScreen", "Profile updated successfully")
                                    } else {
                                        error?.let { Log.e("ProfileScreen", "Error updating profile: $it") }
                                    }
                                }
                            }
                        ) {
                            Text("Kaydet")
                        }
                    } else {
                        Button(
                            onClick = { isEditing = !isEditing },
                            modifier = Modifier.width(300.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xffba68c8)
                            ),
                        ) {
                            Text(if (isEditing) "Düzenlemeyi Kapat" else "Düzenle")
                        }
                    }
                }
                else if (user.userType == "company") {
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Şirket Adı") },
                        enabled = isEditing
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("E-posta") },
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    ProfessionTextField(profession = profession, isEditing = isEditing) { profession = it }

                    Spacer(modifier = Modifier.height(6.dp))
                    if (isEditing) {
                        Button(
                            modifier = Modifier.width(200.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFBF89D4)
                            ),
                            onClick = {
                                // "Kaydet" butonuna basıldığında düzenlemeyi kaydediyoruz
                                isEditing = false
                                authViewModel.updateUserProfile(
                                    user.copy(
                                        name = name,
                                        surname = surname,
                                        companyName = companyName,
                                        profession = profession,
                                        summary = summary
                                    )
                                ) { success, error ->
                                    if (success) {
                                        Log.d("ProfileScreen", "Profile updated successfully")
                                    } else {
                                        error?.let { Log.e("ProfileScreen", "Error updating profile: $it") }
                                    }
                                }
                            }
                        ) {
                            Text("Kaydet")
                        }
                    } else {

                        Button(
                            onClick = { isEditing = !isEditing },
                            modifier = Modifier.width(300.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xffba68c8)
                            ),
                        ) {
                            Text(if (isEditing) "Düzenlemeyi Kapat" else "Düzenle")
                        }
                    }
                }
            }
        }
    }
    BackButton(navController = navController)
}

@Composable
fun ProfileImagePicker(isEditing: Boolean, onImageSelected: (Uri?) -> Unit) {
    val context = LocalContext.current
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri.value = uri
        onImageSelected(uri)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "İzin verilmedi!", Toast.LENGTH_SHORT).show()
        }
    }

    if (isEditing) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    }

                    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                        imagePickerLauncher.launch("image/*")
                    } else {
                        permissionLauncher.launch(permission)
                    }
                },
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .align(Alignment.CenterHorizontally)
            ) {
                if (selectedImageUri.value != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = selectedImageUri.value),
                        contentDescription = "Seçilen Profil Fotoğrafı",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Fotoğraf", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    } else {
        selectedImageUri.value?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(model = uri),
                contentDescription = "Profil Fotoğrafı",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        } ?: run {
            Text("Fotoğraf yok")
        }
    }
}


data class Education(
    var university: String = "",
    var department: String = "",
    var grade: String = ""
)

@Composable
fun EducationSection(isEditing: Boolean) {
    var educationList by remember { mutableStateOf(listOf<Education>()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Eğitim Bilgisi", style = MaterialTheme.typography.titleMedium)

            educationList.forEachIndexed { index, education ->
                EducationItem(
                    education = education,
                    isEditing = isEditing,
                    onDelete = {
                        // Listeyi güncelle ve yeniden ata
                        educationList = educationList.toMutableList().also { it.removeAt(index) }
                    },
                    onEducationChange = { updatedEducation ->
                        educationList = educationList.toMutableList().also { it[index] = updatedEducation }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isEditing) {
                Button(onClick = {
                    // Yeni bir Education nesnesi ekle
                    educationList = educationList + Education()
                }) {
                    Text("Eğitim Ekle")
                }
            }
        }
    }
}

@Composable
fun EducationItem(
    education: Education,
    isEditing: Boolean,
    onDelete: () -> Unit,
    onEducationChange: (Education) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isEditing) {
            OutlinedTextField(
                value = education.university,
                onValueChange = { onEducationChange(education.copy(university = it)) },
                label = { Text("Üniversite") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = education.department,
                onValueChange = { onEducationChange(education.copy(department = it)) },
                label = { Text("Bölüm") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = education.grade,
                onValueChange = { onEducationChange(education.copy(grade = it)) },
                label = { Text("Not Ortalaması") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Sil", color = Color.White)
            }
        } else {
            Text("Üniversite: ${education.university.ifEmpty { "Bilgi yok" }}")
            Text("Bölüm: ${education.department.ifEmpty { "Bilgi yok" }}")
            Text("Not Ortalaması: ${education.grade.ifEmpty { "Bilgi yok" }}")
        }
    }
}
@Composable
fun uploadFileSection(isEditing: Boolean,
                      title: String,
                      onFileSelected: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val selectedFileUri = remember { mutableStateOf<Uri?>(null) }
    val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        selectedFileUri.value = uri
        onFileSelected(uri)
    }

    var showPermissionRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcher.launch(arrayOf("application/pdf"))
        } else {
            showPermissionRationale = true
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isEditing) {
            Button(

                onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(permission)
                    } else {
                        launcher.launch(arrayOf("application/pdf"))
                    }
                }
            ) {
                Text(title)
            }

            if (showPermissionRationale) {
                Text(
                    "Dosya seçmek için izin vermelisiniz.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            selectedFileUri.value?.let { uri ->
                Text(text = "Seçilen dosya: $uri", modifier = Modifier.padding(top = 8.dp))
            } ?: Text("Henüz dosya seçilmedi.")
        }
    }
}
@Composable
fun ProfessionTextField(profession: String, isEditing: Boolean, onProfessionChange: (String) -> Unit) {
    val context = LocalContext.current
    val wordLimit = 500

    fun getWordCount(text: String): Int {
        return text.trim().split("\\s+".toRegex()).size
    }

    OutlinedTextField(
        value = profession,
        onValueChange = { newText ->
            val wordCount = getWordCount(newText)
            if (wordCount <= wordLimit) {
                onProfessionChange(newText)
            } else {

                Toast.makeText(context, "500 kelime sınırını geçemezsiniz!", Toast.LENGTH_SHORT).show()
            }
        },
        label = { Text("Şirket Bilgisi") },
        enabled = isEditing,
        modifier = Modifier.fillMaxWidth()
    )
}
@Composable
fun SummaryTextField(summary: String, isEditing: Boolean, onSummaryChange: (String) -> Unit) {
    val context = LocalContext.current
    val wordLimit = 300

    // Kelime sayısını hesaplayan fonksiyon
    fun getWordCount(text: String): Int {
        return text.trim().split("\\s+".toRegex()).size
    }

    OutlinedTextField(
        value = summary,
        onValueChange = { newText ->
            val wordCount = getWordCount(newText)
            if (wordCount <= wordLimit) {
                onSummaryChange(newText)
            } else {
                Toast.makeText(context, "300 kelime sınırını geçemezsiniz!", Toast.LENGTH_SHORT).show()
            }
        },
        label = { Text("Kişisel Özet") },
        enabled = isEditing,
        modifier = Modifier.fillMaxWidth(),
        maxLines = 5,  // Fazla uzun yazıları engellemek için
        supportingText = {
            Text("Kalan Kelimeler: ${wordLimit - getWordCount(summary)}")
        }
    )
}
@Composable
fun CertificateSection(isEditing: Boolean) {
    var certificates = remember { mutableStateListOf<Uri?>() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Sertifikalar", style = MaterialTheme.typography.titleMedium)

            certificates.forEachIndexed { index, certificateUri ->
                CertificateItem(
                    certificateUri = certificateUri,
                    isEditing = isEditing,
                    onDelete = { certificates.removeAt(index) },
                    onUpdate = { newUri -> certificates[index] = newUri }
                )
            }

            if (isEditing) {
                Button(onClick = {
                    certificates.add(null) // Yeni boş sertifika ekle
                }) {
                    Text("Sertifika Ekle")
                }
            }
        }
    }
}

@Composable
fun CertificateItem(
    certificateUri: Uri?,
    isEditing: Boolean,
    onDelete: () -> Unit,
    onUpdate: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "PDF seçmek için izin verilmedi!", Toast.LENGTH_SHORT).show()
        }
    }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdate(uri)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isEditing) {
            Button(onClick = {
                val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(permission)
                } else {
                    pdfPickerLauncher.launch(arrayOf("application/pdf"))
                }
            }) {
                Text("PDF Seç")
            }

            certificateUri?.let {
                val fileName = it.lastPathSegment ?: "Bilinmeyen Dosya"
                Text("Seçilen Sertifika: $fileName")
            } ?: Text("Henüz bir sertifika seçilmedi")

            certificateUri?.let {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Sertifikayı Sil")
                }
            }
        } else {
            certificateUri?.let {
                val fileName = it.lastPathSegment ?: "Bilinmeyen Dosya"
                Text("Sertifika: $fileName")
            } ?: Text("Sertifika mevcut değil")
        }
    }
}
@Composable
fun CvSection(isEditing: Boolean) {
    val context = LocalContext.current
    val selectedCvUri = remember { mutableStateOf<Uri?>(null) }
    val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE

    val cvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedCvUri.value = uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cvPickerLauncher.launch("application/pdf")
        } else {
            Toast.makeText(
                context,
                "Dosya yüklemek için izin vermelisiniz. Ayarlardan izni etkinleştirin.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("CV", style = MaterialTheme.typography.titleMedium)

            if (isEditing) {
                Button(onClick = {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(permission)
                    } else {
                        cvPickerLauncher.launch("application/pdf")
                    }
                }) {
                    Text("CV Yükle")
                }

                selectedCvUri.value?.let { uri ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Yüklendi: ${uri.lastPathSegment ?: "Bilinmeyen dosya"}")
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { selectedCvUri.value = null }) {
                            Icon(Icons.Default.Delete, contentDescription = "Sil")
                        }
                    }
                }
            } else {
                selectedCvUri.value?.let {
                    Text("CV Yüklendi: ${it.lastPathSegment ?: "Bilinmeyen dosya"}")
                } ?: Text("Henüz CV yüklenmedi.")
            }
        }
    }
}
@Composable
fun UserInfoCard(
    isEditing: Boolean,
    name: String,
    surname: String,
    email: String,
    summary: String,
    onInfoChanged: (String, String, String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { onInfoChanged(it, surname, email, summary) },
                label = { Text("Ad") },
                enabled = isEditing
            )
            OutlinedTextField(
                value = surname,
                onValueChange = { onInfoChanged(name, it, email, summary) },
                label = { Text("Soyad") },
                enabled = isEditing
            )
            OutlinedTextField(
                value = email,
                onValueChange = { onInfoChanged(name, surname, it, summary) },
                label = { Text("E-posta") },
                enabled = false
            )

            SummaryTextField(
                summary = summary,
                isEditing = isEditing,
                onSummaryChange = { onInfoChanged(name, surname, email, it) }
            )
        }
    }
}