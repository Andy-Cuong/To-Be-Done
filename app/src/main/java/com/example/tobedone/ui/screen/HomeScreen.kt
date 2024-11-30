package com.example.tobedone.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.sharp.Done
import androidx.compose.material.icons.sharp.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tobedone.R
import com.example.tobedone.model.Priority
import com.example.tobedone.model.TextNote
import com.example.tobedone.ui.navigation.NavigationDestination
import com.example.tobedone.ui.theme.ToBeDoneTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

object HomeDestination : NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    onAddButtonClicked: () -> Unit,
    onTextNoteClicked: (TextNote) -> Unit,
    modifier: Modifier = Modifier
) {
    val homeScreenViewModel: HomeScreenViewModel = viewModel(factory = HomeScreenViewModel.factory)
    val homeScreenState by homeScreenViewModel.homeScreenFlow.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            HomeSearchBar(
                searchValue = homeScreenState.searchValue,
                onSearchValueChange = homeScreenViewModel::onSearchValueChange,
                onSortedByChange = homeScreenViewModel::changeSortOption,
                isDetailExpanded = homeScreenState.isDetailExpanded,
                onDetailExpandedClicked = homeScreenViewModel::toggleDetailExpanded,
                priorityFilter = homeScreenState.priorityFilter,
                onAddPriorityFilter = homeScreenViewModel::addPriorityFilter,
                isDoneFilter = homeScreenState.isDoneFilter,
                onAddIsDoneFilter = homeScreenViewModel::addIsDoneFilter,
                onResetFilter = homeScreenViewModel::resetFilter
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddButtonClicked,
                modifier = Modifier
                    .size(80.dp)
                    .padding(dimensionResource(R.dimen.padding_medium))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_circle),
                    contentDescription = stringResource(R.string.add_new_note),
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        if (homeScreenState.textNoteList.isEmpty()) {
            EmptyNoteList(modifier = Modifier.padding(innerPadding))
        } else {
            TextNoteList(
                textNoteList = homeScreenState.textNoteList,
                onTextNoteClicked = onTextNoteClicked,
                onCheckBoxClicked = homeScreenViewModel::onCheckBoxClicked,
                onDeleteTextNote = homeScreenViewModel::deleteTextNote,
                modifier = Modifier.padding(innerPadding),
                isExpanded = homeScreenState.isDetailExpanded
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchBar(
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    onSortedByChange: (SortOption) -> Unit,
    isDetailExpanded: Boolean,
    onDetailExpandedClicked: () -> Unit,
    priorityFilter: Set<Int>,
    onAddPriorityFilter: (Int) -> Unit,
    isDoneFilter: Set<Boolean>,
    onAddIsDoneFilter: (Boolean) -> Unit,
    onResetFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            OutlinedTextField(
                value = searchValue,
                onValueChange = onSearchValueChange,
                placeholder = { Text(stringResource(R.string.search_notes)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = stringResource(R.string.search_notes)
                    )
                },
                trailingIcon = {
                    if (searchValue.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchValueChange("") }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_clear),
                                contentDescription = stringResource(R.string.clear)
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                shape = MaterialTheme.shapes.large
            )
        },
        modifier = modifier.fillMaxWidth(),
        actions = {
            SearchBarActions(
                onSortedByChange = onSortedByChange,
                isDetailExpanded = isDetailExpanded,
                onDetailExpandedClicked = onDetailExpandedClicked,
                priorityFilter = priorityFilter,
                onAddPriorityFilter = onAddPriorityFilter,
                isDoneFilter = isDoneFilter,
                onAddIsDoneFilter = onAddIsDoneFilter,
                onResetFilter = onResetFilter
            )
        }
    )
}

@Composable
fun SearchBarActions(
    onSortedByChange: (SortOption) -> Unit,
    isDetailExpanded: Boolean,
    onDetailExpandedClicked: () -> Unit,
    priorityFilter: Set<Int>,
    onAddPriorityFilter: (Int) -> Unit,
    isDoneFilter: Set<Boolean>,
    onAddIsDoneFilter: (Boolean) -> Unit,
    onResetFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSortOptionMenuVisible by remember { mutableStateOf(false) }
    var isFilterMenuVisible by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
    ) {
        IconButton(
            onClick = { isFilterMenuVisible = true }
        ) {
            val filterIconResource = if (priorityFilter.isNotEmpty() || isDoneFilter.isNotEmpty()) {
                R.drawable.ic_filter_filled
            } else {
                R.drawable.ic_filter_outlined
            }
            val filterContentDescription = if (priorityFilter.isNotEmpty() || isDoneFilter.isNotEmpty()) {
                R.string.filter_applied
            } else {
                R.string.no_filter_applied
            }
            Icon(
                painter = painterResource(filterIconResource),
                contentDescription = stringResource(filterContentDescription)
            )
        }
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))
        IconButton(
            onClick = { isSortOptionMenuVisible = true }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sort),
                contentDescription = stringResource(R.string.open_sort_option)
            )
        }
    }

    DropdownMenu(
        expanded = isFilterMenuVisible,
        onDismissRequest = { isFilterMenuVisible = false }
    ) {
        Priority.entries.forEach { priority ->
            DropdownMenuItem(
                text = { Text(text = stringResource(priority.priorityText)) },
                onClick = { onAddPriorityFilter(priority.ordinal + 1) },
                trailingIcon = {
                    if (priorityFilter.contains(priority.ordinal + 1)) {
                        Icon(
                            imageVector = Icons.Sharp.Done,
                            contentDescription = stringResource(R.string.applied)
                        )
                    }
                }
            )
        }

        listOf(true, false).forEach { isDone ->
            DropdownMenuItem(
                text = { Text(text = stringResource(if (isDone) R.string.done else R.string.not_done)) },
                onClick = { onAddIsDoneFilter(isDone) },
                trailingIcon = {
                    if (isDoneFilter.contains(isDone)) {
                        Icon(
                            imageVector = Icons.Sharp.Done,
                            contentDescription = stringResource(R.string.applied)
                        )
                    }
                }
            )
        }

        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.reset_filter)) },
            onClick = onResetFilter,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Sharp.Refresh,
                    contentDescription = stringResource(R.string.applied)
                )
            }
        )
    }

    DropdownMenu(
        expanded = isSortOptionMenuVisible,
        onDismissRequest = { isSortOptionMenuVisible = false }
    ) {
        SortOption.entries.forEach { sortOption ->
            DropdownMenuItem(
                text = { Text(text = stringResource(sortOption.optionText)) },
                onClick = {
                    onSortedByChange(sortOption)
                    isSortOptionMenuVisible = false
                }
            )
        }

        val timeIconResource = if (isDetailExpanded) {
            R.drawable.ic_time_filled
        } else {
            R.drawable.ic_time_outlined
        }
        val timeDetailText = if (isDetailExpanded) {
            R.string.hide_time_detail
        } else {
            R.string.show_time_detail
        }
        DropdownMenuItem(
            text = { Text(text = stringResource(timeDetailText)) },
            onClick = {
                onDetailExpandedClicked()
                isSortOptionMenuVisible = false
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(timeIconResource),
                    contentDescription = stringResource(timeDetailText)
                )
            }
        )
    }
}

