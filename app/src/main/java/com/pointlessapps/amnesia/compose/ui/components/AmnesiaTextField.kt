package com.pointlessapps.amnesia.compose.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.pointlessapps.amnesia.compose.utils.EMPTY_STRING

@Suppress("unused")
@Composable
fun AmnesiaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModel: AmnesiaTextFieldModel = defaultAmnesiaTextFieldModel(),
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    AmnesiaTextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValueState = it
            if (value != it.text) {
                onValueChange(it.text)
            }
        },
        modifier = modifier,
        textFieldModel = textFieldModel,
    )
}

@Composable
fun AmnesiaTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModel: AmnesiaTextFieldModel = defaultAmnesiaTextFieldModel(),
) {
    Box(modifier = modifier) {
        if (value.text.isEmpty()) {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = textFieldModel.placeholder,
                style = textFieldModel.textStyle.copy(
                    color = textFieldModel.placeholderColor,
                ),
            )
        }
        BasicTextField(
            modifier = Modifier.fillMaxSize(),
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = textFieldModel.keyboardOptions,
            visualTransformation = textFieldModel.visualTransformation,
            textStyle = textFieldModel.textStyle.copy(
                textFieldModel.textColor,
            ),
            cursorBrush = SolidColor(textFieldModel.cursorColor),
        )
    }
}

@Composable
fun defaultAmnesiaTextFieldModel() = AmnesiaTextFieldModel(
    keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
    ),
    visualTransformation = VisualTransformation.None,
    placeholder = EMPTY_STRING,
    textStyle = MaterialTheme.typography.body1,
    textColor = MaterialTheme.colors.onPrimary,
    placeholderColor = MaterialTheme.colors.secondaryVariant,
    cursorColor = MaterialTheme.colors.secondary,
)

data class AmnesiaTextFieldModel(
    val keyboardOptions: KeyboardOptions,
    val visualTransformation: VisualTransformation,
    val placeholder: String,
    val textStyle: TextStyle,
    val textColor: Color,
    val placeholderColor: Color,
    val cursorColor: Color,
)
