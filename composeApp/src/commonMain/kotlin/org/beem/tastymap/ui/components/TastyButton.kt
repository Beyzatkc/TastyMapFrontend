// ui/components/TastyButton.kt

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Senior Notu: Butonun stilini belirlemek için bir 'isPrimary' flag'i ekledik.
@Composable
fun TastyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true, // true ise koyu Teal, false ise Outlined
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val buttonShape = RoundedCornerShape(50.dp)

    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().height(56.dp),
            enabled = enabled && !isLoading,
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Teal
                contentColor = Color.White
            )
        ) {
            ButtonContent(text, isLoading, Color.White)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().height(56.dp),
            enabled = enabled && !isLoading,
            shape = buttonShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary), // Teal çizgi
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary // Teal yazı
            )
        ) {
            ButtonContent(text, isLoading, MaterialTheme.colorScheme.primary)
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
            style = MaterialTheme.typography.titleMedium
        )
    }
}