@Composable
fun EmptyNoteList(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.empty_screen),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TextNoteList(
    textNoteList: List<TextNote>,
    onTextNoteClicked: (TextNote) -> Unit,
    onCheckBoxClicked: (TextNote) -> Unit,
    onDeleteTextNote: (TextNote) -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        items(items = textNoteList, key = { textNote -> textNote.id }) { textNote ->
//            TextNoteCard(
//                textNote = textNote,
//                onTextNoteClicked = onTextNoteClicked,
//                onCheckBoxClicked = onCheckBoxClicked,
//                isExpanded = isExpanded
//            )

//            SwipeToDeleteContainer(
//                item = textNote,
//                onDelete = onDeleteTextNote
//            ) {
//                TextNoteCard(
//                    textNote = textNote,
//                    onTextNoteClicked = onTextNoteClicked,
//                    onCheckBoxClicked = onCheckBoxClicked,
//                    isExpanded = isExpanded
//                )
//            }

            var isContextMenuRevealed by remember { mutableStateOf(false) }
            SwipeableItem(
                item = textNote,
                onDelete = onDeleteTextNote,
                isRevealed = isContextMenuRevealed,
                onExpanded = { isContextMenuRevealed = true },
                onCollapsed = { isContextMenuRevealed = false }
            ) {
                TextNoteCard(
                    textNote = textNote,
                    onTextNoteClicked = onTextNoteClicked,
                    onCheckBoxClicked = {
                        if (!textNote.isDone) {
                            isContextMenuRevealed = true
                        }
                        onCheckBoxClicked(textNote)
                    },
                    isExpanded = isExpanded
                )
            }
        }
    }
}

