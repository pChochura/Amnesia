package com.pointlessapps.amnesia.compose.note.ui

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.ui.components.AmnesiaText
import com.pointlessapps.amnesia.compose.ui.components.defaultAmnesiaTextStyle
import com.pointlessapps.amnesia.compose.ui.theme.Icons
import com.pointlessapps.amnesia.compose.utils.AbovePositionProvider
import com.pointlessapps.amnesia.compose.utils.roundToPx
import com.pointlessapps.amnesia.compose.utils.toPercent
import com.pointlessapps.rt_editor.model.Style

@Composable
internal fun TextSizePicker(
    @FloatRange(from = Style.TextSize.MIN_VALUE, to = Style.TextSize.MAX_VALUE) currentValue: Float,
    onDismissListener: () -> Unit,
    onMinusClicked: () -> Unit,
    onPlusClicked: () -> Unit,
) {
    Popup(
        onDismissRequest = onDismissListener,
        popupPositionProvider = AbovePositionProvider(
            IntOffset(
                x = 0,
                y = -dimensionResource(id = R.dimen.medium_padding).roundToPx(),
            ),
        ),
        properties = PopupProperties(),
    ) {
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.secondary)
                .padding(dimensionResource(id = R.dimen.small_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding)),
        ) {
            Icons.Minus(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.tiny_padding))
                    .size(dimensionResource(id = R.dimen.icon_size))
                    .clickable(
                        onClick = onMinusClicked,
                        role = Role.Button,
                    ),
                tint = if (currentValue > Style.TextSize.MIN_VALUE) {
                    MaterialTheme.colors.onSecondary
                } else {
                    MaterialTheme.colors.secondaryVariant
                },
            )
            AmnesiaText(
                text = currentValue.toPercent(),
                textStyle = defaultAmnesiaTextStyle().copy(
                    typography = MaterialTheme.typography.h2,
                    textColor = MaterialTheme.colors.onSecondary,
                ),
            )
            Icons.Plus(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.tiny_padding))
                    .size(dimensionResource(id = R.dimen.icon_size))
                    .clickable(
                        onClick = onPlusClicked,
                        role = Role.Button,
                    ),
                tint = if (currentValue < Style.TextSize.MAX_VALUE) {
                    MaterialTheme.colors.onSecondary
                } else {
                    MaterialTheme.colors.secondaryVariant
                },
            )
        }
    }
}
