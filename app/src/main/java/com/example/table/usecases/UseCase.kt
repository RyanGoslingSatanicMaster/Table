package com.example.table.usecases

import kotlinx.coroutines.*

abstract class UseCase<out Type, in Params> {

    private val backgroundContext = Dispatchers.IO

    private val foregroundContext = Dispatchers.Main

    private var job: Job = Job()

    abstract suspend fun run(p: Params): Type?

    operator fun invoke(params: Params, onResult: (Type?) -> Unit = {}) {
        unsubscribe()
        job = Job()

        CoroutineScope(foregroundContext + job).launch {
            val result = withContext(backgroundContext) {
                run(params)
            }

            onResult(result)
        }
    }

    fun unsubscribe(){
        job.apply {
            cancelChildren()
            cancel()
        }
    }

}