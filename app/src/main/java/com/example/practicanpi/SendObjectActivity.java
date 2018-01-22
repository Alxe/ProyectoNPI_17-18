
    package com.example.practicanpi;

    import android.content.Intent;
    import android.nfc.NdefMessage;
    import android.nfc.NdefRecord;
    import android.nfc.NfcAdapter;
    import android.nfc.Tag;
    import android.nfc.tech.Ndef;
    import android.nfc.tech.NdefFormatable;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.Toast;

    import java.io.ByteArrayOutputStream;
    import java.util.Locale;
    /**
     * Created by soler on 16/01/2018.
     */
    /*
    SendObjectActivity : Activity para "enviar" los objetos. Su proposito es el de "emitir" el /
    codigo del objeto para que sea leido por elemento externo

        - En el caso del QR se muestra por pantalla
        - En el caso del NFC se emite el codigo del objeto

        - Datos:
            - nfcAdapter: Adaptador NFC
            - imageRes: codigo objeto a mostrar
            - entregado: Boton para terminar la activity
            - mQRIds: Ids de las imagenes QR
     */

    public class SendObjectActivity extends NpiActivity
    {

        private NfcAdapter nfcAdapter;
        private int imageRes;
        private Button entregado;

        private Integer[] mQRIds = {
                R.drawable.blank,
                R.drawable.qr1,
                R.drawable.qr2,
                R.drawable.qr3,
                R.drawable.qr4,
                R.drawable.qr5
        };

        /*
        onCreate
            - Obtenemos en imageRes el codigo del objeto a mostrar
            - buscamos las views del botonEntregado y imageView para el QR
            - Asignamos la imagen de QR que corresponda a la imageView
            - Creamos listener de onClick para boton entregado, con nuevo intent para finalizar activity
            - Obtenemos el adaptado NFC en nfcAdapter

         */


        @Override
        protected void onCreate(Bundle savedInstanceState) 
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_entregar_objeto);

            imageRes = getIntent().getIntExtra("objeto",-1);

            entregado = findViewById(R.id.objetoEntregado);
            ImageView qr = findViewById(R.id.imageView_QR);

            qr.setImageResource(mQRIds[imageRes]);

            entregado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("objeto", imageRes);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if(nfcAdapter==null){
                Toast.makeText(this, R.string.noNFC,Toast.LENGTH_LONG).show();
            }else if(!nfcAdapter.isEnabled()){
                Toast.makeText(this, "NFC NOT Enabled!", Toast.LENGTH_LONG).show();
            }

        }

        /*
        onNewIntent se usa para emitir por nfc el codigo del objeto
            - Se crea mensaje con createTextMessage
            - Si se lee el mensaje con writeTag
                - Se finaliza la activity con intent con RESULT_OK
         */
        @Override
        protected void onNewIntent(Intent intent) {
            // Tag writing mode
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

                Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                NdefMessage message = createTextMessage(Integer.toString(imageRes));
                if (writeTag(detectedTag,message)) {
                    Intent intent2 = new Intent();
                    intent2.putExtra("objeto", imageRes);
                    setResult(RESULT_OK, intent2);
                    finish();
                    Toast.makeText(this, "Success: Wrote placeid to nfc tag", Toast.LENGTH_LONG).show();
                }
            }
        }
        /*
            createTextMessage: Para crear mensaje NFC a partir de string

         */

        public NdefMessage createTextMessage(String content) {
            try {
                // Get UTF-8 byte
                byte[] lang = Locale.getDefault().getLanguage().getBytes("UTF-8");
                byte[] text = content.getBytes("UTF-8"); // Content in UTF-8

                int langSize = lang.length;
                int textLength = text.length;

                ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + langSize + textLength);
                payload.write((byte) (langSize & 0x1F));
                payload.write(lang, 0, langSize);
                payload.write(text, 0, textLength);
                NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                        NdefRecord.RTD_TEXT, new byte[0],
                        payload.toByteArray());
                return new NdefMessage(new NdefRecord[]{record});
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /*
            writeTag: Devuelve True o False segun si se ha podido escribir el mensaje por NFC
         */
        public boolean writeTag(Tag tag, NdefMessage message) {
            if (tag != null) {
                try {
                    Ndef ndefTag = Ndef.get(tag);
                    if (ndefTag == null)  {
                        // Let's try to format the Tag in NDEF
                        NdefFormatable nForm = NdefFormatable.get(tag);
                        if (nForm != null) {
                            nForm.connect();
                            nForm.format(message);
                            nForm.close();

                            return true;
                        }
                    }
                    else {
                        ndefTag.connect();
                        ndefTag.writeNdefMessage(message);
                        ndefTag.close();
                        return true;
                    }
                }
                catch(Exception e) {
                    return false;
                }
            }
            return false;
        }
    }
