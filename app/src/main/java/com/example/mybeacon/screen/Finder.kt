import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.mybeacon.ui.theme.MyBeaconTheme
import com.example.mybeacon.vm.FinderViewModel
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.altbeacon.beacon.MonitorNotifier

@Composable
fun finder(model: FinderViewModel) {
    val currentState = model.currentState.collectAsState()

    val state = currentState.value.state
    var txt = if (state == MonitorNotifier.INSIDE) "Entered the " else "Left the "
    val regionName = if (currentState.value.region?.uniqueId != null) currentState.value.region?.uniqueId else "No Region"

    txt += regionName

    model.speak(txt)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row {
            Button(
                onClick = {
                    model.startMonitoring()
                }
            ) {
                Text(text = "Start")
            }

            Button(
                onClick = {
                    model.stopMonitoring()
                }
            ) {
                Text(text = "Stop")
            }
        }

        if (regionName != null) {
            Text(text = regionName)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun finderPreview() {
    MyBeaconTheme {
        finder(FinderViewModel(LocalContext.current))
    }
}