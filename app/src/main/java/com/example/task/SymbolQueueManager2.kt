import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SymbolQueueManager2 {
    private val symbolCountMap = mutableMapOf<String, Int>()
    private val symbolQueue = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val unsubscribeQueue = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            symbolQueue.update {
                it.toMutableMap().apply {
                    putAll(symbolCountMap.toMap())
                }
            }
        }
        scope.launch {
            startProcessingQueue()
        }
        scope.launch {
            symbolQueue.collect {
                println("symbolQueue: $it")
            }
        }
        scope.launch {
            unsubscribeQueue.collect {
                println("unsubscribeQueue: $it")
            }
        }
    }

    fun subscribe(symbols: List<String>) {
        scope.launch {
            symbolQueue.update {
                it.toMutableMap().apply {
                    symbols.forEach { symbol ->
                        put(symbol, getOrDefault(symbol, 0) + 1)
                    }
                }
            }
        }
    }

    fun unsubscribe(symbols: List<String>) {
        scope.launch {
            unsubscribeQueue.update {
                it.toMutableMap().apply {
                    symbols.forEach { symbol ->
                        put(symbol, getOrDefault(symbol, 0) + 1)
                    }
                }
            }
        }
    }

    fun getSymbolQueue() = symbolQueue.asStateFlow()

    fun getUnsubscribeQueue() = unsubscribeQueue.asStateFlow()

    @OptIn(FlowPreview::class)
    private suspend fun startProcessingQueue() {
        unsubscribeQueue.debounce(5000).collect { unsubMap ->
            println("debounce")
            symbolQueue.update { symbolsMap ->
                symbolsMap.toMutableMap().apply {
                    unsubMap.forEach { (key, value) ->
                        get(key)?.let { existCount ->
                            val nextValue = existCount - value
                            if (nextValue > 0) {
                                put(key, nextValue)
                            } else {
                                remove(key)
                            }
                        }
                        unsubscribeQueue.update { emptyMap() }
                    }
                }
            }
        }
    }
}
