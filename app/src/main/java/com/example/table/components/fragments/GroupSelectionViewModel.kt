package com.example.table.components.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.table.exceptions.ExecuteGroupException
import com.example.table.exceptions.ExecuteTimeTableException
import com.example.table.model.LoadingState
import com.example.table.model.db.Group
import com.example.table.model.requests.GroupRequest
import com.example.table.model.requests.TimeTableRequest
import com.example.table.usecases.*
import com.example.table.utils.Constant
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupSelectionViewModel @Inject constructor(
    val groupUseCase: IGetGroups,
    private val timeTableUseCase: IExecuteAndSaveTimeTable,
    private val deleteGroupUseCase: IDeleteGroupUseCase,
    private val isGroupInDbUseCase: IsGroupInDbUseCase,
    private val updateGroup: IUpdateGroup
) : ViewModel() {

    val groupList = MutableLiveData<List<Group>>(listOf())

    val loading = MutableLiveData<LoadingState>(LoadingState.Stopped)


    // TODO Fix error output

    fun checkClickedGroup(group: Group, callback: (Group) -> Unit){
        loading.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                val groupDb = isGroupInDbUseCase.isGroupInDb(group)
                callback.invoke(groupDb)
                loading.postValue(LoadingState.Success(
                    if (groupDb.isActive)
                        Constant.ACTIVE_ALREADY_EXIST_IN_DB
                    else
                        Constant.INACTIVE_ALREADY_EXIST_IN_DB
                ))
            }catch (ex: Exception){
                callback.invoke(group)
                loading.postValue(LoadingState.Success(Constant.NOT_EXIST_IN_DB))
            }
        }
    }

    fun updateGroupTimeTable(group: Group, callback: (Group) -> Unit){
        loading.value = LoadingState.Loading
        viewModelScope.launch{
            try {
                deleteGroupUseCase.deleteGroupData(group)
                callback.invoke(timeTableUseCase.getTimeTable(TimeTableRequest(1, group)))
                loading.postValue(LoadingState.Success(Constant.SUCCESS_TIMETABLE_UPDATE))
            }catch (ex: Exception){
                loading.postValue(LoadingState.Error(ex))
            }
        }
    }

    fun updateGroup(group: Group, callback: (Group) -> Unit){
        loading.value = LoadingState.Loading
        viewModelScope.launch {
            try {
                callback.invoke(updateGroup.updateGroup(group))
                loading.postValue(LoadingState.Success(Constant.ACTIVE_ALREADY_EXIST_IN_DB))
            } catch (ex: Exception) {
                loading.postValue(LoadingState.Error(ex))
            }
        }
    }

   fun executeAndSaveGroupTimeTable(group: Group, callback: (Group) -> Unit){
       loading.value = LoadingState.Loading
       viewModelScope.launch {
           try {
               callback.invoke(timeTableUseCase.getTimeTable(TimeTableRequest(1, group)))
               loading.postValue(LoadingState.Success(Constant.SUCCESS_TIMETABLE_EXECUTE))
           } catch (ex: Exception) {
               loading.postValue(LoadingState.Error(ExecuteTimeTableException(ex.message)))
           }
       }
    }

    fun updateGroupList(str: String){
        if (!str.isNullOrEmpty())
            viewModelScope.launch {
                try {
                    groupList.postValue(groupUseCase.getGroup(GroupRequest(str, 1)))
                }
                catch (ex: Exception){
                    loading.postValue(LoadingState.Error(ExecuteGroupException(ex.message)))
                }
            }
    }

}