package com.example.task

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class SymbolQueueManager3 {

    private val symbolCountMap = mutableMapOf<String, Int>()
    private val symbolQueue = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val unsubscribeQueue = MutableStateFlow<List<String>>(emptyList())
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    val unsubscribeScope = CoroutineScope(Dispatchers.Default)
    val subscribeScope = CoroutineScope(Dispatchers.Default)
    private val mutex = Mutex()

    init {
        symbolQueue.update {
            it + symbolCountMap.toMap()
        }
        startProcessingQueue()
    }

    fun subscribe(symbols: List<String>) {
        subscribeScope.launch {
            symbols.forEach { symbol ->
                symbolCountMap[symbol] = symbolCountMap.getOrDefault(symbol, 0) + 1
            }
            updateQueue()
        }
    }

    fun unsubscribe(symbols: List<String>) {
        unsubscribeScope.launch {
            unsubscribeQueue.value = unsubscribeQueue.value + symbols
            symbolQueue.debounce(5000).collect {
                val removedSymbols = mutableListOf<String>()
                symbols.forEach { symbol ->
                    val count = symbolCountMap[symbol]
                    if (count != null) {
                        if (count > 1) {
                            symbolCountMap[symbol] = count - 1
                        } else {
                            symbolCountMap.remove(symbol)
                            removedSymbols.add(symbol)
                        }
                    }
                }

                unsubscribeQueue.value = unsubscribeQueue.value - removedSymbols
                unsubscribeQueue.value = emptyList()
                updateQueue()
            }
        }

    }

    fun getSymbolQueue(): StateFlow<Map<String, Int>> = symbolQueue.asStateFlow()

    fun getUnsubscribeQueue(): StateFlow<List<String>> = unsubscribeQueue.asStateFlow()

    private suspend fun updateQueue() {
        symbolQueue.value = symbolCountMap.toMap()
    }

    private fun startProcessingQueue() {
        coroutineScope.launch {
            symbolQueue
                .collect { updatedQueue ->
                    println("Queue updated: $updatedQueue")
                }
        }
    }
}