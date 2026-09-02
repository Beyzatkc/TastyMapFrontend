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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.auth.common.PasswordStrength
import org.beem.tastymap.ui.theme.CustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.password_req_digit
import tastymap.composeapp.generated.resources.password_req_min_length
import tastymap.composeapp.generated.resources.password_req_special_char
import tastymap.composeapp.generated.resources.password_req_uppercase
import tastymap.composeapp.generated.resources.password_strength_medium
import tastymap.composeapp.generated.resources.password_strength_strong
import tastymap.composeapp.generated.resources.password_strength_weak

@Composable
fun PasswordStrengthIndicator(
    passwordStrength: PasswordStrength,
    colors: CustomColors
) {
    val checks = listOf(
        stringResource(Res.string.password_req_min_length) to passwordStrength.hasMinLength,
        stringResource(Res.string.password_req_uppercase) to passwordStrength.hasUppercase,
        stringResource(Res.string.password_req_digit) to passwordStrength.hasDigit,
        stringResource(Res.string.password_req_special_char) to passwordStrength.hasSpecialChar
    )

    val score = checks.count { it.second }

    val (strengthText, color) = when (score) {
        0, 1 -> stringResource(Res.string.password_strength_weak) to colors.red
        2, 3 -> stringResource(Res.string.password_strength_medium) to colors.yellow
        else -> stringResource(Res.string.password_strength_strong) to colors.green
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
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary
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
            color = color,
            trackColor = colors.surfaceVariant
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
                    tint = if (passed) colors.green else colors.textSecondary,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (passed) colors.textPrimary else colors.textSecondary
                )
            }
        }
    }
}