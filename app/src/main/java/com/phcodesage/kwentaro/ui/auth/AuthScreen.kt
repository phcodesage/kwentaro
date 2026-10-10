package com.phcodesage.kwentaro.ui.auth

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phcodesage.kwentaro.KwentaroApp
import com.phcodesage.kwentaro.R
import com.phcodesage.kwentaro.data.auth.Account
import com.phcodesage.kwentaro.data.auth.AuthResult
import com.phcodesage.kwentaro.ui.theme.Jade
import com.phcodesage.kwentaro.ui.theme.Paper
import com.phcodesage.kwentaro.ui.theme.SolidSystemBars
import com.phcodesage.kwentaro.ui.theme.solidTextFieldColors
import kotlinx.coroutines.launch

private sealed interface AuthStep {
    data object Picker : AuthStep
    data class SignIn(val accountId: String) : AuthStep
    data object SignUp : AuthStep
    data class Recover(val username: String = "") : AuthStep
    data class ShowRecoveryCode(val account: Account, val code: String, val sampleData: Boolean) : AuthStep
}

/** Offline sign-in / sign-up. Each account opens its own, separate store. */
@Composable
fun AuthScreen(app: KwentaroApp) {
    val accounts by app.accounts.accounts.collectAsStateWithLifecycle(null)
    val list = accounts ?: return
    var step by remember { mutableStateOf<AuthStep>(AuthStep.Picker) }
    var keepSignedIn by rememberSaveable { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    fun enter(account: Account) {
        app.accounts.rememberedAccountId = if (keepSignedIn) account.id else null
        app.sessions.open(account)
    }

    // First launch, or the last account was deleted: go straight to sign-up.
    val current = if (list.isEmpty() && step == AuthStep.Picker) AuthStep.SignUp else step
    val back: (() -> Unit)? = if (list.isNotEmpty() && current != AuthStep.Picker) ({ step = AuthStep.Picker }) else null

    // System back returns to the account picker instead of closing the app mid-form.
    BackHandler(enabled = back != null && current !is AuthStep.ShowRecoveryCode) { back?.invoke() }

    SolidSystemBars(Jade, Jade)
    Surface(Modifier.fillMaxSize(), color = Jade, contentColor = Paper) {
        Column(Modifier.safeDrawingPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (back != null && current !is AuthStep.ShowRecoveryCode) {
                    IconButton(onClick = back) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = Paper) }
                }
                Image(painterResource(R.drawable.ic_kwentaro_mark), null, Modifier.size(40.dp))
                Spacer(Modifier.width(10.dp))
                Text("Kwentaro", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(28.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Column(Modifier.widthIn(max = 440.dp).fillMaxWidth()) {
                    when (current) {
                        AuthStep.Picker -> Picker(
                            accounts = list,
                            onPick = { step = AuthStep.SignIn(it.id) },
                            onNew = { step = AuthStep.SignUp },
                        )
                        is AuthStep.SignIn -> list.firstOrNull { it.id == current.accountId }?.let { account ->
                            SignIn(account, keepSignedIn, { keepSignedIn = it }, onForgot = { step = AuthStep.Recover(account.username) }) { password, done ->
                                scope.launch {
                                    when (val r = app.accounts.signIn(account.id, password)) {
                                        is AuthResult.Success -> enter(r.account)
                                        is AuthResult.Error -> done(r.message)
                                    }
                                }
                            }
                        }
                        AuthStep.SignUp -> SignUp(app, isFirst = list.isEmpty()) { name, user, pw, confirm, sample, done ->
                            scope.launch {
                                when (val r = app.accounts.signUp(name, user, pw, confirm)) {
                                    is AuthResult.Success -> step = AuthStep.ShowRecoveryCode(r.account, r.recoveryCode!!, sample)
                                    is AuthResult.Error -> done(r.message)
                                }
                            }
                        }
                        is AuthStep.Recover -> Recover(current.username) { user, code, pw, confirm, done ->
                            scope.launch {
                                when (val r = app.accounts.resetWithRecoveryCode(user, code, pw, confirm)) {
                                    is AuthResult.Success -> enter(r.account)
                                    is AuthResult.Error -> done(r.message)
                                }
                            }
                        }
                        is AuthStep.ShowRecoveryCode -> RecoveryCode(current.code) {
                            app.accounts.rememberedAccountId = if (keepSignedIn) current.account.id else null
                            val session = app.sessions.open(current.account)
                            // App scope: this screen leaves composition as soon as the session opens.
                            if (current.sampleData) app.appScope.launch { session.repository.seedIfEmpty() }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Heading(title: String, subtitle: String) {
    Text(title, style = MaterialTheme.typography.headlineMedium)
    Spacer(Modifier.height(6.dp))
    Text(subtitle, style = MaterialTheme.typography.bodyLarge)
    Spacer(Modifier.height(20.dp))
}

@Composable
private fun Card(content: @Composable () -> Unit) {
    Surface(color = Paper, contentColor = MaterialTheme.colorScheme.onSurface, shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { content() }
    }
}

@Composable
private fun PrimaryButton(label: String, busy: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick, enabled = !busy, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = Jade, contentColor = Paper, disabledContainerColor = Jade, disabledContentColor = Paper),
    ) {
        if (busy) CircularProgressIndicator(Modifier.size(22.dp), color = Paper, strokeWidth = 2.dp)
        else Text(label, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun ErrorText(message: String?) {
    message?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }
}

@Composable
private fun Field(value: String, onChange: (String) -> Unit, label: String, password: Boolean = false, keyboard: KeyboardType = KeyboardType.Text) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value, onValueChange = onChange, label = { Text(label) }, singleLine = true,
        colors = solidTextFieldColors(), modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (password && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else keyboard, imeAction = ImeAction.Next),
        trailingIcon = if (password) ({
            // Not focusable, so the keyboard's Next goes to the following field rather than this toggle.
            IconButton(onClick = { visible = !visible }, modifier = Modifier.focusProperties { canFocus = false }) {
                Icon(if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, if (visible) "Hide password" else "Show password")
            }
        }) else null,
    )
}

@Composable
private fun Avatar(name: String, size: Int = 44) {
    Surface(Modifier.size(size.dp), shape = CircleShape, color = MaterialTheme.colorScheme.tertiary, contentColor = MaterialTheme.colorScheme.onTertiary) {
        Box(contentAlignment = Alignment.Center) {
            Text(name.split(" ").filter { it.firstOrNull()?.isLetterOrDigit() == true }.take(2).joinToString("") { it.first().uppercase() }, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Picker(accounts: List<Account>, onPick: (Account) -> Unit, onNew: () -> Unit) {
    Heading("Who's selling?", "Each account keeps its own products, sales and receipts.")
    Card {
        accounts.forEach { a ->
            Row(
                Modifier.fillMaxWidth().clickable { onPick(a) }.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Avatar(a.displayName)
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(a.displayName, style = MaterialTheme.typography.titleMedium)
                    Text("@${a.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
    Spacer(Modifier.height(16.dp))
    TextButton(onClick = onNew, colors = ButtonDefaults.textButtonColors(contentColor = Paper)) {
        Icon(Icons.Rounded.PersonAdd, null); Spacer(Modifier.width(8.dp)); Text("Create another account")
    }
}

@Composable
private fun KeepSignedIn(checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.clickable { onChange(!checked) }, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked, onChange, colors = CheckboxDefaults.colors(checkedColor = Jade))
        Text("Keep me signed in on this phone")
    }
}

@Composable
private fun SignIn(
    account: Account, keepSignedIn: Boolean, onKeep: (Boolean) -> Unit, onForgot: () -> Unit,
    submit: (String, (String?) -> Unit) -> Unit,
) {
    var password by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Avatar(account.displayName, 56)
        Spacer(Modifier.width(14.dp))
        Column {
            Text("Hi, ${account.displayName.substringBefore(' ')}!", style = MaterialTheme.typography.headlineMedium)
            Text("@${account.username}", style = MaterialTheme.typography.bodyLarge)
        }
    }
    Spacer(Modifier.height(20.dp))
    Card {
        Field(password, { password = it; error = null }, "Password", password = true)
        KeepSignedIn(keepSignedIn, onKeep)
        ErrorText(error)
        PrimaryButton("Sign in", busy) { busy = true; submit(password) { error = it; busy = false } }
    }
    TextButton(onClick = onForgot, colors = ButtonDefaults.textButtonColors(contentColor = Paper)) { Text("Forgot password?") }
}

@Composable
private fun SignUp(app: KwentaroApp, isFirst: Boolean, submit: (String, String, String, String, Boolean, (String?) -> Unit) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var sample by rememberSaveable { mutableStateOf(true) }
    var legacy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { legacy = app.accounts.hasLegacyData() }

    Heading(
        if (isFirst) "Create your account" else "New account",
        "Everything stays on this phone. No internet or email needed.",
    )
    if (legacy) {
        Surface(color = MaterialTheme.colorScheme.tertiary, contentColor = MaterialTheme.colorScheme.onTertiary, shape = MaterialTheme.shapes.medium) {
            Text(
                "We found store data already on this phone. It will belong to this account.",
                Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(Modifier.height(14.dp))
    }
    Card {
        Field(name, { name = it; error = null }, "Your name")
        Field(username, { username = it.lowercase().filter { c -> !c.isWhitespace() }; error = null }, "Username")
        Field(password, { password = it; error = null }, "Password", password = true)
        Field(confirm, { confirm = it; error = null }, "Confirm password", password = true)
        if (!legacy) {
            Row(Modifier.clickable { sample = !sample }, verticalAlignment = Alignment.CenterVertically) {
                Checkbox(sample, { sample = it }, colors = CheckboxDefaults.colors(checkedColor = Jade))
                Text("Start with sample products")
            }
        }
        ErrorText(error)
        PrimaryButton("Create account", busy) {
            busy = true
            submit(name, username, password, confirm, sample && !legacy) { error = it; busy = false }
        }
    }
}

@Composable
private fun Recover(prefill: String, submit: (String, String, String, String, (String?) -> Unit) -> Unit) {
    var username by rememberSaveable { mutableStateOf(prefill) }
    var code by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    Heading("Reset password", "Use the recovery code you saved when you created the account.")
    Card {
        Field(username, { username = it.lowercase().trim(); error = null }, "Username")
        Field(code, { code = it.uppercase(); error = null }, "Recovery code (XXXX-XXXX-XXXX)")
        Field(password, { password = it; error = null }, "New password", password = true)
        Field(confirm, { confirm = it; error = null }, "Confirm new password", password = true)
        ErrorText(error)
        PrimaryButton("Reset and sign in", busy) { busy = true; submit(username, code, password, confirm) { error = it; busy = false } }
    }
}

@Composable
private fun RecoveryCode(code: String, onContinue: () -> Unit) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }
    Heading("Save your recovery code", "It's the only way to reset your password without internet. Write it down and keep it safe.")
    Card {
        Text(
            code, style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
        )
        TextButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Kwentaro recovery code", code))
                copied = true
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text(if (copied) "Copied" else "Copy code") }
        PrimaryButton("I saved it, continue", false, onContinue)
    }
}
