package com.anto426.liquidmonet.glass.runtime

import androidx.compose.runtime.Immutable

/** Manual CPU-family policy. This ceiling never replaces RAM, clock or measured CPU/GPU limits. */
@Immutable
data class LiquidGlassProcessorFamily(
    val id: String,
    val displayName: String,
    val cpuQualityCeiling: LiquidGlassQualityTier,
    /** Introduction of the underlying generation, not the phone/rebrand release or OS year. */
    val generationIntroducedYear: Int? = null
) {
    /** Fixed generation eras, deliberately independent of the current calendar year. */
    val effectiveCpuQualityCeiling: LiquidGlassQualityTier
        get() = minOf(cpuQualityCeiling, when {
            generationIntroducedYear == null -> LiquidGlassQualityTier.ULTRA
            generationIntroducedYear <= 2016 -> LiquidGlassQualityTier.BALANCED
            generationIntroducedYear <= 2020 -> LiquidGlassQualityTier.HIGH
            else -> LiquidGlassQualityTier.ULTRA
        })
}

/**
 * Offline, reviewable family rules instead of a list of phone models.
 *
 * Edit a generation once to cover its matching SoCs. Policies apply only to a new calibration;
 * updating this table does not change an already saved device profile or its optical fidelity.
 * Unknown identifiers deliberately return null and retain benchmark-only classification.
 */
object LiquidGlassProcessorFamilies {
    private data class Rule(val family: LiquidGlassProcessorFamily, val identifiers: List<Regex>)

    private fun rule(id: String, name: String, ceiling: LiquidGlassQualityTier, vararg patterns: String,
        year: Int? = null) =
        Rule(LiquidGlassProcessorFamily(id, name, ceiling, year),
            patterns.map { Regex("(?:^| )(?:$it)(?: |$)") })

