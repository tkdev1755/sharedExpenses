package com.mds.sharedexpenses.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mds.sharedexpenses.R
import com.mds.sharedexpenses.ui.theme.SharedExpensesTheme

@Composable
fun InstructionCard(
    title: String,
    description: String = "",
    buttonLabel: String = "",
    onButtonClick: () -> Unit = {},
    imageId: Int,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.Start,
        ) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "Group illustration",
                modifier = Modifier.height(150.dp), // Adjust height as needed
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onButtonClick() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = buttonLabel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InstructionCardPreview() {
    SharedExpensesTheme {
        Box(
            modifier = Modifier.padding(16.dp),
        ) {
            InstructionCard(
                title = "No groups found",
                description = "You are currently not in any group. Ask someone to add you to an existing group or start by creating a new group.",
                buttonLabel = "Create Group",
                onButtonClick = {},
                imageId = R.drawable.group,
            )
        }
    }
}
