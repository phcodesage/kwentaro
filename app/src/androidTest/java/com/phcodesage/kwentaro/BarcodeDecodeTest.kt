package com.phcodesage.kwentaro

import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.phcodesage.kwentaro.data.SampleData
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Runs the same bundled ML Kit scanner the camera uses against a rendered EAN-13. */
@RunWith(AndroidJUnit4::class)
class BarcodeDecodeTest {
    @Test
    fun decodesSampleProductEan13() = runBlocking {
        val ctx = InstrumentationRegistry.getInstrumentation().context
        val bitmap = ctx.assets.open("ean13_banana_chips.png").use(BitmapFactory::decodeStream)
        val scanner = BarcodeScanning.getClient()
        val results = scanner.process(InputImage.fromBitmap(bitmap, 0)).await()
        scanner.close()

        val hit = results.single()
        assertEquals(Barcode.FORMAT_EAN_13, hit.format)
        assertEquals("4800000000118", hit.rawValue)
        assertTrue(SampleData.products.any { it.barcode == hit.rawValue })
    }
}
