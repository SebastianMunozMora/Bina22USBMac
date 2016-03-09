package com.example.sebastin.bina2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Collections;

import android.media.AudioRecord;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class RecordActivity extends AppCompatActivity {
    EditText editT;
    String message,PlayFilePath;
    Button boton,boton1;
    ListView listView;
    public static TextView texto;
    AudioRecord audioRecord;
    boolean isRecording = false,isPlaying = false;
    int frequency, channelConfiguration, audioEncoding, bufferSize, offsetInShorts, sizeInShorts,
            readMode;
    public mPlayer mP = new mPlayer();
    private WavAudioRecorder mRecorder;
    private WavAudioRecorder micVis;
    public String mRecordFilePath;
    public String directory = "/BinaRecordings";
    public String filename = "Grabacion";
    public String format = ".wav";
    String mVisualizerFilePath;
    public File root = Environment.getExternalStorageDirectory();
    public File dir = new File(root.getAbsolutePath() + directory);
    public File file;
    byte [] wavBuffer = new byte[20000];
    int r = 0;
    ByteBuffer bb;
    short[] micData = new short[10000];
    short [] micLeftBuffer = new short [5000];
    short[] micRightBuffer = new short [5000];
    int il = 0;
    int ir = 0;
    double micLeftRms = 0;
    double micRightRms = 0;
    double micLeftMax = 0;
    double micRightMax = 0;
    File filevs;
    GraphView graph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
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
        mRecorder = WavAudioRecorder.getInstance();
        filevs = new File(dir, filename+"vis"+format);
        dir = new File(root.getAbsolutePath() + directory);
        mVisualizerFilePath = filevs.toString();
        micVis = WavAudioRecorder.getInstance();
        micVis.setOutputFile(mVisualizerFilePath);
        micVis.prepare();
        micVis.start();
        new Thread(new Task()).start();
        graph = (GraphView) findViewById(R.id.graph);
        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.75);
        graph.getViewport().setMaxX(2.25);

// set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-80);
        graph.getViewport().setMaxY(0);
}
    public void grabacion (View view)
    {
        dir = new File(root.getAbsolutePath() + directory);
        filename = editT.getText().toString();
        file = new File(dir, filename+format);
        if (file.exists()){
//            file.delete();
        }
        file = new File(dir, filename+format);
        mRecordFilePath = file.toString();
        if (mRecorder.getState().equals(WavAudioRecorder.State.INITIALIZING)) {
            micVis.stop();
            micVis.reset();
            mRecorder = WavAudioRecorder.getInstance();
            mRecorder.setOutputFile(mRecordFilePath);
            mRecorder.prepare();
            mRecorder.start();
            new Thread(new Task()).start();
            boton.setText("Grabando");
        } else if (mRecorder.getState().equals(WavAudioRecorder.State.ERROR)) {
            mRecorder.release();
            mRecorder = WavAudioRecorder.getInstance();
            mRecorder.setOutputFile(mRecordFilePath);
            boton.setText("Grabar");
        } else {
            mRecorder.stop();
            mRecorder.reset();
            micVis = WavAudioRecorder.getInstance();
            micVis.setOutputFile(mVisualizerFilePath);
            micVis.prepare();
            micVis.start();
            new Thread(new Task()).start();
            boton.setText("Grabar");
        }
        texto.setText(""+mRecorder.getState());
    }

    public void reproduccion (View view)
    {
        if (boton1.getText().equals("Play")) {
            PlayFilePath = mRecordFilePath;
            try {
                mP.startPlayBack(PlayFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            boton1.setText("Stop");
        }
        else if (boton1.getText().equals("Stop")){
            mP.stopPlayback();
            boton1.setText("Play");
        }

    }
    public class Task implements Runnable {
        @Override
        public void run() {
            while(micVis.getState().equals(WavAudioRecorder.State.RECORDING )|| mRecorder.getState().equals(WavAudioRecorder.State.RECORDING)) {
                micVisualizer();
                final BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                        new DataPoint(1, (20*Math.log10(micLeftRms/32768))),
                        new DataPoint(2, (20*Math.log10(micRightRms/32768)))
                });
                series.setSpacing(50);
                texto.post(new Runnable() {
                    @Override
                    public void run() {
                        graph.removeAllSeries();
                        graph.addSeries(series);
                        texto.setText("" + micLeftRms+" "+micRightRms);
                    }
                });
                try {
                    Thread.sleep(113);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
     public void micVisualizer (){
         if (mRecorder.getState().equals(WavAudioRecorder.State.RECORDING)) {
             wavBuffer = mRecorder.getBuffer();
         }
         else{
             wavBuffer = micVis.getBuffer();
         }
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
                     if (micLeftBuffer[ir] > micRightMax) {
                         micRightMax = micLeftBuffer[ir];
                     }
                     ir++;
                 }
             }
         micRightRms = (int) Math.sqrt(micRightRms/(micRightBuffer.length));
         micLeftRms = (int) Math.sqrt(micLeftRms/(micLeftBuffer.length));
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
        if (!micVis.getState().equals(WavAudioRecorder.State.INITIALIZING)) {
            micVis.stop();
            mRecorder.release();
        }
        if (!mRecorder.getState().equals(WavAudioRecorder.State.INITIALIZING)) {
            mRecorder.stop();
            mRecorder.release();
        }
        filevs.delete();
        startActivity(intent);
    }
}
