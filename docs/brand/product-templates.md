# Built-in product illustrations

[View all 126 illustrations at 56 px](product-templates.svg).

The library is entirely local Android VectorDrawable XML. It uses a 48 × 48 viewport,
three or four simple paths, and two to four colors per illustration. Each file is
under 2,500 bytes; the complete source set is 84,559 bytes (84.56 KB). The largest
file is `tpl_milk_tea.xml`, at 879 bytes. AAPT2-compiled vector entries total 162,920
bytes before APK compression; the final APK size depends on packaging.

Jade `#0B5D4E`, mango `#F2A541`, clay `#C76B50`, and paper `#FBF7EF` anchor the art.
Light jade, bread, cocoa, ube, tomato, cream, and muted steel supply ingredient colors.
There are no bitmap assets in the app library, gradients, filters, cigarettes, or brand logos.
The SVG contact sheet lives in docs and is not packaged in the APK.

| Category | Templates |
| --- | ---: |
| Drinks | 15 |
| Bakery | 13 |
| Meals | 20 |
| Desserts | 10 |
| Snacks | 9 |
| Grocery | 14 |
| Produce | 9 |
| Meat & fish | 5 |
| Frozen | 2 |
| Household | 9 |
| Personal care | 8 |
| School & office | 4 |
| Services | 4 |
| Pharmacy | 3 |
| General | 1 |

Keys are stored in the product record: keep existing keys stable when editing art.
Add new `tpl_<key>.xml` resources together with a registry entry and English/Tagalog
keywords in `ProductTemplates.kt`. Category labels Drinks, Bakery, Meals, and Snacks
match the starter catalogue. Other category labels organize the picker; choosing an
image does not change the product category.

Run `python3 tools/check_templates.py` from the repository root. It checks the minimum
count, XML, palette, viewport, coordinate precision, per-file/total budgets, and both
sides of the registry/resource mapping. `ProductTemplatesTest.kt` covers Tagalog,
phrase specificity, normalization, fuzzy matching, false positives, and starter data.

The editor selects an exact keyword or phrase automatically for a new product while
its name is being entered. Changing the name updates that automatic choice. A manual
choice, photo, or removal stops automatic selection for that editor session. Fuzzy
and partial matches appear as suggestions without automatically selecting an image.
Saved products retain their current image when renamed.

Photos take precedence over a registered template; missing photos fall through to the
template, then the existing monogram. Picking a template clears the photo path, taking
or importing a photo clears the template, and Remove image clears both fields.
Unknown template keys also fall through to the monogram.

Validation completed without Gradle: Python budget/registry checks, a visual review of
all 126 illustrations at 56 px, AAPT2 resource compilation, direct compilation of all
app Kotlin sources with Kotlin 2.3.20 and its Compose compiler using cached Material 3
1.4 / Compose BOM 2025.10.01 dependencies and a temporary R stub, and six JUnit registry
tests. Direct compilation does not run Room/KSP or package an APK.

## Review on your build

- Build with Room/KSP so it generates the version 2 schema and migration. No version 2
  JSON is written by this change. Upgrade a version 1 database and confirm existing
  products, photos, stock, and sale history remain intact; the new nullable column is
  initially null for existing records.
- Check the thumbnails and picker in light/dark themes, on a small phone, with large
  text and the keyboard open. Review the local food silhouettes in the contact sheet.
- Create a new product named Banana Chips, rename it, pick a different image, and
  remove it. The manual choice/removal should remain while the name changes.
- Take/import a photo after selecting a template, then pick a template after a photo.
  Save and reopen each product to confirm the selected image persists.
- Check search/category filters, the Suggested row, selection outlines, and TalkBack.
  Updated starter templates apply on first seeding; existing catalogues are not backfilled.

## Every changed or added file

138 files, including this document.

