package com.pointlessapps.amnesia.home.ui

import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.insets.statusBarsPadding
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.model.Category
import com.pointlessapps.amnesia.model.Note
import com.pointlessapps.amnesia.ui.components.*
import com.pointlessapps.amnesia.ui.theme.Icons
import com.pointlessapps.amnesia.utils.add
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun HomeScreen(
	onNavigateToNoteClicked: (Note?) -> Unit
) {
	val categories = remember {
		listOf(
			Category("All", Color.parseColor("#FBCCCC")),
			Category("Notes", Color.parseColor("#CCFBD9")),
			Category("Ideas", Color.parseColor("#D0CCFB")),
			Category("Reminders", Color.parseColor("#FAFBCC")),
		)
	}
	val notes = remember {
		listOf(
			Note(
				title = "Aplikacja notatnik",
				content = "Some stuff, some other stuff, some more stuff...",
				createdAt = "12.02.2022",
				updatedAt = "yesterday",
				categories = categories.subList(1, 2).toSet(),
				isPinned = false
			),
			Note(
				title = null,
				content = "Content with:\n" +
						"unordered list\n" +
						"multilevel\n" +
						"nice\n" +
						"\n" +
						"ordered list\n" +
						"multilevel\n" +
						"nice\n" +
						"\n" +
						"bold, italic, underline, with different sizes and colorful",
				createdAt = "01.02.2022",
				updatedAt = "03.02.2022",
				categories = categories.subList(2, 3).toSet(),
				isPinned = false
			),
			Note(
				title = null,
				content = "Content with:\n" +
						"unordered list\n" +
						"multilevel\n" +
						"nice\n" +
						"\n" +
						"ordered list\n" +
						"multilevel\n" +
						"nice\n" +
						"\n" +
						"bold, italic, underline, with different sizes and colorful",
				createdAt = "01.02.2022",
				updatedAt = "03.02.2022",
				categories = emptySet(),
				isPinned = false
			),
			Note(
				title = null,
				content = "Content with:\n" +
						"unordered list\n" +
						"multilevel\n" +
						"nice\n" +
						"\n" +
						"ordered list\n" +
						"multilevel\n" +
						"nice\n" +
						"\n" +
						"bold, italic, underline, with different sizes and colorful",
				createdAt = "01.02.2022",
				updatedAt = "03.02.2022",
				categories = categories.subList(2, 3).toSet(),
				isPinned = false
			),
			Note(
				title = null,
				content = "Some stuff, some other stuff, some more stuff...",
				createdAt = "12.02.2022",
				updatedAt = "12.02.2022",
				categories = categories.subList(1, 3).toSet(),
				isPinned = true
			)
		)
	}
	var selectedCategory by remember { mutableStateOf(categories.first()) }

	AmnesiaScaffoldLayout(
		topBar = {
			Column(
				modifier = Modifier
					.wrapContentSize()
					.background(MaterialTheme.colors.primary.copy(alpha = 0.8f))
					.statusBarsPadding()
					.padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
			) {
				TopBar()
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(dimensionResource(id = R.dimen.small_padding))
						.horizontalScroll(rememberScrollState()),
					horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding))
				) {
					categories.forEach { category ->
						AmnesiaChip(
							text = category.text,
							chipModel = defaultAmnesiaChipModel().run {
								copy(
									backgroundColor = ComposeColor(category.color),
									textStyle = MaterialTheme.typography.h3.copy(
										color = textStyle.color
									)
								)
							},
							colored = selectedCategory == category,
							onClick = { selectedCategory = category }
						)
					}
				}
			}
		},
		fab = {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						bottom = dimensionResource(id = R.dimen.big_padding),
						top = dimensionResource(id = R.dimen.medium_padding)
					)
					.navigationBarsPadding(),
				contentAlignment = Alignment.Center,
			) {
				AmnesiaButton(
					text = stringResource(R.string.new_note),
					onClick = { onNavigateToNoteClicked(null) }
				)
			}
		}
	) { innerPadding ->
		Box(
			modifier = Modifier.fillMaxSize(),
			contentAlignment = Alignment.BottomCenter
		) {
			Image(
				modifier = Modifier.fillMaxWidth(),
				alignment = Alignment.BottomCenter,
				contentScale = ContentScale.FillWidth,
				painter = painterResource(id = R.drawable.ic_background),
				contentDescription = null
			)
		}

		LazyColumn(
			contentPadding = innerPadding.add(
				top = dimensionResource(id = R.dimen.small_padding),
				start = dimensionResource(id = R.dimen.medium_padding),
				end = dimensionResource(id = R.dimen.medium_padding),
			),
			verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium_padding))
		) {
			val (pinnedNotes, otherNotes) = notes.filter {
				selectedCategory.text == "All" || it.categories.contains(selectedCategory)
			}.partition { it.isPinned }

			if (pinnedNotes.isNotEmpty()) {
				item {
					Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.tiny_padding))) {
						Icons.Pin(tint = colorResource(id = R.color.grey))
						Text(
							text = stringResource(id = R.string.pinned),
							style = MaterialTheme.typography.body1.copy(
								color = MaterialTheme.colors.secondaryVariant
							)
						)
					}
				}
				items(pinnedNotes) { note ->
					AmnesiaNoteCard(
						note = note,
						modifier = Modifier.fillMaxWidth(),
					)
				}

				if (otherNotes.isNotEmpty()) {
					item {
						Text(
							modifier = Modifier.padding(
								top = dimensionResource(id = R.dimen.medium_padding)
							),
							text = stringResource(id = R.string.other),
							style = MaterialTheme.typography.body1.copy(
								color = MaterialTheme.colors.secondaryVariant
							)
						)
					}
				}
			}

			items(otherNotes) { note ->
				AmnesiaNoteCard(
					note = note,
					modifier = Modifier.fillMaxWidth(),
				)
			}
		}
	}
}

@Composable
private fun TopBar() {
	ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
		val (menuButton, titleText, profileButton) = createRefs()

		IconButton(
			onClick = { /*TODO*/ },
			modifier = Modifier.constrainAs(menuButton) {
				centerVerticallyTo(parent)
				start.linkTo(parent.start)
			}
		) {
			Icons.Menu(
				modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.secondary
			)
		}

		Text(
			text = stringResource(id = R.string.app_name),
			style = MaterialTheme.typography.h2.copy(
				color = MaterialTheme.colors.onPrimary
			),
			modifier = Modifier.constrainAs(titleText) {
				centerTo(parent)
			}
		)

		IconButton(
			onClick = { /*TODO*/ },
			modifier = Modifier
				.clip(CircleShape)
				.size(dimensionResource(id = R.dimen.icon_button_size))
				.background(MaterialTheme.colors.secondary)
				.constrainAs(profileButton) {
					centerVerticallyTo(parent)
					end.linkTo(parent.end)
				},
		) {
			Icons.Profile(
				modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
				tint = MaterialTheme.colors.onSecondary
			)
		}
	}
}
