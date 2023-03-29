package com.doggystyle.table.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.doggystyle.table.datasource.local.dao.TimeTableDao
import com.doggystyle.table.exceptions.NetworkResponseException
import com.doggystyle.table.datasource.remote.Api
import com.doggystyle.table.exceptions.RoomResponseException
import com.doggystyle.table.exceptions.WrongResponseCodeException
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
    suspend fun <ReturnType, Result> executeAndSave(
        loadingStateKey: Pair<String, String>? = null,
        pair: Pair<suspend () -> Response<ReturnType>, suspend (ReturnType) -> Result?>
    ): Result{
        execute(loadingStateKey?.first, pair.first).let {
            return saveLocal(loadingStateKey?.second, pair.second, it)
        }
    }

    @Throws(Exception::class, RoomResponseException::class)
    suspend fun <ReturnType, Result> saveLocal(loadingStateKey: String? = null, request: suspend (ReturnType) -> Result?, data: ReturnType): Result{
        val loadingState = getLoadingState(loadingStateKey)
        loadingState?.postValue(true)
        try {
            val result = request.invoke(data)
            if (result != null) {
                return result
            }
            throw RoomResponseException("Data not saved")
        } catch (ex: Exception) {
            loadingState?.postValue(false)
            throw ex
        }
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
