package com.pointlessapps.amnesia.compose.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.pointlessapps.amnesia.R

object Icons {
	@Composable
	fun Get(
		@DrawableRes iconRes: Int,
		modifier: Modifier = Modifier,
		tint: Color = Color.White
	) = Icon(
		painter = painterResource(id = iconRes),
		contentDescription = null,
		modifier = modifier,
		tint = tint
	)

	@Composable
	fun AlignCenter(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_align_center, modifier, tint)

	@Composable
	fun AlignLeft(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_align_left, modifier, tint)

	@Composable
	fun AlignRight(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_align_right, modifier, tint)

	@Composable
	fun Bold(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_bold, modifier, tint)

	@Composable
	fun Circle(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_circle, modifier, tint)

	@Composable
	fun Done(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_done, modifier, tint)

	@Composable
	fun FormatClear(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_format_clear, modifier, tint)

	@Composable
	fun Italic(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_italic, modifier, tint)

	@Composable
	fun Menu(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_menu, modifier, tint)

	@Composable
	fun Minus(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_minus, modifier, tint)

	@Composable
	fun OrderedList(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_ordered_list, modifier, tint)

	@Composable
	fun Pin(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_pin, modifier, tint)

	@Composable
	fun Plus(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_plus, modifier, tint)

	@Composable
	fun Profile(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_profile, modifier, tint)

	@Composable
	fun Redo(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_redo, modifier, tint)

	@Composable
	fun Strikethrough(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_strikethrough, modifier, tint)

	@Composable
	fun TextSize(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_text_size, modifier, tint)

	@Composable
	fun Underline(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_underline, modifier, tint)

	@Composable
	fun Undo(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_undo, modifier, tint)

	@Composable
	fun UnorderedList(modifier: Modifier = Modifier, tint: Color = Color.White) =
		Get(iconRes = R.drawable.icon_unordered_list, modifier, tint)
}
