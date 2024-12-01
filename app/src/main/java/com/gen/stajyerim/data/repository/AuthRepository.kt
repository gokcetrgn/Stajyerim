package com.gen.stajyerim.data.repository

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthRepository(val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {

    private val firestore = FirebaseFirestore.getInstance()

    fun sendVerificationCode(
        phoneNumber: String,
        activity: FragmentActivity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(120L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    suspend fun verifyCode(verificationId: String, code: String): Result<Unit> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            firebaseAuth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun registerUser(email: String, password: String, userData: Map<String, Any>, onComplete: (Result<Unit>) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = firebaseAuth.currentUser?.uid ?: return@addOnCompleteListener
                    val userRef = firestore.collection("users").document(uid)
                    userRef.set(userData)
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
}
