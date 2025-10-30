package com.mds.sharedexpenses.ui.home

import android.R.attr.contentDescription
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mds.sharedexpenses.ui.components.HeaderTopBar
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = HomeViewModel()
) {
    Scaffold(
        topBar = {
            HeaderTopBar("<add title!>")
        },
//        floatingActionButton = {
//            FloatingActionButton()
//        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "home screen :D")
            Button(onClick = {
                viewModel.onButtonClicked()
            }) {
                Text(text = "Click me")
            }
        }
    }

}





@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SharedExpensesTheme {
        HomeScreen()
    }
}