package com.phcodesage.kwentaro.ui.camera

import org.junit.Assert.assertEquals
import org.junit.Test
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.EAN13Writer

class ZxingDecoderTest {
    // Rendered with ZXing's own encoder: a quiet-zoned EAN-13 like a printed label.
    private val upright: ZxingDecoder.Frame = run {
        val matrix = EAN13Writer().encode("4800000000118", BarcodeFormat.EAN_13, 640, 240)
        val luma = ByteArray(matrix.width * matrix.height) { i ->
            if (matrix.get(i % matrix.width, i / matrix.width)) 0 else 255.toByte()
        }
        ZxingDecoder.Frame(luma, matrix.width, matrix.height)
    }

    /** Builds the frame a camera would deliver when the content needs [degrees] clockwise to be upright. */
    private fun sensorFrame(degrees: Int): ZxingDecoder.Frame {
        var f = upright
        repeat(((360 - degrees) / 90) % 4) { f = f.rotated90() }
        return f
    }

    @Test
    fun decodesEan13AtEverySensorRotation() {
        for (degrees in listOf(0, 90, 180, 270)) {
            val f = sensorFrame(degrees)
            assertEquals("rotation $degrees", "4800000000118", ZxingDecoder().decode(f.data, f.width, f.height, degrees))
        }
    }

    @Test
    fun decodesCodeHeldSideways() {
        val sideways = upright.rotated90()
        assertEquals("4800000000118", ZxingDecoder().decode(sideways.data, sideways.width, sideways.height, 0))
    }
}
