package com.phcodesage.kwentaro.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductTemplatesTest {
    @Test
    fun tagalogShopNamesFindTheirImages() {
        mapOf(
            "kape" to "coffee_cup",
            "tinapay" to "pandesal",
            "itlog" to "eggs_tray",
            "bigas" to "rice_sack",
            "sabon" to "soap_bar",
            "yelo" to "ice_bag",
            "load" to "phone_load",
        ).forEach { (name, key) ->
            assertEquals(name, key, ProductTemplates.suggest(name).firstOrNull()?.key)
        }
    }

    @Test
    fun specificPhrasesBeatSharedIngredients() {
        mapOf(
            "Banana Chips 50g" to "banana_chips",
            "Milk Tea Large" to "milk_tea",
            "Chicken Adobo Rice" to "adobo",
            "Iced Coffee 16 oz" to "iced_coffee",
            "Water refill gallon" to "water_gallon",
            "Hotdog pack 1kg" to "hotdog_pack",
            "Shampoo sachet" to "shampoo_sachet",
        ).forEach { (name, key) ->
            assertEquals(name, key, ProductTemplates.strongSuggestion(name)?.key)
        }
    }

    @Test
    fun punctuationAndCaseDoNotHideMatches() {
        assertEquals("coffee_sachet", ProductTemplates.strongSuggestion("3-in-1 COFFEE")?.key)
        assertEquals("halo_halo", ProductTemplates.strongSuggestion("HALO–HALO")?.key)
        assertEquals("phone_load", ProductTemplates.strongSuggestion("E-load ₱50")?.key)
    }

    @Test
    fun typosAndPartialWordsSuggestWithoutAutomaticallySelecting() {
        mapOf("pandesl" to "pandesal", "chichron" to "chicharon", "toothpste" to "toothpaste", "coff" to "coffee_cup")
            .forEach { (name, key) ->
                assertEquals(name, key, ProductTemplates.suggest(name).firstOrNull()?.key)
                assertNull(name, ProductTemplates.strongSuggestion(name))
            }
    }

    @Test
    fun emptyAndUnrelatedNamesDoNotSelectArbitraryImages() {
        assertTrue(ProductTemplates.suggest("   ").isEmpty())
        assertTrue(ProductTemplates.suggest("unknown xyz").isEmpty())
        assertNull(ProductTemplates.strongSuggestion("unknown xyz"))
        assertFalse(ProductTemplates.suggest("steak").any { it.key == "tea_cup" })
        assertFalse(ProductTemplates.suggest("open").any { it.key == "pen" })
        assertNull(ProductTemplates.byKey(null))
        assertNull(ProductTemplates.byKey("missing_template"))
    }

    @Test
    fun seededProductsHaveRegisteredFittingImages() {
        SampleData.products.forEach { product ->
            val template = ProductTemplates.byKey(product.templateKey)
            assertEquals(product.name, product.category, template?.category)
            assertEquals(product.name, product.templateKey, ProductTemplates.strongSuggestion(product.name)?.key)
        }
    }
}
