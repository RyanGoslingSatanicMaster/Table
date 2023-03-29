package com.doggystyle.table.components.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import com.doggystyle.table.components.TableApp
import com.doggystyle.table.di.DaggerViewModelFactory
import com.doggystyle.table.di.components.SplashScreenActivityComponent
import com.doggystyle.table.model.LoadingState
import com.doggystyle.table.ui.progressBar
import com.doggystyle.table.ui.theme.TableTheme
import javax.inject.Inject

class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: DaggerViewModelFactory

    lateinit var viewModel: SplashScreenViewModel

    lateinit var activityComponent: SplashScreenActivityComponent

    companion object{
        const val ACTIVE_GROUP_TAG = "ACTIVE_GROUP_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = (application as TableApp).appComponent.getSplashScreenActivityComponent()
        activityComponent.inject(this)
        viewModel = ViewModelProvider(this, factory).get(SplashScreenViewModel::class.java)
        viewModel.getActiveGroup()
        setContent {
            TableTheme {
                SplashScreenLayout()
            }
        }
    }

    @Composable
    fun SplashScreenLayout(){
        val loading = viewModel.loading.observeAsState()
        progressBar(visible = loading.value == LoadingState.Loading)
        when{
            loading.value is LoadingState.Success ->{
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(
                    ACTIVE_GROUP_TAG,
                    (loading.value as LoadingState.Success).tag == SplashScreenViewModel.SUCCESS_ACTIVE_GROUP
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}
