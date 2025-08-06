package edu.ucne.ureserve.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.Resource
import edu.ucne.ureserve.data.remote.UsuarioApi
import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import edu.ucne.ureserve.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    // Estado de autenticación (Loading, Authenticated, Error, etc.)
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    // Estado del formulario de usuario
    private val _usuarioUiState = MutableLiveData(UsuarioUiState())
    val usuarioUiState: LiveData<UsuarioUiState> = _usuarioUiState

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Correo y contraseña no pueden estar vacíos")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val localUser = repository.getLocalUserByEmail(email).first()

            if (localUser != null) {
                // Login OFFLINE con usuario guardado localmente
                if (localUser.clave == password) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error("Contraseña incorrecta")
                }
            } else {
                // Intentar login REMOTO (Firebase + API)
                repository.login(email, password).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _authState.value = AuthState.Loading
                        is Resource.Success -> _authState.value = AuthState.Authenticated
                        is Resource.Error -> {
                            // Si falla login remoto (sin internet), intentamos fallback con local
                            val fallbackUser = repository.getLocalUserByEmail(email).first()
                            if (fallbackUser != null && fallbackUser.clave == password) {
                                _authState.value = AuthState.Authenticated
                            } else {
                                _authState.value = AuthState.Error(resource.message ?: "Error desconocido")
                            }
                        }
                    }
                }
            }
        }
    }

    fun signOut() {
        repository.logout()
        _authState.value = AuthState.Unauthenticated
    }
}



