package com.gen.stajyerim.model

data class Job(
    val title: String,
    val user: String,
    val reaction: Reaction? = null
)