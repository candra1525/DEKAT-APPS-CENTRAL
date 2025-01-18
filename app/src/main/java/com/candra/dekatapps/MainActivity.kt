package com.candra.dekatapps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import com.candra.dekatapps.ui.cuaca.CuacaScreen
import com.candra.dekatapps.ui.theme.DEKATAPPSTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DEKATAPPSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column {
                        AppsName(
                            name = "DEKAT APPS",
                            fullname = "Dampak dan Kondisi Terkini",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun AppsName(name: String, fullname : String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(
            text = name,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = "($fullname)",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
        )

        CuacaScreen(lifecycleOwner = context as LifecycleOwner)
    }
}


