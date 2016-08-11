package com.example.sebastin.bina2;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;



public class RecordActivity extends AppCompatActivity {
    EditText editT,countText;
    ImageButton boton;
    public static TextView texto,textLeftdB, textRightdB,actionTextView,izquierdaTextView,derechaTextViewR,recordingsText,dataText;
    public mPlayer mP;
    private WavAudioRecorder mRecorder;
    public String mRecordFilePath;
    public String directory = "/BinaRecordings";
    public String filename = "Grabacion";
    public String format = ".wav";
    String recordingTitle;
//    String [] recStates  = getResources().getStringArray(R.array.recording_states);
    String mVisualizerFilePath,recState;
    public File root = Environment.getExternalStorageDirectory();
    public File dir = new File(root.getAbsolutePath() + directory);
    public File file;
    double leftRms = 0,rightRms = 0,leftDbfs = 0,rightDbfs = 0;
    byte [] wavBuffer = new byte[10000];
    ByteBuffer bb;
    short[] micData = new short[wavBuffer.length/10];
    short [] micLeftBuffer = new short [micData.length/2];
    short[] micRightBuffer = new short [micData.length/2];
    int il = 0;
    int ir = 0;
    int itc = 0;
    int bitDepth = 16;
    int sampleRate = 44100;
    int sampleState = 0;
    int bithState = 1;
    int recCount = 0;
    double micLeftRms = 0;
    double micRightRms = 0;
    double micLeftMax = 0;
    double micRightMax = 0;
    double micLeftDbfs = 0;
    double micRightDbfs = 0;
    double leftLedValue = 0;
    double rightLedValue = 0;
    String rightString = "";
    String leftString = "";
    public byte [] bufar;
    public enum Menus {CONTACT,ABOUT}
    public Menus menus;
    File filevs;
    LedMeter leftLedMeter;
    LedMeter rightLedMeter;
    TabHost th;
    ArrayAdapter titleAdapter,dataAdapter,imageAdapter;
    ListView listView;
    Chronometer timer;
    String listviewitems[] = {"No hay Grabaciones"},metadataitems[] = {"n"};
    String filetoplay = null, fileToEdit = null;
    AudioRead aR = new AudioRead();
    RecordingsAdapter recordingsAdapter;
    AudioManager audioManager;
    boolean overwrite = false;
    long elapsedMillis = 0,clockStart = 0,clockStop = 0;
    UsbDevice device;
    UsbManager mUsbManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        Bundle bundle = getIntent().getExtras();
        final Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/OPTIMA.TTF");
        actionTextView = (TextView)findViewById(R.id.actionText);
        izquierdaTextView = (TextView)findViewById(R.id.textView5);
        derechaTextViewR = (TextView)findViewById(R.id.textView6);
        recordingsText = (TextView)findViewById(R.id.textView2);
        dataText = (TextView)findViewById(R.id.list_item);
        actionTextView.setText(bundle.getString("ProjectActivitiyprojectName"));
        actionTextView.setTextColor(ContextCompat.getColor(this, R.color.windowbackground_color));
        actionTextView.setTextSize(20);


        InputStream inStream =  getResources().openRawResource(R.raw.dc);

        th = (TabHost)findViewById(R.id.tabHost);
        //Record Tab
        th.setup();
        final TabHost.TabSpec tsRecord = th.newTabSpec("Grabar");
        tsRecord.setIndicator("Grabar");
        tsRecord.setContent(R.id.linearLayout);
        th.addTab(tsRecord);
        //Recordings Tab
        th.setup();
        final TabHost.TabSpec tsRecordings = th.newTabSpec("Biblioteca");
        tsRecordings.setIndicator("Biblioteca");
        tsRecordings.setContent(R.id.linearLayout2);
        th.addTab(tsRecordings);
        for (int i = 0;i <=1;i++) {
            TextView tv = (TextView) th.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
            tv.setTextColor(Color.parseColor("#e0e0e0"));
            tv.setTextSize(15);
            tv.setAllCaps(false);
            tv.setTypeface(typeface,Typeface.BOLD);
        }
//        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        registerReceiver(mUsbReceiver, filter);
//        mUsbManager = (UsbManager)  getSystemService(Context.USB_SERVICE);
//        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
////        UsbAccessory[] accessoryList = mUsbManager.getAccessoryList();
////        Log.i("RecordActivity device list",deviceList.toString());
//        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//        while (deviceIterator.hasNext()) {
//            device = deviceIterator.next();
//        }

//        texto.setText(accessoryList.toString());
//        if (device != null ) {
//            int deviceprotocol = device.getDeviceProtocol();
////            Log.i("RecordActivity device protocol",deviceprotocol+"");
//            mUsbManager.requestPermission(device, mPermissionIntent);
//        }
        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals(tsRecord.getTag())) {
                    //destroy earth
//                    boton.setText("Grabar");
                    resetChrono();
                    if (mP.getState() != null) {
                        if (mP.getState().equals(mPlayer.playerState.PLAYING)) {
                            mP.stopPlayback();
                        }
                    }

                }
                if (tabId.equals(tsRecordings.getTag())) {
                    //destroy mars
                    stopRecording();
                    filevs.delete();
                    resetChrono();
                    leftLedMeter.setLevel(0, 80);
                    rightLedMeter.setLevel(0, 80);
                    mP = new mPlayer();
                    filetoplay = null;
                    listView = (ListView) findViewById(R.id.listView);
                    listviewitems = dir.list();

                    if (dir.list()!=null){
                        metadataitems  = new String[listviewitems.length];
                        for(int i = 0;i < listviewitems.length;i++) {
                            aR.setAudioRead(dir.toString()+"/"+listviewitems[i],1);
                            metadataitems[i] = aR.getAudioMetaData();
                        }
                        titleAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_view_custom_layout, R.id.list_item, listviewitems);
                        recordingsAdapter = new RecordingsAdapter(getApplicationContext(),R.layout.list_view_custom_layout);
                        for (int i = 0;i < dir.list().length;i++){
                            RecordingsDataProvider recordingsDataProvider = new RecordingsDataProvider(R.mipmap.ic_launcher,listviewitems[i],metadataitems[i]);
                            recordingsAdapter.add(recordingsDataProvider);
                        }
                    }

