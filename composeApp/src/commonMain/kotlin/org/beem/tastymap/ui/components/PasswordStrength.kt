package org.beem.tastymap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.auth.common.PasswordStrength
import org.beem.tastymap.ui.theme.CustomColors

@Composable
fun PasswordStrengthIndicator(
    passwordStrength: PasswordStrength,
    colors: CustomColors
) {
    val checks = listOf(
        "8+ karakter" to passwordStrength.hasMinLength,
        "Büyük harf" to passwordStrength.hasUppercase,
        "Rakam" to passwordStrength.hasDigit,
        "Özel karakter" to passwordStrength.hasSpecialChar
    )

    val score = checks.count { it.second }

    val (strengthText, color) = when (score) {
        0, 1 -> "Zayıf" to colors.red
        2, 3 -> "Orta" to colors.yellow
        else -> "Güçlü" to colors.green
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Şifre",
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = strengthText,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        LinearProgressIndicator(
            progress = { score / 4f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(50)),
            color = color
        )

        Spacer(modifier = Modifier.height(4.dp))

        checks.forEach { (text, passed) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 1.dp)
            ) {

                Icon(
                    imageVector = if (passed)
                        Icons.Default.CheckCircle
                    else
                        Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (passed) colors.green else Color.Gray,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (passed)
                        MaterialTheme.colorScheme.onSurface
                    else
                        Color.Gray
                )
            }
        }
    }
}