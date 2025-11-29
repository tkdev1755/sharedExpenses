package com.mds.sharedexpenses.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
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
    onNavigateBack: (() -> Unit)? = {},
    actions: @Composable () -> Unit = {},
) {
    MediumTopAppBar(
        colors = sharedColorPallete(),
        title = {
            Text(text = title)
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "go to previous page",
                    )
                }
            }
        },
        actions = { actions() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderTopBar(
    title: String = "Unnamed",
    onProfileClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        colors = sharedColorPallete(),
        title = {
            Text(text = title)
        },
        actions = {
            IconButton(onClick = { onProfileClick() }) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "show profile",
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun NavigationTopBarPreview() {
    SharedExpensesTheme {
        NavigationTopBar(title = "Shared Test", onNavigateBack = {})
    }
}

@Preview(showBackground = true)
@Composable
fun CenterAlignedTopBarPreview() {
    SharedExpensesTheme {
        HeaderTopBar()
    }
}
