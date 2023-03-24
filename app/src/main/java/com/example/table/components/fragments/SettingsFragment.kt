package com.example.table.components.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.glance.ImageProvider
import androidx.glance.appwidget.action.ToggleableStateKey
import com.example.table.BuildConfig
import com.example.table.R
import com.example.table.components.activity.MainActivity
import com.example.table.components.activity.MainViewModel
import com.example.table.di.DaggerViewModelFactory
import com.example.table.model.LoadingState
import com.example.table.model.db.Group
import com.example.table.ui.CustomAlertDialog
import com.example.table.ui.progressBar
import com.example.table.ui.showDialog
import com.example.table.ui.theme.Primary
import com.example.table.ui.theme.Secondary
import com.example.table.ui.theme.TableTheme
import com.example.table.ui.theme.Typography
import javax.inject.Inject

class SettingsFragment @Inject constructor() : Fragment() {

    @Inject
    lateinit var factory: DaggerViewModelFactory

    private lateinit var viewModel: SettingsViewModel

    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)
        activityViewModel = (activity as MainActivity).viewModel
        return ComposeView(requireContext()).apply {
            setContent {
                TableTheme {
                    SettingsLayout()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val request = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            }
            val permission = ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.SCHEDULE_EXACT_ALARM
            )
            if (permission != PackageManager.PERMISSION_GRANTED)
                request.launch(Manifest.permission.SCHEDULE_EXACT_ALARM)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshGroupList()
    }

    @Composable
    private fun SettingsLayout() {
        val settings = activityViewModel.notificationSettings.observeAsState()
        val groupList = viewModel.groupList.observeAsState()
        val loading = remember {
            mutableStateOf(false)
        }
        val showCustomDialog = remember {
            mutableStateOf(false)
        }
        progressBar(visible = loading.value)
        if (showCustomDialog.value)
            CustomAlertDialog(startValue = settings.value?.third!!, onSave = {
                (activity as MainActivity).requestPermission() {
                    (activity as MainActivity).setNotifications(
                        Triple(
                            settings.value?.first!!,
                            settings.value?.second!!,
                            it
                        )
                    )
                }
                showCustomDialog.value = false
            }) {
                showCustomDialog.value = false
            }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Настройки уведомлений", style = Typography.h2, color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))
            showNotificationSettings(settings = settings.value) { showCustomDialog.value = true }
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Сохраненные группы", style = Typography.h2, color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                savedGroupList(
                    groupList = groupList.value,
                    Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .verticalScroll(rememberScrollState())
                        .weight(1f, false), {
                        viewModel.refreshGroupTimeTable(it, { group ->
                            activityViewModel.setGroup(group)
                            (activity as MainActivity).startTimeTableFragment()
                        }) { ex ->
                            Toast.makeText(requireContext(),
                                "Не удалось загрузить расписание",
                                Toast.LENGTH_LONG).show()
                        }
                    }) { group ->
                    activityViewModel.setGroup(group)
                    (activity as MainActivity).startTimeTableFragment()
                }
            }
        }
    }

    @Composable
    fun showNotificationSettings(
        settings: Triple<Boolean, Boolean, Int>?,
        onButtonPressed: () -> Unit,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Включить уведомление  о парах",
                style = Typography.h3, color = Color.Black,
                modifier = Modifier.weight(4f)
            )
            Switch(
                checked = settings?.first!! || settings.second,
                onCheckedChange = { notify ->
                    (activity as MainActivity).requestPermission() {
                        (activity as MainActivity).setNotifications(Triple(notify,
                            notify,
                            settings.third ?: DEFAULT_MINUTE_BEFORE_NOTIFY))
                    }
                },
                colors = SwitchDefaults.colors(checkedThumbColor = Primary,
                    checkedTrackColor = Secondary),
                modifier = Modifier.weight(1f)
            )
        }
        AnimatedVisibility(
            visible = settings?.first!! || settings.second,
            enter = expandHorizontally(),
            exit = shrinkHorizontally(animationSpec = tween(1600))
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.notife_lect),
                        style = Typography.h3, color = Color.Black,
                        modifier = Modifier.weight(4f)
                    )
                    Switch(
                        checked = settings.first,
                        onCheckedChange = { notify ->
                            (activity as MainActivity).requestPermission() {
                                (activity as MainActivity).setNotifications(
                                    Triple(
                                        notify,
                                        settings.second,
                                        settings.third ?: DEFAULT_MINUTE_BEFORE_NOTIFY
                                    )
                                )
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Primary,
                            checkedTrackColor = Secondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.notif_pract),
                        style = Typography.h3, color = Color.Black,
                        modifier = Modifier.weight(4f)
                    )
                    Switch(
                        checked = settings.second,
                        onCheckedChange = { notify ->
                            (activity as MainActivity).requestPermission() {
                                (activity as MainActivity).setNotifications(
                                    Triple(
                                        settings.first,
                                        notify,
                                        settings.third ?: DEFAULT_MINUTE_BEFORE_NOTIFY
                                    )
                                )
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Primary,
                            checkedTrackColor = Secondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.time_before_notify),
                        style = Typography.h3, color = Color.Black,
                        modifier = Modifier.weight(4f)
                    )

                    Button(onClick = { onButtonPressed() }) {
                        Text(text = settings.third.convertToHourMinute())
                    }
                }
            }
        }
    }

    @Composable
    fun savedGroupList(
        groupList: List<Group>?,
        modifier: Modifier,
        onRefresh: (Group) -> Unit,
        onShowGroup: (Group) -> Unit
    ) {
        Column(modifier = modifier,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
            groupList?.forEach {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.width(110.dp).clickable {
                        onShowGroup(it)
                    }) {
                        Text(text = it.groupName, style = Typography.h6, color = Color.Black)
                    }
                    Icon(imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 50.dp, minWidth = 50.dp)
                            .padding(4.dp)
                            .graphicsLayer(alpha = 0.99f)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        Brush.linearGradient(listOf(Primary, Secondary)),
                                        blendMode = BlendMode.SrcAtop
                                    )
                                }
                            }
                            .clickable {
                                onRefresh(it)
                            }
                    )
                    Icon(imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 50.dp, minWidth = 50.dp)
                            .padding(4.dp)
                            .graphicsLayer(alpha = 0.99f)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    if (it.isActive)
                                        drawRect(
                                            Brush.linearGradient(listOf(Primary, Secondary)),
                                            blendMode = BlendMode.SrcAtop
                                        )

                                }
                            }
                            .clickable {
                                if (!it.isActive) {
                                    viewModel.setActiveGroup(it)
                                    activityViewModel.setGroup(it.copy(isActive = true))
                                }
                            }
                    )
                }
            }
        }
    }

    fun Int.convertToHourMinute(): String {
        val hour = this / 60
        val minutes = this - hour * 60
        return "$hour:$minutes"
    }

    companion object {
        const val DEFAULT_MINUTE_BEFORE_NOTIFY = 5
    }

}
