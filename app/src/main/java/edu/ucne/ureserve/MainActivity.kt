package edu.ucne.ureserve

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import dagger.hilt.android.AndroidEntryPoint
import edu.ucne.ureserve.data.local.database.UReserveDb
import edu.ucne.ureserve.presentation.navigation.UreserveNavHost
import edu.ucne.ureserve.ui.theme.URESERVETheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            URESERVETheme {
                val navController = rememberNavController()
                val uReserveDb = Room.databaseBuilder(
                    applicationContext,
                    UReserveDb::class.java,
                    "UReserveDb"
                ).build()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UreserveNavHost(
                        navController = navController,
                        uReserveDb = uReserveDb
                    )
                }
            }
        }
    }
}