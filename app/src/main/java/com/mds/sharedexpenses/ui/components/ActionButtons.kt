package com.mds.sharedexpenses.ui.components

import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme

/**
 * creates an action Button
 * ONLY use in Scaffolds!
 */
@Composable
fun CustomActionButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    iconContentDescription: String,
    text: String
) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = { Icon(imageVector, iconContentDescription) },
        text = { Text(text = text) },
    )
}

@Preview(showBackground = true)
@Composable
fun CustomActionButtonPreview() {
    SharedExpensesTheme {
        CustomActionButton(
            onClick = {},
            imageVector = androidx.compose.material.icons.Icons.Filled.GroupAdd,
            iconContentDescription = "Click to create a new Group",
            text = "Create Group"
        )
    }
}