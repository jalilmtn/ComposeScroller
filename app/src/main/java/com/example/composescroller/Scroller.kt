package com.example.composescroller

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

private val BUBBLE_SIZE = 48.dp
private val BUBBLE_PADDING_BOTTOM = 48.dp

@OptIn(ExperimentalTime::class)
@Composable
fun Scroller(
    modifier: Modifier,
    progress: Float,
    txt: String,
    onDrag: (relativeDragYOffset: Float, maxHeight: Float) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val bubblePaddingBottomInPx =
        with(LocalDensity.current) { BUBBLE_PADDING_BOTTOM.toPx() }

    var scrollDragYOffset by remember {
        mutableStateOf(0f)
    }

    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(0f) }
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.CenterEnd) {
        val boxHeight = constraints.maxHeight.toFloat() - bubblePaddingBottomInPx

        if (!isDragging)
            scrollDragYOffset =
                (boxHeight) * progress
        Box(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            isDragging = true
                            onDrag(
                                it.y,
                                boxHeight
                            )
                        },
                        onDragEnd = {
                            scope.launch {
                                delay(2.seconds)
                                isDragging = false
                            }
                        }
                    ) { change, _ ->
                        isDragging = true
                        dragPosition = change.position.y
                        scrollDragYOffset = dragPosition
                        onDrag(
                            change.position.y,
                            boxHeight
                        )
                    }
                },
        )

        ScrollingBubble(
            modifier = Modifier
                .align(Alignment.TopEnd),
            progress = progress,
            bubbleOffsetYFloat = scrollDragYOffset,
            isDragging = isDragging,
            txt = txt,
            maxYOffset = boxHeight
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ScrollingBubble(
    modifier: Modifier,
    bubbleOffsetYFloat: Float,
    txt: String,
    progress: Float,
    maxYOffset: Float,
    isDragging: Boolean,
) {
    var bubbleVisibility by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = progress) {
        bubbleVisibility = true
        delay(3.seconds)
        bubbleVisibility = false
    }
    Box(
        modifier = modifier
            .offset(
                y = with(LocalDensity.current) {
                    if (bubbleOffsetYFloat < 0) 0.dp else if (bubbleOffsetYFloat > maxYOffset) maxYOffset.toDp() else bubbleOffsetYFloat.toDp()
                },
            ), content = {
            AnimatedVisibility(visible = bubbleVisibility) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(visible = isDragging && txt.isNotEmpty()) {
                        Text(
                            text = txt,
                            modifier = Modifier
                                .shadow(
                                    elevation = 2.dp,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }


                    Image(
                        painter = painterResource(id = R.drawable.ic_scroll),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(
                                    bottomStart = 50.dp,
                                    topStart = 50.dp
                                )
                            )
                            .background(
                                shape = RoundedCornerShape(
                                    bottomStart = 50.dp,
                                    topStart = 50.dp
                                ),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            .size(BUBBLE_SIZE)
                            .padding(start = 8.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                        contentDescription = null
                    )
                }
            }
        }
    )
}
