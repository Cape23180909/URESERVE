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
    onLoginClick: (String, String) -> Unit = { _, _ -> }
) {
    val correo = remember { mutableStateOf("") }
    val clave = remember { mutableStateOf("") }

    // Estados para errores
    val correoError = remember { mutableStateOf<String?>(null) }
    val claveError = remember { mutableStateOf<String?>(null) }

    // Función de validación
    fun validateFields(): Boolean {
        var isValid = true

        // Validar correo
        if (correo.value.isBlank()) {
            correoError.value = "El correo es obligatorio"
            isValid = false
        } else if (!correo.value.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))) {
            correoError.value = "Ingrese un correo válido"
            isValid = false
        } else {
            correoError.value = null
        }

        // Validar clave
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

                // Campo Correo Institucional con validación
                OutlinedTextField(
                    value = correo.value,
                    onValueChange = {
                        correo.value = it
                        // Validación en tiempo real (opcional)
                        if (it.isNotEmpty() && !it.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))) {
                            correoError.value = "Formato de correo inválido"
                        } else {
                            correoError.value = null
                        }
                    },
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

                // Campo Clave con validación
                OutlinedTextField(
                    value = clave.value,
                    onValueChange = {
                        clave.value = it
                        // Validación en tiempo real (opcional)
                        if (it.isNotEmpty() && it.length < 6) {
                            claveError.value = "Mínimo 6 caracteres"
                        } else {
                            claveError.value = null
                        }
                    },
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

                // Botón Conectar con validación
                Button(
                    onClick = {
                        if (validateFields()) {
                            onLoginClick(correo.value, clave.value)
                        }
                    },
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
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}