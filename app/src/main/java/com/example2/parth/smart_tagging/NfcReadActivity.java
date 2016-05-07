package com.example2.parth.smart_tagging;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * Created by rnjth on 06-05-2016 successfully at 16:56
 */
public class NfcReadActivity extends Activity {

        Locale myLocale;
        NfcAdapter nfcAdapter;
        byte access_control;
        String uid_string;
        int speak;
        int encryption;
        int nfcFormat = 0;
        int nfcWrite = 0;
        ToggleButton tglReadWrite;
        EditText textTagContentRead;
        TextView textView1;
        String readText = null;
        NdefMessage msg;
        private TextToSpeech t1;
        nfcSecurity nfcTagSecurity;
        String textDecrypt;
        int i = 0;
        Intent ii;
        Object obj,obj1;
        Bundle b;
        Long children;
        Long counters,count;
        String counter;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_nfc_read);

            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
           // textTagContent = (EditText) findViewById(R.id.textTagContent);
            textView1 = (TextView) findViewById(R.id.textView1);
            setLocale("en");
            access_control = 0xf;
            //access_control=0xe;
            uid_string = "Patient ID";
            speak = 1;
            encryption = 0;
            b=getIntent().getExtras();

            //ii=getIntent();
            if(b!=null){
                count=b.getLong("len");
                counter=b.getString("Coounter");
            }
