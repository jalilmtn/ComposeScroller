package com.example.composescroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composescroller.ui.theme.ComposeScrollerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeScrollerTheme {
                // A surface container using the 'background' color from the theme
                val array = IntArray(1000) {
                    it
                }
                val coroutineScope = rememberCoroutineScope()
                val state = rememberLazyListState()
                val gridRowIndex by remember {
                    derivedStateOf {
                        state.firstVisibleItemIndex
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box {
                        LazyColumn(
                            state = state,
                            content = {
                                items(array.size) {
                                    Text(text = array[it].toString())
                                }
                            })
                        val progress = remember(gridRowIndex) {
                            (gridRowIndex).toFloat() / (array.size / 3)
                        }
                        Scroller(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight(),
                            progress = progress,
                            txt = array[gridRowIndex].toString(),
                            onDrag = { relativeDragYOffset, maxHeight ->
                                coroutineScope.launch {
                                    state.scrollToItem(
                                        ((if (relativeDragYOffset < 0) 0 else relativeDragYOffset.toInt()) * ((array.size / 3) / (maxHeight))).toInt()
                                    )
                                }
                            },
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeScrollerTheme {
        Greeting("Android")
    }
}