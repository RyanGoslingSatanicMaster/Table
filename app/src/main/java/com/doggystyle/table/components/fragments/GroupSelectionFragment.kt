package com.doggystyle.table.components.fragments

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doggystyle.table.R
import com.doggystyle.table.components.activity.MainActivity
import com.doggystyle.table.components.activity.MainViewModel
import com.doggystyle.table.di.DaggerViewModelFactory
import com.doggystyle.table.exceptions.ExecuteGroupException
import com.doggystyle.table.exceptions.ExecuteTimeTableException
import com.doggystyle.table.model.LoadingState
import com.doggystyle.table.model.db.Group
import com.doggystyle.table.ui.progressBar
import com.doggystyle.table.ui.showDialog
import com.doggystyle.table.ui.theme.*
import com.doggystyle.table.utils.Constant
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
        groupInput(
            text = text.value,
            visibleTextField = loading.value is LoadingState.Stopped || loading.value is LoadingState.Error,
            onItemClick = {
                viewModel.checkClickedGroup(it.copy(groupId = 0)) {
                    activityViewModel.activeGroup.postValue(
                        it
                    )
                }
            },
            onTextChanged = {
                text.value = it
                viewModel.updateGroupList(it)
            }
        )
        progressBar(visible = loading.value is LoadingState.Loading)
        when(loading.value){
            is LoadingState.Error -> (loading.value as LoadingState.Error).ex.let {
                showDialog(
                    text = it.message!!,
                    onConfirm = {
                        when(it){
                            is ExecuteGroupException -> viewModel.updateGroupList(text.value)
                            is ExecuteTimeTableException -> if (activityViewModel.activeGroup.value != null )
                                viewModel.checkClickedGroup(activityViewModel.activeGroup.value!!) {
                                    activityViewModel.activeGroup.postValue(
                                        it
                                    )
                                }
                        }
                        viewModel.loading.value = LoadingState.Stopped
                    },
                    confirmText = "Повторить",
                    dismissText = "Отмена"
                )
            }
            is LoadingState.Success -> {
                (loading.value as LoadingState.Success).tag.let { tag ->
                    when(tag){
                        Constant.ACTIVE_ALREADY_EXIST_IN_DB -> showDialog(
                            text = stringResource(R.string.update_timetable),
                            onConfirm = {
                                viewModel.updateGroupTimeTable(activityViewModel.activeGroup.value!!) {
                                    activityViewModel.activeGroup.postValue(it)
                                }
                            },
                            onDismiss = {
                                (activity as MainActivity).startTimeTableFragment()
                            }
                        )
                        Constant.INACTIVE_ALREADY_EXIST_IN_DB -> showDialog(
                            text = stringResource(R.string.do_active),
                            onConfirm = {
                                viewModel.updateGroup(
                                    activityViewModel.activeGroup.value!!.copy(isActive = true)
                                ){group ->
                                    activityViewModel.activeGroup.postValue(group)
                                }
                                viewModel.loading.value = LoadingState.Success(Constant.ACTIVE_ALREADY_EXIST_IN_DB)
                            },
                            onDismiss = {
                                viewModel.loading.value = LoadingState.Success(Constant.ACTIVE_ALREADY_EXIST_IN_DB)
                            }
                        )
                        Constant.NOT_EXIST_IN_DB -> showDialog(text = stringResource(R.string.do_active),
                            onConfirm = {
                                viewModel.executeAndSaveGroupTimeTable(
                                    group = activityViewModel.activeGroup.value!!.copy(isActive = true)
                                ) {
                                    activityViewModel.activeGroup.postValue(
                                        it
                                    )
                                }
                            },
                            onDismiss = {
                                viewModel.executeAndSaveGroupTimeTable(
                                    group = activityViewModel.activeGroup.value!!.copy(isActive = false)
                                ) {
                                    activityViewModel.activeGroup.postValue(
                                        it
                                    )
                                }
                            }
                        )
                        Constant.SUCCESS_TIMETABLE_UPDATE -> {
                            (activity as MainActivity).startTimeTableFragment()
                            viewModel.loading.value = LoadingState.Stopped
                        }
                        Constant.SUCCESS_TIMETABLE_EXECUTE -> {
                            (activity as MainActivity).startTimeTableFragment()
                            viewModel.loading.value = LoadingState.Stopped
                        }
                    }
                }
            }
            else -> {
            }

        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.loading.value = LoadingState.Stopped
    }

    @Composable
    fun groupInput(text: String, visibleTextField: Boolean, onTextChanged: (String) -> Unit, onItemClick: (Group) -> Unit){
        val list = viewModel.groupList.observeAsState()
        groupLayout(text = text,
            list = list.value!!,
            onTextChanged = {
                onTextChanged.invoke(it)
            },
            onItemClick = {
                onItemClick.invoke(it)
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
            placeholder = { Text(text = stringResource(R.string.choose_group_number), style = TextStyle(color = Hint)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Secondary
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

