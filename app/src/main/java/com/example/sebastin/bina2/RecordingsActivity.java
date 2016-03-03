package com.example.sebastin.bina2;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.nfc.Tag;
import android.os.Environment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.MediaController;
import org.w3c.dom.Text;
import android.widget.MediaController.MediaPlayerControl;

import com.h6ah4i.android.media.IBasicMediaPlayer;
import com.h6ah4i.android.media.IMediaPlayerFactory;
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerFactory;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


public class RecordingsActivity extends Activity{
    public ListView listView;
    public ArrayAdapter arrayAdapter;
    public String []  android_versions = {"1","2","3"};
    public String directory = "/BinaRecordings";
    public String filename = "Grabacion";
    public String format = ".wav";
    public File root = Environment.getExternalStorageDirectory();
    public File dir = new File(root.getAbsolutePath() + directory);
    public String filelist[];
    public String listviewitems[] = {"No hay Grabaciones"};
    public long totalspace = dir.getTotalSpace();
    public int listcontrol = 0;
    public String filetoplay = "file";
    TextView text;
    public mPlayer mP = new mPlayer();
    public Visualizer vs = new Visualizer(1);
    public byte [] visbytes;
    public int captureSizeRange[];
    private static final String TAG = RecordingsActivity.class.getSimpleName();
    public MediaController.MediaPlayerControl player;
    public Visualizer.MeasurementPeakRms measurement = new Visualizer.MeasurementPeakRms();
    public int pk;
    public int rate = 15;
    public Visualizer.OnDataCaptureListener listener;
    public AudioRead aR;
    public byte [] bufar;
    public int bufOffSet = 0;
    public byte [] myData;
    public GraphView graph;
    public float rmsValue = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        Bundle bundle = getIntent().getExtras();
        directory = bundle.getString("RecordActivitydirectory");
        filename = "Grabacion";
        format = ".wav";
        root = Environment.getExternalStorageDirectory();
        dir = new File(root.getAbsolutePath() + directory);
        filelist = dir.list();
        text = (TextView)findViewById(R.id.textViewR);
        text.setText(""+totalspace);
        graph = (GraphView) findViewById(R.id.graph);
        listView = (ListView) findViewById(R.id.listView);
        listviewitems = filelist;
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, listviewitems);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mP.getState().equals(mPlayer.playerState.STOPPED)){
                   listcontrol = 0;
                }
                if (!filetoplay.equals(dir.toString()+"/"+parent.getItemAtPosition(position).toString()) && (mP.getState().equals(mPlayer.playerState.PLAYING)))
                {
                    //cambio de grabacion si playing
                    reproduccion();
                }
                filetoplay = dir.toString()+"/"+parent.getItemAtPosition(position).toString();
                aR = new AudioRead();
                aR.setAudioRead(filetoplay);
                aR.getAudioMetaData();
                bufar = aR.getbufAudioRead(32000);
                //FileChannel fc = aR.getByteAudioRead();
                reproduccion();
                if (mP.getState().equals(mPlayer.playerState.PLAYING)){
                    Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " "+mPlayer.playerState.PLAYING.toString(), Toast.LENGTH_SHORT).show();
                }
                else if (mP.getState().equals(mPlayer.playerState.STOPPED)){
                    Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) +" "+mPlayer.playerState.STOPPED.toString(), Toast.LENGTH_SHORT).show();
                }
               // vs.setEnabled(false);
               // captureSizeRange  = vs.getCaptureSizeRange();
               // vs.setCaptureSize(captureSizeRange[1]);
               // vs.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);
//                vs.setDataCaptureListener (Visualizer.OnDataCaptureListener listener, int rate, boolean waveform, boolean fft)
               // vs.setEnabled(true);
                text.setText("" + pk);
//                vs.getWaveForm(visbytes);
//                peakRMS();
               // vs.setDataCaptureListener(listener,rate,true,false);
//                pk = vs.getMeasurementPeakRms(measurement);
//                peakRMS();
                // create

            }
        });
    }
    public void reproduccion ()
    {
        new Thread(new Task()).start();
        if (listcontrol == 0) {
            try {
                mP.startPlayBack(filetoplay);
            } catch (Exception e) {
                e.printStackTrace();
            }
            listcontrol = 1;
        }
        else if (listcontrol == 1){
            mP.stopPlayback();
            listcontrol = 0;
            bufOffSet = 0;
        }

    }
    public class Task implements Runnable {

        @Override
        public void run() {
            bufOffSet = 0;
            aR.setAudioRead(filetoplay);
            //while (bufOffSet <= 10000) {
                //myData = aR.getbufAudioRead(bufOffSet);
                for (int i = 0; i <= 500; i++) {
                    //final LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                    //      new DataPoint(i, myData[i]),
                    
                    //});
                    rmsValue = rmsValue+(bufar[i]*bufar[i]);
                    bufOffSet =  i;
                    text.post(new Runnable() {
                        @Override
                        public void run() {
                            //graph.addSeries(series);
                            text.setText(""+bufar[bufOffSet]);
                        }
                    });
                    }
            //}
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recordings, menu);
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
    public void convolutionActivity(MenuItem item) {
        Intent intent = new Intent(this,ConvolutionActivity.class);
        startActivity(intent);
    }

}
