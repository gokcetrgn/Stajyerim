package com.gen.stajyerim.model

data class PostInfo(
    val id: String = "",
    val timestamp: Long = 0,
    val title: String = "",
    val userId: String = "",
    val userName: String = "",
    val reactions: Map<String, JobReaction> = emptyMap(),
    val reactionCount: Int = 0,
    val applicants: Map<String, JobApplicant> = emptyMap(),
    val applicantCount: Int = 0,
)