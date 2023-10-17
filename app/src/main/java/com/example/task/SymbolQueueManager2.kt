
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SymbolQueueManager2 {
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
        val debounceStartTime = System.currentTimeMillis()
        unsubscribeScope.launch {
            unsubscribeQueue.value = unsubscribeQueue.value + symbols
            symbolQueue.sample(5000).collect {
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
                //unsubscribeQueue.value = emptyList()
                updateQueue()
            }


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
                //unsubscribeQueue.value = emptyList()
                updateQueue()
            }


        }
        val debounceEndTime = System.currentTimeMillis()
        val debounceDuration = debounceEndTime - debounceStartTime
        println("Debounce time: $debounceDuration ms")

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
