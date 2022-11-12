package com.example.table.ui

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.*

@Composable
fun ComposeFragmentContainer(
    modifier: Modifier = Modifier,
    viewId: Int,
    fragmentManager: FragmentManager,
    commit: FragmentTransaction.(containerId: Int) -> Unit
) {
    val containerId = rememberSaveable{ mutableStateOf(viewId) }
    val initialized = rememberSaveable { mutableStateOf(false) }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            FragmentContainerView(context).apply{
                id = containerId.value
            }
        },
        update = { view ->
            if (!initialized.value){
                fragmentManager.commit { commit(view.id) }
                initialized.value = true
            }
            else {
                fragmentManager.onContainerAvailable(view)
            }
        }
    )
}


fun FragmentController(containerId: Int, fragment: Fragment, fragmentManager: FragmentManager){
    val fragments = fragmentManager.fragments
    if (fragments.any { it === fragment && it.isVisible})
        return
    fragmentManager.commit {
        replace(containerId, fragment)
        addToBackStack(fragment.tag)
    }
}

private fun FragmentManager.onContainerAvailable(view: FragmentContainerView){
    val method = FragmentManager::class.java.getDeclaredMethod(
        "onContainerAvailable",
        FragmentContainerView::class.java
    )
    method.isAccessible = true
    method.invoke(this, view)
}
