package com.example.sebastin.bina2;


import android.content.Intent;
import android.media.audiofx.Visualizer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;


public class RecordingsActivity extends AppCompatActivity {
    public ListView listView;
    public ArrayAdapter arrayAdapter;
    public String []  android_versions = {"1","2","3"};
    public String directory = "/BinaRecordings";
    public String filename = "Grabacion";
    public String format = ".wav";
    public File root = Environment.getExternalStorageDirectory();
    public File dir = new File(root.getAbsolutePath() + directory);
    public String filelist[] = dir.list();
    public String listviewitems[] = {"No hay Grabaciones"};
    public long totalspace = dir.getTotalSpace();
    public int listcontrol = 0;
    public String filetoplay = "file";
    TextView text;
    public mPlayer mP = new mPlayer();
    public Visualizer vs = new Visualizer(0);
    public byte [] visbytes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        text = (TextView)findViewById(R.id.textViewR);
        text.setText(""+totalspace);
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
                reproduccion();
                //vs.setEnabled(true);
                //vs.getWaveForm(visbytes);
                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + "is selected", Toast.LENGTH_SHORT).show();
               // text.setText(filetoplay);
            }
        });
    }
    public void reproduccion ()
    {
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
