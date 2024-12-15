package com.gen.stajyerim.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gen.stajyerim.data.repository.AuthRepository
import com.gen.stajyerim.model.User
import com.gen.stajyerim.model.toMap
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState


    fun login(email: String, password: String) {
        _authState.value = AuthState(isLoading = true)
        viewModelScope.launch {
            val loginResult = kotlinx.coroutines.suspendCancellableCoroutine<Result<Unit>> { continuation ->
                authRepository.login(email, password) { result ->
                    continuation.resume(result, onCancellation = null)
                }
            }

            if (loginResult.isSuccess) {
                val userTypeResult = authRepository.getUserType()
                _authState.value = if (userTypeResult.isSuccess) {
                    AuthState(isSuccess = true, userType = userTypeResult.getOrNull())
                } else {
                    AuthState(isError = true, errorMessage = userTypeResult.exceptionOrNull()?.message)
                }
            } else {

                _authState.value = AuthState(isError = true, errorMessage = loginResult.exceptionOrNull()?.message)
            }
        }
    }
    fun signUp(email: String, password: String, user: com.gen.stajyerim.model.User) {
        _authState.value = AuthState(isLoading = true)
        authRepository.registerUser(email, password, user.toMap()) { result ->
            _authState.value = if (result.isSuccess) {
                AuthState(isSuccess = true)
            } else {
                AuthState(isError = true, errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState()
    }
    fun fetchUserType(onComplete: (String?, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.getUserType()
            if (result.isSuccess) {
                onComplete(result.getOrNull(), null)
            } else {
                onComplete(null, result.exceptionOrNull()?.message)
            }
        }
    }
    fun fetchUserProfile(userId: String, callback: (User?, String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    callback(user, null)
                } else {
                    callback(null, "Kullanıcı bulunamadı.")
                }
            }
            .addOnFailureListener { exception ->
                callback(null, exception.message)
            }
    }

    fun updateUserProfile(user: com.gen.stajyerim.model.User, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.updateUserProfile(user.toMap())
            if (result.isSuccess) {
                onComplete(true, null)
            } else {
                onComplete(false, result.exceptionOrNull()?.message)
            }
        }
    }
}

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val userType: String? = null
)