@Composable
fun <T> SwipeableItem(
    item: T,
    onDelete: (T) -> Unit,
    isRevealed: Boolean,
    modifier: Modifier = Modifier,
    deleteAnimationDuration: Int = 500,
    onExpanded: () -> Unit = {},
    onCollapsed: () -> Unit = {},
    content: @Composable (T) -> Unit
) {
    var contextMenuWidth by remember { mutableFloatStateOf(0f) }
    var isDeleted by remember { mutableStateOf(false) }
    val offset = remember { Animatable(initialValue = 0f) }
    val coroutineScope = rememberCoroutineScope()

    // Used to programmatically control the swipe state if needed
    LaunchedEffect(isRevealed, contextMenuWidth, isDeleted) {
        if (isRevealed) {
            offset.animateTo(-contextMenuWidth)
            onExpanded()
        } else {
            offset.animateTo(0f)
            onCollapsed()
        }

        if (isDeleted) {
            delay(deleteAnimationDuration.toLong())
            onDelete(item)
        }
    }

    AnimatedVisibility( // Animate the removal action
        visible = !isDeleted,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = deleteAnimationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Row( // The row for the contextMenu
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .onSizeChanged {
                        // Measure the width of the context menu on drawn
                        contextMenuWidth = it.width.toFloat()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            offset.animateTo(0f)
                        }
                        onCollapsed()
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(Color.LightGray)
                        .padding(dimensionResource(R.dimen.padding_medium))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            offset.animateTo(0f)
                        }
                        isDeleted = true
                        onCollapsed()
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(Color.Red)
                        .padding(dimensionResource(R.dimen.padding_medium))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Surface( // The content overlapping the context menu
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        IntOffset(
                            x = offset.value.roundToInt(),
                            y = 0
                        )
                    } // Use negative since we want to drag from end to start
                    .pointerInput(key1 = contextMenuWidth) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, dragAmount ->
                                coroutineScope.launch {
                                    val newOffset = (offset.value + dragAmount)
                                        .coerceIn( // Used to force swipe from end to start only
                                            minimumValue = -contextMenuWidth,
                                            maximumValue = 0f
                                        )
                                    offset.snapTo(newOffset)
                                }
                            },
                            onDragEnd = {
                                when {
                                    offset.value <= contextMenuWidth / -2f -> {
                                        coroutineScope.launch {
                                            offset.animateTo(-contextMenuWidth)
                                            onExpanded()
                                        }
                                    }
                                    else -> {
                                        coroutineScope.launch {
                                            offset.animateTo(0f)
                                            onCollapsed()
                                        }
                                    }
                                }
                            }
                        )
                    }
            ) {
                content(item)
            }
        }
    }
}

