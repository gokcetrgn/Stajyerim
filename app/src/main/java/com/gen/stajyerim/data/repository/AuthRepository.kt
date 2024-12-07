package com.gen.stajyerim.data.repository


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {

    private val firestore = FirebaseFirestore.getInstance()

    fun registerUser(email: String, password: String, userData: Map<String, Any>, onComplete: (Result<Unit>) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = firebaseAuth.currentUser?.uid ?: return@addOnCompleteListener
                    val userRef = firestore.collection("users").document(uid)


                    val updatedUserData = userData.toMutableMap()
                    updatedUserData["userId"] = uid

                    userRef.set(updatedUserData)
                        .addOnCompleteListener { userTask ->
                            if (userTask.isSuccessful) {
                                onComplete(Result.success(Unit))
                            } else {
                                val exception = userTask.exception ?: Exception("Kullanıcı bilgileri kaydedilemedi.")
                                onComplete(Result.failure(exception))
                            }
                        }
                } else {
                    val exception = task.exception ?: Exception("Kullanıcı kaydedilemedi.")
                    onComplete(Result.failure(exception))
                }
            }
    }

    fun login(email: String, password: String, onComplete: (Result<Unit>) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(Result.success(Unit))
                } else {
                    val exception = task.exception ?: Exception("Bilinmeyen bir hata oluştu.")
                    onComplete(Result.failure(exception))
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    suspend fun getUserType(): Result<String> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: throw Exception("Kullanıcı oturum açmamış.")
            val snapshot = firestore.collection("users").document(uid).get().await()
            val userType = snapshot.getString("userType")
                ?: throw Exception("Kullanıcı tipi bulunamadı.")
            Result.success(userType)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
