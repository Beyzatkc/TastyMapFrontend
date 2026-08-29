import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@Composable
fun TastyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    backcolor: Color,
    textcolor: Color,
    strokecolor: Color = Color.Transparent
) {
    val buttonShape = RoundedCornerShape(20.dp)
    val focusManager = LocalFocusManager.current

    val handleOnClick = {
        focusManager.clearFocus()
        onClick()
    }

    if (isPrimary) {
        Button(
            onClick = handleOnClick,
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = enabled && !isLoading,
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = backcolor,
                contentColor = textcolor,
                disabledContainerColor = backcolor.copy(alpha = 0.4f),
                disabledContentColor = textcolor.copy(alpha = 0.4f)
            )
        ) {
            ButtonContent(text, isLoading, textcolor)
        }
    } else {
        OutlinedButton(
            onClick = handleOnClick,
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = enabled && !isLoading,
            shape = buttonShape,
            border = BorderStroke(
                width = 1.dp,
                color = if (enabled) strokecolor else strokecolor.copy(alpha = 0.4f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = backcolor,
                contentColor = textcolor,
                disabledContainerColor = backcolor.copy(alpha = 0.4f),
                disabledContentColor = textcolor.copy(alpha = 0.4f)
            )
        ) {
            ButtonContent(text, isLoading, textcolor)
        }
    }
}

@Composable
private fun ButtonContent(text: String, isLoading: Boolean, contentColor: Color) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = contentColor,
            strokeWidth = 2.dp
        )
    } else {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.titleMedium
        )
    }
}