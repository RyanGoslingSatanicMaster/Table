package com.doggystyle.table.ui

import android.view.View
import android.view.ViewGroup
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
    AndroidView(
        modifier = modifier,
        factory = { context ->
            fragmentManager.findFragmentById(containerId.value)?.view
                ?.also { (it.parent as? ViewGroup)?.removeView(it) }
                ?: FragmentContainerView(context)
                    .apply { id = containerId.value }
                    .also {
                        fragmentManager.commit { commit(it.id) }
                    }
        },
        update = {}
    )
}


fun FragmentController(containerId: Int, fragment: Fragment, fragmentManager: FragmentManager){
    val currentFragment = fragmentManager.findFragmentById(containerId)
    if (currentFragment?.isVisible == true && currentFragment === fragment)
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
