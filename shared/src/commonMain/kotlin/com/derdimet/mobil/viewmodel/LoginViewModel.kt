package com.derdimet.mobil.viewmodel

import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.repository.AuthRepository
import com.derdimet.mobil.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.derdimet.mobil.viewmodel.ViewModel

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        val (remember, storedEmail) = preferencesRepository.getRememberedEmail()
        _rememberMe.value = remember
        if (remember) {
            _email.value = storedEmail
        }
    }

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onRememberMeToggle() {
        _rememberMe.value = !_rememberMe.value
    }

    fun login(onSuccess: (UserRole) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val success = authRepository.login(_email.value.trim(), _password.value)
                if (success) {
                    preferencesRepository.setRememberPreference(_rememberMe.value, _email.value.trim())
                    val user = authRepository.fetchCurrentUser()
                    if (user != null) {
                        if (user.role == UserRole.ADMIN) {
                            authRepository.logout()
                            _error.value = "Yönetici hesabı mobil uygulamada desteklenmiyor. Lütfen web panelini kullanın."
                            return@launch
                        }
                        preferencesRepository.persistAuthProfileSnapshot(user.role.name, user.accountType)
                        onSuccess(user.role)
                    } else {
                        _error.value = "Profil bilgisi alınamadı"
                    }
                } else {
                    _error.value = authRepository.consumeLastLoginError()
                        ?: "Giriş başarısız. Lütfen bilgilerinizi kontrol edin."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Bir hata oluştu"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
