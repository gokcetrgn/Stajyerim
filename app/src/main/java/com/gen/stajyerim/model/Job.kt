package com.gen.stajyerim.model

import com.gen.stajyerim.ui.screens.Applicant
import com.gen.stajyerim.ui.screens.Reaction
import com.gen.stajyerim.ui.screens.ReactionInfo

data class Job(
    val title: String? = null,
    val timestamp: Long? = null,
    val userId: String? = null,
    val reaction: Reaction? = null,
    val applicants: List<Applicant>? = null,
    val reactions: List<ReactionInfo>? = null
)