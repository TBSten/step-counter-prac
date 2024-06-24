package me.tbsten.prac.foregroundservice

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val STEP_COUNTER = longPreferencesKey("step_counter")
private val LAST_START_AT = longPreferencesKey("step_counter_last_start_at")
private val LAST_UPDATE_AT = longPreferencesKey("step_counter_last_update_at")
private val LAST_FINISH_AT = longPreferencesKey("step_counter_last_finish_at")

val Context.stepCounterFlow
    get() = dataStore.data
        .map { preferences ->
            preferences[STEP_COUNTER]
        }

suspend fun updateStep(context: Context, step: Long) {
    context.dataStore.edit { settings ->
        settings[STEP_COUNTER] = step
        settings[LAST_UPDATE_AT] = System.currentTimeMillis()
    }
}

class SensorStepCounter(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
) : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        val stepsSinceLastReboot = event.values[0].toLong()
        coroutineScope.launch {
            updateStep(context, stepsSinceLastReboot)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private val sensorManager by lazy { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val sensor: Sensor? by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) }
    fun start() {
        sensorManager.registerListener(
            this,
            sensor,
            SensorManager.SENSOR_DELAY_UI,
        )
        sensorManager.requestTriggerSensor(object : TriggerEventListener() {
            override fun onTrigger(event: TriggerEvent?) {}
        }, sensor)
        coroutineScope.launch {
            context.dataStore.edit { settings ->
                settings[LAST_START_AT] = System.currentTimeMillis()
            }
        }
    }

    fun finish() {
        sensorManager.unregisterListener(this)
        coroutineScope.launch {
            context.dataStore.edit { settings ->
                settings[LAST_FINISH_AT] = System.currentTimeMillis()
            }
        }
    }
}
