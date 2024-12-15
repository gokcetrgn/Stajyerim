package com.gen.stajyerim.model

data class JobPost(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Long = 0,
    val userId: String = "",
    val userName: String = "",
    val publisherId: String = "",
    val applicantCount: Int = 0,
    val reactionCount: Int = 0,
    val applicants: Map<String, Applicant> = emptyMap(),
    val reactions: Map<String, Reaction> = emptyMap()
)
