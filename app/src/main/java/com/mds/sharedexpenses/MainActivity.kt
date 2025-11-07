package com.mds.sharedexpenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mds.sharedexpenses.data.datasource.FirebaseService
import com.mds.sharedexpenses.data.repositories.FirebaseRepositoryImpl
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import com.mds.sharedexpenses.ui.navigation.AppNavigation
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme
import com.mds.sharedexpenses.domain.usecase.GetUserUseCase
import com.mds.sharedexpenses.domain.usecase.LoginUseCase
import com.mds.sharedexpenses.domain.usecase.SaveNotificationTokenUseCase


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharedExpensesTheme {
                AppNavigation()
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
