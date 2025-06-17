package edu.ucne.ureserve.presentation.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ucne.ureserve.R
import kotlinx.coroutines.delay

@Composable
fun LoadStartScreen(
    modifier: Modifier = Modifier,
    logoResId: Int = R.drawable.logo_reserve,
    onTimeout: () -> Unit = {}
) {
    val appBlueColor = Color(0xFF2E5C94)

    // Animación de progreso
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(50)
            progress += 0.01f
        }
        delay(500)
        onTimeout()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appBlueColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 150.dp)
        ) {
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = "Logo UReserve",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "UReserve",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Más espacio para bajar la barra
            Spacer(modifier = Modifier.height(120.dp))

            GradientProgressBar(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(6.dp) // Barra más fina
            )
        }
    }
}

@Composable
fun GradientProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val gradientBrush = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF000000),
            0.35f to Color(0xFF023E8A),
            0.73f to Color(0xFFFFD500),
            1.0f to Color(0xFF000000)
        ),
        start = Offset.Zero,
        end = Offset.Infinite,
        tileMode = TileMode.Clamp
    )

    Canvas(modifier = modifier) {
        val width = size.width * progress
        drawRoundRect(
            brush = gradientBrush,
            size = Size(width, size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f) // también más redonda y fina
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadStartScreenPreview() {
    LoadStartScreen()
}