package com.example.table.ui

import android.annotation.SuppressLint
import android.os.FileUtils.copy
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.glance.appwidget.proto.LayoutProto
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.table.R
import com.example.table.ui.theme.Primary
import com.example.table.ui.theme.Secondary
import com.example.table.utils.Constant

@Stable
enum class PositionState{
    Previous, Next, Current
}

@Composable
fun progressBar(visible: Boolean){
    AnimatedVisibility(visible = visible,
        enter = slideInHorizontally(animationSpec = tween(durationMillis = 1600))
                + fadeIn(animationSpec = tween(durationMillis = 1600)),
        exit = slideOutHorizontally(animationSpec = tween(durationMillis = 1000))
                + fadeOut(animationSpec = tween(durationMillis = 1000))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
            LottieAnimation(composition)
        }
    }
}

@Composable
fun showDialog(text: String, onConfirm: () -> Unit = {}, onDismiss: () -> Unit = {}, confirmText: String = Constant.YES, dismissText: String = Constant.NO){
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value)
        AlertDialog(onDismissRequest = {
            onDismiss()
            openDialog.value = false },
            confirmButton = @Composable {
                alertButton(text = confirmText) {
                    onConfirm()
                    openDialog.value = false
                }
            },
            dismissButton = @Composable {alertButton(text = dismissText) {
                openDialog.value = false
                onDismiss()
            }
            },
            text = @Composable { Text(text = text, color = Color.White) },
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(colors = listOf(Primary, Secondary)),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(10.dp),
            backgroundColor = Color.Transparent
        )
}

@Composable
fun alertButton(text: String, onClick: () -> Unit){
    Box(modifier = Modifier
        .clickable { onClick() }
        .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = TextStyle(color = Color.White, fontSize = 20.sp))

    }
}


@Composable
fun AnimatedBackgroundGradient(
    colors: Pair<Color, Color>,
    duration: Int,
    topPosition: Boolean = true,
    padding: Dp = 0.dp,
    innerElement: @Composable (PositionState, Boolean) -> Unit = { a, b ->
    }
){

    val xOffset = remember{ mutableStateOf(0f) }
    val yOffset = remember{ mutableStateOf(0f) }

    val positionTop = remember { mutableStateOf(topPosition) }

    val nextState = remember {
        mutableStateOf(PositionState.Current)
    }

    val colorState = remember {
        mutableStateOf(if (positionTop.value) colors.first else colors.second)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(onDragEnd = {
                    when {
                        xOffset.value > 100 -> {
                            nextState.value = PositionState.Previous
                        }
                        xOffset.value < -100 -> {
                            nextState.value = PositionState.Next
                        }
                        yOffset.value > 100 -> {
                            positionTop.value = true
                        }
                        yOffset.value < -100 -> {
                            positionTop.value = false
                        }
                    }
                    xOffset.value = 0f
                    yOffset.value = 0f
                }) { change, dragAmount ->
                    xOffset.value += dragAmount.x
                    yOffset.value += dragAmount.y
                }
            }
    ) {

        // init size
        val itemWidthPx = with(LocalDensity.current){
            (maxWidth - (padding*2)).toPx()
        }
        val itemHeightPx = with(LocalDensity.current){
            (maxHeight - padding).toPx()
        }
        val gradientWidth = 0.2f * itemHeightPx

        SlideAnimatedContent(
            colors = colors,
            duration = duration,
            positionState = nextState,
            positionTop = positionTop,
            itemHeightPx = itemHeightPx,
            itemWidthPx = itemWidthPx,
            gradientWidth = gradientWidth,
            padding = padding,
            innerElement = innerElement
        )
    }
}

