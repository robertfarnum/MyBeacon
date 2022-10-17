package com.example.mybeacon.vm

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.mybeacon.model.FinderState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.altbeacon.beacon.*
import java.util.*

class FinderViewModel(context: Context) : TextToSpeech.OnInitListener {
    // the list of regions matching the region definitions with a specific set of UUIDs which identify a region
    private val regions: List<Region> = listOf(
        Region("Cafeteria", Identifier.parse("e2c56db5-dffb-48d2-b060-d0f5a71096e0"), null, null)
    )

    // The BeaconManager
    private val beaconManager = BeaconManager.getInstanceForApplication(context)

    // The current state of region finding
    val currentState = MutableStateFlow(FinderState())

    // The TextToSpeech translator
    val tts: TextToSpeech

    // Beacan Monitor Notifier
    private var monitor: MonitorNotifier? = null

    /**
     * Get the Region
     */
    private val region: Region?
        get() = currentState.value.region

    init {
        // TextToSpeech(Context: this, OnInitListener: this)
        tts = TextToSpeech(context, this)

        BeaconManager.setDebug(true)

        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
        // find a different type of beacon like Eddystone or iBeacon, you must specify the byte layout
        // for that beacon's advertisement with a line like below.
        //
        // If you don't care about AltBeacon, you can clear it from the defaults:
        beaconManager.beaconParsers.clear()

        // The example shows how to find iBeacon.
        beaconManager.beaconParsers.add(
            BeaconParser().
            setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))

        monitor = object: MonitorNotifier {
            override fun didEnterRegion(region: Region?) {
//                currentState.update {
//                    currentState.value.copy(state = 1, region = region)
//                }
            }
            override fun didExitRegion(region: Region?) {
//                currentState.update {
//                    currentState.value.copy(state = 0, region = region)
//                }
            }
            override fun didDetermineStateForRegion(state: Int, region: Region?) {
                currentState.update {
                    currentState.value.copy(state = state, region = region)
                }
            }
        }


    }

    fun startMonitoring() {
        monitor?.let { beaconManager.addMonitorNotifier(it) }

        // The code below will start "monitoring" for beacons matching the regions
        for (region in regions) {
            beaconManager.startMonitoring(region)
        }
    }

    fun stopMonitoring() {
        monitor?.let { beaconManager.removeMonitorNotifier(it) }

        // The code below will stop "monitoring" for beacons in all regions
        // the region definition is with a specific set of UUIDs which identify a region
        for (region in regions) {
            beaconManager.stopMonitoring(region)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            }
        }
    }

    fun speak(txt: String?) {
        txt?.let { txt ->
            tts.speak(txt, TextToSpeech.QUEUE_ADD, null, null)
        }
    }
}