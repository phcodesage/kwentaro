package com.phcodesage.kwentaro.ui.onboarding

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.phcodesage.kwentaro.R
import com.phcodesage.kwentaro.data.SettingsRepository
import com.phcodesage.kwentaro.data.StoreSettings
import com.phcodesage.kwentaro.ui.theme.Clay
import com.phcodesage.kwentaro.ui.theme.Ink
import com.phcodesage.kwentaro.ui.theme.Jade
import com.phcodesage.kwentaro.ui.theme.Mango
import com.phcodesage.kwentaro.ui.theme.Paper
import com.phcodesage.kwentaro.ui.theme.SolidSystemBars
import com.phcodesage.kwentaro.ui.theme.solidTextFieldColors
import java.io.IOException
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

private data class IntroPage(val animation: Int, val headline: String, val subtitle: String, val background: Color, val foreground: Color)

private val pages = listOf(
    IntroPage(R.raw.onboarding_sell, "Benta in seconds", "Tap your products. Tuloy ang benta.", Jade, Paper),
    IntroPage(R.raw.onboarding_scan, "Scan, tap, tapos", "Find every item with a quick scan.", Mango, Ink),
    IntroPage(R.raw.onboarding_receipt, "Resibo na agad", "A clear receipt for every sale. Salamat!", Clay, Paper),
    IntroPage(R.raw.onboarding_insights, "Know your kita", "See your sales and profit at a glance.", Ink, Paper),
)