    // These are SDK policy choices, not vendor performance guarantees. Keep code aliases only
    // where their family is known; a generic vendor name such as qcom or mt must not select a tier.
    private val rules = listOf(
        // Specific generations must precede series fallbacks. Rebrands retain their silicon era.
        // Launch dates and code aliases are documented in docs/PROCESSOR_FAMILIES.md.
        rule("snapdragon-820", "Snapdragon 820/821 generation", LiquidGlassQualityTier.HIGH,
            "SNAPDRAGON 82[01]", "MSM8996(?:PRO)?", year = 2015),
        rule("snapdragon-835", "Snapdragon 835 generation", LiquidGlassQualityTier.HIGH,
            "SNAPDRAGON 835", "MSM8998", year = 2016),
        rule("snapdragon-845", "Snapdragon 845 generation", LiquidGlassQualityTier.HIGH,
            "SNAPDRAGON 845", "SDM845", year = 2017),
        rule("snapdragon-855", "Snapdragon 855/860 generation", LiquidGlassQualityTier.HIGH,
            "SNAPDRAGON (?:855|860)", "SM8150(?:AC)?", year = 2018),
        rule("snapdragon-865", "Snapdragon 865/870 generation", LiquidGlassQualityTier.HIGH,
            "SNAPDRAGON (?:865|870)", "SM8250(?:A[BC])?", year = 2019),
        rule("snapdragon-888", "Snapdragon 888 generation", LiquidGlassQualityTier.HIGH,
            "SNAPDRAGON 888", "SM8350(?:AC)?", year = 2020),
        rule("snapdragon-8-gen1", "Snapdragon 8 / 8+ Gen 1", LiquidGlassQualityTier.ULTRA,
            "SNAPDRAGON 8 GEN 1", "SM(?:8450|8475)", year = 2021),
        rule("snapdragon-8-gen2", "Snapdragon 8 Gen 2", LiquidGlassQualityTier.ULTRA,
            "SNAPDRAGON 8 GEN 2", "SM8550(?:A[BC])?", year = 2022),
        rule("snapdragon-8-gen3", "Snapdragon 8 Gen 3", LiquidGlassQualityTier.ULTRA,
            "SNAPDRAGON 8 GEN 3", "SM8650(?:A[ABC])?", year = 2023),
        rule("snapdragon-8-elite", "Snapdragon 8 Elite (Oryon)", LiquidGlassQualityTier.ULTRA,
            "SNAPDRAGON 8 ELITE(?! GEN)", "SM8750(?:AB)?", year = 2024),
        rule("snapdragon-8s", "Snapdragon 8s", LiquidGlassQualityTier.HIGH,
            "SNAPDRAGON 8S(?: GEN [0-9]+)?", "SM(?:8635|8735)"),
        rule("snapdragon-8", "Snapdragon 8 (unspecified generation)", LiquidGlassQualityTier.HIGH,
            "SNAPDRAGON 8(?: GEN [0-9]+| ELITE(?: GEN [0-9]+)?)?", "SM8[0-9]{3}"),
        rule("snapdragon-legacy-7", "Snapdragon 730/765 generation", LiquidGlassQualityTier.BALANCED,
            "SNAPDRAGON (?:73[02]|76[58])G?", "SM7[12]50(?:A[ABC])?", year = 2019),
        rule("snapdragon-7", "Snapdragon 7", LiquidGlassQualityTier.HIGH,
            "SNAPDRAGON 7(?:S)?(?: GEN [0-9]+)?", "SM7[0-9]{3}"),
        rule("snapdragon-6", "Snapdragon 6", LiquidGlassQualityTier.BALANCED,
            "SNAPDRAGON 6(?:S)?(?: GEN [0-9]+)?", "SM6[0-9]{3}"),
        rule("snapdragon-4", "Snapdragon 4", LiquidGlassQualityTier.BALANCED,
            "SNAPDRAGON 4(?:S)?(?: GEN [0-9]+)?", "SM4[0-9]{3}"),
        rule("snapdragon-800", "Snapdragon 800 series", LiquidGlassQualityTier.HIGH,
            "SNAPDRAGON 8[0-9]{2}", "SDM8[0-9]{2}", "MSM899[0-9]"),
        rule("snapdragon-legacy-mid", "Snapdragon 600/700 series", LiquidGlassQualityTier.BALANCED,
            "SNAPDRAGON [67][0-9]{2}G?", "SDM[67][0-9]{2}", "MSM895[0-9]"),
        rule("snapdragon-legacy-entry", "Snapdragon 200/400 series", LiquidGlassQualityTier.MINIMAL,
            "SNAPDRAGON [24][0-9]{2}", "SDM4[0-9]{2}", "MSM89[023][0-9]"),
        rule("dimensity-9000-gen1", "Dimensity 9000 generation", LiquidGlassQualityTier.ULTRA,
            "DIMENSITY 9000", "MT6983[A-Z]*", year = 2021),
        rule("dimensity-9200", "Dimensity 9200 generation", LiquidGlassQualityTier.ULTRA,
            "DIMENSITY 9200", "MT6985[A-Z]*", year = 2022),
        rule("dimensity-9000", "Dimensity 9000 series (unspecified generation)", LiquidGlassQualityTier.HIGH,
            "DIMENSITY 9[0-9]{3}[A-Z]*"),
        rule("dimensity-8000", "Dimensity 8000 series", LiquidGlassQualityTier.HIGH,
            "DIMENSITY 8[0-9]{3}[A-Z]*", "MT6895[A-Z]*"),
        rule("dimensity-7000", "Dimensity 7000 series", LiquidGlassQualityTier.HIGH,
            "DIMENSITY 7[0-9]{3}[A-Z]*"),
        rule("dimensity-6000", "Dimensity 6000 series", LiquidGlassQualityTier.BALANCED,
            "DIMENSITY 6[0-9]{3}[A-Z]*"),
        rule("dimensity-legacy", "Dimensity 700–1300", LiquidGlassQualityTier.HIGH,
            "DIMENSITY (?:[789][0-9]{2}|1[0-3][0-9]{2})[A-Z]*", "MT6893[A-Z]*"),
        rule("helio-g", "Helio G", LiquidGlassQualityTier.BALANCED, "HELIO G[0-9]{2,3}[A-Z]*"),
        rule("helio-p", "Helio P", LiquidGlassQualityTier.BALANCED, "HELIO P[0-9]{2}[A-Z]*"),
        rule("helio-entry", "Helio A", LiquidGlassQualityTier.MINIMAL, "HELIO A[0-9]{2}"),
        rule("exynos-modern-flagship", "Exynos 2200–2900", LiquidGlassQualityTier.ULTRA,
            "EXYNOS ?2[2-9][0-9]{2}"),
        rule("exynos-premium", "Exynos 1000/2100/900 series", LiquidGlassQualityTier.HIGH,
            "EXYNOS ?(?:1[0-9]{3}|21[0-9]{2}|9[0-9]{2,3})"),
        rule("exynos-legacy-mid", "Exynos 7000/8000 series", LiquidGlassQualityTier.BALANCED,
            "EXYNOS ?[78][0-9]{3}"),
        rule("tensor", "Google Tensor", LiquidGlassQualityTier.HIGH, "(?:GOOGLE )?TENSOR(?: G[0-9]+)?"),
        rule("unisoc-4g", "UNISOC T3xx/T6xx/T7x00", LiquidGlassQualityTier.BALANCED,
            "(?:UNISOC |TIGER )?T(?:[36][0-9]{2}|7[0-4][0-9]{2})"),
        rule("unisoc-5g", "UNISOC T760–T890 / T8xxx–T9xxx", LiquidGlassQualityTier.HIGH,
            "(?:UNISOC |TIGER )?T(?:7[6-9][0-9]|8[0-9]{2}|[89][0-9]{3})"),
        rule("kirin-900", "Kirin 900/9000 series", LiquidGlassQualityTier.HIGH, "KIRIN 9[0-9]{2,3}[A-Z]*"),
        rule("kirin-mid", "Kirin 600/700/800 series", LiquidGlassQualityTier.BALANCED, "KIRIN [678][0-9]{2}[A-Z]*")
    )

    val families: List<LiquidGlassProcessorFamily> = rules.map { it.family }

    /** Prefer explicit SoC model over the less specific Android hardware/board identifier. */
    fun identify(socModel: String, hardware: String = ""): LiquidGlassProcessorFamily? {
        for (identifier in listOf(socModel, hardware)) {
            if (identifier.length > 256) continue
            val normalized = identifier.uppercase().replace(Separators, " ").trim()
            if (normalized.isEmpty()) continue
            rules.firstOrNull { rule -> rule.identifiers.any { it.containsMatchIn(normalized) } }
                ?.let { return it.family }
        }
        return null
    }

    private val Separators = Regex("[^A-Z0-9]+")
}
