package com.mds.sharedexpenses.ui.components

import android.preference.PreferenceActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.PersonPin
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun sharedColorPallete() = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    titleContentColor = MaterialTheme.colorScheme.primary,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTopBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit = {},
    actions: @Composable () -> Unit = {}
) {
    MediumTopAppBar(
        colors = sharedColorPallete(),
        title = {
            Text(text = title)
        },
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = { /* logic */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "go to previous page"
                    )
                }
            }
        },
        actions = {
            actions()
            /* example
            *IconButton(onClick = { /* logic */ }) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "show profile"
                )
            }
            * */
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderTopBar(
    title: String = "Unnamed",
    onProfileClick: () -> Unit = {},
){
    CenterAlignedTopAppBar(
        colors = sharedColorPallete(),
        title = {
            Text(text = title)
        },
        actions = {
            IconButton(onClick = { onProfileClick() }) {
                Icon(
                    imageVector = Icons.Rounded.PersonPin,
                    contentDescription = "show profile"
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun NavigationTopBarPreview() {
    SharedExpensesTheme {
        NavigationTopBar(title = "Shared Test", canNavigateBack = true)
    }
}

@Preview(showBackground = true)
@Composable
fun CenterAlignedTopBarPreview() {
    SharedExpensesTheme {
        HeaderTopBar()
    }
}