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
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;
import static android.util.Log.i;

/*
    SensorActivity: Activity para mostrar objetos encontrados y dar paso a las activitys:
        - SendObjectActivity para enviar objetos, cuando se pulse encima de uno existente
        - ScannerUtilityActivity para leer codigos QR o NFC, con su boton para llamar
        - AccelerometerActivity para dar paso a la activity que muestra funcionamiento del acelerometro

        - Datos: Comentados en el interior de la clase.
 */

public class SensorActivity extends NpiActivity  implements SensorEventListener {

    //Codigos para request de permisos y results de activitys
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int BARCODE_READER_REQUEST_CODE = 2;
    private static final int SEND_OBJECT_RESULT = 3;
    private static final int RESULT_NFC = 5;
    //
    //Sensor manager para control de Sensor de proximidad
    private SensorManager sensorManager;
    private boolean statusProximity; //status del sensor de proximidad
    //
    //IU
    private Button botonQr; //Boton para ir a activity ScannerUtility para leer codigo
    private GridView gridview; //Grid de objetos
    private SeekBar seekBar; //Barra de audio
    private Button botonGyro; //Boton para ir a activity AccelerometerActivity para gestion del acelerometro
    //

    //Lista de Objetos encontrados
    private List<Integer> encontradosList;
    //

    //Adaptador para view de cada objeto del grid
    private  ImageAdapter adapter;
    //

    //Audio
    private MediaPlayer mediaPlayer; //Mediaplayer para reproduccion de audio
    private Handler handler; //Manejador
    private AudioManager audioManager; //AudioManager
    //

    //Fotos de los objetos
    private Integer[] mThumbIds = {
            R.drawable.blank,
            R.drawable.o1,
            R.drawable.o2,
            R.drawable.o3,
            R.drawable.o4,
            R.drawable.cuadro
    };
    //
    //Nombre de los objetos
    private Integer[] mNameIds = {
            R.string.empty,
            R.string.o1,
            R.string.o2,
            R.string.o3,
            R.string.o4,
            R.string.cuadro
    };
    //
    //Audios de los objetos = -1 //Ya que no tenemos audio en blanco
    private Integer[] mAudIds = {
            R.raw.a1,
            R.raw.a2,
            R.raw.a3,
            R.raw.a4,
            R.raw.a5
    };
    //

    DecimalFormat dosdecimales = new DecimalFormat("###.###");