@Composable
fun SlideAnimatedContent(
    colors: Pair<Color, Color>,
    duration: Int,
    positionState: MutableState<PositionState>,
    positionTop: State<Boolean>,
    itemHeightPx: Float,
    itemWidthPx: Float,
    gradientWidth: Float,
    padding: Dp,
    innerElement: @Composable (PositionState, Boolean) -> Unit){

    // COLOR ANIMATION
    val colorState = remember {
        mutableStateOf(if (positionTop.value) colors.first else colors.second)
    }

    val color by animateColorAsState(targetValue = colorState.value, animationSpec = tween(duration))

    if (positionTop.value)
        colorState.value = colors.first
    else
        colorState.value = colors.second

    val colors = listOf(color.copy(alpha = 0.9f),color.copy(alpha = 0.6f), color.copy(alpha = 0.9f))

    // NEXT ANIMATION

    val xState = remember { mutableStateOf(0f) }

    val yState = remember { mutableStateOf(0f) }

    val xShimmer by animateFloatAsState(
        targetValue = xState.value,
        animationSpec = tween(duration)
    )
    val yShimmer by animateFloatAsState(
        targetValue = yState.value,
        animationSpec = tween(duration)
    )

    if (xShimmer == (itemWidthPx + gradientWidth))
        positionState.value = PositionState.Current

    when(positionState.value){
        PositionState.Previous -> {
            xState.value = (itemWidthPx + gradientWidth)
            yState.value = (itemHeightPx + gradientWidth)
        }
        PositionState.Next -> {
            xState.value = (itemWidthPx + gradientWidth)
            yState.value = (itemHeightPx + gradientWidth)
        }
        else -> {
            xState.value = 0f
            yState.value = 0f
        }

    }


    shimmerRecipeItem(colors = colors,
        xShimmer = xShimmer,
        yShimmer = yShimmer,
        gradientWidth = gradientWidth,
        padding = padding,
        innerElement = innerElement,
        positionTop = positionTop,
        position = positionState
    )
}


@Composable
fun shimmerRecipeItem(
    positionTop: State<Boolean>,
    position: State<PositionState>,
    colors: List<Color>,
    xShimmer: Float,
    yShimmer: Float,
    gradientWidth: Float,
    padding: Dp,
    innerElement: @Composable (PositionState, Boolean) -> Unit
){
    val brush = Brush.linearGradient(
        colors,
        start = Offset(xShimmer - gradientWidth, yShimmer - gradientWidth),
        end = Offset(xShimmer, yShimmer)
    )
    Box(modifier = Modifier
        .fillMaxSize()
        .background(brush)){
        val positionState by remember{ derivedStateOf { position.value } }
        val isTop by remember{ derivedStateOf { positionTop.value } }
        cloudsAnimation(visible = isTop, Modifier)
        wheatAnimation(visible = !isTop, Modifier)
        innerElement(positionState, isTop)
    }
}

@Composable
fun wheatAnimation(visible: Boolean, modifier: Modifier){
    AnimatedVisibility(visible = visible,
        enter = slideInVertically(initialOffsetY = {
            it / 2
        },
            animationSpec = tween(durationMillis = 1600))
                + fadeIn(animationSpec = tween(durationMillis = 1600)),
        exit = slideOutVertically( targetOffsetY = {
            it / 2
        }, animationSpec = tween(durationMillis = 1000))
                + fadeOut(animationSpec = tween(durationMillis = 1000))
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.wheat))
        LottieAnimation(composition, alignment = Alignment.BottomStart, modifier = modifier
            .fillMaxSize().clip(RectangleShape))
    }
}

@Composable
fun cloudsAnimation(visible: Boolean, modifier: Modifier){
    AnimatedVisibility(visible = visible,
        enter = slideInVertically(animationSpec = tween(durationMillis = 1600))
                + fadeIn(animationSpec = tween(durationMillis = 1600)),
        exit = slideOutVertically(animationSpec = tween(durationMillis = 1000))
                + fadeOut(animationSpec = tween(durationMillis = 1000))
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.clouds))
        LottieAnimation(composition, alignment = Alignment.TopEnd, modifier = Modifier
            .fillMaxWidth()
            .clip(
                RectangleShape
            ))
    }
}
