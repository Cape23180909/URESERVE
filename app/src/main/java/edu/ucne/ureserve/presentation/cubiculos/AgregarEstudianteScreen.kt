import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.ureserve.R
import androidx.compose.material3.TextFieldDefaults


@Composable
fun AgregarEstudianteScreen(
    onCancel: () -> Unit = {},
    onAdd: (String) -> Unit = {}
) {
    val matricula = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1657A8)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de matrícula
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6D87A4)) // Cambiado a #6D87A4
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
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
                    value = matricula.value,
                    onValueChange = { matricula.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF6F8FC),
                        unfocusedContainerColor = Color(0xFFF6F8FA)
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
                            fontSize = 14.sp,   //Cambiar dependiendo mi gusto, si lo veo raro en mi cell
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { onAdd(matricula.value) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3A7BD5),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(50.dp)
                            .width(120.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "AÑADIR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                            onClick = { matricula.value += number.toString() },
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
                    onClick = { matricula.value += "0" },
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

@Preview(showBackground = true)
@Composable
fun PreviewAgregarEstudianteScreen() {
    AgregarEstudianteScreen()
}