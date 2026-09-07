package com.anto426.liquidmonet.glass.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LiquidGlassProcessorFamiliesTest {
    @Test fun hardwareAliasesAndMarketingNamesSelectTheSameGeneration() {
        val aliases = listOf(
            "Snapdragon 821" to "MSM8996PRO", "Snapdragon 835" to "MSM8998",
            "Snapdragon 845" to "SDM845", "Snapdragon 860" to "SM8150-AC",
            "Snapdragon 870" to "SM8250-AC", "Snapdragon 888+" to "SM8350-AC",
            "Snapdragon 8+ Gen 1" to "SM8475", "Snapdragon 8 Gen 2" to "SM8550AB",
            "Snapdragon 8 Gen 3" to "SM8650-AB", "Snapdragon 8 Elite" to "SM8750",
            "Dimensity 9000+" to "MT6983Z", "Dimensity 9200" to "MT6985",
            "Dimensity 8100" to "MT6895", "Dimensity 1200" to "MT6893"
        )
        for ((name, code) in aliases) {
            assertEquals(assertNotNull(LiquidGlassProcessorFamilies.identify(name)),
                LiquidGlassProcessorFamilies.identify(code), "$name / $code")
        }
    }

    @Test fun separatorsCaseAndExplicitModelPrecedenceAreHandled() {
        assertEquals("snapdragon-8-gen3", LiquidGlassProcessorFamilies.identify("qualcomm_sm8650-ab")?.id)
        assertEquals("snapdragon-6", LiquidGlassProcessorFamilies.identify("SM6225", "SM8750")?.id)
        assertEquals("snapdragon-8-elite", LiquidGlassProcessorFamilies.identify("unknown", "SM8750")?.id)
    }

    @Test fun ambiguousVendorsSubstringsAndOverlongNamesNeverInventAProfile() {
        for (name in listOf("", "unknown", "qcom", "mt", "Samsung", "boardSM8750", "SM87500", "x".repeat(257))) {
            assertNull(LiquidGlassProcessorFamilies.identify(name), name.take(32))
        }
        assertEquals("snapdragon-4", LiquidGlassProcessorFamilies.identify("x".repeat(257), "SM4450")?.id)
    }

    @Test fun specificGenerationsWinOverBroadSeriesNames() {
        assertEquals("snapdragon-legacy-7", LiquidGlassProcessorFamilies.identify("SM7150-AB")?.id)
        assertEquals("snapdragon-8s", LiquidGlassProcessorFamilies.identify("Snapdragon 8s Gen 3")?.id)
        val unspecified = assertNotNull(LiquidGlassProcessorFamilies.identify("Snapdragon 8 Gen 99"))
        assertNull(unspecified.generationIntroducedYear)
        assertEquals(LiquidGlassQualityTier.HIGH, unspecified.effectiveCpuQualityCeiling)
        assertNull(LiquidGlassProcessorFamilies.identify("Snapdragon 8 Elite Gen 99")?.generationIntroducedYear)
    }

    @Test fun siliconGenerationAgeSurvivesRebrandsAndDoesNotUsePhoneOrOsYear() {
        val old = assertNotNull(LiquidGlassProcessorFamilies.identify("Snapdragon 835"))
        assertEquals(2016, old.generationIntroducedYear)
        assertEquals(LiquidGlassQualityTier.BALANCED, old.effectiveCpuQualityCeiling)
        assertEquals(2018, LiquidGlassProcessorFamilies.identify("Snapdragon 860")?.generationIntroducedYear)
        assertEquals(2019, LiquidGlassProcessorFamilies.identify("Snapdragon 870")?.generationIntroducedYear)
        assertEquals(2024, LiquidGlassProcessorFamilies.identify("SM8750")?.generationIntroducedYear)
        assertEquals(old, LiquidGlassProcessorFamilies.identify("Snapdragon 835 Android 2026"))
    }

    @Test fun middleAndEntryFamiliesCannotBeMistakenForPremiumByTheirNumber() {
        val tiers = mapOf(
            "SM6225" to LiquidGlassQualityTier.BALANCED,
            "SM4450" to LiquidGlassQualityTier.BALANCED,
            "Helio A22" to LiquidGlassQualityTier.MINIMAL,
            "UNISOC T7200" to LiquidGlassQualityTier.BALANCED,
            "UNISOC T8200" to LiquidGlassQualityTier.HIGH,
            "Exynos 1280" to LiquidGlassQualityTier.HIGH,
            "Google Tensor G3" to LiquidGlassQualityTier.HIGH
        )
        for ((name, tier) in tiers) {
            assertEquals(tier, LiquidGlassProcessorFamilies.identify(name)?.effectiveCpuQualityCeiling, name)
        }
        assertEquals(LiquidGlassProcessorFamilies.families.size,
            LiquidGlassProcessorFamilies.families.map { it.id }.toSet().size)
    }

    private val capable = LiquidGlassDeviceProfile(
        sdkInt = 35, supportsRenderEffect = true, supportsRuntimeShader = true,
        isLowRamDevice = false, totalMemoryBytes = 12L * 1024 * 1024 * 1024,
        appMemoryClassMb = 512, cpuCoreCount = 8, is64Bit = true
    )

    @Test fun familyAndGenerationAreIndependentCeilingsAlongsideMeasurements() {
        val entry = capable.copy(processorFamily = LiquidGlassProcessorFamilies.identify("SM6225"))
        assertEquals(LiquidGlassQualityTier.BALANCED,
            LiquidGlassCalibrationPolicy.cpuCeiling(entry, 500_000, 500_000))
        val old = capable.copy(processorFamily = LiquidGlassProcessorFamilies.identify("MSM8998"))
        assertEquals(LiquidGlassQualityTier.BALANCED,
            LiquidGlassCalibrationPolicy.cpuCeiling(old, 500_000, 500_000))
        val flagship = capable.copy(processorFamily = LiquidGlassProcessorFamilies.identify("SM8750"))
        assertEquals(LiquidGlassQualityTier.MINIMAL,
            LiquidGlassCalibrationPolicy.cpuCeiling(flagship, 9_000_000, 500_000))
        assertEquals(LiquidGlassQualityTier.BALANCED,
            LiquidGlassCalibrationPolicy.cpuCeiling(flagship.copy(totalMemoryBytes = 3L * 1024 * 1024 * 1024), 500_000, 500_000))
        assertTrue(flagship.processorFamily!!.effectiveCpuQualityCeiling > entry.processorFamily!!.effectiveCpuQualityCeiling)
    }

    @Test fun unknownFamilyStillUsesMeasurementsAndAvailableHardware() {
        assertEquals(LiquidGlassQualityTier.ULTRA,
            LiquidGlassCalibrationPolicy.cpuCeiling(capable, 500_000, 500_000))
        assertEquals(LiquidGlassQualityTier.MINIMAL,
            LiquidGlassCalibrationPolicy.cpuCeiling(capable, 8_000_000, 500_000))
    }
}
