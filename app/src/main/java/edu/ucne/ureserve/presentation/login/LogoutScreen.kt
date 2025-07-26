package edu.ucne.ureserve.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.ureserve.R
import edu.ucne.ureserve.data.remote.dto.EstudianteDto
import edu.ucne.ureserve.data.remote.dto.UsuarioDTO

val Amarillo = Color(0xFFFFDF00)
val Azul = Color(0xFF154AD5)

// Administrador simple para usuario en sesión (offline-first)
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

@Composable
fun ProfileScreenOfflineFirst(
    onLogout: () -> Unit,
    onBottomNavClick: (String) -> Unit = {}
) {
    // Obtener usuario local guardado o usuario vacío
    val usuario = AuthManager.currentUser ?: UsuarioDTO(
        usuarioId = 0,
        nombres = "Desconocido",
        apellidos = "",
        correoInstitucional = "",
        clave = "",
        estudiante = EstudianteDto(
            estudianteId = 0,
            matricula = "",
            facultad = "",
            carrera = ""
        )
    )

    ProfileScreen(
        usuario = usuario,
        estudiante = usuario.estudiante ?: EstudianteDto(0, "", "", ""),
        onLogout = {
            AuthManager.logout()
            onLogout()
        },
        onBottomNavClick = onBottomNavClick
    )
}

@Composable
fun ProfileScreen(
    usuario: UsuarioDTO,
    estudiante: EstudianteDto,
    onLogout: () -> Unit,
    onBottomNavClick: (String) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Amarillo)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 100.dp)
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Azul)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp, start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = usuario.nombres,
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = usuario.apellidos,
                        fontSize = 14.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    UserInfoRow(
                        icon = painterResource(id = R.drawable.icon_mensaje),
                        label = "Correo Electrónico",
                        value = usuario.correoInstitucional
                    )
                    UserInfoRow(
                        icon = painterResource(id = R.drawable.icon_home),
                        label = "Carrera",
                        value = estudiante.carrera
                    )
                    UserInfoRow(
                        icon = painterResource(id = R.drawable.icon_number),
                        label = "Matrícula",
                        value = estudiante.matricula
                    )

                    Spacer(modifier = Modifier.height(122.dp))

                    Button(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE0E0E0),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text(
                            text = "Cerrar Sesión",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(122.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0238BA))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BottomNavItem(
                    iconRes = R.drawable.icon_tutorial,
                    label = "Tutorial",
                    onClick = { onBottomNavClick("Tutorial") }
                )
                BottomNavItem(
                    iconRes = R.drawable.icon_inicio,
                    label = "Inicio",
                    isSelected = true,
                    onClick = { onBottomNavClick("Inicio") }
                )
                BottomNavItem(
                    iconRes = R.drawable.icon_perfil,
                    label = "Perfil",
                    onClick = { onBottomNavClick("Perfil") }
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp)
        ) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Perfil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

@Composable
fun UserInfoRow(icon: Painter, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(30.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Amarillo, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun BottomNavItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val usuarioPrueba = UsuarioDTO(
        usuarioId = 1,
        nombres = "Juan",
        apellidos = "Perez",
        correoInstitucional = "juan.perez@example.com",
        clave = "123456",
        estudiante = EstudianteDto(
            estudianteId = 1,
            matricula = "123456789",
            facultad = "Facultad X",
            carrera = "Ingeniería de Sistemas"
        )
    )

    ProfileScreen(
        usuario = usuarioPrueba,
        estudiante = usuarioPrueba.estudiante ?: EstudianteDto(0, "", "", ""),
        onLogout = {}
    )
}
