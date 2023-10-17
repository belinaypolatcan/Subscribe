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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.task.ui.theme.TaskTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val symbolQueueManager = SymbolQueueManager2()

        lifecycleScope.launch {
            symbolQueueManager.subscribe(
                listOf(
                    "bist100",
                    "bist30",
                    "usdtry",
                    "eurtry",
                    "garan"
                )
            )
        }
        lifecycleScope.launch {
            delay(2000)
            symbolQueueManager.unsubscribe(
                listOf(
                    "bist100",
                    "bist30",
                    "usdtry",
                    "eurtry",
                    "garan"
                )
            )
        }
        lifecycleScope.launch {
            delay(3000)
            symbolQueueManager.subscribe(listOf("garan"))
        }
        lifecycleScope.launch {
            delay(4000)
            symbolQueueManager.unsubscribe(listOf("garan"))
        }
        lifecycleScope.launch {
            delay(5000)
            symbolQueueManager.subscribe(
                listOf(
                    "bist100",
                    "bist30",
                    "usdtry",
                    "eurtry",
                    "garan"
                )
            )
        }
        lifecycleScope.launch {
            delay(8000)
            /*symbolQueueManager.unsubscribe(
                listOf(
                    "bist100",
                    "bist30",
                    "usdtry",
                    "eurtry",
                    "garan"
                )
            )*/
        }
        lifecycleScope.launch {
            delay(8500)
            symbolQueueManager.subscribe(
                listOf(
                    "akbnk",
                    "aefes",
                    "isctr",
                    "eregl",
                    "thyao",
                    "f_akbnk1023",
                    "f_akbnk1123",
                    "tabgd",
                    "bist100",
                )
            )
        }
        lifecycleScope.launch {
            delay(9500)
            /*symbolQueueManager.unsubscribe(
                listOf(
                    "akbnk",
                    "aefes",
                    "isctr",
                    "eregl",
                    "thyao",
                    "f_akbnk1023",
                    "f_akbnk1123",
                    "tabgd",
                    "bist100",
                )
            )*/
        }
        lifecycleScope.launch {
            delay(9800)
            symbolQueueManager.subscribe(
                listOf(
                    "akbnk",
                )
            )
        }
        lifecycleScope.launch {
            delay(11111)
            symbolQueueManager.unsubscribe(
                listOf(
                    "akbnk",
                )
            )
        }
        lifecycleScope.launch {
            delay(12000)
            symbolQueueManager.subscribe(
                listOf(
                    "akbnk",
                    "aefes",
                    "isctr",
                    "eregl",
                    "thyao",
                    "f_akbnk1023",
                    "f_akbnk1123",
                    "tabgd",
                    "bist100",
                )
            )
        }

        setContent {
            TaskTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var symbolQueue by remember { mutableStateOf(emptyMap<String, Int>()) }
                    var unsubscribeQueue by remember { mutableStateOf(emptyMap<String, Int>()) }

                    LaunchedEffect(Unit) {
                        launch {
                            val symbolQueueFlow = symbolQueueManager.getSymbolQueue()
                            symbolQueueFlow.collect { updatedQueue ->
                                symbolQueue = updatedQueue
                            }
                        }

                        launch {
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

