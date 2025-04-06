package com.example.lab5

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var currentSensor: Sensor? = null

    private lateinit var spinnerSensorType: Spinner
    private lateinit var cardSensorData: MaterialCardView
    private lateinit var tvSensorName: TextView
    private lateinit var tvValue1: TextView
    private lateinit var tvValue2: TextView
    private lateinit var tvValue3: TextView
    private lateinit var tvLabelValue1: TextView
    private lateinit var tvLabelValue2: TextView
    private lateinit var tvLabelValue3: TextView
    private lateinit var tvSensorInfo: TextView

    private val sensorTypes = listOf(
        Sensor.TYPE_ACCELEROMETER,
        Sensor.TYPE_GYROSCOPE,
        Sensor.TYPE_MAGNETIC_FIELD,
        Sensor.TYPE_LIGHT,
        Sensor.TYPE_PROXIMITY,
        Sensor.TYPE_PRESSURE,
        Sensor.TYPE_AMBIENT_TEMPERATURE,
        Sensor.TYPE_RELATIVE_HUMIDITY,
        Sensor.TYPE_GRAVITY
    )

    private val sensorNames = mapOf(
        Sensor.TYPE_ACCELEROMETER to "Акселерометр",
        Sensor.TYPE_GYROSCOPE to "Гіроскоп",
        Sensor.TYPE_MAGNETIC_FIELD to "Магнітометр",
        Sensor.TYPE_LIGHT to "Датчик освітлення",
        Sensor.TYPE_PROXIMITY to "Датчик наближення",
        Sensor.TYPE_PRESSURE to "Барометр",
        Sensor.TYPE_AMBIENT_TEMPERATURE to "Термометр",
        Sensor.TYPE_RELATIVE_HUMIDITY to "Вологість",
        Sensor.TYPE_GRAVITY to "Гравітація"
    )

    private val valueLabels = mapOf(
        Sensor.TYPE_ACCELEROMETER to Triple("X (m/s²)", "Y (m/s²)", "Z (m/s²)"),
        Sensor.TYPE_GYROSCOPE to Triple("X (rad/s)", "Y (rad/s)", "Z (rad/s)"),
        Sensor.TYPE_MAGNETIC_FIELD to Triple("X (μT)", "Y (μT)", "Z (μT)"),
        Sensor.TYPE_LIGHT to Triple("Освітлення (lx)", "", ""),
        Sensor.TYPE_PROXIMITY to Triple("Відстань (см)", "", ""),
        Sensor.TYPE_PRESSURE to Triple("Тиск (hPa)", "", ""),
        Sensor.TYPE_AMBIENT_TEMPERATURE to Triple("Температура (°C)", "", ""),
        Sensor.TYPE_RELATIVE_HUMIDITY to Triple("Вологість (%)", "", ""),
        Sensor.TYPE_GRAVITY to Triple("X (m/s²)", "Y (m/s²)", "Z (m/s²)")
    )

    private val availableSensors = mutableListOf<Int>()
    private val availableSensorNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerSensorType = findViewById(R.id.spinner_sensor_type)
        cardSensorData = findViewById(R.id.card_sensor_data)
        tvSensorName = findViewById(R.id.tv_sensor_name)
        tvValue1 = findViewById(R.id.tv_value_1)
        tvValue2 = findViewById(R.id.tv_value_2)
        tvValue3 = findViewById(R.id.tv_value_3)
        tvLabelValue1 = findViewById(R.id.tv_label_value_1)
        tvLabelValue2 = findViewById(R.id.tv_label_value_2)
        tvLabelValue3 = findViewById(R.id.tv_label_value_3)
        tvSensorInfo = findViewById(R.id.tv_sensor_info)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        loadAvailableSensors()

        setupSensorSpinner()
    }

    private fun loadAvailableSensors() {
        sensorTypes.forEach { sensorType ->
            val sensor = sensorManager.getDefaultSensor(sensorType)
            if (sensor != null) {
                availableSensors.add(sensorType)
                availableSensorNames.add(sensorNames[sensorType] ?: "Невідомий датчик")
            }
        }

        if (availableSensors.isEmpty()) {
            availableSensors.add(-1)
            availableSensorNames.add("Немає доступних датчиків")
            cardSensorData.visibility = View.GONE
        }
    }

    private fun setupSensorSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, availableSensorNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSensorType.adapter = adapter

        spinnerSensorType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val sensorType = availableSensors[position]
                if (sensorType != -1) {
                    selectSensor(sensorType)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        if (availableSensors.isNotEmpty() && availableSensors[0] != -1) {
            selectSensor(availableSensors[0])
        }
    }

    private fun selectSensor(sensorType: Int) {
        currentSensor?.let {
            sensorManager.unregisterListener(this, it)
        }

        currentSensor = sensorManager.getDefaultSensor(sensorType)

        tvSensorName.text = sensorNames[sensorType] ?: "Невідомий датчик"

        val labels = valueLabels[sensorType] ?: Triple("Значення 1", "Значення 2", "Значення 3")
        tvLabelValue1.text = labels.first
        tvValue1.text = "0.00"

        if (labels.second.isNotEmpty()) {
            tvLabelValue2.visibility = View.VISIBLE
            tvValue2.visibility = View.VISIBLE
            tvLabelValue2.text = labels.second
            tvValue2.text = "0.00"
        } else {
            tvLabelValue2.visibility = View.GONE
            tvValue2.visibility = View.GONE
        }

        if (labels.third.isNotEmpty()) {
            tvLabelValue3.visibility = View.VISIBLE
            tvValue3.visibility = View.VISIBLE
            tvLabelValue3.text = labels.third
            tvValue3.text = "0.00"
        } else {
            tvLabelValue3.visibility = View.GONE
            tvValue3.visibility = View.GONE
        }

        currentSensor?.let {
            tvSensorInfo.text = """
                Виробник: ${it.vendor}
                Версія: ${it.version}
                Максимальний діапазон: ${it.maximumRange}
                Роздільна здатність: ${it.resolution}
                Споживання енергії: ${it.power} mA
            """.trimIndent()
        }

        currentSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onResume() {
        super.onResume()
        currentSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_GRAVITY -> {
                // Датчики з трьома значеннями (X, Y, Z)
                tvValue1.text = String.format("%.2f", event.values[0])
                tvValue2.text = String.format("%.2f", event.values[1])
                tvValue3.text = String.format("%.2f", event.values[2])
            }
            Sensor.TYPE_LIGHT, Sensor.TYPE_PROXIMITY, Sensor.TYPE_PRESSURE,
            Sensor.TYPE_AMBIENT_TEMPERATURE, Sensor.TYPE_RELATIVE_HUMIDITY -> {
                tvValue1.text = String.format("%.2f", event.values[0])
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        val accuracyText = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "Висока"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Середня"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Низька"
            else -> "Ненадійна"
        }

        tvSensorInfo.text = """
            Виробник: ${sensor.vendor}
            Версія: ${sensor.version}
            Максимальний діапазон: ${sensor.maximumRange}
            Роздільна здатність: ${sensor.resolution}
            Споживання енергії: ${sensor.power} mA
            Точність: $accuracyText
        """.trimIndent()
    }
}