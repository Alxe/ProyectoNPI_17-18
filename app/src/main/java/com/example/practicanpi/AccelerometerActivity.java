package com.example.practicanpi;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by soler on 19/01/2018.
 */

public class AccelerometerActivity extends NpiActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private ImageView image;
    private ScrollView scroll;
    private int[] imageMax = {0,0};
    private int[] actualPosition = {0,0};
    private int[] center =  {0,0};
    private float[] initialPosition = {0,0};
    private long lastUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);

        image = findViewById(R.id.bigImage);
        scroll = findViewById(R.id.scrollVertical);


        int width = imageMax[0] = image.getBackground().getIntrinsicWidth();
        int height = imageMax[1] = image.getBackground().getIntrinsicHeight();

        Log.e("MAX",Integer.toString(width) + " " + Integer.toString(height));

        center[0] =width/2;
        center[1] =height/2;

        scroll.scrollTo(center[0],center[1]);

        lastUpdate = 0;

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);

    }
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

    @Override
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {

        synchronized (this) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //ACelerometro para la inclinacion
                if (imageMax[0] == 0) return;

                long curTime = System.currentTimeMillis();
                if ((curTime - lastUpdate) > 100) {
                    //float xmove = sensorEvent.values[0]; //
                    float ymove = sensorEvent.values[1];
                    //float zmove = sensorEvent.values[2]; //


                    Log.e("RAW", Float.toString(ymove));

                    float gforce = ymove / SensorManager.GRAVITY_EARTH;

                    if (gforce > 0.3) {
                        scroll.smoothScrollBy(0, 100);
                        Log.e("Action","Subir");
                    } else if (gforce < -0.3) {
                        scroll.smoothScrollBy(0, -100);
                        Log.e("Action","Bajar");
                    }
                    Log.e("GFORCE", Float.toString(gforce));
                    lastUpdate = curTime;
                }
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
