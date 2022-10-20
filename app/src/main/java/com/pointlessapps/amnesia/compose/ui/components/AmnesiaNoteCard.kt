package com.pointlessapps.amnesia.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.model.NoteModel
import com.pointlessapps.dash.border.dashedBorder
import com.pointlessapps.rt_editor.model.RichTextValue
import com.pointlessapps.rt_editor.ui.RichText
import com.pointlessapps.rt_editor.ui.defaultRichTextStyle
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
internal fun AmnesiaNoteCard(
    note: NoteModel,
    modifier: Modifier = Modifier,
    onNoteClicked: (NoteModel) -> Unit,
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colors.primary)
            .dashedBorder(
                color = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.3f),
                width = dimensionResource(id = R.dimen.border_size),
                on = dimensionResource(id = R.dimen.dash_size),
                off = dimensionResource(id = R.dimen.dash_gap_size),
            )
            .clickable { onNoteClicked(note) }
            .padding(dimensionResource(id = R.dimen.medium_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding)),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.tiny_padding),
            ),
        ) {
            note.categories.forEach {
                AmnesiaChip(
                    text = it.name,
                    chipModel = defaultAmnesiaChipModel().run {
                        copy(
                            backgroundColor = ComposeColor(it.color),
                            typography = MaterialTheme.typography.subtitle1.copy(
                                color = typography.color,
                            ),
                        )
                    },
                )
            }
            if (note.categories.isNotEmpty()) {
                AmnesiaText(
                    text = stringResource(id = R.string.dot),
                    textStyle = defaultAmnesiaTextStyle().copy(
                        typography = MaterialTheme.typography.subtitle1,
                        textColor = MaterialTheme.colors.secondaryVariant,
                    ),
                )
            }
            AmnesiaText(
                text = note.updatedAt,
                textStyle = defaultAmnesiaTextStyle().copy(
                    typography = MaterialTheme.typography.subtitle1,
                    textColor = MaterialTheme.colors.secondaryVariant,
                ),
            )
        }

        if (!note.title.isNullOrBlank()) {
            AmnesiaText(
                text = note.title,
                textStyle = defaultAmnesiaTextStyle().copy(
                    typography = MaterialTheme.typography.h1,
                    textColor = MaterialTheme.colors.onPrimary,
                )
            )
        }

        RichText(
            modifier = Modifier.fillMaxWidth(),
            value = RichTextValue.fromSnapshot(note.content),
            textStyle = defaultRichTextStyle().copy(
                textStyle = MaterialTheme.typography.body1,
                textColor = MaterialTheme.colors.secondaryVariant,
            )
        )
    }
}
