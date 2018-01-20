package com.example.practicanpi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;
import static android.util.Log.i;

public class SensorActivity extends NpiActivity  implements SensorEventListener {


    private static final String TAG = "SensorActivity";

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int BARCODE_READER_REQUEST_CODE = 2;
    private static final int SEND_OBJECT_RESULT = 3;

    private SensorManager sensorManager;
    private Button botonQr;
    private GridView gridview;
    private List<Integer> encontradosList;
    //QR
    private String token = "";

    //My changes

    //NFC
    //just a comment
    private long lastUpdate = -1;
    //private float x,y,z;

    private NfcAdapter nfcAdapter;
    //TextView textViewInfo;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 800;
    //

    private  ImageAdapter adapter;
    //Audio
    private ImageButton buttonPlayStop;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Handler handler;
    private AudioManager audioManager;
    private boolean statusProximity;

    //


    private Integer[] mThumbIds = {
            R.drawable.blank,
            R.drawable.o1,
            R.drawable.o2,
            R.drawable.o3,
            R.drawable.o4,
            R.drawable.cuadro
    };
    private Integer[] mNameIds = {
            R.string.empty,
            R.string.o1,
            R.string.o2,
            R.string.o3,
            R.string.o4,
            R.string.cuadro
    };

    private Integer[] mAudIds = {
            R.raw.a1,
            R.raw.a2,
            R.raw.a3,
            R.raw.a4,
            R.raw.a5
    };

    DecimalFormat dosdecimales = new DecimalFormat("###.###");

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        i("info","Empezamos bien");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        botonQr = findViewById(R.id.activarQR);
        gridview = (GridView) findViewById(R.id.gridview);
        encontradosList = new ArrayList<Integer>();
        adapter = (ImageAdapter) new ImageAdapter(this, encontradosList);
        gridview.setAdapter(adapter);


        //Audio
        buttonPlayStop = findViewById(R.id.playpauseButton);
        seekBar = findViewById(R.id.seekBar);

        buttonPlayStop.setEnabled(false);
        seekBar.setEnabled(false);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        handler = new Handler();


        this.iniciarSensores();

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getBaseContext(), SendObjectActivity.class);
                if(encontradosList.size()>position){
                    intent.putExtra("objeto",encontradosList.get(position));
                    startActivityForResult(intent,SEND_OBJECT_RESULT);
                }else{
                    Toast.makeText(SensorActivity.this, R.string.pulsasobreobjeto ,Toast.LENGTH_LONG).show();

                }
            }
        });


        botonQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    iniciarRecoQR();
            }
        });

        //AUDIO
        buttonPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer!= null) {
                    if (mediaPlayer.isPlaying()) {
                        pause();
                    } else {
                       play();
                    }
                }
            }
        });

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
            seekChange(v);
            return false; }
        });

        //NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null){
            Toast.makeText(this,R.string.noNFC,Toast.LENGTH_LONG).show();
        }

    }

    protected void iniciarRecoQR(){
        iniciarQR();
    }

    protected void iniciarSensores(){
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
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

                        if (Round(x, 4) > 10.0000) {
                            Log.d("sensor", " X Right axis: " + x);
                            //DER

                        } else if (Round(x, 4) < -10.0000) {
                            Log.d("sensor", "X Left axis: " + x);
                           //IZQ
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
                            statusProximity = false;
                        }else {
                            statusProximity = true;
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
                int res;
                try
                {
                    res = Integer.parseInt(result);
                }
                catch (NumberFormatException nfe)
                {
                    res = -1;
                }
                añadirObjeto(res);

            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), com.example.practicanpi.R.string.qrfallido, Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == SEND_OBJECT_RESULT){
            if(resultCode == RESULT_OK){
                int objeto = data.getIntExtra("objeto",-1);
                if(objeto != -1){
                    encontradosList.remove(encontradosList.indexOf(objeto));
                    adapter.update(encontradosList);
                    Toast.makeText(getApplicationContext(), R.string.objetoentregado, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        i("info"," entering on resume");
        if (nfcAdapter!=null) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                i("info"," NFC tag discovered");
                //Toast.makeText(this,"onResume() - ACTION_TAG_DISCOVERED",Toast.LENGTH_SHORT).show();
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (tag == null) {
                    i("info_null","NFC Tag null");
                } else {
                    String tagInfo = "";
                    byte[] tagId = tag.getId();
                    for (int i = 0; i < tagId.length; i++) {
                        tagInfo += Integer.toHexString(tagId[i] & 0xFF);
                    }

                    tagInfo.replaceAll(" ",""); //no blanks
                    Log.e(TAG, "NFC scanned " + tagInfo );
                    //associate int to NFC Tag then add object to the list
                    handleNFCId(tagInfo);
                }
            } else {
                Toast.makeText(this,
                        "onResume() : " + action,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void handleNFCId(String myTag){
        //This function associates an 'int' to an 'NFC tag'
        int res = -1;
        if(myTag.equals( "4d888629")){
            Log.e("Student card:",myTag);
            res =1; //Lanza
            //Toast.makeText(this, "Estas en la primera sala, bienvenido", Toast.LENGTH_SHORT).show();
        }
        if(myTag.equals( "7d34874e")){
            Log.e("Bus card de Sophïa:",myTag);
            res = 2; //jarron
            //Toast.makeText(this, "Estas en la segunda sala, bienvenido", Toast.LENGTH_SHORT).show();
        }
        //add objet to list
        añadirObjeto(res);
    }
    public static float Round(float Rval, int Rpl) {
        float p = (float)Math.pow(10,Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return (float)tmp/p;
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void añadirObjeto(int res){
        if(res <= mNameIds.length && res > 0 ){
            if (!encontradosList.contains(res)) {
                encontradosList.add(res);
                adapter.update(encontradosList);
                //RES = CODIGO QR LEIDO
                //moveTo(res);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Hasencontrado) + getResources().getString(mNameIds[res]), Toast.LENGTH_SHORT).show();
                            }else {
                Toast.makeText(getApplicationContext(), getString(R.string.yatienes) + getResources().getString(mNameIds[res]), Toast.LENGTH_SHORT).show();
            }
            loadAudio(mAudIds[res-1]);
            play();
        }else{
            Toast.makeText(getApplicationContext(),com.example.practicanpi.R.string.valornovalido, Toast.LENGTH_SHORT).show();
        }
    }

    private void play(){
        if(mediaPlayer!=null){
            if(!mediaPlayer.isPlaying()){
                buttonPlayStop.setBackgroundResource(android.R.drawable.ic_media_pause);
                mediaPlayer.start();
                seekBar.setEnabled(true);
                startPlayProgressUpdater();
            }
        }
    }

    private void pause(){
        buttonPlayStop.setBackgroundResource(android.R.drawable.ic_media_play);
        if (mediaPlayer!=null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        }

    }
    private void loadAudio(int resource){
        mediaPlayer = MediaPlayer.create(getBaseContext(),resource);
        seekBar.setMax(mediaPlayer.getDuration());
        audioManager.setMode(AudioManager.STREAM_MUSIC);
        //play();
    }

    private void seekChange(View v){
        if(mediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mediaPlayer.seekTo(sb.getProgress());
        }
    }

    public void startPlayProgressUpdater() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        audioManager.setSpeakerphoneOn(statusProximity);
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,100);
        }else{
            pause();
            buttonPlayStop.setEnabled(false);
            seekBar.setProgress(0);
            seekBar.setEnabled(false);
        }
    }





}



