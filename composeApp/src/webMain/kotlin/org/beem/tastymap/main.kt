import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import org.beem.tastymap.App
import org.beem.tastymap.core.di.appModule
import org.beem.tastymap.data.model.LocationData
import org.beem.tastymap.di.webModule
import org.beem.tastymap.map.TastyMapComponent
import org.beem.tastymap.map.TastyMapState
import org.koin.core.context.GlobalContext.startKoin
import org.w3c.dom.HTMLElement


@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(appModule, webModule)
    }

    ComposeViewport(viewportContainerId = "compose-root") {
       App()
    }
}