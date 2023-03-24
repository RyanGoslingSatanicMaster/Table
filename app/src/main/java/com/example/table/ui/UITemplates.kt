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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.glance.appwidget.proto.LayoutProto
import com.airbnb.lottie.compose.*
import com.example.table.R
import com.example.table.ui.theme.*
import com.example.table.utils.Constant
import com.example.table.utils.ConverterUtils

@Stable
enum class PositionState {
    Previous, Next, Current
}

@Composable
fun progressBar(visible: Boolean) {
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
fun showDialog(
    text: String,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
    confirmText: String = Constant.YES,
    dismissText: String = Constant.NO,
) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value)
        AlertDialog(onDismissRequest = {
            onDismiss()
            openDialog.value = false
        },
            confirmButton = @Composable {
                alertButton(text = confirmText) {
                    onConfirm()
                    openDialog.value = false
                }
            },
            dismissButton = @Composable {
                alertButton(text = dismissText) {
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
fun CustomAlertDialog(startValue: Int, onSave: (Int) -> Unit, onExit: () -> Unit) {

    val hour = remember { mutableStateOf((startValue/60).toString()) }
    val minutes = remember { mutableStateOf((startValue - (startValue/60) * 60).toString()) }
    Dialog(onDismissRequest = { onExit() }, properties = DialogProperties(
        dismissOnBackPress = true, dismissOnClickOutside = true
    )) {
        Card(
            //shape = MaterialTheme.shapes.medium,
            shape = RoundedCornerShape(10.dp),
            // modifier = modifier.size(280.dp, 240.dp)
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = 8.dp
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Primary)
                        .padding(8.dp, 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,

                    ) {

                    OutlinedTextField(
                        value = hour.value,
                        onValueChange = {
                            if (it.validateInputTimeHours()) hour.value = it
                        },
                        textStyle = TextStyle(fontSize = 25.sp, color = Color.White),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxWidth(),
                        placeholder = {
                            Text(text = stringResource(R.string.choose_hours),
                                style = TextStyle(color = Hint))
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        )
                    )
                    Text(
                        text = ":",
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(2f)
                            .fillMaxWidth(),
                        color = Color.White,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center
                    )
                    OutlinedTextField(
                        value = minutes.value,
                        onValueChange = {
                            if (it.validateInputTimeMinute()) minutes.value = it
                        },
                        textStyle = TextStyle(fontSize = 25.sp, color = Color.White),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxWidth(),
                        placeholder = {
                            Text(text = stringResource(R.string.choose_minutes),
                                style = TextStyle(color = Hint))
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        )
                    )
                }

                Row(Modifier.padding(top = 10.dp)) {
                    OutlinedButton(
                        onClick = { onSave(hour.value.convertToTime() * 60 + minutes.value.convertToTime()) },
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = "Сохранить")
                    }
                }

            }
        }
    }
}

@Composable
fun alertButton(text: String, onClick: () -> Unit) {
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
    isNightMode: Boolean = false,
    innerElement: @Composable (PositionState, Boolean) -> Unit = { a, b ->
    },
) {

    val xOffset = remember { mutableStateOf(0f) }
    val yOffset = remember { mutableStateOf(0f) }

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
        val itemWidthPx = with(LocalDensity.current) {
            (maxWidth - (padding * 2)).toPx()
        }
        val itemHeightPx = with(LocalDensity.current) {
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
            isNightMode = isNightMode,
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
    isNightMode: Boolean,
    innerElement: @Composable (PositionState, Boolean) -> Unit,
) {

    // COLOR ANIMATION
    val colorState = remember {
        mutableStateOf(if (positionTop.value) colors.first else colors.second)
    }

    val color by animateColorAsState(targetValue = colorState.value,
        animationSpec = tween(duration))

    if (positionTop.value)
        colorState.value = colors.first
    else
        colorState.value = colors.second

    val colors =
        listOf(color.copy(alpha = 0.9f), color.copy(alpha = 0.6f), color.copy(alpha = 0.9f))

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

    when (positionState.value) {
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
        position = positionState,
        isNightMode = isNightMode
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
    isNightMode: Boolean,
    padding: Dp,
    innerElement: @Composable (PositionState, Boolean) -> Unit,
) {
    val brush = Brush.linearGradient(
        colors,
        start = Offset(xShimmer - gradientWidth, yShimmer - gradientWidth),
        end = Offset(xShimmer, yShimmer)
    )
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(brush)) {
        val positionState by remember { derivedStateOf { position.value } }
        val isTop by remember { derivedStateOf { positionTop.value } }
        if (isNightMode)
            starAnimation(visible = isTop)
        else
            cloudsAnimation(visible = isTop)

        wheatAnimation(visible = !isTop, Modifier)
        innerElement(positionState, isTop)
    }
}

@Composable
fun wheatAnimation(visible: Boolean, modifier: Modifier) {
    AnimatedVisibility(visible = visible,
        enter = slideInVertically(initialOffsetY = {
            it / 2
        },
            animationSpec = tween(durationMillis = 1600))
                + fadeIn(animationSpec = tween(durationMillis = 1600)),
        exit = slideOutVertically(targetOffsetY = {
            it / 2
        }, animationSpec = tween(durationMillis = 1000))
                + fadeOut(animationSpec = tween(durationMillis = 1000))
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.wheat))
        val progress by animateLottieCompositionAsState(composition = composition,
            iterations = LottieConstants.IterateForever)
        LottieAnimation(composition,
            alignment = Alignment.BottomStart,
            progress = progress,
            modifier = modifier
                .fillMaxSize()
                .clip(RectangleShape))
    }
}

@Composable
fun cloudsAnimation(visible: Boolean) {
    AnimatedVisibility(visible = visible,
        enter = slideInVertically(animationSpec = tween(durationMillis = 1600))
                + fadeIn(animationSpec = tween(durationMillis = 1600)),
        exit = slideOutVertically(animationSpec = tween(durationMillis = 1000))
                + fadeOut(animationSpec = tween(durationMillis = 1000))
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.clouds))
        val progress by animateLottieCompositionAsState(composition = composition,
            iterations = LottieConstants.IterateForever)
        LottieAnimation(composition,
            alignment = Alignment.TopEnd,
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RectangleShape
                ))
    }
}

@Composable
fun starAnimation(visible: Boolean) {
    AnimatedVisibility(visible = visible,
        enter = slideInVertically(animationSpec = tween(durationMillis = 1600))
                + fadeIn(animationSpec = tween(durationMillis = 1600)),
        exit = slideOutVertically(animationSpec = tween(durationMillis = 1000))
                + fadeOut(animationSpec = tween(durationMillis = 1000))
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.starw))
        LottieAnimation(composition, alignment = Alignment.TopCenter, modifier = Modifier
            .fillMaxWidth()
            .clip(
                RectangleShape
            ))
    }
}

fun Modifier.drawColoredShadow(
    color: Color,
    alpha: Float = 0.2f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 10.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
) = this.drawBehind {
    val transparentColor = color.copy(alpha = 0.0f).toArgb()
    val shadowColor = color.copy(alpha = alpha).toArgb()
    this.drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}


