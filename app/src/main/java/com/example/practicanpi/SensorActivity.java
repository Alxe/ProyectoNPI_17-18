package com.example.practicanpi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

import static android.util.Log.d;
import static android.util.Log.i;

public class SensorActivity extends NpiActivity  implements SensorEventListener {


    private static final String TAG = "SensorActivity";

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int BARCODE_READER_REQUEST_CODE = 2;

    private SensorManager sensorManager;
    private Button botonQr;

    //QR
    private String token = "";

    //My changes

    //NFC
    //ajout solucion internet
    private long lastUpdate = -1;
    //private float x,y,z;

    private NfcAdapter nfcAdapter;
    //TextView textViewInfo;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 800;
    //

    //Audio
    MediaPlayer mp;
    //SLIDE
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] TOY= {R.raw.i1,R.raw.i2,R.raw.i3,R.raw.i4};
    private ArrayList<Integer> TOYNArray = new ArrayList<Integer>();
    //

    DecimalFormat dosdecimales = new DecimalFormat("###.###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        i("info","Empezamos bien");
        //prox = (TextView) findViewById(R.id.proximidad);
        //acel = (TextView) findViewById(R.id.acel);
        //detecta = (TextView) findViewById(R.id.detecta);
        //qr = (TextView) findViewById(R.id.qr);
        //
        //
        botonQr = findViewById(R.id.activarQR);
        mPager = findViewById(R.id.pager);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        this.iniciarSensores();

        botonQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    iniciarRecoQR();
            }
        });
        //NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null){
            Toast.makeText(this,"NFC NOT supported on this device!",Toast.LENGTH_LONG).show();
            //finish();
        }
        //
        init();
    }

    protected void iniciarRecoQR(){
        iniciarQR();
    }

    protected void iniciarSensores(){
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener((SensorEventListener) this, sensorManager.getDefaultSensor(Sensor.T),SensorManager.SENSOR_DELAY_NORMAL);

    }

    protected void iniciarQR(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            Intent intent = new Intent(this, ScannerUtility.class);
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        }
    }

    protected void reproduce(int resource){
        Log.e("audio","Reproduciendo audio");
        mp=MediaPlayer.create(this, resource);
        mp.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String txt = "\n\nSensor: ";
        synchronized (this) {
            d("sensor", event.sensor.getName());

            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:

                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];


                    long curTime = System.currentTimeMillis();
                    if ((curTime - lastUpdate) > 500) {
                        long diffTime = (curTime - lastUpdate);
                        lastUpdate = curTime;

                        //c'est quoi values ? Où l'initialiser?
                       // x = values[SensorManager.DATA_X];
                       // y = values[SensorManager.DATA_Y];
                        // z = values[SensorManager.DATA_Z];

                        if (Round(x, 4) > 10.0000) {
                            Log.d("sensor", " X Right axis: " + x);
                            moveTo(currentPage-1);
                            //mPager.setCurrentItem(currentPage, true);
                            //Toast.makeText(this, "Right shake detected", Toast.LENGTH_SHORT).show();

                        } else if (Round(x, 4) < -10.0000) {
                            Log.d("sensor", "X Left axis: " + x);
                            moveTo(currentPage+1);

                            //mPager.setCurrentItem(currentPage, true);
                           // Toast.makeText(this, "Left shake detected", Toast.LENGTH_SHORT).show();
                        }
                        float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                        if (speed > SHAKE_THRESHOLD) {
                            last_x = x;
                            last_y = y;
                            last_z = z;
                        }

                    }

                    break;
                case Sensor.TYPE_PROXIMITY:
                    if (event.values[0] == 0) { //Cerca

                        if(mp!=null){
                            mp.release();
                            Toast.makeText(getApplicationContext(), "Parando audio!", Toast.LENGTH_SHORT).show();

                            mp = null;
                        }

                    }
                    break;

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_READER_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                String result=data.getStringExtra("result");
                Log.e("qr scanned:",result);
                int res = Integer.parseInt(result);

                moveTo(res);
                Toast.makeText(getApplicationContext(),"Personaje " + result, Toast.LENGTH_SHORT).show();
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Algo fue mal leyendo el QR!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter!=null) {
            Intent intent = getIntent();
            String action = intent.getAction();

            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                Toast.makeText(this,
                        "onResume() - ACTION_TAG_DISCOVERED",
                        Toast.LENGTH_SHORT).show();

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (tag == null) {
                    //textViewInfo.setText("tag == null");
                } else {
                    String tagInfo = tag.toString() + "\n";

                    tagInfo += "\nTag Id: \n";
                    byte[] tagId = tag.getId();
                    tagInfo += "length = " + tagId.length + "\n";
                    for (int i = 0; i < tagId.length; i++) {
                        tagInfo += Integer.toHexString(tagId[i] & 0xFF) + " ";
                    }
                    tagInfo += "\n";

                    String[] techList = tag.getTechList();
                    tagInfo += "\nTech List\n";
                    tagInfo += "length = " + techList.length + "\n";
                    for (int i = 0; i < techList.length; i++) {
                        tagInfo += techList[i] + "\n ";
                    }

                    //textViewInfo.setText(tagInfo);
                }
            } else {
                Toast.makeText(this,
                        "onResume() : " + action,
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
    public static float Round(float Rval, int Rpl) {
        float p = (float)Math.pow(10,Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return (float)tmp/p;
    }

    private void init() {
        for(int i=0;i<TOY.length;i++)
            TOYNArray.add(TOY[i]);

        mPager.setAdapter(new SensorPageAdapter(SensorActivity.this,TOYNArray));
        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        mPager.setCurrentItem(currentPage, true);
        //mPager.setCurrentItem(currentPage++, true);

    }

    private void moveTo(int pos){
        if((pos != currentPage) && pos < TOYNArray.size() && pos > -1 ){
            if(mp!=null) {
                mp.release();
                mp = null;
            }
            currentPage = pos;
            mPager.setCurrentItem(currentPage, true);
            int aud = R.raw.a1 + pos;
            reproduce(aud);
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if(mp!=null) {
            mp.release();
            mp = null;
        }


    }

}

