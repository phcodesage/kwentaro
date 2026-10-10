package com.phcodesage.kwentaro.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.phcodesage.kwentaro.KwentaroApp
import com.phcodesage.kwentaro.data.auth.PasswordHasher
import com.phcodesage.kwentaro.ui.components.LocalSession
import com.phcodesage.kwentaro.ui.theme.actionTextColor
import com.phcodesage.kwentaro.ui.theme.solidTextFieldColors
import kotlinx.coroutines.launch

private enum class AccountDialog { ChangePassword, Delete }

@Composable
fun AccountSection() {
    val app = LocalContext.current.applicationContext as KwentaroApp
    val account = LocalSession.current.account
    val scope = rememberCoroutineScope()
    var dialog by remember { mutableStateOf<AccountDialog?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }

    fun signOut() {
        app.accounts.rememberedAccountId = null
        app.sessions.close()
    }

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Account", style = MaterialTheme.typography.titleMedium, color = actionTextColor)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(44.dp), shape = CircleShape, color = MaterialTheme.colorScheme.tertiary, contentColor = MaterialTheme.colorScheme.onTertiary) {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(account.displayName.split(" ").filter { it.firstOrNull()?.isLetterOrDigit() == true }.take(2).joinToString("") { it.first().uppercase() }, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(account.displayName, style = MaterialTheme.typography.bodyLarge)
                    Text("@${account.username} · data stays on this phone", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            notice?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = actionTextColor) }
            ActionRow(Icons.Rounded.Password, "Change password") { dialog = AccountDialog.ChangePassword }
            ActionRow(Icons.AutoMirrored.Rounded.Logout, "Sign out / switch account") { signOut() }
            ActionRow(Icons.Rounded.DeleteForever, "Delete this account", destructive = true) { dialog = AccountDialog.Delete }
        }
    }

    when (dialog) {
        AccountDialog.ChangePassword -> PasswordDialog(
            title = "Change password",
            fields = listOf("Current password", "New password", "Confirm new password"),
            confirmLabel = "Save",
            onDismiss = { dialog = null },
        ) { values, setError ->
            scope.launch {
                val error = app.accounts.changePassword(account.id, values[0], values[1], values[2])
                if (error == null) { dialog = null; notice = "Password changed" } else setError(error)
            }
        }
        AccountDialog.Delete -> PasswordDialog(
            title = "Delete ${account.displayName}?",
            message = "This permanently erases this account's products, sales and settings from this phone. Other accounts are not affected.",
            fields = listOf("Password"),
            confirmLabel = "Delete forever",
            onDismiss = { dialog = null },
        ) { values, setError ->
            scope.launch {
                // Close the session first so the database files can be removed.
                val id = account.id
                val ok = app.accounts.get(id)?.let { PasswordHasher.verify(values[0], it.passwordHash, it.passwordSalt, it.passwordIterations) } == true
                if (!ok) { setError("Wrong password"); return@launch }
                signOut()
                app.accounts.delete(id, values[0])
            }
        }
        null -> Unit
    }
}

@Composable
private fun ActionRow(icon: ImageVector, label: String, destructive: Boolean = false, onClick: () -> Unit) {
    val color = if (destructive) MaterialTheme.colorScheme.error else actionTextColor
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, null, tint = color)
        Spacer(Modifier.width(12.dp))
        Text(label, color = color, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PasswordDialog(
    title: String,
    fields: List<String>,
    confirmLabel: String,
    onDismiss: () -> Unit,
    message: String? = null,
    onConfirm: (List<String>, (String) -> Unit) -> Unit,
) {
    val values = remember { fields.map { mutableStateOf("") } }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                message?.let { Text(it) }
                fields.forEachIndexed { i, label ->
                    OutlinedTextField(
                        value = values[i].value, onValueChange = { values[i].value = it; error = null },
                        label = { Text(label) }, singleLine = true, colors = solidTextFieldColors(),
                        visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(),
                    )
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(values.map { it.value }) { error = it } }) { Text(confirmLabel) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
