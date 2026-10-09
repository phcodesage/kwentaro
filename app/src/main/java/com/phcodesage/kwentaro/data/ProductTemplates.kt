package com.phcodesage.kwentaro.data

import androidx.annotation.DrawableRes
import com.phcodesage.kwentaro.R
import java.text.Normalizer
import java.util.Locale

data class ProductTemplate(
    val key: String,
    val label: String,
    val category: String,
    @DrawableRes val res: Int,
    val keywords: List<String>,
)

/** Small, offline illustrations; keys are persisted, so keep them stable. */
object ProductTemplates {
    val all: List<ProductTemplate> = listOf(
        ProductTemplate("coffee_cup", "Hot coffee", "Drinks", R.drawable.tpl_coffee_cup, listOf("kape", "coffee", "brewed coffee", "kapeng barako")),
        ProductTemplate("coffee_sachet", "3-in-1 coffee", "Drinks", R.drawable.tpl_coffee_sachet, listOf("3 in 1", "3in1", "instant coffee", "kape sachet")),
        ProductTemplate("tea_cup", "Hot tea", "Drinks", R.drawable.tpl_tea_cup, listOf("tea", "hot tea", "tsaa")),
        ProductTemplate("hot_chocolate", "Hot chocolate", "Drinks", R.drawable.tpl_hot_chocolate, listOf("hot chocolate", "hot choco", "tsokolate", "cocoa")),
        ProductTemplate("softdrink_can", "Softdrink can", "Drinks", R.drawable.tpl_softdrink_can, listOf("softdrink can", "soda can", "soft drinks lata")),
        ProductTemplate("softdrink_bottle", "Softdrink bottle", "Drinks", R.drawable.tpl_softdrink_bottle, listOf("softdrink bottle", "soda bottle", "softdrinks", "soft drinks")),
        ProductTemplate("juice", "Fruit juice", "Drinks", R.drawable.tpl_juice, listOf("juice", "fruit juice", "orange juice", "inumin")),
        ProductTemplate("iced_tea", "Iced tea", "Drinks", R.drawable.tpl_iced_tea, listOf("iced tea", "ice tea", "lemon tea")),
        ProductTemplate("water_bottle", "Bottled water", "Drinks", R.drawable.tpl_water_bottle, listOf("water", "bottled water", "mineral water", "tubig")),
        ProductTemplate("milk_tea", "Milk tea", "Drinks", R.drawable.tpl_milk_tea, listOf("milk tea", "milktea", "boba", "pearl tea")),
        ProductTemplate("mango_shake", "Mango shake", "Drinks", R.drawable.tpl_mango_shake, listOf("mango shake", "shake", "mangga shake", "fruit shake")),
        ProductTemplate("buko_juice", "Buko juice", "Drinks", R.drawable.tpl_buko_juice, listOf("buko juice", "coconut juice", "buko water")),
        ProductTemplate("iced_coffee", "Iced coffee", "Drinks", R.drawable.tpl_iced_coffee, listOf("iced coffee", "ice coffee", "cold coffee", "iced kape")),
        ProductTemplate("beer_bottle", "Beer bottle", "Drinks", R.drawable.tpl_beer_bottle, listOf("beer", "beer bottle", "serbesa", "bir")),
        ProductTemplate("gin_bottle", "Gin bottle", "Drinks", R.drawable.tpl_gin_bottle, listOf("gin", "gin bottle", "alak", "liquor")),
        ProductTemplate("pandesal", "Pandesal", "Bakery", R.drawable.tpl_pandesal, listOf("pandesal", "pan de sal", "tinapay", "bread roll")),
        ProductTemplate("ensaymada", "Ensaymada", "Bakery", R.drawable.tpl_ensaymada, listOf("ensaymada", "cheese bun")),
        ProductTemplate("loaf_bread", "Loaf bread", "Bakery", R.drawable.tpl_loaf_bread, listOf("loaf bread", "sliced bread", "loaf", "tasty")),
        ProductTemplate("cake_slice", "Cake slice", "Bakery", R.drawable.tpl_cake_slice, listOf("cake slice", "cake", "keyk")),
        ProductTemplate("cupcake", "Cupcake", "Bakery", R.drawable.tpl_cupcake, listOf("cupcake", "muffin")),
        ProductTemplate("donut", "Donut", "Bakery", R.drawable.tpl_donut, listOf("donut", "doughnut")),
        ProductTemplate("croissant", "Croissant", "Bakery", R.drawable.tpl_croissant, listOf("croissant")),
        ProductTemplate("cookies", "Cookies", "Bakery", R.drawable.tpl_cookies, listOf("cookie", "cookies", "galletas")),
        ProductTemplate("hopia", "Hopia", "Bakery", R.drawable.tpl_hopia, listOf("hopia", "mung bean pastry")),
        ProductTemplate("pie", "Pie", "Bakery", R.drawable.tpl_pie, listOf("pie", "buko pie", "egg pie")),
        ProductTemplate("ube_cheese_roll", "Ube cheese roll", "Bakery", R.drawable.tpl_ube_cheese_roll, listOf("ube cheese roll", "ube roll", "ube bread")),
        ProductTemplate("cheese_bread", "Cheese bread", "Bakery", R.drawable.tpl_cheese_bread, listOf("cheese bread", "cheese roll")),
        ProductTemplate("spanish_bread", "Spanish bread", "Bakery", R.drawable.tpl_spanish_bread, listOf("spanish bread")),
        ProductTemplate("rice_plate", "Rice plate", "Meals", R.drawable.tpl_rice_plate, listOf("rice plate", "steamed rice", "kanin", "rice meal")),
        ProductTemplate("adobo", "Adobo rice", "Meals", R.drawable.tpl_adobo, listOf("adobo", "chicken adobo", "pork adobo", "adobong manok")),
        ProductTemplate("silog_plate", "Silog plate", "Meals", R.drawable.tpl_silog_plate, listOf("silog", "tapsilog", "tocilog", "longsilog", "bangsilog")),
        ProductTemplate("pancit", "Pancit", "Meals", R.drawable.tpl_pancit, listOf("pancit", "pansit", "pancit canton", "bihon")),
        ProductTemplate("noodle_bowl", "Noodle bowl", "Meals", R.drawable.tpl_noodle_bowl, listOf("noodle bowl", "mami", "noodle soup", "ramen")),
        ProductTemplate("sopas", "Sopas", "Meals", R.drawable.tpl_sopas, listOf("sopas", "macaroni soup")),
        ProductTemplate("lugaw", "Lugaw / arroz caldo", "Meals", R.drawable.tpl_lugaw, listOf("lugaw", "arroz caldo", "goto", "porridge")),
        ProductTemplate("barbecue_stick", "Barbecue stick", "Meals", R.drawable.tpl_barbecue_stick, listOf("barbecue", "bbq", "ihaw", "inihaw", "barbekyu")),
        ProductTemplate("fried_chicken", "Fried chicken", "Meals", R.drawable.tpl_fried_chicken, listOf("fried chicken", "pritong manok")),
        ProductTemplate("siopao", "Siopao", "Meals", R.drawable.tpl_siopao, listOf("siopao", "steamed bun")),
        ProductTemplate("siomai", "Siomai", "Meals", R.drawable.tpl_siomai, listOf("siomai", "shumai", "dumpling")),
        ProductTemplate("burger", "Burger", "Meals", R.drawable.tpl_burger, listOf("burger", "hamburger", "cheeseburger")),
        ProductTemplate("hotdog", "Hotdog sandwich", "Meals", R.drawable.tpl_hotdog, listOf("hotdog sandwich", "hotdog bun", "hot dog sandwich")),
        ProductTemplate("sandwich", "Sandwich", "Meals", R.drawable.tpl_sandwich, listOf("sandwich", "ham sandwich", "egg sandwich")),
        ProductTemplate("pizza_slice", "Pizza slice", "Meals", R.drawable.tpl_pizza_slice, listOf("pizza", "pizza slice")),
        ProductTemplate("spaghetti", "Spaghetti", "Meals", R.drawable.tpl_spaghetti, listOf("spaghetti", "pasta", "ispageti")),
        ProductTemplate("fries", "French fries", "Meals", R.drawable.tpl_fries, listOf("fries", "french fries", "potato fries")),
        ProductTemplate("fried_rice", "Fried rice", "Meals", R.drawable.tpl_fried_rice, listOf("fried rice", "sinangag", "yang chow")),
        ProductTemplate("pork_bbq_rice", "Barbecue rice", "Meals", R.drawable.tpl_pork_bbq_rice, listOf("barbecue rice", "bbq rice")),
        ProductTemplate("chicken_curry", "Chicken curry", "Meals", R.drawable.tpl_chicken_curry, listOf("chicken curry", "curry", "kare")),
        ProductTemplate("puto", "Puto", "Desserts", R.drawable.tpl_puto, listOf("puto", "steamed rice cake")),
        ProductTemplate("kutsinta", "Kutsinta", "Desserts", R.drawable.tpl_kutsinta, listOf("kutsinta", "cuchinta")),
        ProductTemplate("bibingka", "Bibingka", "Desserts", R.drawable.tpl_bibingka, listOf("bibingka", "rice cake")),
        ProductTemplate("turon", "Turon", "Desserts", R.drawable.tpl_turon, listOf("turon", "banana spring roll")),
        ProductTemplate("banana_cue", "Banana cue", "Desserts", R.drawable.tpl_banana_cue, listOf("banana cue", "bananacue", "banana q")),
        ProductTemplate("halo_halo", "Halo-halo", "Desserts", R.drawable.tpl_halo_halo, listOf("halo halo", "halohalo")),
        ProductTemplate("ice_cream", "Ice cream cone", "Desserts", R.drawable.tpl_ice_cream, listOf("ice cream", "sorbetes", "cone")),
        ProductTemplate("ice_candy", "Ice candy", "Desserts", R.drawable.tpl_ice_candy, listOf("ice candy", "icecandy")),
        ProductTemplate("leche_flan", "Leche flan", "Desserts", R.drawable.tpl_leche_flan, listOf("leche flan", "flan", "custard")),
        ProductTemplate("buko_pandan", "Buko pandan", "Desserts", R.drawable.tpl_buko_pandan, listOf("buko pandan", "pandan dessert")),
        ProductTemplate("chips_bag", "Chips bag", "Snacks", R.drawable.tpl_chips_bag, listOf("chips", "potato chips", "chips bag", "tsitsirya")),
        ProductTemplate("banana_chips", "Banana chips", "Snacks", R.drawable.tpl_banana_chips, listOf("banana chips", "saging chips")),
        ProductTemplate("chicharon", "Chicharon", "Snacks", R.drawable.tpl_chicharon, listOf("chicharon", "chicharron", "pork rinds")),
        ProductTemplate("peanuts", "Peanuts", "Snacks", R.drawable.tpl_peanuts, listOf("peanuts", "mani", "nuts")),
        ProductTemplate("crackers", "Crackers", "Snacks", R.drawable.tpl_crackers, listOf("crackers", "salt crackers")),
        ProductTemplate("candy", "Candy", "Snacks", R.drawable.tpl_candy, listOf("candy", "sweets", "kendi")),
        ProductTemplate("chocolate_bar", "Chocolate bar", "Snacks", R.drawable.tpl_chocolate_bar, listOf("chocolate bar", "chocolate", "tsokolate bar")),
        ProductTemplate("biscuits_pack", "Biscuits pack", "Snacks", R.drawable.tpl_biscuits_pack, listOf("biscuits", "biscuit pack", "biskwit")),
        ProductTemplate("popcorn", "Popcorn", "Snacks", R.drawable.tpl_popcorn, listOf("popcorn")),
        ProductTemplate("rice_sack", "Rice sack", "Grocery", R.drawable.tpl_rice_sack, listOf("rice sack", "rice grain", "bigas", "sako")),
        ProductTemplate("sardines_can", "Canned sardines", "Grocery", R.drawable.tpl_sardines_can, listOf("sardines", "sardinas", "canned sardines")),
        ProductTemplate("corned_beef_can", "Corned beef can", "Grocery", R.drawable.tpl_corned_beef_can, listOf("corned beef", "cornedbeef", "karne norte")),
        ProductTemplate("instant_noodles", "Instant noodles pack", "Grocery", R.drawable.tpl_instant_noodles, listOf("instant noodles", "instant pancit canton", "noodles pack")),
        ProductTemplate("eggs_tray", "Eggs tray", "Grocery", R.drawable.tpl_eggs_tray, listOf("eggs", "egg", "itlog", "eggs tray")),
        ProductTemplate("cooking_oil", "Cooking oil", "Grocery", R.drawable.tpl_cooking_oil, listOf("cooking oil", "oil", "mantika")),
        ProductTemplate("soy_sauce", "Soy sauce", "Grocery", R.drawable.tpl_soy_sauce, listOf("soy sauce", "toyo")),
        ProductTemplate("vinegar", "Vinegar", "Grocery", R.drawable.tpl_vinegar, listOf("vinegar", "suka")),
        ProductTemplate("sugar", "Sugar", "Grocery", R.drawable.tpl_sugar, listOf("sugar", "asukal")),
        ProductTemplate("salt", "Salt", "Grocery", R.drawable.tpl_salt, listOf("salt", "asin")),
        ProductTemplate("coffee_jar", "Coffee jar", "Grocery", R.drawable.tpl_coffee_jar, listOf("coffee jar", "coffee beans", "kape garapon")),
        ProductTemplate("powdered_milk", "Powdered milk", "Grocery", R.drawable.tpl_powdered_milk, listOf("powdered milk", "milk powder", "gatas powder")),
        ProductTemplate("condensed_milk", "Condensed milk", "Grocery", R.drawable.tpl_condensed_milk, listOf("condensed milk", "kondensada", "evaporated milk", "evap")),
        ProductTemplate("bread_spread", "Bread spread", "Grocery", R.drawable.tpl_bread_spread, listOf("bread spread", "peanut butter", "jam", "palaman")),
        ProductTemplate("banana", "Banana", "Produce", R.drawable.tpl_banana, listOf("banana", "saging")),
        ProductTemplate("mango", "Mango", "Produce", R.drawable.tpl_mango, listOf("mango", "mangga")),
        ProductTemplate("tomato", "Tomato", "Produce", R.drawable.tpl_tomato, listOf("tomato", "kamatis")),
        ProductTemplate("onion", "Onion", "Produce", R.drawable.tpl_onion, listOf("onion", "sibuyas")),
        ProductTemplate("garlic", "Garlic", "Produce", R.drawable.tpl_garlic, listOf("garlic", "bawang")),
        ProductTemplate("potato", "Potato", "Produce", R.drawable.tpl_potato, listOf("potato", "patatas")),
        ProductTemplate("vegetables_bundle", "Vegetables bundle", "Produce", R.drawable.tpl_vegetables_bundle, listOf("vegetables", "gulay", "pechay", "kangkong", "talbos")),
        ProductTemplate("calamansi", "Calamansi", "Produce", R.drawable.tpl_calamansi, listOf("calamansi", "kalamansi", "lime")),
        ProductTemplate("coconut", "Coconut", "Produce", R.drawable.tpl_coconut, listOf("coconut", "buko", "niyog")),
        ProductTemplate("pork", "Pork cut", "Meat & fish", R.drawable.tpl_pork, listOf("pork", "baboy", "liempo", "kasim", "karne baboy")),
        ProductTemplate("chicken", "Chicken", "Meat & fish", R.drawable.tpl_chicken, listOf("chicken", "manok", "whole chicken")),
        ProductTemplate("fish", "Fish", "Meat & fish", R.drawable.tpl_fish, listOf("fish", "isda", "bangus", "tilapia", "galunggong")),
        ProductTemplate("shrimp", "Shrimp", "Meat & fish", R.drawable.tpl_shrimp, listOf("shrimp", "hipon", "prawn")),
        ProductTemplate("hotdog_pack", "Hotdog pack", "Meat & fish", R.drawable.tpl_hotdog_pack, listOf("hotdog pack", "hotdogs", "hot dog", "frankfurter")),
        ProductTemplate("ice_bag", "Ice bag", "Frozen", R.drawable.tpl_ice_bag, listOf("ice", "yelo", "ice tube", "tube ice", "ice bag")),
        ProductTemplate("frozen_goods", "Frozen goods", "Frozen", R.drawable.tpl_frozen_goods, listOf("frozen goods", "frozen food", "frozen")),
        ProductTemplate("detergent_sachet", "Detergent sachet", "Household", R.drawable.tpl_detergent_sachet, listOf("detergent", "detergent sachet", "sabon panlaba", "powder detergent")),
        ProductTemplate("dish_soap", "Dish soap", "Household", R.drawable.tpl_dish_soap, listOf("dish soap", "dishwashing liquid", "sabon panghugas")),
        ProductTemplate("bleach", "Bleach", "Household", R.drawable.tpl_bleach, listOf("bleach", "chlorine", "pampaputi")),
        ProductTemplate("tissue_roll", "Tissue roll", "Household", R.drawable.tpl_tissue_roll, listOf("tissue", "toilet paper", "tissue roll")),
        ProductTemplate("candle", "Candle", "Household", R.drawable.tpl_candle, listOf("candle", "kandila")),
        ProductTemplate("matches", "Matches", "Household", R.drawable.tpl_matches, listOf("matches", "matchbox", "posporo")),
        ProductTemplate("battery", "Battery", "Household", R.drawable.tpl_battery, listOf("battery", "batteries", "baterya")),
        ProductTemplate("light_bulb", "Light bulb", "Household", R.drawable.tpl_light_bulb, listOf("light bulb", "bulb", "bombilya", "ilaw")),
        ProductTemplate("lpg_tank", "LPG tank", "Household", R.drawable.tpl_lpg_tank, listOf("lpg", "lpg tank", "gas tank", "gasul")),
        ProductTemplate("shampoo_sachet", "Shampoo sachet", "Personal care", R.drawable.tpl_shampoo_sachet, listOf("shampoo", "shampoo sachet", "siyampu")),
        ProductTemplate("soap_bar", "Soap bar", "Personal care", R.drawable.tpl_soap_bar, listOf("soap", "soap bar", "sabon", "sabong panligo")),
        ProductTemplate("toothpaste", "Toothpaste", "Personal care", R.drawable.tpl_toothpaste, listOf("toothpaste", "paste", "tooth paste")),
        ProductTemplate("toothbrush", "Toothbrush", "Personal care", R.drawable.tpl_toothbrush, listOf("toothbrush", "tooth brush", "sipilyo")),
        ProductTemplate("deodorant", "Deodorant", "Personal care", R.drawable.tpl_deodorant, listOf("deodorant", "deo", "roll on")),
        ProductTemplate("diapers", "Diapers", "Personal care", R.drawable.tpl_diapers, listOf("diapers", "diaper", "lampin")),
        ProductTemplate("sanitary_pads", "Sanitary pads", "Personal care", R.drawable.tpl_sanitary_pads, listOf("sanitary pads", "pads", "napkin", "menstrual pads")),
        ProductTemplate("rubbing_alcohol", "Rubbing alcohol", "Personal care", R.drawable.tpl_rubbing_alcohol, listOf("rubbing alcohol", "isopropyl", "ethyl alcohol", "alcohol bottle")),
        ProductTemplate("notebook", "Notebook", "School & office", R.drawable.tpl_notebook, listOf("notebook", "kwaderno", "school notebook")),
        ProductTemplate("pen", "Pen", "School & office", R.drawable.tpl_pen, listOf("pen", "ballpen", "ballpoint")),
        ProductTemplate("pad_paper", "Pad paper", "School & office", R.drawable.tpl_pad_paper, listOf("pad paper", "paper", "sulat", "intermediate pad")),
        ProductTemplate("envelope", "Envelope", "School & office", R.drawable.tpl_envelope, listOf("envelope", "sobre")),
        ProductTemplate("phone_load", "Phone load", "Services", R.drawable.tpl_phone_load, listOf("load", "e load", "eload", "phone load", "prepaid load", "regular load")),
        ProductTemplate("ewallet", "E-wallet", "Services", R.drawable.tpl_ewallet, listOf("gcash", "e wallet", "ewallet", "maya", "cash in", "cash out", "wallet")),
        ProductTemplate("printing", "Photocopy / print", "Services", R.drawable.tpl_printing, listOf("photocopy", "print", "printing", "xerox", "document")),
        ProductTemplate("water_gallon", "Water refill gallon", "Services", R.drawable.tpl_water_gallon, listOf("water refill", "refill gallon", "water gallon", "tubig refill")),
        ProductTemplate("medicine_tablets", "Medicine tablets", "Pharmacy", R.drawable.tpl_medicine_tablets, listOf("medicine", "tablet", "tablets", "gamot", "paracetamol")),
        ProductTemplate("vitamins", "Vitamins", "Pharmacy", R.drawable.tpl_vitamins, listOf("vitamins", "vitamin", "supplement")),
        ProductTemplate("bandage", "Bandage", "Pharmacy", R.drawable.tpl_bandage, listOf("bandage", "band aid", "bandaid", "plaster")),
        ProductTemplate("generic_box", "Generic item", "General", R.drawable.tpl_generic_box, listOf("item", "box", "generic", "product", "produkto")),
    )

