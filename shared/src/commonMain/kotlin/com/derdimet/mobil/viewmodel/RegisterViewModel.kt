package com.derdimet.mobil.viewmodel

import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.derdimet.mobil.viewmodel.ViewModel

enum class AccountType {
    INDIVIDUAL, BUSINESS
}

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _role = MutableStateFlow(UserRole.MEAT_BUYER)
    val role: StateFlow<UserRole> = _role.asStateFlow()

    private val _accountType = MutableStateFlow(AccountType.INDIVIDUAL)
    val accountType: StateFlow<AccountType> = _accountType.asStateFlow()

    private val _companyName = MutableStateFlow("")
    val companyName: StateFlow<String> = _companyName.asStateFlow()

    private val _taxNumber = MutableStateFlow("")
    val taxNumber: StateFlow<String> = _taxNumber.asStateFlow()

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun onNameChange(value: String) { _name.value = value }
    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }
    fun onConfirmPasswordChange(value: String) { _confirmPassword.value = value }
    fun onRoleChange(value: UserRole) { _role.value = value }
    fun onAccountTypeChange(value: AccountType) { _accountType.value = value }
    fun onCompanyNameChange(value: String) { _companyName.value = value }
    fun onTaxNumberChange(value: String) { _taxNumber.value = value }
    fun onAddressChange(value: String) { _address.value = value }

    fun register(onSuccess: (UserRole) -> Unit) {
        if (_password.value != _confirmPassword.value) {
            _error.value = "Şifreler uyuşmuyor"
            return
        }
        if (_name.value.trim().isEmpty() || _email.value.trim().isEmpty()) {
            _error.value = "Ad soyad ve e-posta zorunlu"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val ok = authRepository.register(
                    email = _email.value.trim(),
                    password = _password.value,
                    name = _name.value.trim(),
                    role = _role.value.name,
                    accountType = _accountType.value.name,
                    companyName = _companyName.value.takeIf { it.isNotBlank() },
                    taxNumber = _taxNumber.value.takeIf { it.isNotBlank() },
                    addressLine = _address.value.takeIf { it.isNotBlank() }
                )
                if (ok) {
                    val loginOk = authRepository.login(_email.value.trim(), _password.value)
                    if (loginOk) {
                        val user = authRepository.fetchCurrentUser()
                        if (user != null) {
                            onSuccess(user.role)
                        } else {
                            _error.value = "Profil bilgisi alınamadı"
                        }
                    } else {
                        _error.value = "Kayıt sonrası giriş başarısız"
                    }
                } else {
                    _error.value = "Kayıt başarısız"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Kayıt sırasında bir hata oluştu"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
