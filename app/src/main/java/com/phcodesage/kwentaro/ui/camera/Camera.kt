package com.phcodesage.kwentaro.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.NoPhotography
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import java.io.File
import java.util.UUID

private fun Context.hasCameraPermission() =
    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

/** Full-screen camera dialog that asks for permission first. */
@Composable
private fun CameraDialog(
    onDismiss: () -> Unit,
    content: @Composable BoxScope.(controller: LifecycleCameraController) -> Unit,
) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(context.hasCameraPermission()) }
    var denied by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        granted = it
        denied = !it
    }
    LaunchedEffect(Unit) { if (!granted) launcher.launch(Manifest.permission.CAMERA) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            if (granted) {
                val lifecycleOwner = LocalLifecycleOwner.current
                val controller = remember { LifecycleCameraController(context) }
                DisposableEffect(lifecycleOwner) {
                    // Some tablets and Chromebooks only have a front camera; fall back to it.
                    controller.initializationFuture.addListener({
                        if (!controller.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) &&
                            controller.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
                        ) controller.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                    }, ContextCompat.getMainExecutor(context))
                    controller.bindToLifecycle(lifecycleOwner)
                    onDispose { controller.unbind() }
                }
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            this.controller = controller
                        }
                    },
                )
                content(controller)
            } else if (denied) {
                Column(
                    Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(Icons.Rounded.NoPhotography, null, tint = Color.White, modifier = Modifier.size(56.dp))
                    Text(
                        "Kwentaro needs the camera to scan barcodes and photograph products.",
                        color = Color.White, textAlign = TextAlign.Center,
                    )
                    Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) { Text("Allow camera") }
                }
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.safeDrawingPadding().padding(8.dp),
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.4f), contentColor = Color.White),
            ) { Icon(Icons.Rounded.Close, "Close camera") }
        }
    }
}

@Composable
private fun TorchToggle(controller: LifecycleCameraController, modifier: Modifier = Modifier) {
    var on by remember { mutableStateOf(false) }
    IconButton(
        onClick = { on = !on; controller.enableTorch(on) },
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.4f), contentColor = Color.White),
    ) { Icon(if (on) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff, "Toggle flashlight") }
}

/**
 * Live barcode scanner (EAN/UPC/QR/Code128...). Calls [onScanned] once per distinct code;
 * set [continuous] to keep scanning after a hit, e.g. when ringing up many items.
 */
@Composable
fun BarcodeScannerDialog(
    onScanned: (String) -> Unit,
    onDismiss: () -> Unit,
    continuous: Boolean = false,
    hint: String = "Point at a barcode",
) {
    val context = LocalContext.current
    var lastCode by remember { mutableStateOf<String?>(null) }
    var lastAt by remember { mutableStateOf(0L) }
    CameraDialog(onDismiss) { controller ->
        DisposableEffect(controller) {
            val scanner = BarcodeScanning.getClient()
            val executor = ContextCompat.getMainExecutor(context)
            controller.setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            controller.setImageAnalysisAnalyzer(
                executor,
                MlKitAnalyzer(listOf(scanner), CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED, executor) { result ->
                    val code = result?.getValue(scanner)?.firstOrNull()?.rawValue ?: return@MlKitAnalyzer
                    val now = System.currentTimeMillis()
                    // The same code stays in frame for many frames; debounce repeats.
                    if (code == lastCode && now - lastAt < 2000) return@MlKitAnalyzer
                    lastCode = code
                    lastAt = now
                    onScanned(code)
                    if (!continuous) onDismiss()
                },
            )
            onDispose {
                controller.clearImageAnalysisAnalyzer()
                scanner.close()
            }
        }
        ScanOverlay()
        Column(
            Modifier.align(Alignment.BottomCenter).safeDrawingPadding().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(shape = CircleShape, color = Color.Black.copy(alpha = 0.55f)) {
                Text(
                    lastCode?.let { if (continuous) "Added $it" else it } ?: hint,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                )
            }
            TorchToggle(controller)
        }
    }
}

@Composable
private fun BoxScope.ScanOverlay() {
    val sweep by rememberInfiniteTransition(label = "scan").animateFloat(
        0f, 1f, infiniteRepeatable(tween(1600), RepeatMode.Reverse), label = "sweep",
    )
    val accent = MaterialTheme.colorScheme.tertiary
    Canvas(
        Modifier.matchParentSize().graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
    ) {
        val w = size.width * 0.78f
        val h = w * 0.62f
        val topLeft = Offset((size.width - w) / 2, (size.height - h) / 2.4f)
        drawRect(Color.Black.copy(alpha = 0.55f))
        drawRoundRect(Color.Transparent, topLeft, Size(w, h), CornerRadius(28.dp.toPx()), blendMode = BlendMode.Clear)
        drawRoundRect(
            accent, topLeft, Size(w, h), CornerRadius(28.dp.toPx()),
            style = Stroke(3.dp.toPx()),
        )
        val y = topLeft.y + 16.dp.toPx() + (h - 32.dp.toPx()) * sweep
        drawLine(accent, Offset(topLeft.x + 20.dp.toPx(), y), Offset(topLeft.x + w - 20.dp.toPx(), y), 3.dp.toPx())
    }
}

/** In-app photo capture for product images; saves into app storage and returns the file path. */
@Composable
fun PhotoCaptureDialog(onCaptured: (String) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    CameraDialog(onDismiss) { controller ->
        var ready by remember { mutableStateOf(false) }
        LaunchedEffect(controller) {
            controller.setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            // takePicture() throws until the camera provider is up, so gate the shutter on it.
            controller.initializationFuture.addListener({ ready = true }, ContextCompat.getMainExecutor(context))
        }
        Box(
            Modifier.align(Alignment.Center).fillMaxWidth(0.8f).aspectRatio(1f)
                .border(2.dp, Color.White.copy(alpha = 0.7f), MaterialTheme.shapes.large),
        )
        Row(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().safeDrawingPadding().padding(32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TorchToggle(controller)
            FilledIconButton(
                onClick = {
                    if (busy) return@FilledIconButton
                    busy = true
                    val file = productPhotoFile(context)
                    try { controller.takePicture(
                        ImageCapture.OutputFileOptions.Builder(file).build(),
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                onCaptured(file.absolutePath)
                                onDismiss()
                            }

                            override fun onError(exception: ImageCaptureException) {
                                busy = false
                                error = exception.message ?: "Capture failed"
                            }
                        },
                    ) } catch (e: IllegalStateException) {
                        busy = false
                        error = "Camera is still starting, try again"
                    }
                },
                enabled = ready && !busy,
                modifier = Modifier.size(76.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White),
            ) {
                Box(Modifier.size(60.dp).border(3.dp, Color.Black, CircleShape))
            }
            Box(Modifier.size(48.dp))
        }
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.TopCenter).safeDrawingPadding().padding(top = 64.dp))
        }
    }
}

fun productPhotoFile(context: Context): File =
    File(context.filesDir, "product_photos").apply { mkdirs() }.let { File(it, "${UUID.randomUUID()}.jpg") }
