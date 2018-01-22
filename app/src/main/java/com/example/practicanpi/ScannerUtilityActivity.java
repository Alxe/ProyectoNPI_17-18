package com.example.practicanpi;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by soler on 21/12/2017.
 */

/*
    ScannerUtilityActivity : Esta activity se encarga de leer los codigos tanto QR como NFC.
        - Para leer codigos QR se ha hecho uso de la libreria: https://github.com/dm77/barcodescanner /
          Que nos proporciona los metodos y layouts necesarios.

        - Para los codigos NFC usamos ejemplo encontrado en https://android.jlelse.eu/create-a-nfc-reader-application-for-android-74cf24f38a6f

        - Los dos recursos usados, QR y NFC, han sido adaptados y fusinados para nuestro proposito.

        Datos:
            - RESULT_NFC = 5 : Codigo para identificar que ha sido leido un NFC este codigo se usa /
            en sensorActivity para tratar los datos de manera diferente a un QR, que devuelve RESULT_OK
            - mScannerView: View del lector de QR
            - mNfcAdapter: Adaptador NFC
            - mPendingIntent : Intent para NFC
 */

public class ScannerUtilityActivity extends Activity implements ZBarScannerView.ResultHandler {
    private static final int RESULT_NFC = 5;
    private ZBarScannerView mScannerView;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;

    /*
        onCreate
            - En mNfcAdapter recogemos el adaptador NFC y comprobamos que no sea nulo
            - Creamos un PendingIntent en mPendingIntent para el NFC
            - Intentamos Leer NFC con handleItent
     */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);


        //NFC

        if(mNfcAdapter==null){
            Toast.makeText(this,R.string.noNFC,Toast.LENGTH_LONG).show();
            //finish();
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        try {
            handleIntent(getIntent());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /*
       onResume
           - habilitamos foreground del adaptador nfc
           - .. Codigo de la libreria QR
     */
    @Override
    public void onResume() {
        super.onResume();
        if(mNfcAdapter!=null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        mScannerView.setResultHandler(this); // Registrar esta clase como manejador de los result
        mScannerView.startCamera();          // Lanzar camara
    }

    /*
       onPause
           - deshabilitamos foreground del adaptador nfc
           - .. Codigo de la libreria QR
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
        mScannerView.stopCamera();           // Paramos camara
    }
    /*
       handleResult : Usado cuando detectamos un QR
           - Creamos un nuevo intent con extra de result con el codigo obtenido
           - Asignamos el result RESULT_OK y finalizamos
     */

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",rawResult.getContents());
        setResult(RESULT_OK, returnIntent);
        finish();
        Log.v("Scan", rawResult.getContents()); // Prints scan results
        Log.v("Scan", rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }

    /*
      onNewIntent : Para manejar nuevos intent de esta activity
          - Llamamos a handleIntent
    */
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        try {
            handleIntent(intent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    /*
          handleIntent : Manejador de los intent
              - comprobamos la action del intent: si es igual que NfcAdapter.ACTION_TAG_DISCOVERED continuamos
              - Obtenemos los datos leidos por el nfc
              - Decodificamos el mensaje
              - Si se decodifico se crea nuevo intent se asignan los resultados en result y se finaliza la activity
    */
    private void handleIntent(Intent intent) throws UnsupportedEncodingException {
        String action = intent.getAction();
        if (action == null){
            Log.e("Action", "empty");

        }else{
            Log.e("Action", action);
        }
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {

            String type = intent.getType(); //Tipo de etiqueta
            if (type==null){
                Log.e("TYPE_TAG:","Empty");
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if(rawMsgs == null) {
                    NdefMessage[] msgs;
                    byte[] empty = new byte[0];
                    byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                    Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                    byte[] payload = dumpTagData(tag).getBytes(); //Limpiar datos leidos del tag

                    NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                    NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                    msgs = new NdefMessage[] {msg};

                    Log.e("NFC_ID:", id.toString());
                    //Intentamos decodificar los datos leidos
                    String text;
                    try {

                        String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                        int languageCodeLength = payload[0] & 0077;
                        String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                        text = new String(payload, languageCodeLength + 1,
                                payload.length - languageCodeLength - 1, textEncoding);
                    }catch (UnsupportedEncodingException e){
                        throw new IllegalArgumentException(e);
                    }

                    Log.e("TEXTO:",text + " "); //En text nos queda la cadena de texto leida

                    //Terminamos activity
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",text);
                    setResult(RESULT_NFC, returnIntent);
                    finish();
                    //handleNFCId(id.toString()); RETURN ID.TOSTRING

                }
            }else{
                Log.e("TYPE_TAG:",type);

            }
        }
    }
    /*
         toHex : convertir a Hexadecimal
             - Funcion para convertir datos a hexadecimal usado por dumpTagData
   */
    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /*
         dumpTagData : Obtiene los datos en hexadecimal del TAG
             - Los devuelve en formato String
   */
    private String dumpTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("ID (hex): ").append(toHex(id));
        //sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n');
        //sb.append("ID (dec): ").append(toDec(id)).append('\n');
        //sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n');

        return sb.toString();
    }

}
