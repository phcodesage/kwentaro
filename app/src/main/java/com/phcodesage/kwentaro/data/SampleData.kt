package com.phcodesage.kwentaro.data

/** A starter catalogue so the register is usable on first launch. */
object SampleData {
    private fun p(name: String, cat: String, price: Long, cost: Long, stock: Int, code: String) =
        Product(name = name, category = cat, priceCents = price * 100, costCents = cost * 100, stock = stock, barcode = code)

    val products = listOf(
        p("Brewed Coffee", "Drinks", 45, 15, 80, "4800000000011"),
        p("Iced Tea", "Drinks", 35, 10, 60, "4800000000028"),
        p("Bottled Water", "Drinks", 20, 8, 120, "4800000000035"),
        p("Mango Shake", "Drinks", 65, 25, 30, "4800000000042"),
        p("Pandesal (10 pcs)", "Bakery", 30, 12, 40, "4800000000059"),
        p("Ensaymada", "Bakery", 25, 9, 24, "4800000000066"),
        p("Ube Cheese Roll", "Bakery", 35, 14, 4, "4800000000073"),
        p("Chicken Adobo Rice", "Meals", 95, 40, 20, "4800000000080"),
        p("Pancit Canton", "Meals", 75, 28, 25, "4800000000097"),
        p("Tapsilog", "Meals", 110, 48, 15, "4800000000103"),
        p("Banana Chips", "Snacks", 28, 10, 50, "4800000000110"),
        p("Chicharon", "Snacks", 40, 15, 3, "4800000000127"),
    )
}