    private val byKey = all.associateBy { it.key }
    private val aliases = all.associate { template ->
        template.key to (listOf(template.key.replace('_', ' '), template.label) + template.keywords)
            .map(::normalize).distinct()
    }

    fun byKey(key: String?): ProductTemplate? = byKey[key]

    /** Whole phrases rank first, followed by partial words and small spelling errors. */
    fun suggest(name: String): List<ProductTemplate> = ranked(name).map { it.first }

    /** Only a whole keyword/phrase can automatically select an image in a new form. */
    fun strongSuggestion(name: String): ProductTemplate? =
        ranked(name).firstOrNull()?.takeIf { it.second >= 100 }?.first

    private fun ranked(name: String): List<Pair<ProductTemplate, Int>> {
        val query = normalize(name)
        if (query.isBlank()) return emptyList()
        val words = query.split(' ')
        return all.map { template ->
            template to aliases.getValue(template.key).maxOf { alias -> score(query, words, alias) }
        }.filter { it.second > 0 }
            .sortedWith(compareByDescending<Pair<ProductTemplate, Int>> { it.second }.thenBy { it.first.label })
    }

    private fun score(query: String, words: List<String>, alias: String): Int {
        if (query == alias) return 200 + alias.length.coerceAtMost(60)
        // Boundaries keep e.g. "tea" from matching "steak" or "pen" from matching "open".
        if (" $query ".contains(" $alias ")) return 100 + alias.length.coerceAtMost(60)
        val aliasWords = alias.split(' ')
        val scores = aliasWords.map { candidate ->
            words.maxOf { word ->
                when {
                    word == candidate -> 85
                    word.length >= 3 && candidate.startsWith(word) -> 65
                    word.length >= 4 && candidate.length >= 4 &&
                        editDistance(word, candidate) <= if (candidate.length >= 8) 2 else 1 -> 55
                    else -> 0
                }
            }
        }
        return if (scores.all { it > 0 }) scores.average().toInt() else 0
    }

    private fun normalize(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase(Locale.ROOT).replace(Regex("[^a-z0-9]+"), " ").trim()

    private fun editDistance(a: String, b: String): Int {
        if (kotlin.math.abs(a.length - b.length) > 2) return 3
        var previous = IntArray(b.length + 1) { it }
        for (i in a.indices) {
            val next = IntArray(b.length + 1)
            next[0] = i + 1
            for (j in b.indices) {
                next[j + 1] = minOf(next[j] + 1, previous[j + 1] + 1, previous[j] + if (a[i] == b[j]) 0 else 1)
            }
            previous = next
        }
        return previous[b.length]
    }
}