    /*
        onCreate
         - Obtenemos las views correspondientes por su ID
         - Creamos los listener para los botones de acelerometro y leer codigo
         - Crear Listener de pulsacion sobre objeto de la grid
         - Crear listener de cambio de la barra de audio
         - Llamamos a iniciarSensores
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        i("info","Empezamos bien");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        botonQr = findViewById(R.id.activarQR);
        botonGyro = findViewById(R.id.buttonGyroscope);
        gridview = (GridView) findViewById(R.id.gridview);
        encontradosList = new ArrayList<Integer>();
        adapter = (ImageAdapter) new ImageAdapter(this, encontradosList);
        gridview.setAdapter(adapter);


        //Audio

        seekBar = findViewById(R.id.seekBar);
        seekBar.setEnabled(false);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        handler = new Handler();

        //

        //OnitemClicklistener de objetos del grid
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getBaseContext(), SendObjectActivity.class);
                if(encontradosList.size()>position){
                    if(mediaPlayer!=null){
                        mediaPlayer.stop();
                    }
                    intent.putExtra("objeto",encontradosList.get(position));
                    startActivityForResult(intent,SEND_OBJECT_RESULT);
                }else{
                    Toast.makeText(SensorActivity.this, R.string.pulsasobreobjeto ,Toast.LENGTH_LONG).show();

                }
            }
        });
        //

        botonQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    iniciarRecoQR();
            }
        });

        botonGyro.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iniciarGyro();
                    }
                }

        );


        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
            seekChange(v);
            return false; }
        });

        this.iniciarSensores();

    }


    protected void iniciarRecoQR(){
        iniciarQR();
    }

    /*
    iniciarSensores
        - Registra el uso del sensor de proximidad
     */
    protected void iniciarSensores(){
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),SensorManager.SENSOR_DELAY_NORMAL);
    }
    /*
    iniciarGyro
         - Crea intent y llama a activity AccelerometerActivity sin esperar result
     */
    private void iniciarGyro(){
        Intent intent = new Intent(this,AccelerometerActivity.class);
        startActivity(intent);
    }

    /*
        iniciarQR
             - Comprueba permisos de uso de camara y /
             Crea intent y llama a activity ScannerUtilityActivity esperando result
         */
    protected void iniciarQR(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            Intent intent = new Intent(this, ScannerUtilityActivity.class);
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        }
    }

    /*
            onSensorChanged
                 - Detecta cambios en el sensor de proximidad
                    - Si event.values[0] == 0 significa que hay algo cerca del sensor
                        - Segun el estado de event.values[0] se pone statusProximity a False o True /
                          Esto se usa para emitir el audio por el auricular o los altavoces
             */
    @Override
    public void onSensorChanged(SensorEvent event) 
    {
        synchronized (this) 
        {
            d("sensor", event.sensor.getName());
                if(event.sensor.getType() == Sensor.TYPE_PROXIMITY)
                {
                        if (event.values[0] == 0) 
                        { //Cerca
                            statusProximity = false;
                            Toast.makeText(getApplicationContext(), R.string.earpiece, Toast.LENGTH_SHORT).show();
                        }
                    else {
                            statusProximity = true;
                        }
                }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /*
    onActivityResult : Manejar cuando finaliza una activity que lanzamos

        - Si corresponde a algun codigo de activity que hemos lanzado lo interpretamos
        - Si requestCode == BARCODE_READER_REQUEST_CODE
            - si resultCode == RESULT_OK es un QR, por lo que decodificamos el mensaje /
          y añadimos el objeto con  añadirObjeto(res);
            - si resultCode == RESULT_NFC es un nfc y llamamos a handleNFCId para analizar el mensaje
            - si resultCode == RESULT_CANCELED algo fallo leyendo el codigo

        - si requestCode == SEND_OBJECT_RESULT y resultCode == RESULT_OK: /
            Se envio el objeto correctamente lo eliminamos de la lista de objetos y actualizamos la view

     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_READER_REQUEST_CODE){
            Log.e("Request code:",Integer.toString(resultCode));

            if(resultCode == RESULT_OK){
                String result=data.getStringExtra("result");
                Log.e("code scanned:",result);
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

            }else if(resultCode == RESULT_NFC){
                handleNFCId(data.getStringExtra("result"));
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
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),SensorManager.SENSOR_DELAY_NORMAL);


    }

    /*
        handleNFCId: Interpreta los codigos NFC recibidos
         - Si el codigo recibido existe se añade objeto correspondiente
     */
    private void handleNFCId(String myTag){
        //This function associates an 'int' to an 'NFC tag'
        int res = -1;
        if(myTag.equals( "4e 87 34 7d")){
            Log.e("Bus card de Sophïa:",myTag);
            res =1; //Jarron
            //Toast.makeText(this, "Estas en la primera sala, bienvenido", Toast.LENGTH_SHORT).show();
        }
        else if(myTag.equals( "7d 34 87 4e")){
            Log.e("Student card Sophïa:",myTag);
            res = 2; //Lanza
            //Toast.makeText(this, "Estas en la segunda sala, bienvenido", Toast.LENGTH_SHORT).show();
        }else if (myTag.equals( "1e 30 05 0c 91 13 a7"))
        {
            Log.e("Credit Card Jorge:",myTag);
            res = 3;
        }
        else{
            Log.e("NFC READ",myTag);
            Toast.makeText(this,getResources().getText(R.string.valornovalido), Toast.LENGTH_SHORT).show();
        }

        //add objet to list
        añadirObjeto(res);
    }

    /*
    onDestroy , onPause
     - Se para y libera el reproductor de audio
     */
    @Override protected void onDestroy() {
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        sensorManager.unregisterListener(this);

        super.onDestroy();
    }

    @Override protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /*
        añadirObjeto : Añade objeto a la lista de encontrados y carga audio correspondiente
            - Comprobar que el objeto existe
            - añadirlo a la lista y actualizar el view
            - Cargar audio del objeto y reproducirlo

     */
    private void añadirObjeto(int res){
        if(res <= mNameIds.length && res > 0 ){
            if (!encontradosList.contains(res)) {
                encontradosList.add(res);
                adapter.update(encontradosList);
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

    /*
        play : Comenzar la reproduccion del audio
     */
    private void play(){
        if(mediaPlayer!=null){
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
                seekBar.setEnabled(true);
                startPlayProgressUpdater();
            }
        }
    }

    /*
           pause : Pausar la reproduccion del audio
        */
    private void pause(){
        if (mediaPlayer!=null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        }

    }

    /*
           loadAudio : Cargar Audio y asignar tamaño de seekBar
    */
    private void loadAudio(int resource){
        mediaPlayer = MediaPlayer.create(getBaseContext(),resource);
        seekBar.setMax(mediaPlayer.getDuration());
        audioManager.setMode(AudioManager.STREAM_MUSIC);
    }
    /*
       seekChange : Cambio de la posicion del seekbar por el usuario
        - Mover mediaPlayer hasta posicion del seekbar actual
   */
    private void seekChange(View v){
        if(mediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mediaPlayer.seekTo(sb.getProgress());
        }
    }

    /*
        startPlayProgressUpdater : Actualización del seekbar continua y comprobacion de statusProximity
            - Asignamos progreso del audio a la seekbar
            - Activar o desactivar altavoz del telefono
            - Cuando termina la reproduccion para audio y deshabilita la seekbar
     */
    public void startPlayProgressUpdater() {
        if (mediaPlayer != null) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            audioManager.setSpeakerphoneOn(statusProximity);
            if (mediaPlayer.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        startPlayProgressUpdater();
                    }
                };
                handler.postDelayed(notification, 100);
            } else {
                pause();
                seekBar.setEnabled(false);
            }
        }
    }


}



