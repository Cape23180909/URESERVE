package edu.ucne.ureserve.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.ureserve.data.remote.UsuarioApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val usuarioApi: UsuarioApi
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Correo y contraseña no pueden estar vacíos")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val usuarios = usuarioApi.getAll()
                val usuario = usuarios.find {
                    it.correoInstitucional == email && it.clave == password
                }

                if (usuario != null) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error("Correo o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error al iniciar sesión: ${e.localizedMessage}")
            }
        }
    }

    fun signOut() {
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

data class UsuarioUiState(
    val nombres: String = "",
    val apellidos: String = "",
    val correoelectronico: String = "",
    val clave: String = ""
)