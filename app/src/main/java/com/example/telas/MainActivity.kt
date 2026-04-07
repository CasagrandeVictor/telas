package com.example.telas

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.telas.ui.theme.TelasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TelasTheme {
                val navController = rememberNavController()
                
                NavHost(navController = navController, startDestination = "permissao") {
                    composable("permissao") { PermissionScreen(navController) }
                    composable("mapa") { MapScreen(navController) }
                }
            }
        }
    }
}

@Composable
fun PermissionScreen(navController: NavController) {
    val context = LocalContext.current
    var showExplanationDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Estado para mostrar o status na tela para a apresentação
    val permissionStatus = remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION))
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navController.navigate("mapa")
        } else {
            val activity = context as ComponentActivity
            if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showSettingsDialog = true
            } else {
                showExplanationDialog = true
            }
        }
        // Atualiza o status após a tentativa
        permissionStatus.value = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = if (permissionStatus.value == PackageManager.PERMISSION_GRANTED) Color.Green else MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Status: ${if (permissionStatus.value == PackageManager.PERMISSION_GRANTED) "CONCEDIDO" else "NEGADO"}",
            color = if (permissionStatus.value == PackageManager.PERMISSION_GRANTED) Color.Green else Color.Red,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sistema de Localização",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Para visualizar o mapa em tempo real, precisamos da sua permissão de localização.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    navController.navigate("mapa")
                } else {
                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Acessar Mapa em Tempo Real")
        }
        
        // Botão extra para resetar e mostrar o erro (para a apresentação)
        TextButton(onClick = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }) {
            Text("Gerenciar permissão manualmente")
        }
    }

    if (showExplanationDialog) {
        AlertDialog(
            onDismissRequest = { showExplanationDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Acesso Negado") },
            text = { Text("O mapa não pode ser carregado sem a permissão. O fluxo correto exige que o usuário aceite para continuar.") },
            confirmButton = {
                Button(onClick = {
                    showExplanationDialog = false
                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }) { Text("Tentar Novamente") }
            },
            dismissButton = {
                TextButton(onClick = { showExplanationDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Permissão Bloqueada") },
            text = { Text("Você marcou 'Não perguntar novamente'. Agora é necessário habilitar manualmente nas configurações do sistema.") },
            confirmButton = {
                Button(onClick = {
                    showSettingsDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) { Text("Abrir Configurações") }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) { Text("Voltar") }
            }
        )
    }
}

@Composable
fun MapScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE0F7FA)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Red, modifier = Modifier.size(60.dp))
            Text("MAPA EM TEMPO REAL", style = MaterialTheme.typography.headlineLarge)
            Text("Localização: Criciúma, SC", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(50.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Voltar para Início")
            }
        }
    }
}
