package com.example.practicanpi;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * Created by soler on 19/01/2018.
 */

/*
AccelerometerActivity: Esta activity se encarga del manejo del acelerometro.

    - El acelerometro se usa para hacer scroll de un scrollView segun la inclinación del dispositivo.
    - Si inclinamos de forma que la parte inferior del dispositivo queda por debajo de la superior superando un limite el scroll bajara.
    - Si inclinamos al contrario el scroll subira, si el dispositivo se encuentra paralelo al suelo dentro de unos limites el scroll se mantendra en la posicion.

 */

public class AccelerometerActivity extends NpiActivity implements SensorEventListener{

    private SensorManager sensorManager; //SensorManager para el control de acelerometro
    private ScrollView scroll; //Scroll
    private long lastUpdate; //Para llevar control del tiempo en la actualización del scroll

    /*
    onCreate
        - Asiganamos contenView : activity_gyroscope
        - Registramos el sensorManager para el acelerometro
        - Buscamos el scroll con id: scrollVertical
        - Hacemos lastUpdate = 0
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);

        scroll = findViewById(R.id.scrollVertical);


        lastUpdate = 0;

    }

    /*
    onResume
        - Registramos sensorManager con el acelerometro
     */
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);

    }
    /*
    onPause
    onDestroy
        - Dejamos de leer el acelerometro
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    /*
    onSensorChanged : Se llama cuando cambian los valores del sensor , en este caso acelerometro.
        - Sincronizamos
        - Comprobar qe el evento es del tipo acelerometro
        - Comprobamos que ha pasado tiempo suficiente desde el ultimo cambio
        - Comprobamos la inclinacion y determinamos el movimiento segun esta
        - Actualizamos tiempo de ultimo cambio
     */
    @Override
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {

        synchronized (this) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //ACelerometro para la inclinacion

                long curTime = System.currentTimeMillis();
                if ((curTime - lastUpdate) > 100) {
                    //float xmove = sensorEvent.values[0]; //
                    float ymove = sensorEvent.values[1]; // Aqui encontramos los valores que nos son utiles
                    //float zmove = sensorEvent.values[2]; //


                    Log.e("RAW", Float.toString(ymove));

                    float gforce = ymove / SensorManager.GRAVITY_EARTH;

                    if (gforce > 0.3) { //Inclinacion hacia arriba
                        scroll.scrollBy(0, 750);
                        Log.e("Action","Subir");
                        //Toast.makeText(this, R.string.acelerometroSubir, Toast.LENGTH_SHORT).show();


                    } else if (gforce < -0.3) { //Inclinacion hacia abajo
                        scroll.scrollBy(0, -750);
                        Log.e("Action","Bajar");
                        //Toast.makeText(this, R.string.acelerometroBajar, Toast.LENGTH_SHORT).show();

                    }
                    Log.e("GFORCE", Float.toString(gforce));
                    lastUpdate = curTime;
                }
            }
        }


    }

    /*
        onAccuracyChanged: Sin uso.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