@Composable
fun OnboardingScreen(settings: StoreSettings, repository: SettingsRepository) {
    val pager = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val reducedMotion = rememberReducedMotion()
    var settingUp by rememberSaveable { mutableStateOf(false) }
    var storeName by rememberSaveable { mutableStateOf(settings.storeName) }
    var currency by rememberSaveable { mutableStateOf(settings.currencySymbol) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val page = pages[pager.currentPage]
    val background = if (settingUp) Jade else page.background
    val foreground = if (settingUp) Paper else page.foreground

    fun finish(skip: Boolean) {
        if (saving) return
        if (!skip && (storeName.isBlank() || currency.isBlank())) {
            error = "Add a store name and currency symbol to continue."
            return
        }
        saving = true
        error = null
        scope.launch {
            try {
                if (skip) repository.setOnboardingDone(true)
                else repository.completeOnboarding(storeName, currency)
            } catch (_: IOException) {
                saving = false
                error = "Couldn't save just yet. Please try again."
            }
        }
    }

    BackHandler(enabled = settingUp && !saving) { settingUp = false; error = null }
    SolidSystemBars(background, background)
    Surface(Modifier.fillMaxSize(), color = background, contentColor = foreground) {
        Column(Modifier.safeDrawingPadding().imePadding()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(color = Jade, shape = MaterialTheme.shapes.small) {
                    Image(painterResource(R.drawable.ic_kwentaro_mark), null, Modifier.size(40.dp))
                }
                Text("Kwentaro", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f).padding(start = 12.dp))
                TextButton(
                    onClick = { finish(skip = true) }, enabled = !saving,
                    colors = ButtonDefaults.textButtonColors(contentColor = foreground, disabledContentColor = foreground),
                ) { Text("Skip") }
            }
            if (settingUp) {
                Column(
                    Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                ) {
                    BrandFallback(Modifier.size(96.dp))
                    Text("Set up your store", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                    Text("Your shop, your kwenta.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                    Surface(
                        Modifier.widthIn(max = 440.dp).fillMaxWidth(), color = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface, shape = MaterialTheme.shapes.large,
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = storeName, onValueChange = { storeName = it; error = null },
                                label = { Text("Store name") }, singleLine = true, enabled = !saving,
                                colors = solidTextFieldColors(), modifier = Modifier.fillMaxWidth(),
                            )
                            OutlinedTextField(
                                value = currency, onValueChange = { currency = it.take(4); error = null },
                                label = { Text("Currency symbol") }, singleLine = true, enabled = !saving,
                                colors = solidTextFieldColors(), modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    error?.let { Text(it, color = Paper, textAlign = TextAlign.Center) }
                }
                // Pinned outside the scroll area so the primary action stays visible on small screens.
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    IntroButton(if (saving) "Saving…" else "Get started", !saving) { finish(skip = false) }
                    TextButton(
                        onClick = { settingUp = false; error = null }, enabled = !saving,
                        colors = ButtonDefaults.textButtonColors(contentColor = Paper, disabledContentColor = Paper),
                    ) { Text("Back to intro") }
                }
            } else {
                HorizontalPager(state = pager, modifier = Modifier.weight(1f), userScrollEnabled = !saving) { index ->
                    val intro = pages[index]
                    val offset = ((pager.currentPage - index) + pager.currentPageOffsetFraction).absoluteValue.coerceIn(0f, 1f)
                    BoxWithConstraints(Modifier.fillMaxSize().background(intro.background).padding(horizontal = 24.dp)) {
                        val illustrationSize = (maxHeight * 0.55f).coerceIn(144.dp, 340.dp)
                        val wide = maxWidth >= 600.dp
                        val scale = if (reducedMotion) 1f else 1f - offset * 0.06f
                        if (wide) {
                            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                IntroAnimation(intro.animation, reducedMotion, pager.currentPage == index, Modifier.size(illustrationSize).graphicsLayer { scaleX = scale; scaleY = scale })
                                IntroCopy(intro, Modifier.widthIn(max = 420.dp).weight(1f).verticalScroll(rememberScrollState()).padding(start = 32.dp))
                            }
                        } else {
                            Column(
                                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
                            ) {
                                IntroAnimation(intro.animation, reducedMotion, pager.currentPage == index, Modifier.size(illustrationSize).graphicsLayer { scaleX = scale; scaleY = scale })
                                IntroCopy(intro)
                            }
                        }
                    }
                }
                Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(
                        Modifier.semantics { contentDescription = "Page ${pager.currentPage + 1} of ${pages.size}" },
                        horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(pages.size) { index ->
                            val selected = index == pager.currentPage
                            val width by animateDpAsState(if (selected) 28.dp else 8.dp, if (reducedMotion) snap() else tween(220), label = "Page indicator")
                            val fill = if (page.background == Mango) { if (selected) Jade else Ink } else { if (selected) Mango else Paper }
                            Box(Modifier.width(width).height(8.dp).background(fill, CircleShape))
                        }
                    }
                    error?.let { Text(it, color = foreground, textAlign = TextAlign.Center) }
                    IntroButton(if (pager.currentPage == pages.lastIndex) "Get started" else "Next", !saving) {
                        if (pager.currentPage == pages.lastIndex) settingUp = true
                        else scope.launch {
                            if (reducedMotion) pager.scrollToPage(pager.currentPage + 1)
                            else pager.animateScrollToPage(pager.currentPage + 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IntroCopy(page: IntroPage, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(page.headline, color = page.foreground, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
        Text(page.subtitle, color = page.foreground, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    }
}

@Composable
private fun IntroButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick, enabled = enabled,
        modifier = Modifier.widthIn(max = 440.dp).fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = Paper, contentColor = Jade, disabledContainerColor = Paper, disabledContentColor = Jade,
        ),
    ) { Text(label, style = MaterialTheme.typography.titleMedium) }
}

@Composable
private fun IntroAnimation(resource: Int, reducedMotion: Boolean, active: Boolean, modifier: Modifier) {
    val result = rememberLottieComposition(LottieCompositionSpec.RawRes(resource))
    val composition by result
    Surface(modifier, color = Paper, shape = MaterialTheme.shapes.extraLarge) {
        when {
            result.isFailure || composition == null -> Box(contentAlignment = Alignment.Center) { BrandFallback(Modifier.size(132.dp)) }
            reducedMotion -> LottieAnimation(composition = composition, progress = { 1f })
            else -> LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever, isPlaying = active)
        }
    }
}

@Composable
private fun BrandFallback(modifier: Modifier) {
    Surface(modifier, color = Jade, shape = MaterialTheme.shapes.large) {
        Image(painterResource(R.drawable.ic_kwentaro_mark), contentDescription = "Kwentaro tally mark", modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun rememberReducedMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    fun read() = Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    var reduced by remember(resolver) { mutableStateOf(read()) }
    DisposableEffect(resolver) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) { reduced = read() }
        }
        resolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, observer)
        reduced = read()
        onDispose { resolver.unregisterContentObserver(observer) }
    }
    return reduced
}
