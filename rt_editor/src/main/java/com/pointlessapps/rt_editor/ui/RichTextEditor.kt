package com.pointlessapps.rt_editor.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pointlessapps.rt_editor.model.RichTextValue

@Composable
fun RichTextEditor(
	value: RichTextValue,
	onValueChange: (RichTextValue) -> Unit,
	modifier: Modifier = Modifier,
	textFieldModel: RTTextFieldModel = defaultRTTextFieldModel()
) {
	RichTextField(
		modifier = modifier,
		value = value.value,
		onValueChange = {
			if (value.updatedValueAndStyles(it)) {
				onValueChange(value)
			}
		},
		textFieldModel = textFieldModel
	)
}
