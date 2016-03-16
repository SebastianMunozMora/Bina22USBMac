package com.example.sebastin.bina2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;
import java.nio.ByteBuffer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;



public class RecordActivity extends AppCompatActivity {
    EditText editT;
    Button boton;
    public static TextView texto,textLeftdB, textRightdB;
    public mPlayer mP = new mPlayer();
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
    int bithState = 0;
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
    ArrayAdapter arrayAdapter;
    ListView listView;
    Chronometer timer;
    String listviewitems[] = {"No hay Grabaciones"};
    int listcontrol = 0;
    String filetoplay ="Grabacion";
    AudioRead aR;
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
        th.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals(tsRecord.getTag())) {
                    //destroy earth
                    boton.setText("Grabar");
                    timer.stop();
                }
                if(tabId.equals(tsRecordings.getTag())) {
                    //destroy mars
                    stopRecording();
                    filevs.delete();
                    timer.stop();
                    leftLedMeter.setLevel(0, 80);
                    rightLedMeter.setLevel(0,80);
                    listView = (ListView) findViewById(R.id.listView);
                    listviewitems = dir.list();
                    arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_expandable_list_item_1, listviewitems);
                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView parent, View view, int position, long id) {
                            if (mP.getState().equals(mPlayer.playerState.STOPPED)) {
                                listcontrol = 0;
                            }
                            if (!filetoplay.equals(dir.toString() + "/" + parent.getItemAtPosition(position).toString()) && (mP.getState().equals(mPlayer.playerState.PLAYING))) {
                                //cambio de grabacion si playing
                                reproduccion();
                            }
                            filetoplay = dir.toString() + "/" + parent.getItemAtPosition(position).toString();
                            aR = new AudioRead();
                            aR.setAudioRead(filetoplay, 100);
                            reproduccion();
                            if (mP.getState().equals(mPlayer.playerState.PLAYING)) {
                                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " " + mPlayer.playerState.PLAYING.toString(), Toast.LENGTH_SHORT).show();
                            } else if (mP.getState().equals(mPlayer.playerState.STOPPED)) {
                                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " " + mPlayer.playerState.STOPPED.toString(), Toast.LENGTH_SHORT).show();
                            }
//
                        }
                    });
                }
            }});
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
        boton = (Button) findViewById(R.id.button);
        texto = (TextView) findViewById(R.id.textView);
        textLeftdB = (TextView)findViewById(R.id.textLeftdB);
        textRightdB = (TextView)findViewById(R.id.textRightdB);
        mRecorder = WavAudioRecorder.getInstance(0,0);
        filevs = new File(dir, filename+"vis"+format);
        dir = new File(root.getAbsolutePath() + directory);
        mVisualizerFilePath = filevs.toString();
        timer = (Chronometer)findViewById(R.id.chronometer);
}
    public void grabacionBoton(View view){
        if(boton.getText().equals("Grabar")){
            boton.setText("Grabando");
            startRecording();
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();
            texto.setText("" + mRecorder.getState());
        }else if(boton.getText().equals("Grabando")){
            stopRecording();
            boton.setText("Grabar");
            timer.stop();
            texto.setText("" + mRecorder.getState());
        }
    }
    public void startRecording () {
        dir = new File(root.getAbsolutePath() + directory);
        filename = editT.getText().toString();
        file = new File(dir, filename+format);
        mRecordFilePath = file.toString();
        if (mRecorder.getState().equals(WavAudioRecorder.State.INITIALIZING)) {
            bitDepth = SettingsClass.getInstance().getBitDepth();
            sampleRate = SettingsClass.getInstance().getSampleRate();
            switch (bitDepth){
                case 8:
                    bithState = 1;
                    break;
                case 16:
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
    }
    public void stopRecording(){
        if (mRecorder.getState().equals(WavAudioRecorder.State.ERROR) || mRecorder.getState().equals(WavAudioRecorder.State.RECORDING)) {
            mRecorder.release();
            mRecorder = WavAudioRecorder.getInstance(0,0);
            mRecorder.setOutputFile(mRecordFilePath);
        }


    }

    public void reproduccion () {
        if (listcontrol == 0) {
            try {
                mP.startPlayBack(filetoplay);
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
                new Thread(new PlayTask()).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            listcontrol = 1;
        }
        else if (listcontrol == 1){
            mP.stopPlayback();
            timer.stop();
            listcontrol = 0;
        }

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
                        leftLedMeter.setLevel(Math.abs(-80 - leftDbfs), 120);
                    }
                });
                rightLedMeter.post(new Runnable() {
                    @Override
                    public void run() {
                        rightLedMeter.setLevel(Math.abs(-80 - rightDbfs), 120);
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
         micRightRms = (int) Math.sqrt(micRightRms/(micRightBuffer.length));
         micLeftRms = (int) Math.sqrt(micLeftRms/(micLeftBuffer.length));
         if (micLeftRms >= 0.001) {
             micLeftDbfs = Math.round(10 * Math.log10(micLeftRms / 32768));
         }
         else{
             micLeftDbfs = -80;
         }
         if (micRightRms >= 0.001) {
             micRightDbfs = Math.round(10 * Math.log10(micRightRms / 32768));
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
        if (!mRecorder.getState().equals(WavAudioRecorder.State.INITIALIZING)) {
            mRecorder.stop();
            mRecorder.release();
        }
        filevs.delete();
        startActivity(intent);
    }


}
