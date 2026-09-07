# Processor family policy

`LiquidGlassProcessorFamilies` is an offline, manually maintained table of processor generations
and series. It is not a list of phones or an authoritative ranking of every SoC. Its quality
ceilings are SDK policy choices which still need representative-device measurements.

The Android hardware probe resolves the explicit SoC model first, then the hardware identifier,
on IO. Case and punctuation are normalized; bounded identifier matching never runs in drawing.
Specific generation aliases precede generic series rules. Vendor-only identifiers such as `qcom`
and unknown identifiers return no family, leaving the existing measured classification in charge.

Each family contains a stable ID, display name, CPU budget ceiling and optional introduction year
of the underlying generation. A rebrand keeps the original generation's year. The final CPU
ceiling is the minimum of the manual limit, generation era, CPU/memory-copy timings, core/clock
limits, RAM/heap limits and available graphics APIs. GPU rendering then selects a budget within
that ceiling. A large RAM allocation cannot compensate for a weak CPU, nor can a flagship name
compensate for failed or slow measurements.

Generation eras use fixed policy boundaries: generations introduced through 2016 are capped at
BALANCED, 2017–2020 at HIGH; later or unknown dates add no extra age ceiling. These are conservative
SDK defaults, not manufacturer performance claims. No current-year arithmetic or age inferred
from the phone, Android release, firmware or build timestamp is used. Unknown dates stay null.

The table currently covers Qualcomm Snapdragon, MediaTek Dimensity/Helio, Samsung Exynos,
Google Tensor, UNISOC and Kirin families. Coverage and dated generations are deliberately partial.
For example, unspecified Snapdragon 8 generations use a conservative series fallback rather than
inheriting the year of the first 8-series chip. Unverified MediaTek/Samsung board-code aliases are
not inferred from a vendor prefix. Add a specific generation before its series fallback, document
its source here and test both the commercial name and available board codes.

Saved profiles retain their existing version and hardware key. Table changes affect only a new
calibration, not an already persisted result. Family metadata is available through
`LocalLiquidGlassPerformance.current.device.processorFamily`. Optical fidelity and the explicit
LiquidCard treatment are separate from the classification table.

## Primary references

| Generation | Introduction | Manufacturer reference |
| --- | --- | --- |
| Snapdragon 820 / 821 silicon generation | 2015 | [Qualcomm launch](https://www.qualcomm.com/news/releases/2015/09/qualcomm-announces-breakthrough-connectivity-features-snapdragon-820) |
| Snapdragon 835 | 2016 | [Qualcomm announcement](https://www.qualcomm.com/news/releases/2016/11/qualcomm-and-samsung-collaborate-10nm-process-technology-latest-snapdragon) |
| Snapdragon 845 | 2017 | [Qualcomm launch](https://www.qualcomm.com/news/releases/2017/12/qualcomm-snapdragon-845-mobile-platform-introduces-new-innovative) |
| Snapdragon 855 / 860 generation | 2018 | [Qualcomm launch](https://www.qualcomm.com/news/releases/2018/12/qualcomm-announces-new-flagship-snapdragon-855-mobile-platform-new-decade) |
| Snapdragon 865 / 870 generation | 2019 | [Qualcomm launch](https://www.qualcomm.com/news/releases/2019/12/qualcomm-introduces-worlds-most-advanced-5g-mobile-platform) |
| Snapdragon 888 generation | 2020 | [Qualcomm launch](https://www.qualcomm.com/news/releases/2020/12/qualcomm-redefines-premium-flagship-snapdragon-888-5g-mobile-platform) |
| Snapdragon 8 / 8+ Gen 1 generation | 2021 | [Qualcomm launch](https://www.qualcomm.com/news/releases/2021/11/qualcomm-announces-worlds-most-advanced-mobile-platform-snapdragon-8-gen-1) |
| Snapdragon 8 Gen 2 | 2022 | [Qualcomm summit](https://www.qualcomm.com/news/press-kits/snapdragon-summit-2022) |
| Snapdragon 8 Gen 3 | 2023 | [Qualcomm summit](https://www.qualcomm.com/news/press-kits/snapdragon-summit-2023-press-kit) |
| Snapdragon 8 Elite | 2024 | [Qualcomm launch](https://www.qualcomm.com/news/releases/2024/10/qualcomm-unveils-snapdragon-8-elite-with-the-world-s-fastest-mob) |
| Dimensity 9000 generation | 2021 | [MediaTek architecture history](https://www.mediatek.com/hubfs/MediaTek%20Assets/Pdfs/Dimensity-9300-All-Big-Cores_Whitepaper_ENG_Final.pdf) |
| Dimensity 9200 generation | 2022 | [MediaTek launch](https://corp.mediatek.com/news-events/press-releases/mediatek-launches-flagship-dimensity-9200-chipset-for-incredible-performance-and-unmatched-power-savings) |

Qualcomm's [chipset/alias list](https://www.qualcomm.com/company/product-security/bulletins/february-2024-bulletin)
documents SM8150/SM8250/SM8350 variants and SM7150/SM7250 legacy midrange variants. The
[8 Elite product page](https://www.qualcomm.com/smartphones/products/8-series/snapdragon-8-elite-mobile-platform)
identifies SM8750. MediaTek's [developer documentation](https://developer.mediatek.com/ai/64254ccbf55b040d6989a99a.html)
associates MT6893 with Dimensity 1100/1200, MT6895 with 8000/8100, MT6983 with 9000 and MT6985
with 9200. Generic series use the [MediaTek family catalog](https://www.mediatek.com/products/smartphones/dimensity-5g),
[Samsung mobile processor catalog](https://semiconductor.samsung.com/processor/mobile-processor/)
and [UNISOC smartphone catalog](https://www.unisoc.com/en/product/SmartPhone) as naming references.
