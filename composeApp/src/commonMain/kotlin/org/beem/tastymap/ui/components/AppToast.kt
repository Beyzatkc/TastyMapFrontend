package org.beem.tastymap.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.core.util.ToastManager

@Composable
fun AppToast() {
    val event by ToastManager.toastEvents.collectAsState(null)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            visible = event != null,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            event?.let { toast ->
                Surface(
                    color = Color.Gray.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(horizontal = 48.dp)
                        .wrapContentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = toast.message,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}



















