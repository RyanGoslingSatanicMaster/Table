package com.example.table.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.table.datasource.local.dao.TimeTableDao
import com.example.table.exceptions.NetworkResponseException
import com.example.table.datasource.remote.Api
import com.example.table.exceptions.RoomResponseException
import com.example.table.exceptions.WrongResponseCodeException
import retrofit2.Response
import java.util.concurrent.Flow
import javax.inject.Inject
import kotlin.jvm.Throws

open class ApiService @Inject constructor() {

    @Inject
    lateinit var api: Api

    @Inject
    lateinit var dao: TimeTableDao

    private val loadingStates: HashMap<String, MutableLiveData<Boolean>> = HashMap()

    /** Abstract Composite function:
     * 1. Execute from remote
     * 2. If success, Save Local
     */
    suspend fun <ReturnType> executeAndSave(loadingStateKey: Pair<String, String>? = null, pair: Pair<suspend () -> Response<ReturnType>, suspend (ReturnType) -> Unit>){
        execute(loadingStateKey?.first, pair.first).let {
            saveLocal(loadingStateKey?.second, pair.second, it)
        }
    }

    @Throws(Exception::class)
    suspend fun <ReturnType> saveLocal(loadingStateKey: String? = null, request: suspend (ReturnType) -> Unit, data: ReturnType){
        val loadingState = getLoadingState(loadingStateKey)
        loadingState?.postValue(true)
        try {
            request.invoke(data)
        } catch (ex: Exception) {
            loadingState?.postValue(false)
            throw ex
        }
        loadingState?.postValue(false)
    }

    suspend fun <ReturnType> executeRemoteOrLocal(
        loadingStateKey: Pair<String, String>? = null,
        pair: Pair<suspend () -> (ReturnType), suspend () -> Response<ReturnType>>
    ): ReturnType{
        try {
            executeLocal(loadingStateKey?.first, pair.first).let { return it }
        }catch (ex: Exception){
            execute(loadingStateKey?.second, pair.second).let { return it }
        }
    }

    @Throws(Exception::class)
    suspend fun <ReturnType> executeLocal(loadingStateKey: String? = null, request: suspend () -> ReturnType): ReturnType{
        val loadingState = getLoadingState(loadingStateKey)
        loadingState?.postValue(true)
        try {
            request.invoke()?.let { return it } ?: throw RoomResponseException("EmptyResponse")
        } catch (ex: Exception) {
            loadingState?.postValue(false)
            throw ex
        }
        loadingState?.postValue(false)
    }

    @Throws(NetworkResponseException::class, WrongResponseCodeException::class)
    suspend fun <ReturnType> execute(
        loadingStateKey: String? = null,
        request: suspend () -> Response<ReturnType>
    ): ReturnType {
        val result = executeRaw(loadingStateKey, request)
        if (result.isSuccessful) {
            result.body()?.let { return it } ?: throw NetworkResponseException("Empty response")
        } else {
            throw WrongResponseCodeException("Response code is not successful", result.code())
        }
    }

    suspend fun <ReturnType> executeOrNull(
        loadingStateKey: String? = null,
        request: suspend () -> Response<ReturnType>
    ): ReturnType? {
        return try {
            executeRaw(loadingStateKey, request).body()
        } catch (ex: Exception) {
            null
        }
    }

    @Throws(Exception::class)
    private suspend fun <ReturnType> executeRaw(
        loadingStateKey: String? = null,
        request: suspend () -> Response<ReturnType>
    ): Response<ReturnType> {
        val loadingState = getLoadingState(loadingStateKey)
        loadingState?.postValue(true)
        val response = try {
            request.invoke()
        } catch (ex: Exception) {
            loadingState?.postValue(false)
            throw ex
        }
        loadingState?.postValue(false)
        return response

    }

    fun createLoadingState(loadingStateKey: String) {
        val currentLoadingState = loadingStates.getOrElse(loadingStateKey) { null }

        if (currentLoadingState == null) {
            loadingStates.put(loadingStateKey, MutableLiveData<Boolean>().apply { value = false })
        }
    }

    fun createAndGetLoadingState(loadingStateKey: String): LiveData<Boolean>? {
        val currentLoadingState = loadingStates.getOrElse(loadingStateKey) { null }

        return if (currentLoadingState == null) {
            loadingStates.put(loadingStateKey, MutableLiveData<Boolean>().apply { value = false })
            getLoadingState(loadingStateKey)
        } else {
            currentLoadingState
        }
    }


    fun getLoadingState(loadingStateKey: String?): MutableLiveData<Boolean>? {
        return if (loadingStateKey != null) {
            loadingStates.getOrElse(loadingStateKey) { null }
        } else {
            null
        }
    }
}