@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    animationDuration: Int = 500,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    var isDeleted by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(key1 = isDeleted) {
        if (isDeleted) {
            delay(animationDuration.toLong())
            onDelete(item)
        }
    }

    AnimatedVisibility( // Animate the removal action
        visible = !isDeleted,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                dismissState.reset()
                                // Use dismissState.dismiss() to control the swipe state if needed
                            }
                        },
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(Color.LightGray)
                            .padding(dimensionResource(R.dimen.padding_medium))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                    IconButton(
                        onClick = { isDeleted = true },
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(Color.Red)
                            .padding(dimensionResource(R.dimen.padding_medium))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            },
            modifier = modifier,
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = true,
            content = {
                content(item)
            }
        )
    }
}

@Composable
fun TextNoteCard(
    textNote: TextNote,
    onTextNoteClicked: (TextNote) -> Unit,
    onCheckBoxClicked: (TextNote) -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false
) {
    val backGroundColor = when (textNote.priority) {
        1 -> Priority.CRITICAL.color
        2 -> Priority.NEED_ATTENTION.color
        3 -> Priority.DEFAULT.color
        4 -> Priority.OPTIONAL.color
        else -> Priority.TRIVIAL.color
    }

    Card(
        modifier = modifier
            .background(backGroundColor)
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium))
            .animateContentSize()
            .clickable(onClick = { onTextNoteClicked(textNote) })
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = textNote.title,
                    fontWeight = FontWeight.ExtraBold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            },
            overlineContent = {
                AnimatedVisibility(visible = isExpanded) {
                    TimeOverline(
                        updateEpochSecond = textNote.updateEpochSecond,
                        creationEpochSecond = textNote.creationEpochSecond
                    )
                }
            },
            supportingContent = {
                Text(
                    text = textNote.content,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            },
            trailingContent = {
                Checkbox(
                    checked = textNote.isDone,
                    onCheckedChange = { _ -> onCheckBoxClicked(textNote) }
                )
            }
        )
    }
}

@Composable
fun TimeOverline(
    updateEpochSecond: Long,
    creationEpochSecond: Long,
    modifier: Modifier = Modifier
) {
    val now = LocalDateTime.now()
    val creationTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(creationEpochSecond),
        ZoneId.systemDefault()
    )
    val creationTimeString = if (now.year == creationTime.year && now.dayOfYear == creationTime.dayOfYear) {
        DateTimeFormatter.ofPattern("HH:mm")
            .format(creationTime)
    } else {
        DateTimeFormatter.ofPattern("HH:mm EEE dd/MM/yyyy")
            .format(creationTime)
    }

    val updateTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(updateEpochSecond),
        ZoneId.systemDefault()
    )
    val updateTimeString = if (now.year == updateTime.year && now.dayOfYear == updateTime.dayOfYear) {
        DateTimeFormatter.ofPattern("HH:mm")
            .format(updateTime)
    } else {
        DateTimeFormatter.ofPattern("HH:mm EEE dd/MM/yyyy")
            .format(updateTime)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.created, creationTimeString),
            modifier = Modifier.weight(3f),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.last_updated, updateTimeString),
            modifier = Modifier.weight(3f),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextNoteScreenPrev() {
    val textNoteList = listOf(
        TextNote(
            id = 1,
            title = "Note 1",
            content = "You need to do this, that, these, those, and many more things, go do them now",
            isDone = false,
            updateEpochSecond = 1732117509,
            creationEpochSecond = 1712117509,
            priority = 2
        ),
        TextNote(
            id = 2,
            title = "Note 2",
            content = "You need to do this, that, these, those, and many more things, go do them now",
            isDone = true,
            updateEpochSecond = 1730087509,
            creationEpochSecond = 1612117809,
            priority = 5
        )
    )

    ToBeDoneTheme {
        TextNoteList(
            textNoteList = textNoteList,
            onTextNoteClicked = {},
            onCheckBoxClicked = {},
            onDeleteTextNote = {},
            isExpanded = true
        )
    }
}