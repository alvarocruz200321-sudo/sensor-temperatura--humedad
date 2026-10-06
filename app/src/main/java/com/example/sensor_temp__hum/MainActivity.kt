package com.example.sensor_temp_hum

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

// Paleta de colores personalizada de alto contraste
val DarkBg = Color(0xFF0F172A)
val CardSurface = Color(0xFF1E293B)
val CardSurfaceLighter = Color(0xFF334155)
val AccentCyan = Color(0xFF38BDF8)
val AlertRed = Color(0xFFF43F5E)
val SafeEmerald = Color(0xFF10B981)
val TextMuted = Color(0xFF94A3B8)

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()

        setContent {
            MaterialTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DarkBg
                ) { innerPadding ->
                    var isUserLoggedIn by remember { mutableStateOf(auth.currentUser != null) }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (!isUserLoggedIn) {
                            ModernLoginScreen(
                                auth = auth,
                                onLoginSuccess = { isUserLoggedIn = true }
                            )
                        } else {
                            ModernDashboardScreen(
                                userEmail = auth.currentUser?.email ?: "Operador",
                                onLogout = {
                                    auth.signOut()
                                    isUserLoggedIn = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// PANTALLA 1: LOGIN & REGISTRO
// ---------------------------------------------------------------------------
@Composable
fun ModernLoginScreen(auth: FirebaseAuth, onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono del Sistema
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentCyan, Color(0xFF0284C7))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "IoT Thermal Guard",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = "Monitoreo en Tiempo Real & Control Seguro",
            fontSize = 13.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta de Acceso
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardSurfaceLighter, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Acceso a Terminal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AccentCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = CardSurfaceLighter
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = TextMuted) },
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AccentCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = CardSurfaceLighter
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        color = AlertRed,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            errorMessage = ""
                            auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = task.exception?.localizedMessage ?: "Credenciales no válidas"
                                    }
                                }
                        } else {
                            errorMessage = "Complete todos los campos requeridos."
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = DarkBg, modifier = Modifier.size(22.dp))
                    } else {
                        Text("Iniciar Sesión", color = DarkBg, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            errorMessage = ""
                            auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Operador registrado correctamente", Toast.LENGTH_SHORT).show()
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = task.exception?.localizedMessage ?: "Error al registrar"
                                    }
                                }
                        } else {
                            errorMessage = "Ingrese correo y contraseña para crear cuenta."
                        }
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(CardSurfaceLighter, AccentCyan))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Registrar Nuevo Usuario", color = Color.White)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// PANTALLA 2: DASHBOARD INDUSTRIAL IOT
// ---------------------------------------------------------------------------
@Composable
fun ModernDashboardScreen(userEmail: String, onLogout: () -> Unit) {
    var temperatura by remember { mutableDoubleStateOf(0.0) }
    var alertaActiva by remember { mutableStateOf(false) }

    val database = FirebaseDatabase.getInstance().getReference("sensores")

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempVal = snapshot.child("temperatura").value
                if (tempVal != null) {
                    temperatura = when (tempVal) {
                        is Double -> tempVal
                        is Long -> tempVal.toDouble()
                        else -> 0.0
                    }
                }

                val alertaVal = snapshot.child("alerta").getValue(Boolean::class.java)
                if (alertaVal != null) alertaActiva = alertaVal
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        database.addValueEventListener(listener)

        onDispose {
            database.removeEventListener(listener)
        }
    }

    val isAlert = temperatura > 25.0 || alertaActiva
    val themeColor by animateColorAsState(
        targetValue = if (isAlert) AlertRed else SafeEmerald,
        animationSpec = tween(durationMillis = 600),
        label = "ThemeColor"
    )
    val gaugeProgress by animateFloatAsState(
        targetValue = (temperatura / 60.0).coerceIn(0.0, 1.0).toFloat(),
        animationSpec = tween(durationMillis = 700),
        label = "GaugeProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Nodo ESP32 (DHT22)", fontSize = 14.sp, color = TextMuted)
                Text(text = "Monitoreo Activo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardSurface)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(SafeEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Firebase RTDB", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Gauge / Indicador Circular
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = 1f,
                modifier = Modifier.fillMaxSize(),
                color = CardSurfaceLighter,
                strokeWidth = 14.dp,
                trackColor = Color.Transparent
            )
            CircularProgressIndicator(
                progress = gaugeProgress,
                modifier = Modifier.fillMaxSize(),
                color = themeColor,
                strokeWidth = 14.dp,
                trackColor = Color.Transparent
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format(Locale.US, "%.1f", temperatura),
                    fontSize = 54.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "°C",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(themeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isAlert) "SOBRETEMPERATURA" else "ÓPTIMO",
                        color = themeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Tarjetas de Métricas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Umbral de Alerta", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("> 25.0 °C", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Actuador Remoto", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (alertaActiva) "ACTIVADO" else "STANDBY",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (alertaActiva) AlertRed else AccentCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Alarma Manual
        Button(
            onClick = {
                val nuevoEstado = !alertaActiva
                database.child("alerta").setValue(nuevoEstado)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (alertaActiva) AlertRed else CardSurfaceLighter
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = if (alertaActiva) "DESACTIVAR ALARMA REMOTA" else "FORZAR ALARMA MANUAL",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sesión
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sesión: $userEmail",
                fontSize = 12.sp,
                color = TextMuted,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onLogout) {
                Text("Desconectar", color = AlertRed, fontSize = 13.sp)
            }
        }
    }
}