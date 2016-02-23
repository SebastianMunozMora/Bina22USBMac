package com.example.sebastin.bina2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.RandomAccess;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static android.media.AudioFormat.CHANNEL_CONFIGURATION_STEREO;


public class MainActivity extends AppCompatActivity {
    EditText editT;
    String message,PlayFilePath;
    Button boton,boton1;
    ListView listView;
    public static TextView texto;
    AudioRecord audioRecord;
    File recordingFile;
    boolean isRecording = false,isPlaying = false;
    int frequency, channelConfiguration, audioEncoding, bufferSize, offsetInShorts, sizeInShorts,
            readMode;
    public startRecording sr = new startRecording();
    public mPlayer mP = new mPlayer();
    private WavAudioRecorder mRecorder;
    ArrayAdapter arrayAdapter;
    String []  android_versions = {"1","2","3"};
    public String mRecordFilePath;
    public String directory = "/BinaRecordings";
    public String filename = "Grabacion";
    public String format = ".wav";
    public File root = Environment.getExternalStorageDirectory();
    public File dir = new File(root.getAbsolutePath() + directory);
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, filename+format);
        mRecordFilePath = file.toString();
        File f = new File(mRecordFilePath);
        String [] filelist = f.list();
        editT = (EditText) findViewById(R.id.editText);
        boton = (Button) findViewById(R.id.button);
        boton1 = (Button) findViewById(R.id.button2);

        texto = (TextView) findViewById(R.id.textView);
        mRecorder = WavAudioRecorder.getInstance();
        String nRec = editT.getText().toString();


}
    public void grabacion (View view)
    {
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + directory);
        if (!dir.exists()) {
            dir.mkdir();
        }
        filename = editT.getText().toString();
        File file = new File(dir, filename+format);
        mRecordFilePath = file.toString();
        if (mRecorder.getState().equals(WavAudioRecorder.State.INITIALIZING)) {
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
        startActivity(intent);
    }
}
