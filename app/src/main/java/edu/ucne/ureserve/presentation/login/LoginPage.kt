package edu.ucne.ureserve.presentation.login

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.registrotecnicos.common.NotificationHandler
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
        Color(0xFFFFDF00),
        Color(0xFF0238BA)
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
    apiUrl: String = "https://ureserve-hghra5gdhzgzdghk.eastus2-01.azurewebsites.net/api/Usuarios"
) {
    val context = LocalContext.current
    val notificationHandler = remember { NotificationHandler(context) }
    val correo = remember { mutableStateOf("") }
    val clave = remember { mutableStateOf("") }
    val correoError = remember { mutableStateOf<String?>(null) }
    val claveError = remember { mutableStateOf<String?>(null) }
    val loginError = remember { mutableStateOf<String?>(null) }
    val isLoading = remember { mutableStateOf(false) }

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

        isLoading.value = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val usuarios = ApiModule.api.getAll()
                val usuarioValido = usuarios.find { usuario ->
                    usuario.correoInstitucional == correo.value && usuario.clave == clave.value
                }

                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    if (usuarioValido != null) {
                        AuthManager.login(usuarioValido)
                        notificationHandler.showNotification(
                            title = "Bienvenido",
                            message = "Hola ${usuarioValido.nombres} 👋"
                        )
                        onLoginSuccess(usuarioValido)
                    } else {
                        loginError.value = "Usuario o clave incorrectos"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading.value = false // Ocultar carga
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp)),
                    singleLine = true,
                    isError = correoError.value != null,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_usuariologin),
                            contentDescription = "Icono de usuario",
                            tint = Color(0xFFFDEA28),
                            modifier = Modifier.size(42.dp)
                        )
                    },
                    supportingText = {
                        correoError.value?.let {
                            Text(text = it, color = Color(0xFFFF6D6D))
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF6D87A4),
                        unfocusedContainerColor = Color(0xFF6D87A4),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorContainerColor = Color(0xFFFF6D6D).copy(alpha = 0.3f),
                        errorIndicatorColor = Color(0xFFFF6D6D)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = clave.value,
                    onValueChange = { clave.value = it },
                    label = { Text("Clave", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp)),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = claveError.value != null,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_candado),
                            contentDescription = "Icono de contraseña",
                            tint = Color(0xFFFDEA28),
                            modifier = Modifier.size(42.dp)
                        )
                    },
                    supportingText = {
                        claveError.value?.let {
                            Text(text = it, color = Color(0xFFFF6D6D))
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF6D87A4),
                        unfocusedContainerColor = Color(0xFF6D87A4),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorContainerColor = Color(0xFFFF6D6D).copy(alpha = 0.3f),
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
                    onClick = {
                        login()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D87A4),
                        contentColor = Color.White
                    ),
                    enabled = !isLoading.value
                ) {
                    Text("Conectar", fontSize = 16.sp)
                }

                if (isLoading.value) {
                    Spacer(modifier = Modifier.height(16.dp))

                    val infiniteTransition = rememberInfiniteTransition()
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1000, easing = LinearEasing)
                        )
                    )

                    Image(
                        painter = painterResource(id = R.drawable.loading_spinner),
                        contentDescription = "Cargando...",
                        modifier = Modifier
                            .size(48.dp)
                            .graphicsLayer {
                                rotationZ = rotation
                            }
                            .align(Alignment.CenterHorizontally)
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginSuccess = {})
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