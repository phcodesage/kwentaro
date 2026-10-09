package com.phcodesage.kwentaro.data

/** A starter catalogue so the register is usable on first launch. */
object SampleData {
    private fun p(name: String, cat: String, price: Long, cost: Long, stock: Int, code: String) =
        Product(name = name, category = cat, priceCents = price * 100, costCents = cost * 100, stock = stock, barcode = code)

    val products = listOf(
        p("Brewed Coffee", "Drinks", 45, 15, 80, "4800000000019"),
        p("Iced Tea", "Drinks", 35, 10, 60, "4800000000026"),
        p("Bottled Water", "Drinks", 20, 8, 120, "4800000000033"),
        p("Mango Shake", "Drinks", 65, 25, 30, "4800000000040"),
        p("Pandesal (10 pcs)", "Bakery", 30, 12, 40, "4800000000057"),
        p("Ensaymada", "Bakery", 25, 9, 24, "4800000000064"),
        p("Ube Cheese Roll", "Bakery", 35, 14, 4, "4800000000071"),
        p("Chicken Adobo Rice", "Meals", 95, 40, 20, "4800000000088"),
        p("Pancit Canton", "Meals", 75, 28, 25, "4800000000095"),
        p("Tapsilog", "Meals", 110, 48, 15, "4800000000101"),
        p("Banana Chips", "Snacks", 28, 10, 50, "4800000000118"),
        p("Chicharon", "Snacks", 40, 15, 3, "4800000000125"),
    )
}
