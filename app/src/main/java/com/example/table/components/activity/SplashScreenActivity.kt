package com.example.table.components.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import com.example.table.components.TableApp
import com.example.table.di.DaggerViewModelFactory
import com.example.table.di.components.SplashScreenActivityComponent
import com.example.table.model.LoadingState
import com.example.table.ui.progressBar
import com.example.table.ui.theme.TableTheme
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
                startActivity(intent)
            }
        }
    }
}