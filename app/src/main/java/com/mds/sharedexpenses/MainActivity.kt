package com.mds.sharedexpenses

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge



import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mds.sharedexpenses.data.datasource.FirebaseService
import com.mds.sharedexpenses.data.repositories.FirebaseRepositoryImpl
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import com.google.firebase.messaging.messaging
import com.mds.sharedexpenses.ui.navigation.AppNavigation
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme
import com.mds.sharedexpenses.domain.usecase.GetUserUseCase
import com.mds.sharedexpenses.domain.usecase.LoginUseCase
import com.mds.sharedexpenses.domain.usecase.SaveNotificationTokenUseCase



import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }
    private fun askNotificationPermission() {
        println("ASKING FOR NOTIFICATION PERMISSION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
        enableEdgeToEdge()
        setContent {
            SharedExpensesTheme {
                AppNavigation(::askNotificationPermission)
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SharedExpensesTheme {
        Greeting("World!")
    }
}

