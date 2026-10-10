package com.phcodesage.kwentaro.ui.camera

import android.content.Context
import android.graphics.Matrix
import android.util.Size
import androidx.camera.core.ImageProxy
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.core.ImageAnalysis
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning

/** Google ML Kit (bundled model, works offline). */
fun createBarcodeAnalyzer(context: Context, onCode: (String) -> Unit): BarcodeAnalyzer {
    val scanner = BarcodeScanning.getClient()
    // Coordinates aren't used, so ORIGINAL avoids depending on the view transform.
    val delegate = MlKitAnalyzer(listOf(scanner), ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL, ContextCompat.getMainExecutor(context)) { result ->
        result?.getValue(scanner)?.firstOrNull()?.rawValue?.let(onCode)
    }
    return object : BarcodeAnalyzer {
        override fun analyze(image: ImageProxy) = delegate.analyze(image)
        override fun getDefaultTargetResolution(): Size? = delegate.defaultTargetResolution
        override fun getTargetCoordinateSystem() = delegate.targetCoordinateSystem
        override fun updateTransform(matrix: Matrix?) = delegate.updateTransform(matrix)
        override fun close() = scanner.close()
    }
}
