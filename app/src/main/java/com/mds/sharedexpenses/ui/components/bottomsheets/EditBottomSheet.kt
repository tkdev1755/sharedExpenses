package com.mds.sharedexpenses.ui.components.bottomsheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mds.sharedexpenses.ui.groupdetail.GroupDetailViewModel

@Composable
fun EditBottomSheet(
    viewModel: GroupDetailViewModel,
    initialName: String,
    initialDescription: String,
    onSave: (String, String) -> Unit,
) {
    // local state for input
    var tempName by remember { mutableStateOf(initialName) }
    var tempDescription by remember { mutableStateOf(initialDescription) }

    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = tempName,
            onValueChange = { tempName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = tempDescription,
            onValueChange = { tempDescription = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {onSave(tempName, tempDescription)},
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("save")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
        Text("Group Members", style = TextStyle(fontWeight = FontWeight.Bold))
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            viewModel.members().forEach { member ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(member.name)
                    IconButton(onClick = { viewModel.removeMember(member) }) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Removes",
                        )
                    }
                }
            }
        }
        if (viewModel.uiState.collectAsState().value.isAddMemberFieldVisible) {
            var newMember by remember { mutableStateOf("") }

            OutlinedTextField(
                value = newMember,
                onValueChange = { newMember = it },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = { viewModel.addMember(newMember) },
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("Add")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        }
        Button(onClick = { viewModel.onAddMemberClicked() }) {
            Text("Add Member")
        }
    }
}
