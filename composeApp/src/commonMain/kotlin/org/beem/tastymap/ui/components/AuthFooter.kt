package org.beem.tastymap.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.theme.LocalCustomColors

@Composable
fun AuthFooter(
    modifier: Modifier = Modifier
) {
    val colors = LocalCustomColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(15.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = colors.borderLight,
        )

        Spacer(Modifier.height(15.dp))

        Text(
            text = "© 2026 TastyMap",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gizlilik",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.clickable {
                    /* Gizlilik tıklandığında ne olacağını buraya yaz */
                }
            )
            Text(
                text = "Kullanım Koşulları",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.clickable {
                    /* Kullanım Koşulları tıklandığında ne olacağını buraya yaz */
                }
            )
            Text(
                text = "İletişim",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.clickable {
                    /* İletişim tıklandığında ne olacağını buraya yaz */
                }
            )
            Text(
                text = "Yardım",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.clickable {
                    /* Yardım tıklandığında ne olacağını buraya yaz */
                }
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}