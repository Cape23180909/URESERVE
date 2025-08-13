package edu.ucne.ureserve.presentation.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Correo y contraseña no pueden estar vacíos")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            if (tryLocalLogin(email, password)) {
                return@launch
            }
            performRemoteLogin(email, password)
        }
    }

    private suspend fun tryLocalLogin(email: String, password: String): Boolean {
        val localUser = repository.getLocalUserByEmail(email).first()
        return if (localUser != null && localUser.clave == password) {
            _authState.value = AuthState.Authenticated
            true
        } else {
            false
        }
    }

    private suspend fun performRemoteLogin(email: String, password: String) {
        repository.login(email, password).collect { resource ->
            when (resource) {
                is Resource.Loading -> _authState.value = AuthState.Loading
                is Resource.Success -> _authState.value = AuthState.Authenticated
                is Resource.Error -> {
                    _authState.value = AuthState.Error(resource.message ?: "Error desconocido")
                }
            }
        }
    }
}