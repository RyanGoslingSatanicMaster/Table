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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.table.BuildConfig
import com.example.table.R
import com.example.table.components.activity.MainActivity
import com.example.table.components.activity.MainViewModel
import com.example.table.di.DaggerViewModelFactory
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

    @Composable
    private fun SettingsLayout() {
        val settings = activityViewModel.notificationSettings.observeAsState()
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)) {
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
                    checked = settings.value?.first!! || settings.value?.second!!,
                    onCheckedChange = { notify ->
                        (activity as MainActivity).requestPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) {
                            (activity as MainActivity).setNotifications(Triple(notify,
                                notify,
                                settings.value?.third ?: DEFAULT_MINUTE_BEFORE_NOTIFY))
                        }
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = Primary,
                        checkedTrackColor = Secondary),
                    modifier = Modifier.weight(1f)
                )
            }
            AnimatedVisibility(
                visible = settings.value?.first!! || settings.value?.second!!,
                enter = expandHorizontally(),
                exit = shrinkHorizontally(animationSpec = tween(1600))
            ) {
                Column(modifier = Modifier
                    .fillMaxSize()) {
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
                            checked = settings.value?.first!!,
                            onCheckedChange = { notify ->
                                (activity as MainActivity).requestPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) {
                                    (activity as MainActivity).setNotifications(
                                        Triple(
                                            notify,
                                            settings.value?.second!!,
                                            settings.value?.third ?: DEFAULT_MINUTE_BEFORE_NOTIFY
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
                            checked = settings.value?.second!!,
                            onCheckedChange = { notify ->
                                (activity as MainActivity).requestPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) {
                                    (activity as MainActivity).setNotifications(
                                        Triple(
                                            settings.value?.first!!,
                                            notify,
                                            settings.value?.third ?: DEFAULT_MINUTE_BEFORE_NOTIFY
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
                }
            }
        }
    }

    companion object {
        const val DEFAULT_MINUTE_BEFORE_NOTIFY = 5
    }

}
