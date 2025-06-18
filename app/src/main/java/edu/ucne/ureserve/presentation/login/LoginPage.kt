package edu.ucne.ureserve.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.di.ApiModule
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    val colors = listOf(
        Color(0xFFFFDF00),  // Amarillo
        Color(0xFF0238BA)   // Azul
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = colors,
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (UsuarioDTO) -> Unit,
    apiUrl: String = "https://ureserve-hghra5gdhzgzdghk.eastus2-01.azurewebsites.net/api/Usuarios" // Reemplaza con la URL de tu API
) {
    val correo = remember { mutableStateOf("") }
    val clave = remember { mutableStateOf("") }

    val correoError = remember { mutableStateOf<String?>(null) }
    val claveError = remember { mutableStateOf<String?>(null) }
    val loginError = remember { mutableStateOf<String?>(null) }

    fun validateFields(): Boolean {
        var isValid = true

        if (correo.value.isBlank()) {
            correoError.value = "El correo es obligatorio"
            isValid = false
        } else if (!correo.value.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))) {
            correoError.value = "Ingrese un correo válido"
            isValid = false
        } else {
            correoError.value = null
        }

        if (clave.value.isBlank()) {
            claveError.value = "La clave es obligatoria"
            isValid = false
        } else if (clave.value.length < 6) {
            claveError.value = "La clave debe tener al menos 6 caracteres"
            isValid = false
        } else {
            claveError.value = null
        }

        return isValid
    }

    fun login() {
        if (!validateFields()) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val usuarios = ApiModule.api.getAll()
                val usuarioValido = usuarios.find { usuario ->
                    usuario.correoInstitucional == correo.value && usuario.clave == clave.value
                }

                withContext(Dispatchers.Main) {
                    if (usuarioValido != null) {
                        AuthManager.login(usuarioValido)  // Guarda el usuario
                        onLoginSuccess(usuarioValido)     // Pasa el usuario
                    } else {
                        loginError.value = "Usuario o clave incorrectos"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    loginError.value = "Error: ${e.message}"
                }
            }
        }
    }

    GradientBackground {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 34.dp)
                    .padding(top = 160.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_reserve),
                    contentDescription = "Logo de RESERVE",
                    modifier = Modifier
                        .size(160.dp)
                        .padding(bottom = 34.dp),
                    contentScale = ContentScale.Fit
                )

                OutlinedTextField(
                    value = correo.value,
                    onValueChange = { correo.value = it },
                    label = { Text("Correo Institucional", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = correoError.value != null,
                    supportingText = {
                        correoError.value?.let {
                            Text(text = it, color = Color(0xFFFF6D6D))
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.White.copy(alpha = 0.8f),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.6f),
                        errorIndicatorColor = Color(0xFFFF6D6D),
                        errorLabelColor = Color(0xFFFF6D6D),
                        errorTextColor = Color(0xFFFF6D6D)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = clave.value,
                    onValueChange = { clave.value = it },
                    label = { Text("Clave", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = claveError.value != null,
                    supportingText = {
                        claveError.value?.let {
                            Text(text = it, color = Color(0xFFFF6D6D))
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.White.copy(alpha = 0.8f),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.6f),
                        errorIndicatorColor = Color(0xFFFF6D6D),
                        errorLabelColor = Color(0xFFFF6D6D),
                        errorTextColor = Color(0xFFFF6D6D)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                loginError.value?.let {
                    Text(
                        text = it,
                        color = Color(0xFFFF6D6D),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Button(
                    onClick = { login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Conectar", fontSize = 16.sp)
                }
            }
        }
    }
}

class RetrofitInstance {

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginSuccess = {}) // Ahora cumple con la firma
}

object AuthManager {
    var currentUser: UsuarioDTO? = null
        private set

    fun login(usuario: UsuarioDTO) {
        currentUser = usuario
    }

    fun logout() {
        currentUser = null
    }
}