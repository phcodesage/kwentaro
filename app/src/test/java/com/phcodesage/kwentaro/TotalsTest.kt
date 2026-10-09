package com.phcodesage.kwentaro

import com.phcodesage.kwentaro.data.CartLine
import com.phcodesage.kwentaro.data.Product
import com.phcodesage.kwentaro.data.SampleData
import com.phcodesage.kwentaro.data.StoreSettings
import com.phcodesage.kwentaro.data.Totals
import com.phcodesage.kwentaro.util.formatMoney
import com.phcodesage.kwentaro.util.parseMoneyToCents
import org.junit.Assert.assertEquals
import org.junit.Test

class TotalsTest {
    private fun line(price: Long, qty: Int) = CartLine(Product(name = "x", priceCents = price), qty)

    @Test
    fun inclusiveVatIsBackedOutOfTheTotal() {
        val t = Totals.of(listOf(line(2800, 2), line(2000, 1)), 10, StoreSettings(taxRatePercent = 12f, taxInclusive = true))
        assertEquals(7600, t.subtotalCents)
        assertEquals(760, t.discountCents)
        assertEquals(6840, t.totalCents)
        assertEquals(733, t.taxCents) // 6840 - 6840 / 1.12
    }

    @Test
    fun exclusiveTaxIsAddedOnTop() {
        val t = Totals.of(listOf(line(10000, 1)), 0, StoreSettings(taxRatePercent = 12f, taxInclusive = false))
        assertEquals(1200, t.taxCents)
        assertEquals(11200, t.totalCents)
    }

    @Test
    fun moneyParsingAndFormatting() {
        assertEquals(125050L, "1,250.5".parseMoneyToCents())
        assertEquals(null, "abc".parseMoneyToCents())
        assertEquals("₱1,250.50", 125050L.formatMoney("₱"))
        assertEquals("-₱7.60", (-760L).formatMoney("₱"))
    }

    @Test
    fun sampleBarcodesHaveValidEan13CheckDigits() {
        SampleData.products.mapNotNull { it.barcode }.forEach { code ->
            val sum = code.take(12).mapIndexed { i, c -> (c - '0') * if (i % 2 == 1) 3 else 1 }.sum()
            assertEquals("bad check digit in $code", (10 - sum % 10) % 10, code.last() - '0')
        }
    }
}
