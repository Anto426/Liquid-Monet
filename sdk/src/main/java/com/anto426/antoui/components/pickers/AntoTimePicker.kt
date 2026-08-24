package com.anto426.antoui.components.pickers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.icons.AntoIcons
import com.anto426.antoui.components.buttons.AntoIconButton
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle
import java.util.Locale

/**
 * State holder for AntoTimePicker.
 */
@Stable
class AntoTimePickerState(
    initialHour: Int = 12,
    initialMinute: Int = 0
) {
    var hour: Int by mutableIntStateOf(initialHour)
    var minute: Int by mutableIntStateOf(initialMinute)

    val formattedTime: String
        get() = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

    fun incrementHour() {
        hour = (hour + 1) % 24
    }

    fun decrementHour() {
        hour = (hour + 23) % 24
    }

    fun incrementMinute() {
        minute = (minute + 1) % 60
    }

    fun decrementMinute() {
        minute = (minute + 59) % 60
    }
}

@Composable
fun rememberAntoTimePickerState(
    initialHour: Int = 12,
    initialMinute: Int = 0
): AntoTimePickerState {
    return remember { AntoTimePickerState(initialHour, initialMinute) }
}

/**
 * AntoTimePicker - Inline Liquid Glass Time Picker.
 */
@Composable
fun AntoTimePicker(
    state: AntoTimePickerState,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hours Column
        AntoTimeNumberColumn(
            value = state.hour,
            onIncrement = { state.incrementHour() },
            onDecrement = { state.decrementHour() },
            label = "Ore",
            backdropState = backdropState
        )

        Text(
            text = ":",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Minutes Column
        AntoTimeNumberColumn(
            value = state.minute,
            onIncrement = { state.incrementMinute() },
            onDecrement = { state.decrementMinute() },
            label = "Minuti",
            backdropState = backdropState
        )
    }
}

@Composable
private fun AntoTimeNumberColumn(
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    label: String,
    backdropState: Backdrop
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AntoIconButton(
            icon = AntoIcons.KeyboardArrowUp,
            onClick = onIncrement,
            backdropState = backdropState
        )

        Box(
            modifier = Modifier
                .size(width = 68.dp, height = 56.dp)
                .antoLiquidGlass(
                    backdrop = backdropState,
                    shape = RoundedRectangle(16.dp),
                    role = AntoGlassRole.Control,
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format(Locale.getDefault(), "%02d", value),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        AntoIconButton(
            icon = AntoIcons.KeyboardArrowDown,
            onClick = onDecrement,
            backdropState = backdropState
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)
        )
    }
}

/**
 * AntoTimePickerField - Liquid Glass Time Input Trigger Field.
 */
@Composable
fun AntoTimePickerField(
    selectedHour: Int?,
    selectedMinute: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Seleziona Ora",
    placeholder: String = "HH:MM",
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val formattedTime = remember(selectedHour, selectedMinute) {
        if (selectedHour != null && selectedMinute != null) {
            String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
        } else null
    }

    val interactiveHighlight = rememberAntoControlHighlight()
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .antoLiquidGlass(
                backdrop = backdropState,
                shape = RoundedRectangle(18.dp),
                role = AntoGlassRole.Control,
                layerBlock = antoControlLayerBlock(enabled, interactiveHighlight)
            )
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick
            )
            .antoControlPressFeedback(enabled, interactiveHighlight)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = AntoIcons.Time,
                    contentDescription = label,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )

                Column {
                    if (formattedTime != null) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colorScheme.onSurface
                        )
                    } else {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurface.copy(alpha = 0.45f)
                        )
                    }
                }
            }

            Icon(
                imageVector = AntoIcons.ChevronRight,
                contentDescription = null,
                tint = colorScheme.onSurface.copy(alpha = 0.40f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
