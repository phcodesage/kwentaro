package com.phcodesage.kwentaro.ui.camera

import androidx.camera.core.ImageAnalysis
import java.io.Closeable

/**
 * A camera frame analyzer that reports decoded barcodes. Each build flavor provides
 * `createBarcodeAnalyzer(context, onCode)`: ML Kit in `full`, the open-source ZXing in `foss`.
 * [onCode] is always delivered on the main thread.
 */
interface BarcodeAnalyzer : ImageAnalysis.Analyzer, Closeable