```text
app/src/main/java/com/phcodesage/kwentaro/data/AppDatabase.kt
app/src/main/java/com/phcodesage/kwentaro/data/Entities.kt
app/src/main/java/com/phcodesage/kwentaro/data/ProductTemplates.kt
app/src/main/java/com/phcodesage/kwentaro/data/SampleData.kt
app/src/main/java/com/phcodesage/kwentaro/ui/components/Components.kt
app/src/main/java/com/phcodesage/kwentaro/ui/products/ProductEditorScreen.kt
app/src/main/java/com/phcodesage/kwentaro/ui/products/ProductTemplatePicker.kt
app/src/main/java/com/phcodesage/kwentaro/ui/products/ProductViewModels.kt
app/src/main/res/drawable/tpl_adobo.xml
app/src/main/res/drawable/tpl_banana.xml
app/src/main/res/drawable/tpl_banana_chips.xml
app/src/main/res/drawable/tpl_banana_cue.xml
app/src/main/res/drawable/tpl_bandage.xml
app/src/main/res/drawable/tpl_barbecue_stick.xml
app/src/main/res/drawable/tpl_battery.xml
app/src/main/res/drawable/tpl_beer_bottle.xml
app/src/main/res/drawable/tpl_bibingka.xml
app/src/main/res/drawable/tpl_biscuits_pack.xml
app/src/main/res/drawable/tpl_bleach.xml
app/src/main/res/drawable/tpl_bread_spread.xml
app/src/main/res/drawable/tpl_buko_juice.xml
app/src/main/res/drawable/tpl_buko_pandan.xml
app/src/main/res/drawable/tpl_burger.xml
app/src/main/res/drawable/tpl_cake_slice.xml
app/src/main/res/drawable/tpl_calamansi.xml
app/src/main/res/drawable/tpl_candle.xml
app/src/main/res/drawable/tpl_candy.xml
app/src/main/res/drawable/tpl_cheese_bread.xml
app/src/main/res/drawable/tpl_chicharon.xml
app/src/main/res/drawable/tpl_chicken.xml
app/src/main/res/drawable/tpl_chicken_curry.xml
app/src/main/res/drawable/tpl_chips_bag.xml
app/src/main/res/drawable/tpl_chocolate_bar.xml
app/src/main/res/drawable/tpl_coconut.xml
app/src/main/res/drawable/tpl_coffee_cup.xml
app/src/main/res/drawable/tpl_coffee_jar.xml
app/src/main/res/drawable/tpl_coffee_sachet.xml
app/src/main/res/drawable/tpl_condensed_milk.xml
app/src/main/res/drawable/tpl_cookies.xml
app/src/main/res/drawable/tpl_cooking_oil.xml
app/src/main/res/drawable/tpl_corned_beef_can.xml
app/src/main/res/drawable/tpl_crackers.xml
app/src/main/res/drawable/tpl_croissant.xml
app/src/main/res/drawable/tpl_cupcake.xml
app/src/main/res/drawable/tpl_deodorant.xml
app/src/main/res/drawable/tpl_detergent_sachet.xml
app/src/main/res/drawable/tpl_diapers.xml
app/src/main/res/drawable/tpl_dish_soap.xml
app/src/main/res/drawable/tpl_donut.xml
app/src/main/res/drawable/tpl_eggs_tray.xml
app/src/main/res/drawable/tpl_ensaymada.xml
app/src/main/res/drawable/tpl_envelope.xml
app/src/main/res/drawable/tpl_ewallet.xml
app/src/main/res/drawable/tpl_fish.xml
app/src/main/res/drawable/tpl_fried_chicken.xml
app/src/main/res/drawable/tpl_fried_rice.xml
app/src/main/res/drawable/tpl_fries.xml
app/src/main/res/drawable/tpl_frozen_goods.xml
app/src/main/res/drawable/tpl_garlic.xml
app/src/main/res/drawable/tpl_generic_box.xml
app/src/main/res/drawable/tpl_gin_bottle.xml
app/src/main/res/drawable/tpl_halo_halo.xml
app/src/main/res/drawable/tpl_hopia.xml
app/src/main/res/drawable/tpl_hot_chocolate.xml
app/src/main/res/drawable/tpl_hotdog.xml
app/src/main/res/drawable/tpl_hotdog_pack.xml
app/src/main/res/drawable/tpl_ice_bag.xml
app/src/main/res/drawable/tpl_ice_candy.xml
app/src/main/res/drawable/tpl_ice_cream.xml
app/src/main/res/drawable/tpl_iced_coffee.xml
app/src/main/res/drawable/tpl_iced_tea.xml
app/src/main/res/drawable/tpl_instant_noodles.xml
app/src/main/res/drawable/tpl_juice.xml
app/src/main/res/drawable/tpl_kutsinta.xml
app/src/main/res/drawable/tpl_leche_flan.xml
app/src/main/res/drawable/tpl_light_bulb.xml
app/src/main/res/drawable/tpl_loaf_bread.xml
app/src/main/res/drawable/tpl_lpg_tank.xml
app/src/main/res/drawable/tpl_lugaw.xml
app/src/main/res/drawable/tpl_mango.xml
app/src/main/res/drawable/tpl_mango_shake.xml
app/src/main/res/drawable/tpl_matches.xml
app/src/main/res/drawable/tpl_medicine_tablets.xml
app/src/main/res/drawable/tpl_milk_tea.xml
app/src/main/res/drawable/tpl_noodle_bowl.xml
app/src/main/res/drawable/tpl_notebook.xml
app/src/main/res/drawable/tpl_onion.xml
app/src/main/res/drawable/tpl_pad_paper.xml
app/src/main/res/drawable/tpl_pancit.xml
app/src/main/res/drawable/tpl_pandesal.xml
app/src/main/res/drawable/tpl_peanuts.xml
app/src/main/res/drawable/tpl_pen.xml
app/src/main/res/drawable/tpl_phone_load.xml
app/src/main/res/drawable/tpl_pie.xml
app/src/main/res/drawable/tpl_pizza_slice.xml
app/src/main/res/drawable/tpl_popcorn.xml
app/src/main/res/drawable/tpl_pork.xml
app/src/main/res/drawable/tpl_pork_bbq_rice.xml
app/src/main/res/drawable/tpl_potato.xml
app/src/main/res/drawable/tpl_powdered_milk.xml
app/src/main/res/drawable/tpl_printing.xml
app/src/main/res/drawable/tpl_puto.xml
app/src/main/res/drawable/tpl_rice_plate.xml
app/src/main/res/drawable/tpl_rice_sack.xml
app/src/main/res/drawable/tpl_rubbing_alcohol.xml
app/src/main/res/drawable/tpl_salt.xml
app/src/main/res/drawable/tpl_sandwich.xml
app/src/main/res/drawable/tpl_sanitary_pads.xml
app/src/main/res/drawable/tpl_sardines_can.xml
app/src/main/res/drawable/tpl_shampoo_sachet.xml
app/src/main/res/drawable/tpl_shrimp.xml
app/src/main/res/drawable/tpl_silog_plate.xml
app/src/main/res/drawable/tpl_siomai.xml
app/src/main/res/drawable/tpl_siopao.xml
app/src/main/res/drawable/tpl_soap_bar.xml
app/src/main/res/drawable/tpl_softdrink_bottle.xml
app/src/main/res/drawable/tpl_softdrink_can.xml
app/src/main/res/drawable/tpl_sopas.xml
app/src/main/res/drawable/tpl_soy_sauce.xml
app/src/main/res/drawable/tpl_spaghetti.xml
app/src/main/res/drawable/tpl_spanish_bread.xml
app/src/main/res/drawable/tpl_sugar.xml
app/src/main/res/drawable/tpl_tea_cup.xml
app/src/main/res/drawable/tpl_tissue_roll.xml
app/src/main/res/drawable/tpl_tomato.xml
app/src/main/res/drawable/tpl_toothbrush.xml
app/src/main/res/drawable/tpl_toothpaste.xml
app/src/main/res/drawable/tpl_turon.xml
app/src/main/res/drawable/tpl_ube_cheese_roll.xml
app/src/main/res/drawable/tpl_vegetables_bundle.xml
app/src/main/res/drawable/tpl_vinegar.xml
app/src/main/res/drawable/tpl_vitamins.xml
app/src/main/res/drawable/tpl_water_bottle.xml
app/src/main/res/drawable/tpl_water_gallon.xml
app/src/test/java/com/phcodesage/kwentaro/data/ProductTemplatesTest.kt
docs/brand/product-templates.md
docs/brand/product-templates.svg
tools/check_templates.py
```
