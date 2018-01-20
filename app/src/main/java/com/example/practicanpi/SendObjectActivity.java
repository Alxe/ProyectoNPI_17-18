<<<<<<< HEAD
    package com.example.practicanpi;

    import android.app.PendingIntent;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.nfc.NdefMessage;
    import android.nfc.NdefRecord;
    import android.nfc.Tag;
    import android.nfc.tech.Ndef;
    import android.nfc.tech.NdefFormatable;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.TextView;

    import android.nfc.NfcAdapter;
    import android.widget.Toast;

    import java.io.ByteArrayOutputStream;
    import java.util.Locale;
    import static android.util.Log.d;
    import static android.util.Log.i;
    /**
     * Created by soler on 16/01/2018.
     */

    public class SendObjectActivity extends NpiActivity{

        private NfcAdapter nfcAdapter;
        private PendingIntent mNfcPendingIntent;
        private int imageRes;
        private Integer[] mQRIds = {
                R.drawable.blank,
                R.drawable.qr1,
                R.drawable.qr2,
                R.drawable.qr3,
                R.drawable.qr4,
                R.drawable.qr5
        };
        private Integer[] mNameIds = {
                R.string.empty,
                R.string.o1,
                R.string.o2,
                R.string.o3,
                R.string.o4,
                R.string.cuadro
        };

        Button entregado;
        TextView textViewInfo;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.entregar_objeto);
            //textViewInfo = findViewById(R.id.info); //sophia
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
                    //finish();
                }
            });

            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if(nfcAdapter==null){
                Toast.makeText(this, R.string.noNFC,Toast.LENGTH_LONG).show();
                //finish();
            }else if(!nfcAdapter.isEnabled()){Toast.makeText(this, "NFC NOT Enabled!", Toast.LENGTH_LONG).show();
                //finish();
=======
package com.example.practicanpi;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import android.nfc.NfcAdapter;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * Created by soler on 16/01/2018.
 */

public class SendObjectActivity extends NpiActivity{

    private NfcAdapter nfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private int imageRes;
    private Integer[] mQRIds = {
            R.drawable.blank,
            R.drawable.qr1,
            R.drawable.qr2,
            R.drawable.qr3,
            R.drawable.qr4,
            R.drawable.qr5
    };
    private Integer[] mNameIds = {
            R.string.empty,
            R.string.o1,
            R.string.o2,
            R.string.o3,
            R.string.o4,
            R.string.cuadro
    };

    Button entregado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
>>>>>>> 63f550ebc3a783d7bd48fbe1b95add7cb6cb8319
            }

            /*else {
                mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                IntentFilter[] mWriteTagFilters = new IntentFilter[]{tagDetected};
                nfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
            }*/
        }

        @Override
        //Jorge
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
        protected void onResume() {
            super.onResume();
            Log.e("Bus card de Sophïa:", String.valueOf(1));
            Intent intent = getIntent();
            String action = intent.getAction();

            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                Toast.makeText(this, "coucou", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "onResume() - ACTION_TAG_DISCOVERED", Toast.LENGTH_SHORT).show();
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if(tag == null){
                    textViewInfo.setText("tag == null");
                }
                else{
                    String tagInfo = "";
                    byte[] tagId = tag.getId();
                    //tagInfo += "length = " + tagId.length +"\n";

                    for(int i=0; i<tagId.length; i++){
                        tagInfo += Integer.toHexString(tagId[i] & 0xFF);
                    }
                    Log.e("NFC scanned:",tagInfo);
                    //pour être sûr qu'il n'y ait pas d'espace indésirables
                    tagInfo.replaceAll(" ","");
                    //Para hacer cosas con el NFC Tag leido
                    handleNFCId(tagInfo);
                    //student card ID : 4d 88 86 29
                    //bus card Sophïa : 7d34874e
                }
            }else{
                //Toast.makeText(this, "onResume() : " + action, Toast.LENGTH_SHORT).show();
            }
        }*/

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
                    //e.printStackTrace();
                }
            }
            return false;
        }
    }