//                    new Thread(new dataitems()).start();
                    listView.setAdapter(recordingsAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView parent, View view, int position, long id) {
                            TextView tv = (TextView) view.findViewById(R.id.list_item);
                            recordingTitle = tv.getText().toString();
                            view.setSelected(true);
                            if (filetoplay == null) {
                                filetoplay = dir.toString() + "/" + recordingTitle;
                                mP.setFilePath(filetoplay);
                            }
                            if (!filetoplay.equals(dir.toString() + "/" + recordingTitle) && !mP.getState().equals(mPlayer.playerState.STOPPED)) {
                                //cambio de grabacion si playing
                                mP.stopPlayback();
                                filetoplay = dir.toString() + "/" + recordingTitle;
                                mP.setFilePath(filetoplay);
                            }
                            filetoplay = dir.toString() + "/" + recordingTitle;
                            reproduccion();
                            aR = new AudioRead();
                            aR.setAudioRead(filetoplay, 100);
                            if (mP.getState().equals(mPlayer.playerState.PLAYING)) {
                                Toast.makeText(getBaseContext(), recordingTitle + " " + mPlayer.playerState.PLAYING.toString(), Toast.LENGTH_SHORT).show();
                            } else if (mP.getState().equals(mPlayer.playerState.STOPPED)) {
                                Toast.makeText(getBaseContext(), recordingTitle + " " + mPlayer.playerState.STOPPED.toString(), Toast.LENGTH_SHORT).show();
                            }
//                            texto.setText(mP.getState().toString());
//                            new Thread(new PlayTask()).start();
                        }
                    });
                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                                       int pos, long id) {
                            // TODO Auto-generated method stub
                            TextView tv = (TextView) view.findViewById(R.id.list_item);
                            recordingTitle = tv.getText().toString();
                            Log.e("long clicked", "pos: " + recordingTitle);
                            fileToEdit = dir.toString() + "/" + recordingTitle;
                            PopupMenu popUpMenu = new PopupMenu(listView.getContext(), view);
                            MenuInflater menuInflater = popUpMenu.getMenuInflater();
                            menuInflater.inflate(R.menu.editpopup, popUpMenu.getMenu());
                            EditPopUp editPopUp = new EditPopUp(getApplicationContext());
                            popUpMenu.setOnMenuItemClickListener(editPopUp);
                            popUpMenu.show();

                            return true;
                        }
                    });
                }
            }
        });
        leftLedMeter = new LedMeter(this);
        rightLedMeter = new LedMeter(this);
        leftLedMeter = (LedMeter)findViewById(R.id.leftLedView);
        rightLedMeter = (LedMeter)findViewById(R.id.rightLedView);
        directory = "/BinaRecordings";
