package com.anto426.liquidmonet.components.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.components.buttons.LiquidIconButton
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * LiquidStepper / LiquidNumberInput - Optical Liquid Glass Numeric Stepper.
 */
@Composable
fun LiquidStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    minValue: Int = 0,
    maxValue: Int = 100,
    step: Int = 1,
    unit: String? = null,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                backdrop = backdropState,
                shape = RoundedRectangle(18.dp),
                role = LiquidGlassRole.Control
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (label != null) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colorScheme.onSurface
                )
                if (unit != null) {
                    Text(
                        text = "Unità: $unit",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LiquidIconButton(
                icon = LiquidIcons.Remove,
                onClick = {
                    if (value - step >= minValue) {
                        onValueChange(value - step)
                    }
                },
                backdropState = backdropState
            )

            Box(
                modifier = Modifier
                    .size(width = 54.dp, height = 40.dp)
                    .liquidGlass(
                        backdrop = backdropState,
                        shape = RoundedRectangle(12.dp),
                        role = LiquidGlassRole.Control,
                        containerColor = colorScheme.primary.copy(alpha = 0.16f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unit != null) "$value $unit" else value.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colorScheme.onSurface
                )
            }

            LiquidIconButton(
                icon = LiquidIcons.Add,
                onClick = {
                    if (value + step <= maxValue) {
                        onValueChange(value + step)
                    }
                },
                backdropState = backdropState
            )
        }
    }
}
