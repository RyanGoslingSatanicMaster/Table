package com.example.table.components.fragments

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.table.model.LoadingState
import com.example.table.model.db.Group
import com.example.table.model.requests.TimeTableRequest
import com.example.table.usecases.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val getSavedGroups: GetSavedGroups,
    private val updateActiveGroup: UpdateGroup,
    private val executeAndSaveTimeTable: ExecuteAndSaveTimeTable
) : ViewModel() {

    val groupList = MutableLiveData<List<Group>?>(null)

    init {
        refreshGroupList()
    }

    fun refreshGroupList(){
        viewModelScope.launch {
            groupList.postValue(getSavedGroups.getSavedGroups())
        }
    }

    fun setActiveGroup(group: Group){
        viewModelScope.launch {
            updateActiveGroup.updateGroup(group.copy(isActive = true))
            groupList.postValue(null)
            groupList.postValue(getSavedGroups.getSavedGroups())
        }
    }

    fun refreshGroupTimeTable(group: Group, callback: (Group) -> Unit, onError: (Exception) -> Unit){
        viewModelScope.launch {
            try {
                executeAndSaveTimeTable.getTimeTable(TimeTableRequest(1, group))
                callback(group)
            }catch (ex: Exception){
                onError(ex)
            }
        }
    }
}
