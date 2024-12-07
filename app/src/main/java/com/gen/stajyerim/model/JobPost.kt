package com.gen.stajyerim.model

data class JobPost(
    val id: String = "",
    val title: String = "",
    val publisherId: String = "",
    val reactions: Map<String, JobReaction> = emptyMap(),
    val applicants: Map<String, JobApplicant> = emptyMap(),
)