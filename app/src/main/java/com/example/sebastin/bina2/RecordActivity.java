package com.example.sebastin.bina2;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;



public class RecordActivity extends AppCompatActivity {
    EditText editT;
    Button boton;
    public static TextView texto,textLeftdB, textRightdB;
    public mPlayer mP;
    private WavAudioRecorder mRecorder;
    public String mRecordFilePath;
    public String directory = "/BinaRecordings";
    public String filename = "Grabacion";
    public String format = ".wav";
    String mVisualizerFilePath;
    public File root = Environment.getExternalStorageDirectory();
    public File dir = new File(root.getAbsolutePath() + directory);
    public File file;
    double leftRms = 0,rightRms = 0,leftDbfs = 0,rightDbfs = 0;
    byte [] wavBuffer = new byte[10000];
    ByteBuffer bb;
    short[] micData = new short[wavBuffer.length/2];
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
    public byte [] bufar;
    File filevs;
    LedMeter leftLedMeter;
    LedMeter rightLedMeter;
    TabHost th;
    ArrayAdapter titleAdapter,dataAdapter,imageAdapter;
    ListView listView;
    Chronometer timer;
    String listviewitems[] = {"No hay Grabaciones"};
    SeekBar seekBar;
    String filetoplay = null;
    AudioRead aR;
    RecordingsAdapter recordingsAdapter;
    long elapsedMillis = 0,clockStart = 0,clockStop = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        th = (TabHost)findViewById(R.id.tabHost);
        //Record Tab
        th.setup();
        final TabHost.TabSpec tsRecord = th.newTabSpec("Grabar");
        tsRecord.setIndicator("Grabar");
        tsRecord.setContent(R.id.linearLayout);
        th.addTab(tsRecord);
        //Recordings Tab
        th.setup();
        final TabHost.TabSpec tsRecordings = th.newTabSpec("Grabaciones");
        tsRecordings.setIndicator("Grabaciones");
        tsRecordings.setContent(R.id.linearLayout2);
        th.addTab(tsRecordings);
        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals(tsRecord.getTag())) {
                    //destroy earth
//                    boton.setText("Grabar");
                    resetChrono();
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
                    titleAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_view_custom_layout, R.id.list_item, listviewitems);
                    recordingsAdapter = new RecordingsAdapter(getApplicationContext(),R.layout.list_view_custom_layout);
                    for (int i = 0;i < dir.list().length;i++){
                        RecordingsDataProvider recordingsDataProvider = new RecordingsDataProvider(R.drawable.cs_10em_angle_gal,listviewitems[i],"a");
                        recordingsAdapter.add(recordingsDataProvider);
                    }
                    listView.setAdapter(recordingsAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView parent, View view, int position, long id) {
                            TextView tv = (TextView)view.findViewById(R.id.list_item);
                            String recordingTitle = tv.getText().toString();
                            view.setSelected(true);
                            if (filetoplay == null){
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
                            texto.setText(mP.getState().toString());
                            new Thread(new PlayTask()).start();
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
        Bundle bundle = getIntent().getExtras();
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
//        boton = (Button) findViewById(R.id.button);
        texto = (TextView) findViewById(R.id.textView);
        textLeftdB = (TextView)findViewById(R.id.textLeftdB);
        textRightdB = (TextView)findViewById(R.id.textRightdB);
        mRecorder = WavAudioRecorder.getInstance(0, 1);
        filevs = new File(dir, filename+"vis"+format);
        dir = new File(root.getAbsolutePath() + directory);
        mVisualizerFilePath = filevs.toString();
        timer = (Chronometer)findViewById(R.id.chronometer);
    }
    public void grabacionBoton(View view){
        if(!mRecorder.getState().equals(WavAudioRecorder.State.RECORDING)){
//            boton.setText("Grabando");
            startChrono();
            startRecording();
            texto.setText("" + mRecorder.getState());
        }else {
            pauseRecording();
//            boton.setText("Grabar");
            stopChrono();
            texto.setText("" + mRecorder.getState());
        }
    }
    public void stopButton(View view){
        stopRecording();
        resetChrono();
//        boton.setText("Grabar");
        texto.setText(""+mRecorder.getState());
    }
    public void startRecording () {
        dir = new File(root.getAbsolutePath() + directory);
        filename = editT.getText().toString();
        file = new File(dir, filename+format);
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
                case 44100:
                    sampleState = 1;
                    break;
                case 48000:
                    sampleState = 0;
                    break;
            }
            mRecorder = WavAudioRecorder.getInstance(sampleState, bithState);
            mRecorder.setOutputFile(mRecordFilePath);
            mRecorder.prepare();
            mRecorder.start();
            new Thread(new RecTask()).start();
        }
        if (mRecorder.getState().equals(WavAudioRecorder.State.PAUSED)){
            mRecorder.start();
            new Thread(new RecTask()).start();
        }
    }
    public void pauseRecording(){
        if (mRecorder.getState().equals(WavAudioRecorder.State.RECORDING)) {
            mRecorder.pause();
        }

    }
    public void stopRecording(){
        if (mRecorder.getState().equals(WavAudioRecorder.State.ERROR) || mRecorder.getState().equals(WavAudioRecorder.State.RECORDING ) || mRecorder.getState().equals(WavAudioRecorder.State.PAUSED)) {
            mRecorder.stop();
            mRecorder.release();
            recCount+=1;
            filename = editT.getText().toString()+recCount;
            editT.setText(filename);

        }
    }

    public void reproduccion () {
        switch (mP.getState()){
            case INITIALIZED:
                try {
                    mP.startPlayBack();
                    startChrono();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PLAYING:
                mP.pausePlayback();
                stopChrono();
                break;
            case STOPPED:
                    mP.setFilePath(filetoplay);
                try {
                    mP.startPlayBack();
                    resetChrono();
                    startChrono();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case PAUSED:
                try {
                    startChrono();
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
                textLeftdB.post(new Runnable() {
                    @Override
                    public void run() {
                        textLeftdB.setText("" + Math.abs(-80 - micLeftDbfs) + "dB Fs ");
                    }
                });
                textRightdB.post(new Runnable() {
                    @Override
                    public void run() {
                        textRightdB.setText("" + Math.abs(-80 - micRightDbfs) + "dB Fs ");
                    }
                });
                leftLedMeter.post(new Runnable() {
                    @Override
                    public void run() {
                        leftLedMeter.setLevel(Math.abs(-80 - micLeftDbfs), 80);
                    }
                });
                rightLedMeter.post(new Runnable() {
                    @Override
                    public void run() {
                        rightLedMeter.setLevel(Math.abs(-80 - micRightDbfs), 80);
                    }
                });
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
                leftLedMeter.post(new Runnable() {
                    @Override
                    public void run() {
                        leftLedMeter.setLevel(Math.abs(-80 - leftDbfs), 80);
                    }
                });
                rightLedMeter.post(new Runnable() {
                    @Override
                    public void run() {
                        rightLedMeter.setLevel(Math.abs(-80 - rightDbfs), 80);
                    }
                });
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

    public void recordingsActivity(MenuItem item) {
        Intent intent = new Intent(this,RecordingsActivity.class);
        intent.putExtra("RecordActivitydirectory", directory);
        stopRecording();
        filevs.delete();
        startActivity(intent);
    }


}