//        Bundle bundle = getIntent().getExtras();
        directory = directory+"/"+bundle.getString("ProjectActivitiyprojectName");
        filename = "Grabacion";
        format = ".wav";
        root = Environment.getExternalStorageDirectory();
        dir = new File(root.getAbsolutePath() + directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, filename+format);
        mRecordFilePath = file.toString();
        editT = (EditText) findViewById(R.id.editText);
        boton = (ImageButton) findViewById(R.id.button);
        texto = (TextView) findViewById(R.id.textView);
        countText = (EditText) findViewById(R.id.textView3);
        textLeftdB = (TextView)findViewById(R.id.textLeftdB);
        textRightdB = (TextView)findViewById(R.id.textRightdB);
        mRecorder = WavAudioRecorder.getInstance(0, 1,this);
        filevs = new File(dir, filename+"vis"+format);
        dir = new File(root.getAbsolutePath() + directory);
        mVisualizerFilePath = filevs.toString();
        timer = (Chronometer)findViewById(R.id.chronometer);
        actionTextView.setTypeface(typeface,Typeface.BOLD);
        editT.setTypeface(typeface);
        izquierdaTextView.setTypeface(typeface);
        derechaTextViewR.setTypeface(typeface);
        textLeftdB.setTypeface(typeface);
        textRightdB.setTypeface(typeface);
    }
    public void grabacionBoton(View view){
        filename = editT.getText().toString();
        if (!filename.equals("") || mRecorder.getState().equals(WavAudioRecorder.State.PAUSED)) {
            if (!mRecorder.getState().equals(WavAudioRecorder.State.RECORDING)) {
//            boton.setText("Grabando");
                dir = new File(root.getAbsolutePath() + directory);
                filename = editT.getText().toString();
                if (recCount != 0) {
                    file = new File(dir, filename + recCount + format);
                } else {
                    file = new File(dir, filename + format);
                }
                if (file.exists() && !mRecorder.getState().equals(WavAudioRecorder.State.PAUSED)) {
                    PopupMenu popUpMenu = new PopupMenu(this, view);
                    MenuInflater menuInflater = popUpMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.warningpopup, popUpMenu.getMenu());
                    WarningPopUp warningPopUp = new WarningPopUp(getApplicationContext());
                    popUpMenu.setOnMenuItemClickListener(warningPopUp);
                    popUpMenu.show();
                } else {
                    startChrono();
                    startRecording();
                }

                textState();
                boton.setImageResource(R.drawable.recording_image);
                texto.setText(recState);
            } else {
                pauseRecording();
//            boton.setText("Grabar");
                stopChrono();
                textState();
                texto.setText(recState);
            }
        }else{
            Toast.makeText(getBaseContext(),"Seleccione un Nombre de Grabación", Toast.LENGTH_SHORT).show();
        }
    }
    public void stopButton(View view){
        stopRecording();
        resetChrono();
//        boton.setText("Grabar");
        textState();
        texto.setText(recState);
    }
    public void startRecording () {
        mRecordFilePath = file.toString();
        if (mRecorder.getState().equals(WavAudioRecorder.State.INITIALIZING)  || mRecorder.getState().equals(WavAudioRecorder.State.STOPPED)) {
            bitDepth = SettingsClass.getInstance().getBitDepth();
            sampleRate = SettingsClass.getInstance().getSampleRate();
            switch (bitDepth){
                case 8:
                    bithState = 2;
                    break;
                case 16:
                    bithState = 1;
                    break;
                case 24:
                    bithState = 0;
                    break;
            }
            switch (sampleRate){
                case 8000:
                    sampleState = 0;
                    break;
                case 11025:
                    sampleState = 1;
                    break;
                case 22050:
                    sampleState = 2;
                    break;
                case 44100:
                    sampleState = 3;
                    break;
                case 48000:
                    sampleState = 4;
                    break;
            }
            boton.setImageResource(R.drawable.recording_image);
            mRecorder = WavAudioRecorder.getInstance(sampleState, bithState,this);
            mRecorder.setOutputFile(mRecordFilePath);
            mRecorder.prepare();
            mRecorder.start();
//            new Thread(new RecTask()).start();
        }
        if (mRecorder.getState().equals(WavAudioRecorder.State.PAUSED)){
            mRecorder.start();
            new Thread(new RecTask()).start();
        }
    }
    public void pauseRecording(){
        if (mRecorder.getState().equals(WavAudioRecorder.State.RECORDING)) {
            boton.setImageResource(R.drawable.record);
            mRecorder.pause();
        }

    }
    public void stopRecording(){
        if (mRecorder.getState().equals(WavAudioRecorder.State.ERROR) || mRecorder.getState().equals(WavAudioRecorder.State.RECORDING ) || mRecorder.getState().equals(WavAudioRecorder.State.PAUSED)) {
            boton.setImageResource(R.drawable.record);
            mRecorder.stop();
            mRecorder.release();
            recCount+=1;
            filename = editT.getText().toString()+recCount;
            countText.setText("" + recCount);

        }
    }
    public void textState () {
        switch (mRecorder.getState()) {
            case INITIALIZING:
//                recState = recStates[3];
                recState = "";
                break;
            case RECORDING:
//                recState = recStates[1];
                recState = "Grabando";
                break;
            case READY:
//                recState = recStates[3];
                recState = "";
                break;
            case STOPPED:
//                recState = recStates[3];
                recState = "";
                break;
            case PAUSED:
//                recState = recStates[2];
                recState = "Pausa";
                break;
        }
    }
    public void reproduccion () {
        switch (mP.getState()){
            case INITIALIZED:
                try {
                    mP.startPlayBack();
//                    resetChrono();
//                    startChrono();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PLAYING:
                mP.pausePlayback();
//                resetChrono();
//                stopChrono();
                break;
            case STOPPED:
                    mP.setFilePath(filetoplay);
                try {
                    mP.startPlayBack();
//                    resetChrono();
//                    startChrono();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case PAUSED:
                try {
//                    startChrono();
                    mP.startPlayBack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    public void startChrono (){
        elapsedMillis = elapsedMillis+(clockStop-clockStart);
        timer.setBase(SystemClock.elapsedRealtime()-(elapsedMillis));
        clockStart = SystemClock.elapsedRealtime();
        timer.start();
    }
    public void stopChrono(){
        timer.stop();
        clockStop = SystemClock.elapsedRealtime();
    }
    public void resetChrono(){
        elapsedMillis = 0;
        clockStart = 0;
        clockStop = 0;
        timer.stop();
        timer.setText("00:00");
    }
    public class RecTask implements Runnable {
        @Override
        public void run() {
            while(mRecorder.getState().equals(WavAudioRecorder.State.RECORDING)) {
                micVisualizer();
                leftLedValue = Math.abs(-80 - micLeftDbfs);
                rightLedValue = Math.abs(-80 - micRightDbfs);
                leftString = String.format("%.2f", micLeftDbfs);
//                leftString = leftString+" dB Fs";
                rightString = String.format("%.2f", micRightDbfs);
//                rightString = rightString+" dB Fs";
                textLeftdB.post(new Runnable() {
                    @Override
                    public void run() {
                        textLeftdB.setText(leftString);
                        textRightdB.setText(rightString);
                        leftLedMeter.setLevel(leftLedValue, 80);
                        rightLedMeter.setLevel(rightLedValue, 80);
                    }
                });
//                textRightdB.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        textRightdB.setText("" + Math.abs(-80 - micRightDbfs) + "dB Fs ");
//                    }
//        });
//                if (Math.abs((Math.abs(-80 - micLeftDbfs)-leftLedValue))> 10){
//                leftLedValue = Math.abs(-80 - micLeftDbfs);
//                        leftLedMeter.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                leftLedMeter.setLevel(leftLedValue, 80);
//                            }
//                        });
//                }
//                if (Math.abs((Math.abs(-80 - micRightDbfs)-rightLedValue))> 10){
//                    rightLedValue = Math.abs(-80 - micRightDbfs);
//                    rightLedMeter.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            rightLedMeter.setLevel(rightLedValue, 80);
//
//                        }
//                    });
//                }
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }
    public class dataitems implements Runnable {
        @Override
        public void run() {
            for(int i = 0;i < listviewitems.length;i++) {
                aR.setAudioRead(dir.toString()+"/"+listviewitems[i],1);
                metadataitems[i] = aR.getAudioMetaData();
            }
//                leftLedMeter.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        leftLedMeter.setLevel(Math.abs(-80 - leftDbfs), 80);
//                    }
//                });
            }
        }
    public class PlayTask implements Runnable {
        @Override
        public void run() {
            itc = 0;
            while(mP.getState().equals(mPlayer.playerState.PLAYING)) {
                bufar = aR.getbufAudioRead(itc);
                leftRms = aR.getLeftRMSvalue();
                rightRms = aR.getRightRMSvalue();
                leftDbfs = Math.round(aR.getLeftDbfsValue());
                rightDbfs = Math.round(aR.getRightDbfsValue());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                leftLedMeter.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        leftLedMeter.setLevel(Math.abs(-80 - leftDbfs), 80);
//                    }
//                });
//                rightLedMeter.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        rightLedMeter.setLevel(Math.abs(-80 - rightDbfs), 80);
//                    }
//                });
                textLeftdB.post(new Runnable() {
                    @Override
                    public void run() {
                        textLeftdB.setText("" + leftDbfs);
                    }
                });
                textRightdB.post(new Runnable() {
                    @Override
                    public void run() {
                        textRightdB.setText("" +rightDbfs);
                    }
                });
                itc += aR.getNumberSamples();
            }
        }
    }
     public void micVisualizer (){
         wavBuffer = mRecorder.getBuffer();
         bb = ByteBuffer.wrap(wavBuffer);
             il = 0;
             ir = 0;
             for (int i = 0; i < micData.length; i++) {
                 micData[i] = Short.reverseBytes(bb.getShort());
                 //micData[i] = bb.getShort();
                 if ((i % 2) == 0) {
                     // number is even
                     micLeftBuffer[il] = micData[i];
                     micLeftRms = micLeftRms + Math.pow(micLeftBuffer[il],2);
                     if (micLeftBuffer[il] > micLeftMax) {
                         micLeftMax = micLeftBuffer[il];
                     }
                     il++;
                 }
                 else {
                     // number is odd
                     micRightBuffer[ir] = micData[i];
                     micRightRms = micRightRms + Math.pow(micRightBuffer[ir],2);
                     if (micRightBuffer[ir] > micRightMax) {
                         micRightMax = micRightBuffer[ir];
                     }
                     ir++;
                 }
             }
         micRightRms =  Math.sqrt(micRightRms/(micRightBuffer.length));
         micLeftRms =  Math.sqrt(micLeftRms/(micLeftBuffer.length));
         if (micLeftRms >= 0.001) {
             micLeftDbfs = Math.round(20 * Math.log10(micLeftRms / 32768));
         }
         else{
             micLeftDbfs = -80;
         }
         if (micRightRms >= 0.001) {
             micRightDbfs = Math.round(20 * Math.log10(micRightRms / 32768));
         }
         else{
             micRightDbfs = -80;
         }
     }

    public class WarningPopUp implements PopupMenu.OnMenuItemClickListener{
        Context context;
        public  WarningPopUp (Context context){
            this.context = context;
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            overwrite = false;
            String selection = item.getTitle().toString();
            if (selection.equals("Sobreesribir archivo")) {
                overwrite = true;
            }else if (selection.equals("no sobre escribir archivo")){
                overwrite = false;
            }
            if (overwrite) {
                startChrono();
                startRecording();
            }
            return true;
        }
    }
    public class EditPopUp implements PopupMenu.OnMenuItemClickListener{
        Context context;
        public  EditPopUp (Context context){
            this.context = context;
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            String selection = item.getTitle().toString();
            switch (selection) {
                case "Eliminar":
                    File fileToDelete = new File(fileToEdit);
                    fileToDelete.delete();
                    listviewitems = dir.list();
                    if (dir.list() != null) {
                        metadataitems = new String[listviewitems.length];
                        for (int i = 0; i < listviewitems.length; i++) {
                            aR.setAudioRead(dir.toString() + "/" + listviewitems[i], 1);
                            metadataitems[i] = aR.getAudioMetaData();
                        }
                        titleAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_view_custom_layout, R.id.list_item, listviewitems);
                        recordingsAdapter = new RecordingsAdapter(getApplicationContext(), R.layout.list_view_custom_layout);
                        for (int i = 0; i < dir.list().length; i++) {
                            RecordingsDataProvider recordingsDataProvider = new RecordingsDataProvider(R.mipmap.ic_launcher, listviewitems[i], metadataitems[i]);
                            recordingsAdapter.add(recordingsDataProvider);
                        }
                    }
//                    new Thread(new dataitems()).start();
                    listView.setAdapter(recordingsAdapter);
                    break;
                case "Compartir":
                    File filetoShare = new File(fileToEdit);
                    final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("audio/wav");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileToEdit));
//                shareIntent.setPackage("com.whatsapp");
                    startActivity(Intent.createChooser(shareIntent, "Compartir por:"));
                    break;
                case "Procesar":
                    new Thread(new audioProcessing()).start();
                    listviewitems = dir.list();
                    if (dir.list() != null) {
                        metadataitems = new String[listviewitems.length];
                        for (int i = 0; i < listviewitems.length; i++) {
                            aR.setAudioRead(dir.toString() + "/" + listviewitems[i], 1);
                            metadataitems[i] = aR.getAudioMetaData();
                        }
                        titleAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_view_custom_layout, R.id.list_item, listviewitems);
                        recordingsAdapter = new RecordingsAdapter(getApplicationContext(), R.layout.list_view_custom_layout);
                        for (int i = 0; i < dir.list().length; i++) {
                            RecordingsDataProvider recordingsDataProvider = new RecordingsDataProvider(R.mipmap.ic_launcher, listviewitems[i], metadataitems[i]);
                            recordingsAdapter.add(recordingsDataProvider);
                        }
                    }
//                    new Thread(new dataitems()).start();
                    listView.setAdapter(recordingsAdapter);
                    break;
            }
            return true;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater Inflater = getMenuInflater();
        //Inflater.inflate(R.menu.menu_main,menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            //call method to set up device communication
                        }
                    }
                    else {
//                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    public void recordingsActivity(MenuItem item) {
        Intent intent = new Intent(this,RecordingsActivity.class);
        intent.putExtra("RecordActivitydirectory", directory);
        stopRecording();
        filevs.delete();
        startActivity(intent);
    }
    public void contactActivity(MenuItem item){
        int menus = 0;
        Intent intent = new Intent(this,ContactActivity.class);
        intent.putExtra("menuclick", menus);
        startActivity(intent);
    }
    public void aboutActivity(MenuItem item){
        int menus = 1;
        Intent intent = new Intent(this,ContactActivity.class);
        intent.putExtra("menuclick",menus);
        startActivity(intent);
    }
    Context context;
    public class audioProcessing implements Runnable {
        @Override
        public void run() {
            try{
                ByteBuffer bb;
//                    if (resou) {
//                fileClose(cacheFile);
                File fileToProcess = new File(fileToEdit);
                byte[] audioToProcess;
                aR.setAudioRead(fileToEdit, 0);
                audioToProcess = aR.getbufAudioRead(0);
                int resourceId = R.raw.di8000;
                byte[] numChanB = new byte[2];
                byte[] bitDepthB = new byte[2];
                byte[] samplingRateB = new byte[4];
                byte[] audioSizeB = new byte[4];
                int audioBufferSize;
                byte[] audioBuffer,stConvbyte,recBuffer;
                short[]audioSamples,audioLeft,audioRight,audioInverseLeft,audioInverseRight,
                audioImpOpLeft,audioImpOpRight,audioImpOpLeftOut,audioImpOpRightOut
                        ,recSamples,rConv,shorts,lConv,length,stConv;
                short numChanS,bitDepthS;
                int samplingRateI,lCount,rCount;

                File cacheFile = createCacheFile( resourceId, "delete-me-please");
                RandomAccessFile randomAccessFile = new RandomAccessFile(cacheFile, "r");
//            numero de canales
                randomAccessFile.seek(22);
                randomAccessFile.read(numChanB, 0, 2);
                bb = ByteBuffer.wrap(numChanB);
                numChanS = Short.reverseBytes(bb.getShort());
//            frecuencia de muestreo
                randomAccessFile.seek(24);
                randomAccessFile.read(samplingRateB, 0, 4);
                bb = ByteBuffer.wrap(samplingRateB);
                samplingRateI = Integer.reverseBytes(bb.getInt());
//            profundiad en bits
                randomAccessFile.seek(34);
                randomAccessFile.read(bitDepthB, 0, 2);
                bb = ByteBuffer.wrap(bitDepthB);
                bitDepthS = Short.reverseBytes(bb.getShort());
                randomAccessFile.seek(70);
                randomAccessFile.read(audioSizeB);
//            data size
                randomAccessFile.seek(74);
                randomAccessFile.read(audioSizeB, 0, 4);
                bb = ByteBuffer.wrap(audioSizeB);
                audioBufferSize = Integer.reverseBytes(bb.getInt());
//            variable init
//                        if (this.audioBufferSize != audioBufferSize) {
                audioBuffer = new byte[audioBufferSize];
                audioSamples = new short[audioBufferSize / 2];
                audioLeft = new short[audioBufferSize / 4];
                audioRight = new short[audioBufferSize / 4];
                audioInverseLeft = new short[audioSamples.length/2];
                audioInverseRight = new short[audioSamples.length/2];
//                            this.audioBufferSize = audioBufferSize;
//                        }
                randomAccessFile.seek(78);
                randomAccessFile.read(audioBuffer,0, audioBufferSize);
                bb = ByteBuffer.wrap(audioBuffer);
                il = 0;
                ir = 0;
                for (int i = 0; i < audioSamples.length; i++) {
                    audioSamples[i] = Short.reverseBytes(bb.getShort());
                    if ((i % 2) == 0) {
                        // number is even
                        audioLeft[il]=audioSamples[i];
                        il++;
                    }
                    else {
                        // number is odd
                        audioRight[ir]=audioSamples[i];
                        ir++;
                    }
                }
                Log.e("File", "wavWriter: imp samples ok" );
                for(int i = 0; i < audioLeft.length; i++){
                    audioInverseLeft[i] = audioLeft[audioLeft.length-1 - i];
                    audioInverseRight[i] = audioRight[audioRight.length-1 - i];
                }
                recBuffer = audioToProcess;
                bb = ByteBuffer.wrap(recBuffer);
                recSamples = new short[recBuffer.length / 2];
                lConv = new short[audioLeft.length+audioLeft.length-1];
                rConv = new short[audioRight.length+audioRight.length-1];
                stConv = new short[2*lConv.length];
                stConvbyte = new byte[2*stConv.length];
                audioImpOpLeft = new short[recSamples.length];
                audioImpOpRight = new short[recSamples.length];
                audioImpOpLeftOut = new short[recSamples.length];
                audioImpOpRightOut = new short[recSamples.length];
                int [] lCountIntArray = new int[audioLeft.length+audioLeft.length-1];
                int [] rCountIntArray = new int[audioRight.length+audioRight.length-1];
                for (int i = 0; i < recSamples.length; i++) {
                    recSamples[i] = Short.reverseBytes(bb.getShort());
                }
                Log.e("File", "wavWriter: recSamples ok" );
                for (int p = 0; p < audioInverseLeft.length; p++) {
                    lCount = 0;
                    rCount = 0;
//                audioImpOpLeft = Arrays.copyOfRange(audioLeft, audioInverseLeft.length -1 - p, recSamples.length);
//                Arrays.fill(audioImpOpLeft, p + 1, audioImpOpLeft.length, (short) 0);
                    System.arraycopy(audioInverseLeft,audioInverseLeft.length-1-p,audioImpOpLeft,0,p+1);
                    System.arraycopy(audioInverseRight,audioInverseRight.length-1-p,audioImpOpRight,0,p+1);
                    for (int i = 0; i < audioInverseLeft.length; i++) {
                        lCount = recSamples[i] * audioImpOpLeft[i] + lCount;
                        rCount = recSamples[i] * audioImpOpRight[i] + rCount;
                    }
//                    double lopdouble = (double) (Integer.valueOf(lCount).doubleValue()* Short.valueOf(Short.MAX_VALUE).doubleValue()/ Integer.valueOf(Integer.MAX_VALUE).doubleValue());
//                    double ropdouble = (double) (Integer.valueOf(lCount).doubleValue()* Short.valueOf(Short.MAX_VALUE).doubleValue()/ Integer.valueOf(Integer.MAX_VALUE).doubleValue());
//                    lConv[p] =  Double.valueOf(lopdouble).shortValue();
//                    rConv[p] = Double.valueOf(ropdouble).shortValue();
//                    lConv[p] = Integer.valueOf(lCount).shortValue();
//                    rConv[p] = Integer.valueOf(rCount).shortValue();
//                    lConv[p] = (short) lCount;
//                    rConv[p] = (short) rCount;
                    lCountIntArray[p] = lCount;
                    rCountIntArray[p] = rCount;
                }
                Log.e("File", "wavWriter: first conv ok" );
                for (int q = 0; q < audioInverseLeft.length-1; q++) {
                    lCount = 0;
                    rCount = 0;
                    System.arraycopy(audioInverseLeft,0,audioImpOpLeft,q+1,audioInverseLeft.length-1-q);
                    System.arraycopy(audioInverseRight,0,audioImpOpRight,q+1,audioInverseRight.length-1-q);
                    Arrays.fill(audioImpOpLeft, 0, q, (short) 0);
                    Arrays.fill(audioImpOpRight, 0, q, (short) 0);
//                System.arraycopy(audioImpOpLeft,0,audioImpOpLeftOut,0,audioImpOpLeft.length-1-q);
                    for (int i = 0; i < audioInverseLeft.length; i++) {
                        lCount = recSamples[i] * audioImpOpLeft[i] + lCount;
                        rCount = recSamples[i] * audioImpOpRight[i] + rCount;
                    }
//                    lConv[audioInverseLeft.length+q] = (short) (lCount*Short.MAX_VALUE/Integer.MAX_VALUE);
//                    rConv[audioInverseRight.length+q] = (short) (rCount*Short.MAX_VALUE/Integer.MAX_VALUE);
//                    Log.e("File", "wavWriter: 2ndconv st"+"q: "+q+" leng:"+ recSamples.length);
//                    lConv[audioInverseLeft.length+q] = (short) lCount;
//                    rConv[audioInverseLeft.length+q] = (short) rCount;
//                    lConv[audioInverseLeft.length+q] = Integer.valueOf(lCount).shortValue();
//                    rConv[audioInverseLeft.length+q] = Integer.valueOf(rCount).shortValue();
//                    double lopdouble = (double) (Integer.valueOf(lCount).doubleValue()* Short.valueOf(Short.MAX_VALUE).doubleValue()/ Integer.valueOf(Integer.MAX_VALUE).doubleValue());
//                    double ropdouble = (double) (Integer.valueOf(lCount).doubleValue()* Short.valueOf(Short.MAX_VALUE).doubleValue()/ Integer.valueOf(Integer.MAX_VALUE).doubleValue());
//                    lConv[audioInverseLeft.length+q] =  Double.valueOf(lopdouble).shortValue();
//                    rConv[audioInverseLeft.length+q] = Double.valueOf(ropdouble).shortValue();
                    lCountIntArray[audioInverseLeft.length+q] = lCount;
                    rCountIntArray[audioInverseLeft.length+q] = rCount;
                }
                float lmax = 0,rmax = 0;
                for (int i = 0; i < lCountIntArray.length; i++) {
                    if (Math.abs(lCountIntArray[i]) > lmax){
                        lmax = Math.abs(lCountIntArray[i]);
                    }
                    if (Math.abs(rCountIntArray[i]) > rmax){
                        rmax = Math.abs(rCountIntArray[i]);
                    }
                }
//                for(int i = 0; i < lCountIntArray.length; i++){
//                    lConv[i] =(short) (Math.round(lCountIntArray[i]*Short.MAX_VALUE/(double) lmax));
//                    rConv[i] =(short) (Math.round(rCountIntArray[i]*Short.MAX_VALUE/(double) rmax));
//                }
                for(int i = 0; i < lCountIntArray.length; i++){
                    lConv[i] =(short) (lCountIntArray[i] >> 16);
                    rConv[i] =(short) (rCountIntArray[i] >> 16);
                }
                Log.e("File", "wavWriter: 2 conv ok");
                int iil = 0,iir = 0;
                for (int i = 0; i < stConv.length; i++){
                    if ((i % 2) == 0) {
                        // number is even
                        stConv[i] = lConv[iil];
                        iil++;
                    }
                    else {
                        // number is odd
                        stConv[i] = rConv[iir];
                        iir++;
                    }
                }
                Log.e("File", "wavWriter: "+" stConv: "+stConv);
//                for (int i = 0; i < stConv.length; i++) {
//                    stConv[i] = Short.reverseBytes(stConv[i]);
//                }
//                stConvbyte = ShortToByte_ByteBuffer_Method(Short.reverseBytes(audioSamples));
                stConvbyte = ShortToByte_Twiddle_Method(stConv);
                wavWriter(stConvbyte,numChanS,bitDepthS,samplingRateI,dir.toString()+"/"+recordingTitle);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    byte [] ShortToByte_Twiddle_Method(short [] input)
    {
        int short_index, byte_index;
        int iterations = input.length;

        byte [] buffer = new byte[input.length * 2];

        short_index = byte_index = 0;

        for(/*NOP*/; short_index != iterations; /*NOP*/)
        {
            buffer[byte_index]     = (byte) (input[short_index] & 0x00FF);
            buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

            ++short_index; byte_index += 2;
        }

        return buffer;
    }
    public void wavWriter (byte[] audioByte,short nChannels,short mBitsPersample, int sRate,String filePath){
        int payloadSize = audioByte.length;
        try {
            Log.e("title","Rectitle: "+recordingTitle);
            RandomAccessFile randomAccessWriter = new RandomAccessFile(dir.toString()+"/"
                    +recordingTitle.substring(0,recordingTitle.length()-4)+"cnv.wav", "rw");
            randomAccessWriter.setLength(0); // Set file length to 0, to prevent unexpected behavior in case the file already existed
            randomAccessWriter.writeBytes("RIFF");
            randomAccessWriter.writeInt(0); // Final file size not known yet, write 0
            randomAccessWriter.writeBytes("WAVE");
            randomAccessWriter.writeBytes("fmt ");
            randomAccessWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
            randomAccessWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
            randomAccessWriter.writeShort(Short.reverseBytes(nChannels));// Number of channels, 1 for mono, 2 for stereo
            randomAccessWriter.writeInt(Integer.reverseBytes(sRate)); // Sample rate
            randomAccessWriter.writeInt(Integer.reverseBytes(sRate * nChannels * mBitsPersample / 8)); // Byte rate, SampleRate*NumberOfChannels*mBitsPersample/8
            randomAccessWriter.writeShort(Short.reverseBytes((short) (nChannels * mBitsPersample / 8))); // Block align, NumberOfChannels*mBitsPersample/8
            randomAccessWriter.writeShort(Short.reverseBytes(mBitsPersample)); // Bits per sample
            randomAccessWriter.writeBytes("data");
            randomAccessWriter.writeInt(0); // Data chunk size not known yet, write 0
            randomAccessWriter.seek(44);
            randomAccessWriter.write(audioByte);
            randomAccessWriter.seek(4); // Write size to RIFF header
            randomAccessWriter.writeInt(Integer.reverseBytes(36 + payloadSize));
            randomAccessWriter.seek(40); // Write size to Subchunk2Size field
            randomAccessWriter.writeInt(Integer.reverseBytes(payloadSize));
            randomAccessWriter.close();
            Log.e("File", "wavWriter: done" );

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private File createCacheFile( int resourceId, String filename)
            throws IOException {
        File cacheFile = new File(this.getCacheDir(), filename);

        if (cacheFile.createNewFile() == false) {
            cacheFile.delete();
            cacheFile.createNewFile();
        }

        // from: InputStream to: FileOutputStream.
        InputStream inputStream = this.getResources().openRawResource(resourceId);
        FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);

        byte[] buffer = new byte[1024 * 512];
        while (inputStream.read(buffer, 0, 1024 * 512) != -1) {
            fileOutputStream.write(buffer);
        }

        fileOutputStream.close();
        inputStream.close();

        return cacheFile;
    }
    byte [] ShortToByte_ByteBuffer_Method(short [] input)
    {
        int index;
        int iterations = input.length;

        ByteBuffer bb = ByteBuffer.allocate(input.length * 2);

        for(index = 0; index != iterations; ++index)
        {
            bb.putShort(input[index]);
        }

        return bb.array();
    }
//    public void fileClose (File cacheFile){
//        try {
//            if (!(randomAccessFile == null)) {
//                randomAccessFile.close();
//                cacheFile.delete();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }


}
