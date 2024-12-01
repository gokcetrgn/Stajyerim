package com.gen.stajyerim.model

data class User(
    val email: String,
    val name: String,
    val surname: String? = null,
    val companyName: String? = null,
    val companyNumber: String? = null,
    val profession: String? = null,
    val userType: String
)

fun User.toMap(): Map<String, Any> {
    return mapOf(
        "email" to email,
        "name" to name,
        "surname" to (surname ?: ""),
        "companyName" to (companyName ?: ""),
        "companyNumber" to (companyNumber ?: ""),
        "profession" to (profession ?: ""),
        "userType" to userType
    )
}