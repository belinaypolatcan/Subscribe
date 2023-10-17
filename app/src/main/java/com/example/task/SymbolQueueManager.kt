package com.example.task

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

class SymbolQueueManager {
    private val symbolCountMap = mutableMapOf<String, Int>()
    private val symbolQueue = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    val unsubscribeScope = CoroutineScope(Dispatchers.Default)
    val subscribeScope = CoroutineScope(Dispatchers.Default)
    val mutex = Mutex()

    init {
       // symbolQueue.update { güncelleme yapmadan önce içindeki statei verir. mutex çalıştırır. }
        startProcessingQueue()
    }

    fun subscribe(symbols: List<String>) {
        subscribeScope.launch {
            symbols.forEach { symbol ->
                //mutex.withLock {
                    symbolCountMap[symbol] = symbolCountMap.getOrDefault(symbol, 0) + 1
               // }
            }
            updateQueue()
        }
    }

    fun unsubscribe(symbols: List<String>) {
        unsubscribeScope.launch {
            delay(5000)
            symbols.forEach { symbol ->
                //mutex.withLock{
                    val count = symbolCountMap[symbol]
                    if (count != null) {
                        if (count > 1) {
                            symbolCountMap[symbol] = count - 1
                        } else {
                            symbolCountMap.remove(symbol)
                        }
                    }
                //}
            }
            updateQueue()
        }
    }

    fun getSymbolQueue(): StateFlow<Map<String, Int>> = symbolQueue.asStateFlow()

    private suspend fun updateQueue() {
        //mutex.withLock {
            symbolQueue.value = symbolCountMap.toMap()
        //}

    }

    private fun startProcessingQueue() {
        coroutineScope.launch {
            symbolQueue
                .collect { updatedQueue ->
                    println("Queue updated: $updatedQueue")
                }
        }
       // yeni bir coroutine scope oluşturup debans veya sample
         //       ile 5 saniye bekleyeceğiz unsubrcibe için sonra symbolsountmapi güncelle. işledikten sonra unsubscribe unsubscribe listesini boşalt.
    }
}

// unsubscribeları Flow'da tut. SymbolQueu gibi unsubscribe que oluştur. içerisinde string listesi olsun. unsubscribe yaptığında listeyi güncelle.
// mutex, debans, sample, exception handling çalış scope oluşturuken nasıl oluyor?