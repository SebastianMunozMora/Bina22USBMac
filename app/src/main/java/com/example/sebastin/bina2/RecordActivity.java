package com.example.sebastin.bina2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;

import android.media.AudioRecord;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
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
    public String mRecordFilePath;
    public String directory = "/BinaRecordings";
    public String filename = "Grabacion";
    public String format = ".wav";
    public File root = Environment.getExternalStorageDirectory();
    public File dir = new File(root.getAbsolutePath() + directory);
    public File file;
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
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);

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
            mRecorder = WavAudioRecorder.getInstance();
            mRecorder.setOutputFile(mRecordFilePath);
            mRecorder.prepare();
            mRecorder.start();
            boton.setText("Grabando");
        } else if (mRecorder.getState().equals(WavAudioRecorder.State.ERROR)) {
            mRecorder.release();
            mRecorder = WavAudioRecorder.getInstance();
            mRecorder.setOutputFile(mRecordFilePath);
            boton.setText("Grabar");
        } else {
            mRecorder.stop();
            mRecorder.reset();
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
        intent.putExtra("RecordActivitydirectory",directory );
        startActivity(intent);
    }
}
