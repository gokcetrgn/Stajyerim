package com.gen.stajyerim.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun debounceSearch(
    query: String,
    delayMillis: Long = 300L,
    onSearchTriggered: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(query) {
        debounceJob?.cancel()

        debounceJob = coroutineScope.launch {
            delay(delayMillis)
            onSearchTriggered(query)
        }
    }
}