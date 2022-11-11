package com.example.table.components.activity

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.table.components.fragments.GroupSelectionFragment
import com.example.table.components.TableApp
import com.example.table.components.fragments.TimeTableFragment
import com.example.table.di.DaggerViewModelFactory
import com.example.table.di.components.MainActivityComponent
import com.example.table.ui.ComposeFragmentContainer
import com.example.table.ui.FragmentController
import com.example.table.ui.theme.TableTheme
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: DaggerViewModelFactory

    lateinit var viewModel: MainViewModel

    lateinit var activityComponent: MainActivityComponent

    @Inject
    lateinit var fragmentMap: Map<Class<out Fragment>, @JvmSuppressWildcards(true) Provider<Fragment>>

    val fragmentContainerId: Int = View.generateViewId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = (application as TableApp).appComponent.getMainActivityComponent()
        activityComponent.inject(this)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        val activeGroup = intent.getBooleanExtra(SplashScreenActivity.ACTIVE_GROUP_TAG, false)
        setContent {
            TableTheme {
                ComposeFragmentContainer(viewId = fragmentContainerId, fragmentManager = this.supportFragmentManager
                ) {
                    when{
                        activeGroup && viewModel.activeGroup.value == null -> {
                            viewModel.getActiveGroup()
                            add(it, fragmentMap.get(TimeTableFragment::class.java)!!.get())
                        }
                        // TODO controversial decision, may be replace with another features
                        viewModel.activeGroup.value != null -> {
                            add(it, fragmentMap.get(TimeTableFragment::class.java)!!.get())
                        }
                        else -> {
                            add(it, fragmentMap.get(GroupSelectionFragment::class.java)!!.get())
                        }
                    }
                }

            }
        }
    }

    fun startTimeTableFragment(){
        FragmentController(fragmentContainerId, fragmentMap.get(TimeTableFragment::class.java)!!.get(), supportFragmentManager)
    }

    fun startGroupSelectionFragment(){
        FragmentController(fragmentContainerId, fragmentMap.get(GroupSelectionFragment::class.java)!!.get(), supportFragmentManager)
    }

}
