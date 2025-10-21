package com.example.proyectohealthconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectohealthconnect.ui.theme.ProyectoHealthConnectTheme
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.HeartRateRecord
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var requestPermissions: ActivityResultLauncher<Set<String>>
    private lateinit var healthConnectClient: HealthConnectClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Crear cliente de Health Connect
        healthConnectClient = HealthConnectClient.getOrCreate(this)

        // 2. Definir permisos que pedirá la app
        val permissions = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getWritePermission(HeartRateRecord::class)
        )

        // 3. Registrar launcher para solicitar permisos
        requestPermissions = registerForActivityResult(
            PermissionController.createRequestPermissionResultContract()
        ) { grantedPermissions ->
            if (grantedPermissions.containsAll(permissions)) {
                println("✅ Permisos concedidos")
            } else {
                println("❌ Permisos denegados")
            }
        }

        // 4. Configurar la UI
        setContent {
            ProyectoHealthConnectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        onRequestPermissions = {
                            requestPermissions.launch(permissions)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onRequestPermissions: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Presiona el botón para pedir permisos") }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = status)
        Button(onClick = {
            scope.launch {
                onRequestPermissions()
                status = "Verifica en Logcat si se concedieron los permisos"
            }
        }) {
            Text("Conectar con Health Connect")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    ProyectoHealthConnectTheme {
        MainScreen(onRequestPermissions = {})
    }
}