//            counters=  getIntent().getExtras().getLong("Length");

            Toast.makeText(NfcReadActivity.this,counter+" :"+counters,Toast.LENGTH_SHORT).show();
            // Toast.makeText(NfcActivity.this,"Counters: "+counters,Toast.LENGTH_SHORT).show();
            nfcTagSecurity = new nfcSecurity();

            Log.d("NFCTAG", " start NfcActivity");

            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.UK);
                    }
                }
            });
            byte[] encryptedValue = new byte[0];
            try {
                // encryptedValue = nfcSecurity.encrypt("plz try encrypt and decrypt me");
                encryptedValue = nfcSecurity.encrypt(" ");
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            Log.d("NFCTAG encryptedValue1 ", String.valueOf(encryptedValue));
            String decryptedValue = null;
            try {
                decryptedValue = nfcSecurity.decrypt(encryptedValue);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            Log.d("NFCTAG encryptdValue2::", String.valueOf(decryptedValue));

        }

        @Override
        protected void onResume() {
            super.onResume();

            enableForegroundDispatchSystem();
        }

        @Override
        protected void onPause() {
            super.onPause();

            disableForegroundDispatchSystem();
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            Log.d("NFCTAG", " intent");
            String writeText;

            textTagContentRead=(EditText)findViewById(R.id.textTagContentRead);
            if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
                Log.d("NFCTAG", "NfcIntent!");

                        Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                        if (parcelables != null && parcelables.length > 0) {
                            if ((access_control & 0x4) == 0x4) {
                                Log.d("NFCTAG", " reading");

                                String readText = null;
                                try {
                                    readText = readTextFromMessage((NdefMessage) parcelables[0]);
                                } catch (BadPaddingException e) {

                                    e.printStackTrace();
                                } catch (InvalidKeyException e) {
                                    e.printStackTrace();
                                } catch (IllegalBlockSizeException e) {
                                    e.printStackTrace();
                                }
                                if (encryption == 1) {
                                    Log.d("NFCTAG before decrypt ", readText);
                                    String tagContentDecrypt = null;
                                    try {
                                        tagContentDecrypt = nfcSecurity.decrypt(readText.getBytes());
                                    } catch (InvalidKeyException e) {
                                        Log.d("NFCTAG", "exception 1");
                                        e.printStackTrace();
                                    } catch (BadPaddingException e) {
                                        Log.d("NFCTAG", "exception 2");
                                        e.printStackTrace();
                                    } catch (IllegalBlockSizeException e) {
                                        Log.d("NFCTAG", "exception 3");
                                        e.printStackTrace();
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    }
                                    readText = tagContentDecrypt;
                                }

                                Log.d("NFCTAG read: ", readText);
                                //readTextFromMessage((NdefMessage)parcelables[1]);
                                Log.d("NFCTAG", " after reading1");
                                textView1.setText(readText);
                                Log.d("NFCTAG", " after reading2");
                                if (speak == 1) {
                                    t1.speak(readText, TextToSpeech.QUEUE_FLUSH, null, null);
                                }
                            }
                        } else {
                            Log.d("NFCTAG", "No NDEF messages found!");
                            if ((access_control & 0x1) == 0x1) {
                                // .setText("enter" + uid_string + ": ");
                                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                                NdefMessage msg = createNdefMessage(" " + textTagContentRead.getText());
                                //NdefMessage pswd=createNdefMessage(pwd.getText()+"");

                                writeNdefMessage(tag, msg);
                                //writeNdefMessage(tag,nMesaage);
                                // writeNdefMessage(tag,pswd);
                            }

                        }
                    }
                }




        private String readTextFromMessage(NdefMessage ndefMessage) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {

            NdefRecord[] ndefRecords = ndefMessage.getRecords();

            if (ndefRecords != null && ndefRecords.length > 0) {

                NdefRecord ndefRecord = ndefRecords[0];
                //NdefRecord ndefRecord1=ndefRecord[1];

                String tagContent = getTextFromNdefRecord(ndefRecord);

                // Log.d("NFCTAG before decrypt", tagContent);
                // String tagContentDecrypt = nfcSecurity.decrypt(tagContent.getBytes());
                // Log.d("NFCTAG after decrypt", tagContentDecrypt);

                // return tagContentDecrypt;
                return tagContent;

            } else {
                Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
                Log.d("NFCTAG", "No NDEF records found!");
                return null;
            }


        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_admin_log_in, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }


        private void enableForegroundDispatchSystem() {

            Intent intent = new Intent(this, NfcReadActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            IntentFilter[] intentFilters = new IntentFilter[]{};

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }

        private void disableForegroundDispatchSystem() {
            nfcAdapter.disableForegroundDispatch(this);
        }

        private void formatTag(Tag tag, NdefMessage ndefMessage) {
            try {

                NdefFormatable ndefFormatable = NdefFormatable.get(tag);

                if (ndefFormatable == null) {
                    Toast.makeText(this, "Tag is not ndef formatable!", Toast.LENGTH_SHORT).show();
                    Log.d("NFCTAG", "Tag is not ndef formatable!");
                    return;
                }


                ndefFormatable.connect();
                ndefFormatable.format(ndefMessage);
                ndefFormatable.close();

                Toast.makeText(this, "Tag formatted!", Toast.LENGTH_SHORT).show();
                Log.d("NFCTAG", "Tag formatted!");
            } catch (Exception e) {
                Log.d("NFCTAG", e.getMessage());
            }

        }

        private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {
            Log.d("NDFTAG", "writing ndefmessage");
            try {

                if (tag == null) {
                    Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
                    Log.d("NDFTAG", "Tag object cannot be null");
                    return;
                }

                Ndef ndef = Ndef.get(tag);

                if (ndef == null) {
                    // format tag with the ndef format and writes the message.
                    formatTag(tag, ndefMessage);
                } else {
                    ndef.connect();

                    if (!ndef.isWritable()) {
                        Toast.makeText(this, "Tag is not writable!", Toast.LENGTH_SHORT).show();
                        Log.d("NFCTAG", "Tag is not writable!");
                        ndef.close();
                        return;
                    }

                    ndef.writeNdefMessage(ndefMessage);
                    ndef.close();

                    Toast.makeText(this, "Tag written!", Toast.LENGTH_SHORT).show();
                    Log.d("NFCTAG", "Tag written!");
                }

            } catch (Exception e) {
                Log.d("NFCTAG writeNdefMessage", e.getMessage());
            }

        }


        private NdefRecord createTextRecord(String content) {
            Log.d("NFCTAG", "Text Record Creating started");
            try {
                byte[] language;
                language = Locale.getDefault().getLanguage().getBytes("UTF-8");

                final byte[] text = content.getBytes("UTF-8");
                final int languageSize = language.length;
                final int textLength = text.length;
                final ByteArrayOutputStream payload = new ByteArrayOutputStream((1 + languageSize + textLength));
                //payload=new ByteArrayOutputStream()
                Log.d("NFCTAG", "Size used in tag: " + (1 + languageSize + textLength));
                payload.write((byte) (languageSize & 0x1F));
                // payload.write();
                payload.write(language, 0, languageSize);
                payload.write(text, 0, textLength);
                //payload.write(text,0,textLength);


                return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

            } catch (UnsupportedEncodingException e) {
                Log.d("NFCTAG createTextRecord", e.getMessage());
            }
            return null;
        }


        private NdefMessage createNdefMessage(String content) {
            Log.d("NFCTAG", "Creating NDef message");

            NdefRecord[] nr = new NdefRecord[3];
            nr[0] = createTextRecord(content);
            NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{nr[0]});
       /*defMessage msg = new NdefMessage(
                new NdefRecord[]{
                        NdefRecord.createMime(
                                "application/vnd.com.example.android.beam",
                                content.getBytes())
                });*/
            return ndefMessage;
        }

        private NdefMessage createNdefMessage1(String content) {
            Log.d("NFCTAG", "Creating NDef message");
            String s1 = new String("");
            String s2 = new String("");
            NdefRecord[] ndefRecord = new NdefRecord[3];
            NdefRecord ndefRecord1;
            StringTokenizer st = new StringTokenizer(content, "/");
            while (st.hasMoreTokens()) {
                s1 = st.nextToken();
                s2 = st.nextToken();
            }
            //ndefRecord[0]= createTextRecord(s1);
            // ndefRecord[1]= createTextRecord(s2);
            ndefRecord1 = createTextRecord(content);

            NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord1});
            //NdefMessage ndefMessage=new NdefMessage(ndefRecord);
            //new NdefMessage()
            return ndefMessage;
        }




        public String getTextFromNdefRecord(NdefRecord ndefRecord) {

            Log.d("NFCTAG", "Reading Text From NDef Record");
            String tagContent = null;
            String textEncoding;
            String s = null;
            try {
                byte[] payload = ndefRecord.getPayload();
                //String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                int languageSize = payload[0] & 0063;
                //tagContent=new String();

                if ((payload[0] & 128) == 0) textEncoding = "UTF-8";
                else textEncoding = "UTF-16";
                tagContent = new String(payload, languageSize + 1,
                        payload.length - languageSize - 1, textEncoding);
                //new String()
                Log.d("NFCTAG", "Creating string from ndefrecord");
            } catch (UnsupportedEncodingException e) {
                Log.d("NFCTAG msg", e.getMessage(), e);
            }
            Log.d("NFCTAG", tagContent);
            return tagContent;
        }

        public void nfcFormat(View view) {
            if ((access_control & 0x2) == 0x2) {
                nfcFormat = 1;
            }
        }

        public void nfcWrite(View view) {
            if ((access_control & 0x8) == 0x8) {
                nfcWrite = 1;
            }
        }

        public void setLocale(String lang) {
            myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            // Intent refresh = new Intent(this, MainActivity.class);
            // startActivity(refresh);
            // finish();
        }



    }


