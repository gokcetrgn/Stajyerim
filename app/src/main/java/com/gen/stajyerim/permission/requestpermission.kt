package com.gen.stajyerim.permission

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun RequestPermissionSection(
    permission: String,
    rationaleMessage: String,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(permission) {
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                permission
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(permission)
        } else {
            onPermissionGranted()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = rationaleMessage)
    }
}