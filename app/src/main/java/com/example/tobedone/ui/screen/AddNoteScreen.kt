package com.example.tobedone.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tobedone.R
import com.example.tobedone.model.Priority
import com.example.tobedone.ui.navigation.NavigationDestination
import com.example.tobedone.ui.theme.ToBeDoneTheme

object AddNoteDestination : NavigationDestination {
    override val route = "add_note"
}

@Composable
fun AddNoteScreen(
    onNavigateUp: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val addNoteScreenViewModel: AddNoteScreenViewModel = viewModel(factory = AddNoteScreenViewModel.factory)
    val addNoteScreenState by addNoteScreenViewModel.addNoteScreenFlow.collectAsStateWithLifecycle()

    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AddNoteTopBar(
                title = addNoteScreenState.title,
                onNavigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            if (addNoteScreenState.title.isNotBlank() || addNoteScreenState.content.isNotBlank()) {
                FloatingActionButton(
                    onClick = {
                        addNoteScreenViewModel.onDone()
                        onNavigateBack()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = stringResource(R.string.done)
                    )
                }
            }
        }
    ) { innerPadding ->
        NoteDetailLayout(
            title = addNoteScreenState.title,
            onTitleChange = addNoteScreenViewModel::onTitleChange,
            priority = addNoteScreenState.priority,
            onPriorityChange = addNoteScreenViewModel::onPriorityChange,
            content = addNoteScreenState.content,
            onContentChange = addNoteScreenViewModel::onContentChange,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteTopBar(
    title: String,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = if (title.isNotEmpty()) title else stringResource(R.string.new_note),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        modifier = modifier.fillMaxWidth(),
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    )
}

@Composable
fun NoteDetailLayout(
    title: String,
    onTitleChange: (String) -> Unit,
    priority: Int,
    onPriorityChange: (Int) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember {
        FocusRequester()
    }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        TitleTextField(
            title = title,
            onTitleChange = onTitleChange,
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
        )
        Row {
            Spacer(modifier = Modifier.weight(1f))
            PriorityMenu(
                priority = priority,
                onPriorityClicked = onPriorityChange,
                modifier = Modifier.weight(1f)
            )
        }
        ContentTextField(
            content = content,
            onContentChange = onContentChange,
//            onDone = {
//                focusManager.clearFocus()
//            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxSize()
        )
    }
}

@Composable
fun TitleTextField(
    title: String,
    onTitleChange: (String) -> Unit,
    onNext: KeyboardActionScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = title,
        onValueChange = onTitleChange,
        modifier = modifier,
        placeholder = { Text(text = stringResource(R.string.enter_title)) },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = onNext
        ),
        singleLine = true
    )
}

@Composable
fun PriorityMenu(
    priority: Int,
    onPriorityClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityText = Priority.entries[priority - 1].priorityText
    var isPriorityMenuVisible by remember { mutableStateOf(false) }
//    var itemWidth by remember {
//        mutableStateOf(0.dp)
//    }
//    val density = LocalDensity.current

    ListItem(
        headlineContent = { Text(text = stringResource(priorityText)) },
        modifier = modifier
//            .onSizeChanged {
//                itemWidth = with(density) { it.width.toDp() } // Used to measure content size
//            }
            .border(
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = MaterialTheme.shapes.medium
            )
            .clickable(
                onClick = {
                    isPriorityMenuVisible = true
                }
            ),
        overlineContent = { Text(text = stringResource(R.string.priority)) },
        trailingContent = {
            Icon(
                painter = painterResource(R.drawable.arrow_drop_down),
                contentDescription = stringResource(R.string.select_priority)
            )
        }
    )

    DropdownMenu(
        expanded = isPriorityMenuVisible,
        onDismissRequest = { isPriorityMenuVisible = false },
        offset = DpOffset(x = (-1).dp, y = 0.dp)
    ) {
        Priority.entries.forEach { option ->
            DropdownMenuItem(
                text = { Text(stringResource(option.priorityText)) },
                onClick = {
                    isPriorityMenuVisible = false
                    onPriorityClicked(option.ordinal + 1)
                }
            )
        }
    }
}

@Composable
fun ContentTextField(
    content: String,
    onContentChange: (String) -> Unit,
//    onDone: KeyboardActionScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = content,
        onValueChange = onContentChange,
        modifier = modifier,
        placeholder = { Text(text = stringResource(R.string.enter_content)) },
//        keyboardOptions = KeyboardOptions.Default.copy(
//            imeAction = ImeAction.Done
//        ),
//        keyboardActions = KeyboardActions(
//            onDone = onDone
//        )
    )
}

@Preview(showBackground = true)
@Composable
private fun PriorityMenuPrev() {
    ToBeDoneTheme {
        NoteDetailLayout(
            title = "",
            onTitleChange = {},
            priority = 4,
            onPriorityChange = {},
            content = "",
            onContentChange = {}
        )
    }
}