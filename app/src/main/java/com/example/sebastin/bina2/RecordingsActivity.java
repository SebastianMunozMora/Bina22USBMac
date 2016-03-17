package com.example.sebastin.bina2;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.DashPathEffect;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Visualizer;
import android.nfc.Tag;
import android.os.Environment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.MediaController;
import org.w3c.dom.Text;
import android.widget.MediaController.MediaPlayerControl;


import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class RecordingsActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    Button backButton;
    int[] samplesRate ={48000,44100, 22050, 11025, 8000};
    int[] bitsPerSample ={16,8};
    int currentSampleRate = 44100;
    int currentBitsPerSample = 16;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        expandableListView = (ExpandableListView)findViewById(R.id.expandableListView);
        backButton = (Button)findViewById(R.id.home);
        final List<String>headings = new ArrayList<String>();
        final List<String>sampleRates = new ArrayList<String>();
        List<String>bitDepths = new ArrayList<String>();
        final HashMap<String,List<String>>childList = new HashMap<String,List<String>>();
        final String headingItems[] = getResources().getStringArray(R.array.header_titles);
        final String samplingRates[] = getResources().getStringArray(R.array.sampling_rates);
        final String bitDepth[] = getResources().getStringArray(R.array.bit_depth);
        for (String title: headingItems){
            headings.add(title);
        }
        for (String title: samplingRates){
            sampleRates.add(title);
        }
        for (String title: bitDepth){
            bitDepths.add(title);
        }
        childList.put(headings.get(0),sampleRates);
        childList.put(headings.get(1),bitDepths);
        MyAdapter myAdapter = new MyAdapter(this,headings,childList);
        expandableListView.setAdapter(myAdapter);
        // Listview on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String currentChild = childList.get(headings.get(groupPosition)).get(childPosition);
                String currentGroup = headings.get(groupPosition);
                if (currentGroup.equals(headingItems[0])) {
                    for (int i = 0; i < sampleRates.size(); i++) {
                        if (currentChild.equals(samplingRates[i])) {
                            SettingsClass.getInstance().setSampleRate(samplesRate[i]);
                            break;
                        }
                    }
                }
                if (currentGroup.equals(headingItems[1])) {
                    for (int i = 0; i < bitsPerSample.length; i++) {
                        if (currentChild.equals(bitDepth[i])) {
                            SettingsClass.getInstance().setBitDepth(bitsPerSample[i]);
                            break;
                        }
                    }
                }
                Toast.makeText(
                        getApplicationContext(),
                        currentGroup
                                + " : "
                                + currentChild, Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
    }
    public int getCurrentSampleRate (){
        return currentSampleRate;
    }
    public int getCurrentBitsPerSample (){
        return currentBitsPerSample;
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
