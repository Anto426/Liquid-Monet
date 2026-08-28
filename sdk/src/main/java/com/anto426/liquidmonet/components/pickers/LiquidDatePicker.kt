package com.anto426.liquidmonet.components.pickers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.components.internal.LiquidControlDefaults
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.theme.LiquidGlassTheme
import com.anto426.liquidmonet.components.buttons.LiquidIconButton
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * State holder for LiquidDatePicker.
 */
@Stable
class LiquidDatePickerState(
    initialDate: LocalDate = currentLocalDate()
) {
    var selectedDate: LocalDate? by mutableStateOf(initialDate)
    var displayedYear: Int by mutableIntStateOf(initialDate.year)
    var displayedMonth: Int by mutableIntStateOf(initialDate.monthNumber)

    val monthName: String
        get() {
            return "${monthNames[displayedMonth - 1]} $displayedYear"
        }

    val daysInMonth: Int
        get() {
            return daysInMonth(displayedYear, displayedMonth)
        }

    val firstDayOffset: Int
        get() {
            return LocalDate(displayedYear, displayedMonth, 1).dayOfWeek.ordinal
        }

    fun previousMonth() {
        if (displayedMonth == 1) {
            displayedMonth = 12
            displayedYear--
        } else {
            displayedMonth--
        }
    }

    fun nextMonth() {
        if (displayedMonth == 12) {
            displayedMonth = 1
            displayedYear++
        } else {
            displayedMonth++
        }
    }

    fun selectDay(day: Int) {
        selectedDate = LocalDate(displayedYear, displayedMonth, day)
    }

    fun isSelected(day: Int): Boolean {
        val current = selectedDate ?: return false
        return current.year == displayedYear &&
                current.monthNumber == displayedMonth &&
                current.dayOfMonth == day
    }

    fun isToday(day: Int): Boolean {
        val today = currentLocalDate()
        return today.year == displayedYear &&
                today.monthNumber == displayedMonth &&
                today.dayOfMonth == day
    }
}

@Composable
fun rememberLiquidDatePickerState(
    initialDate: LocalDate = currentLocalDate()
): LiquidDatePickerState {
    return remember { LiquidDatePickerState(initialDate) }
}

/**
 * LiquidDatePicker - Inline Liquid Glass Calendar Component.
 */
@Composable
fun LiquidDatePicker(
    state: LiquidDatePickerState,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Month / Year Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(
                targetState = state.monthName,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "monthTitle"
            ) { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colorScheme.onSurface
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LiquidIconButton(
                    icon = LiquidIcons.ChevronLeft,
                    onClick = { state.previousMonth() },
                    backdropState = backdropState
                )
                LiquidIconButton(
                    icon = LiquidIcons.ChevronRight,
                    onClick = { state.nextMonth() },
                    backdropState = backdropState
                )
            }
        }

        // Weekday abbreviations
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val weekDays = listOf("L", "M", "M", "G", "V", "S", "D")
            weekDays.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = LiquidGlassTheme.colors.secondaryContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(36.dp)
                )
            }
        }

        // Calendar Grid
        val totalCells = state.firstDayOffset + state.daysInMonth
        val rows = (totalCells + 6) / 7

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            for (rowIndex in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (colIndex in 0..6) {
                        val cellIndex = rowIndex * 7 + colIndex
                        val dayNumber = cellIndex - state.firstDayOffset + 1

                        if (dayNumber in 1..state.daysInMonth) {
                            val isSelected = state.isSelected(dayNumber)
                            val isToday = state.isToday(dayNumber)
                            val primaryColor = colorScheme.primary

                            val cellScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.08f else 1f,
                                animationSpec = tween(150),
                                label = "dayCellScale"
                            )

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .graphicsLayer {
                                        scaleX = cellScale
                                        scaleY = cellScale
                                    }
                                    .liquidGlass(
                                        backdrop = backdropState,
                                        shape = Capsule(),
                                        role = LiquidGlassRole.Control,
                                        containerColor = if (isSelected) {
                                            LiquidGlassTheme.colors.selectedContainer
                                        } else if (isToday) primaryColor.copy(alpha = 0.12f) else null
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else if (isToday) 1.dp else 0.dp,
                                        color = if (isSelected) primaryColor else if (isToday) primaryColor.copy(alpha = 0.45f) else Color.Transparent,
                                        shape = Capsule()
                                    )
                                    .clickable { state.selectDay(dayNumber) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else if (isToday) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    color = when {
                                        isSelected -> colorScheme.onSurface
                                        isToday -> primaryColor
                                        else -> colorScheme.onSurface
                                    }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(38.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * LiquidDatePickerField - Liquid Glass Date Input Trigger Field.
 */
@Composable
fun LiquidDatePickerField(
    selectedDate: LocalDate?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Seleziona Data",
    placeholder: String = "GG/MM/AAAA",
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val formattedDate = remember(selectedDate) {
        if (selectedDate != null) {
            val month = monthNames[selectedDate.monthNumber - 1]
            "${selectedDate.dayOfMonth} $month ${selectedDate.year}"
        } else null
    }

    val interactiveHighlight = rememberLiquidControlHighlight()
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .liquidGlass(
                backdrop = backdropState,
                shape = RoundedRectangle(18.dp),
                role = LiquidGlassRole.Control,
                layerBlock = liquidControlLayerBlock(enabled, interactiveHighlight)
            )
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick
            )
            .liquidControlPressFeedback(enabled, interactiveHighlight)
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
                    imageVector = LiquidIcons.Calendar,
                    contentDescription = label,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )

                Column {
                    if (formattedDate != null) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = LiquidGlassTheme.colors.secondaryContent
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colorScheme.onSurface
                        )
                    } else {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LiquidGlassTheme.colors.secondaryContent.copy(alpha = 0.72f)
                        )
                    }
                }
            }

            Icon(
                imageVector = LiquidIcons.ChevronRight,
                contentDescription = null,
                tint = LiquidGlassTheme.colors.secondaryContent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private val monthNames = listOf(
    "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
    "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"
)

private fun currentLocalDate(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    2 -> if (year % 400 == 0 || year % 4 == 0 && year % 100 != 0) 29 else 28
    4, 6, 9, 11 -> 30
    else -> 31
}
