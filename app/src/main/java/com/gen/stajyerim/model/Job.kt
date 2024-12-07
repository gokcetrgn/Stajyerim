package com.gen.stajyerim.model


data class Job(
    val title: String? = null,
    val timestamp: Long? = null,
    val userId: String? = null,
    val reaction: Reaction? = null,
    val applicants: List<Applicant>? = null,
    val reactions: List<ReactionInfo>? = null
)