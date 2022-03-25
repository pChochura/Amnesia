package com.pointlessapps.amnesia.compose.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.pointlessapps.amnesia.LocalSnackbarHostState
import com.pointlessapps.amnesia.R
import com.pointlessapps.amnesia.compose.ui.components.*
import com.pointlessapps.amnesia.compose.ui.theme.Icons
import com.pointlessapps.amnesia.compose.utils.add
import com.pointlessapps.amnesia.compose.utils.foregroundColor
import com.pointlessapps.amnesia.model.CategoryModel
import com.pointlessapps.amnesia.model.NoteModel
import org.koin.androidx.compose.getViewModel
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = getViewModel(),
    onNavigateToNoteClicked: (NoteModel?) -> Unit,
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val focusManager = LocalFocusManager.current
    SideEffect { focusManager.clearFocus() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeViewModel.Event.ShowMessage ->
                    snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    AmnesiaLoader(enabled = viewModel.state.isLoading)

    AmnesiaScaffoldLayout(
        topBar = {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.8f))
                    .statusBarsPadding(),
            ) {
                TopBar()
                CategoriesRow(viewModel)
            }
        },
        fab = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = dimensionResource(id = R.dimen.big_padding),
                        top = dimensionResource(id = R.dimen.medium_padding),
                    )
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center,
            ) {
                AmnesiaButton(
                    text = stringResource(R.string.new_note),
                    onClick = { onNavigateToNoteClicked(null) },
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                alignment = Alignment.BottomCenter,
                contentScale = ContentScale.FillWidth,
                painter = painterResource(id = R.drawable.ic_background),
                contentDescription = null,
            )
        }

        LazyColumn(
            contentPadding = innerPadding.add(
                top = dimensionResource(id = R.dimen.small_padding),
                start = dimensionResource(id = R.dimen.medium_padding),
                end = dimensionResource(id = R.dimen.medium_padding),
            ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium_padding)),
        ) {
            val (pinnedNotes, otherNotes) = viewModel.partitionNotesByPinned()

            if (pinnedNotes.isNotEmpty()) {
                item {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.tiny_padding)),
                    ) {
                        Icons.Pin(
                            modifier = Modifier.size(dimensionResource(id = R.dimen.small_icon_size)),
                            tint = colorResource(id = R.color.grey),
                        )
                        Text(
                            text = stringResource(id = R.string.pinned),
                            style = MaterialTheme.typography.body1.copy(
                                color = MaterialTheme.colors.secondaryVariant,
                            ),
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
                                top = dimensionResource(id = R.dimen.medium_padding),
                            ),
                            text = stringResource(id = R.string.other),
                            style = MaterialTheme.typography.body1.copy(
                                color = MaterialTheme.colors.secondaryVariant,
                            ),
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
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.medium_padding)),
    ) {
        val (menuButton, titleText, profileButton) = createRefs()

        AmnesiaTooltipWrapper(
            modifier = Modifier.constrainAs(menuButton) {
                centerVerticallyTo(parent)
                start.linkTo(parent.start)
            },
            tooltip = stringResource(R.string.menu),
            onClick = { /*TODO*/ },
        ) {
            Icons.Menu(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.tiny_padding))
                    .size(dimensionResource(id = R.dimen.icon_size)),
                tint = MaterialTheme.colors.secondary,
            )
        }

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h2.copy(
                color = MaterialTheme.colors.onPrimary,
            ),
            modifier = Modifier.constrainAs(titleText) {
                centerTo(parent)
            },
        )

        AmnesiaTooltipWrapper(
            modifier = Modifier
                .clip(CircleShape)
                .size(dimensionResource(id = R.dimen.icon_button_size))
                .background(MaterialTheme.colors.secondary)
                .constrainAs(profileButton) {
                    centerVerticallyTo(parent)
                    end.linkTo(parent.end)
                },
            tooltip = stringResource(R.string.profile),
            onClick = { /*TODO*/ },
        ) {
            Icons.Profile(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                tint = MaterialTheme.colors.onSecondary,
            )
        }
    }
}

@Composable
private fun CategoriesRow(viewModel: HomeViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(
                vertical = dimensionResource(id = R.dimen.small_padding),
                horizontal = dimensionResource(id = R.dimen.medium_padding),
            ),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_padding)),
    ) {
        CategoryItem(
            category = CategoryModel(
                name = stringResource(id = R.string.all),
                color = colorResource(id = R.color.orange).toArgb(),
            ),
            isSelected = viewModel.state.selectedCategory == null,
            onCategorySelected = { viewModel.onCategorySelected(null) }
        )
        viewModel.state.categories.forEach { category ->
            CategoryItem(
                category = category,
                isSelected = viewModel.state.selectedCategory === category,
                onCategorySelected = viewModel::onCategorySelected
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: CategoryModel,
    isSelected: Boolean,
    onCategorySelected: (CategoryModel?) -> Unit,
) {
    AmnesiaChip(
        text = category.name,
        chipModel = defaultAmnesiaChipModel().copy(
            backgroundColor = ComposeColor(category.color),
            textStyle = MaterialTheme.typography.h3.copy(
                color = MaterialTheme.colors.foregroundColor(
                    ComposeColor(category.color),
                ),
            ),
        ),
        colored = isSelected,
        onClick = { onCategorySelected(category) },
    )
}
