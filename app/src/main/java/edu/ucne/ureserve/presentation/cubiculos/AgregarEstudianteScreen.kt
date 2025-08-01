import android.Manifest
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import edu.ucne.registrotecnicos.common.NotificationHandler
import edu.ucne.ureserve.R
import edu.ucne.ureserve.presentation.cubiculos.ReservaCubiculoViewModel
import edu.ucne.ureserve.presentation.laboratorios.ReservaLaboratorioViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AgregarEstudianteScreen(
    viewModel: ReservaCubiculoViewModel = hiltViewModel(),
    navController: NavController,
    onCancel: () -> Unit = {},
    onAdd: (String) -> Unit = {}
) {

    val context = LocalContext.current


    // Solicitud de permiso para notificaciones en Android 13+
    val postNotificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        } else null

    val notificationHandler = remember { NotificationHandler(context) }

    LaunchedEffect(true) {
        if (postNotificationPermission != null && !postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

    var matricula by remember { mutableStateOf("") }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.members) {
        Log.d("AgregarEstudianteScreen", "Miembros actualizados: ${viewModel.members.value.size}")
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    fun formatMatricula(input: String): String {
        return when {
            input.length <= 4 -> input
            input.length <= 8 -> "${input.substring(0, 4)}-${input.substring(4)}"
            else -> "${input.substring(0, 4)}-${input.substring(4, 8)}"
        }
    }

    fun onMatriculaChange(input: String) {
        val cleanInput = input.replace("-", "")
        if (cleanInput.length <= 8) {
            matricula = formatMatricula(cleanInput)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1657A8)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6D87A4))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFFFD700), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_reserve),
                        contentDescription = "Logo",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Digite la matrícula:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = matricula,
                    onValueChange = { onMatriculaChange(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center, color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF6F8FC),
                        unfocusedContainerColor = Color(0xFFF6F8FA),
                        cursorColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0D47A1),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(50.dp)
                            .width(120.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "CANCELAR",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // En la pantalla AgregarEstudianteScreen
                    Button(
                        onClick = {
                            val matriculaLimpia = matricula.replace("-", "")
                            if (matriculaLimpia.length == 8) {
                                viewModel.buscarUsuarioPorMatricula(matriculaLimpia) { usuarioEncontrado ->
                                    if (usuarioEncontrado != null) {
                                        viewModel.addMember(usuarioEncontrado)

                                        // Notificación
                                        notificationHandler.showNotification(
                                            title = "Estudiante añadido",
                                            message = "Matrícula ${matriculaLimpia} añadida correctamente."
                                        )

                                        navController.popBackStack()
                                    } else {
                                        viewModel.setError("Matrícula no válida")
                                    }
                                }
                            } else {
                                viewModel.setError("La matrícula debe tener 8 dígitos")
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3A7BD5),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(50.dp)
                            .width(120.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = "AÑADIR",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Teclado numérico
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D47A1))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in 1..3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (j in 1..3) {
                        val number = (i - 1) * 3 + j
                        Button(
                            onClick = { onMatriculaChange(matricula + number.toString()) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E5C94),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .size(100.dp)
                                .padding(8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = number.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { onMatriculaChange(matricula + "0") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E5C94),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "0",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewAgregarEstudianteScreen() {
//    AgregarEstudianteScreen()
//}