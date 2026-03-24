package com.example.telas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.telas.ui.theme.TelasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TelasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CacaAoTesouroApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CacaAoTesouroApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    var tempoInicio by remember { mutableLongStateOf(0L) }
    var tempoTotalSegundos by remember { mutableLongStateOf(0L) }

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500)) }
    ) {
        composable("home") {
            HomeScreen(onIniciar = {
                tempoInicio = System.currentTimeMillis()
                navController.navigate("pista1")
            })
        }

        composable("pista1") {
            PistaScreen(
                titulo = "Pista 1",
                pergunta = "O que tem que ser quebrado antes de ser usado?",
                respostaCorreta = "ovo",
                onNext = { navController.navigate("pista2") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("pista2") {
            PistaScreen(
                titulo = "Pista 2",
                pergunta = "Sou alto quando sou jovem e baixo quando sou velho. Quem sou eu?",
                respostaCorreta = "vela",
                onNext = { navController.navigate("pista3") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("pista3") {
            PistaScreen(
                titulo = "Pista 3",
                pergunta = "Tem pescoço mas não tem cabeça, tem braços mas não tem mãos.",
                respostaCorreta = "camisa",
                onNext = {
                    tempoTotalSegundos = (System.currentTimeMillis() - tempoInicio) / 1000
                    navController.navigate("tesouro") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("tesouro") {
            TesouroScreen(
                tempoSegundos = tempoTotalSegundos,
                onJogarNovamente = {
                    tempoInicio = System.currentTimeMillis()
                    navController.navigate("pista1") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onVoltarInicio = {
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }
    }
}

@Composable
fun HomeScreen(onIniciar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🗺️", fontSize = 100.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Bem-vindo à", fontSize = 24.sp)
        Text("Caça ao Tesouro!", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onIniciar, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Iniciar Caça ao Tesouro", fontSize = 18.sp)
        }
    }
}

@Composable
fun PistaScreen(
    titulo: String,
    pergunta: String,
    respostaCorreta: String,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var respostaUsuario by remember { mutableStateOf("") }
    var statusValidacao by remember { mutableStateOf<Boolean?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(titulo, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        Text(pergunta, fontSize = 20.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = respostaUsuario,
            onValueChange = {
                respostaUsuario = it
                statusValidacao = null
            },
            label = { Text("Digite sua resposta") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = statusValidacao == false,
            trailingIcon = {
                if (statusValidacao == true) Icon(Icons.Default.Check, contentDescription = "Correto", tint = Color(0xFF4CAF50))
            }
        )

        // NOVO: Mensagens de feedback
        if (statusValidacao == false) {
            Text("Resposta incorreta, tente novamente!", color = MaterialTheme.colorScheme.error, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
        } else if (statusValidacao == true) {
            Text("Correto! Pode avançar.", color = Color(0xFF4CAF50), fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = {
                statusValidacao = respostaUsuario.trim().equals(respostaCorreta, ignoreCase = true)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Validar Resposta")
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text("Voltar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                enabled = statusValidacao == true 
            ) {
                Text("Próxima Pista")
            }
        }
    }
}

@Composable
fun TesouroScreen(
    tempoSegundos: Long,
    onJogarNovamente: () -> Unit,
    onVoltarInicio: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFFD700).copy(alpha = 0.2f)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏴‍☠️🪙", fontSize = 120.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Parabéns Pirata!", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB8860B))
        Text("Você encontrou o ONE PIECE!", fontSize = 22.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(24.dp))

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Text(
                text = "Tempo total: $tempoSegundos segundos ⏱️",
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = onJogarNovamente, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Jogar Novamente", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onVoltarInicio, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Voltar ao Início")
        }
    }
}