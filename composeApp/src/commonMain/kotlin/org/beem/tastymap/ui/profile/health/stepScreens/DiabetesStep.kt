package org.beem.tastymap.ui.profile.health.stepScreens

import androidx.compose.runtime.Composable
import org.beem.tastymap.ui.profile.health.HealthUiState
import org.beem.tastymap.ui.profile.health.components.BaseStepContainer
import org.beem.tastymap.ui.theme.LocalCustomColors
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.ic_diabetes_svg

@Composable
fun DiabetesStep(
    state: HealthUiState,
    onDiabetesChanged: (Boolean) -> Unit
) {
    val customColors = LocalCustomColors.current

    BaseStepContainer(
        iconResource = Res.drawable.ic_diabetes_svg,
        title = "Diyabet Durumunuz Nedir?",
        description = "Size en uygun tarifleri önerebilmemiz için bu küçük bilgiye ihtiyacımız var."
    ) {
        RadioButton(
            text = "Evet, diyabetim var",
            selected = state.hasDiabetes,
            customColors = customColors
        ) {
            onDiabetesChanged(true)
        }

        RadioButton(
            text = "Hayır, diyabetim yok",
            selected = !state.hasDiabetes,
            customColors = customColors
        ) {
            onDiabetesChanged(false)
        }
    }
}