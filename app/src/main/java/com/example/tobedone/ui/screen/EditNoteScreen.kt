package com.example.tobedone.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tobedone.R
import com.example.tobedone.ui.navigation.NavigationDestination

object EditNoteDestination : NavigationDestination {
    override val route = "edit_note"
    const val NOTE_ID_ARG = "note_id"
    val routeWithArgs = "$route/{$NOTE_ID_ARG}" // {note_id} serves as the NavArgument name
                                                // (put after slash)
}

@Composable
fun EditNoteScreen(
    onNavigateUp: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val editNoteScreenViewModel: EditNoteScreenViewModel = viewModel(factory = EditNoteScreenViewModel.factory)
    val editNoteScreenState by editNoteScreenViewModel.editNoteScreenFlow.collectAsStateWithLifecycle()

    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EditNoteTopBar(
                title = editNoteScreenState.title,
                onNavigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            if (editNoteScreenState.title.isNotBlank() || editNoteScreenState.content.isNotBlank()) {
                FloatingActionButton(
                    onClick = {
                        editNoteScreenViewModel.onDone()
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
            title = editNoteScreenState.title,
            onTitleChange = editNoteScreenViewModel::onTitleChange,
            priority = editNoteScreenState.priority,
            onPriorityChange = editNoteScreenViewModel::onPriorityChange,
            content = editNoteScreenState.content,
            onContentChange = editNoteScreenViewModel::onContentChange,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteTopBar(
    title: String,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = if (title.isNotEmpty()) title else stringResource(R.string.edit_note),
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