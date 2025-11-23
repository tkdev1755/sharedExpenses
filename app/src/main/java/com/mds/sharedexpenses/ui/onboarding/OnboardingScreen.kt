package com.example.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text("Welcome to SharedExpenses", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        Text("This is an app for sharing expenses between friends")

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Let's get started !")
        }
    }
}
