package com.example.tobedone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tobedone.ui.navigation.ToBeDoneNavHost
import com.example.tobedone.ui.theme.ToBeDoneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToBeDoneTheme {
                ToBeDoneApp()
            }
        }
    }
}

@Composable
fun ToBeDoneApp(modifier: Modifier = Modifier) {
    ToBeDoneNavHost(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    )
}