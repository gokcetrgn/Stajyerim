package com.gen.stajyerim.viewmodel

import androidx.lifecycle.ViewModel
import com.gen.stajyerim.data.repository.AuthRepository
import com.gen.stajyerim.model.toMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    // Login işlemi
    fun login(email: String, password: String) {
        _authState.value = AuthState(isLoading = true)
        authRepository.login(email, password) { result ->
            _authState.value = if (result.isSuccess) {
                AuthState(isSuccess = true)
            } else {
                AuthState(isError = true, errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }

    // SignUp işlemi
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

    // Logout işlemi
    fun logout() {
        authRepository.logout()
        _authState.value = AuthState()  // Logout sonrası authState sıfırlanabilir
    }
}

// AuthState - Giriş ve kayıt durumlarını takip eder
data class AuthState(
    val isLoading: Boolean = false,  // Yükleniyor durumu
    val isSuccess: Boolean = false,  // Başarılı giriş
    val isError: Boolean = false,   // Hata durumu
    val errorMessage: String? = null // Hata mesajı
)