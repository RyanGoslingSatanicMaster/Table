package com.example.table.components.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.table.components.activity.MainActivity
import com.example.table.components.activity.MainViewModel
import com.example.table.di.DaggerViewModelFactory
import com.example.table.exceptions.ExecuteGroupException
import com.example.table.exceptions.ExecuteTimeTableException
import com.example.table.model.LoadingState
import com.example.table.model.db.Group
import com.example.table.ui.progressBar
import com.example.table.ui.showDialog
import com.example.table.ui.theme.*
import com.example.table.utils.Constant
import javax.inject.Inject

class GroupSelectionFragment @Inject constructor() : Fragment() {

    @Inject
    lateinit var factory: DaggerViewModelFactory

    lateinit var viewModel: GroupSelectionViewModel

    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(GroupSelectionViewModel::class.java)
        activityViewModel = (activity as MainActivity).viewModel
        return ComposeView(requireContext()).apply {
            setContent {
                GroupSelectionLayout()
            }
        }
    }

    @Composable
    fun GroupSelectionLayout(){
        val text = remember { mutableStateOf("") }
        val loading = viewModel.loading.observeAsState()
        groupInput(text = text, visibleTextField = loading.value is LoadingState.Stopped || loading.value is LoadingState.Error)
        progressBar(visible = loading.value is LoadingState.Loading)
        when(loading.value){
            is LoadingState.Error -> (loading.value as LoadingState.Error).ex.let {
                showDialog(
                    text = it.message!!,
                    onConfirm = {
                        when(it){
                            is ExecuteGroupException -> viewModel.updateGroupList(text.value)
                            is ExecuteTimeTableException -> viewModel.checkClickedGroup(activityViewModel.activeGroup.value!!)
                        }
                        viewModel.loading.value = LoadingState.Stopped
                    }
                )
            }
            is LoadingState.Success -> {
                (loading.value as LoadingState.Success).tag.let {
                    when(it){
                        Constant.ACTIVE_ALREADY_EXIST_IN_DB -> showDialog(
                            text = Constant.UPDATE_TIMETABLE,
                            onConfirm = {
                                viewModel.updateGroup(activityViewModel.activeGroup.value!!)
                            },
                            onDismiss = {
                                (activity as MainActivity).startTimeTableFragment()
                            }
                        )
                        Constant.INACTIVE_ALREADY_EXIST_IN_DB -> {
                            showDialog(
                                text = Constant.DO_ACTIVE,
                                onConfirm = {
                                    val actGroup = activityViewModel.activeGroup.value
                                    activityViewModel.activeGroup.value = Group(actGroup!!.groupId, actGroup!!.groupName, true)
                                    viewModel.loading.value = LoadingState.Success(Constant.ACTIVE_ALREADY_EXIST_IN_DB)
                                },
                                onDismiss = {
                                    viewModel.loading.value = LoadingState.Success(Constant.ACTIVE_ALREADY_EXIST_IN_DB)
                                }
                            )
                        }
                        Constant.NOT_EXIST_IN_DB -> {
                            showDialog(text = Constant.DO_ACTIVE,
                                onConfirm = {
                                    val actGroup = Group(
                                        activityViewModel.activeGroup.value!!.groupId,
                                        activityViewModel.activeGroup.value!!.groupName,
                                        true)
                                    activityViewModel.activeGroup.value = actGroup
                                    viewModel.executeAndSaveGroupTimeTable(group = actGroup)
                                },
                                onDismiss = {
                                    viewModel.executeAndSaveGroupTimeTable(activityViewModel.activeGroup.value!!)
                                }
                            )
                        }
                        Constant.SUCCESS_TIMETABLE_UPDATE -> (activity as MainActivity).startTimeTableFragment()
                        Constant.SUCCESS_TIMETABLE_EXECUTE -> (activity as MainActivity).startTimeTableFragment()
                    }
                }
            }
            else -> {
            }

        }

    }

    @Composable
    fun groupInput(text: MutableState<String>, visibleTextField: Boolean){
        val list = viewModel.groupList.observeAsState()
        groupLayout(text = text.value,
            list = list.value!!,
            onTextChanged = {
                text.value = it
                viewModel.updateGroupList(it)
            },
            onItemClick = {
                activityViewModel.activeGroup.value = it
                viewModel.checkClickedGroup(it)
            },
            visibleTextField = visibleTextField
        )
    }

}

@Composable
fun listItem(group: Group, onItemClick: (Group) -> Unit){
    Box(
        modifier = Modifier
            .background(brush = Brush.linearGradient(listOf(Primary, Secondary)), Shapes.small)
            .defaultMinSize(40.dp, 20.dp)
            .clickable { onItemClick(group) },

    ){
        Text(text = group.groupName, style = Typography.h3, color = Color.White, modifier = Modifier.padding(10.dp))
    }
}

@Composable
fun groupLayout(text: String, visibleTextField: Boolean, list: List<Group>, onTextChanged: (String) -> Unit, onItemClick: (Group) -> Unit = {}){
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        val visibility = text.isNotEmpty() && visibleTextField
        textField(text = text, visibleTextField = visibleTextField, onTextChanged = onTextChanged)

        listGroup(visibility = visibility,list = list, onItemClick = onItemClick)

    }
}

@Composable
fun textField(text: String, visibleTextField: Boolean, onTextChanged: (String) -> Unit){
    AnimatedVisibility(visible = visibleTextField,
        enter = slideInHorizontally(animationSpec = tween(durationMillis = 1000))
                + fadeIn(animationSpec = tween(durationMillis = 1000)),
        exit = slideOutHorizontally(animationSpec = tween(durationMillis = 1000))
                + fadeOut(animationSpec = tween(durationMillis = 1000))) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            textStyle = TextStyle(fontSize = 25.sp),
            singleLine = true,
            placeholder = { Text(text = "Введите номер группы", style = TextStyle(color = Hint)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Secondary,
                errorBorderColor = yellow
            )
        )
    }
}

@Composable
fun listGroup(visibility: Boolean, list: List<Group>, onItemClick: (Group) -> Unit){
    AnimatedVisibility(visible = visibility,
        enter = slideInHorizontally(animationSpec = tween(durationMillis = 1000))
                + fadeIn(animationSpec = tween(durationMillis = 1000)),
        exit = slideOutHorizontally(animationSpec = tween(durationMillis = 1000))
                + fadeOut(animationSpec = tween(durationMillis = 1000))) {

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            items(list) {
                Spacer(Modifier.height(10.0.dp))
                listItem(it, onItemClick)
            }
        }
    }
}

