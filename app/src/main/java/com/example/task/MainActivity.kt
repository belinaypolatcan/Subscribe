package com.example.task

import SymbolQueueManager2
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.task.ui.theme.TaskTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val symbolQueueManager = SymbolQueueManager2()

        lifecycleScope.launch{
            symbolQueueManager.subscribe(
                listOf(
                    "bist100",
                    "bist30",
                    "usdtry",
                    "selam",
                    "naber"
                )
            )
        }
        lifecycleScope.launch{
            symbolQueueManager.subscribe(listOf("merhaba"))
        }
        lifecycleScope.launch{
            symbolQueueManager.unsubscribe(listOf("bist100"))
        }
        lifecycleScope.launch{
            symbolQueueManager.subscribe(listOf("hello"))
        }
        lifecycleScope.launch{
            symbolQueueManager.unsubscribe(listOf("merhaba"))
        }
        lifecycleScope.launch{
            symbolQueueManager.subscribe(listOf("merhaba"))
        }
        lifecycleScope.launch{
            symbolQueueManager.subscribe(listOf("merhaba"))
        }
        lifecycleScope.launch{
            symbolQueueManager.unsubscribe(listOf("hello"))
        }

        setContent {
            TaskTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var symbolQueue by remember { mutableStateOf(emptyMap<String, Int>()) }
                    var unsubscribeQueue by remember { mutableStateOf(emptyList<String>()) }

                    LaunchedEffect(Unit) {
                        lifecycleScope.launch {
                            val symbolQueueFlow = symbolQueueManager.getSymbolQueue()
                            symbolQueueFlow.collect { updatedQueue ->
                                symbolQueue = updatedQueue
                            }
                        }

                        lifecycleScope.launch {
                            val unsubscribeQueueFlow = symbolQueueManager.getUnsubscribeQueue()
                            unsubscribeQueueFlow.collect { updatedUnsubscribeQueue ->
                                unsubscribeQueue = updatedUnsubscribeQueue
                            }
                        }
                    }

                    Column {
                        Text("Symbol Queue:")
                        Text(symbolQueue.toString())

                        Text("Unsubscribe Queue:")
                        Text(unsubscribeQueue.toString())
                    }
                }
            }
        }

    }
}

