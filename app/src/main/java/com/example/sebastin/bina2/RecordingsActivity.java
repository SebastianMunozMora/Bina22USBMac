package com.example.sebastin.bina2;


import android.graphics.Typeface;

import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RecordingsActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    Button backButton;
    int[] samplesRate ={48000,44100, 22050, 11025, 8000};
    int[] bitsPerSample ={24,16,8};
    int[] numCh = {2,1};
    int currentSampleRate = 44100;
    int currentBitsPerSample = 16;

    TextView actionTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/OPTIMA.TTF");
        actionTextView = (TextView)findViewById(R.id.actionText);
        actionTextView.setTypeface(typeface,Typeface.BOLD);
        actionTextView.setText("Configuración");
        expandableListView = (ExpandableListView)findViewById(R.id.expandableListView);
        backButton = (Button)findViewById(R.id.home);
        final List<String>headings = new ArrayList<String>();
        final List<String>sampleRates = new ArrayList<String>();
        List<String>bitDepths = new ArrayList<String>();
        List<String>numChannel = new ArrayList<String>();
        List<String>visState = new ArrayList<String>();
        final HashMap<String,List<String>>childList = new HashMap<String,List<String>>();
        final String headingItems[] = getResources().getStringArray(R.array.header_titles);
        final String samplingRates[] = getResources().getStringArray(R.array.sampling_rates);
        final String numChannels[] = getResources().getStringArray(R.array.number_channels);
        final String visualizerStates[] = getResources().getStringArray(R.array.visualizer_states);
//        final String bitDepth[] = getResources().getStringArray(R.array.bit_depth);
        for (String title: headingItems){
            headings.add(title);
        }
        for (String title: samplingRates){
            sampleRates.add(title);
        }
        for (String title: numChannels){
            numChannel.add(title);
        }
        for (String title: visualizerStates){
            visState.add(title);
        }
        childList.put(headings.get(0),sampleRates);
        childList.put(headings.get(1),numChannel);
        childList.put(headings.get(2),visState);
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
                    for (int i = 0; i < numChannels.length; i++) {
                        if (currentChild.equals(numChannels[i])) {
                            SettingsClass.getInstance().setNumCh(numCh[i]);
                            break;
                        }
                    }
                }
                if (currentGroup.equals(headingItems[2])) {
                    for (int i = 0; i < visualizerStates.length; i++) {
                        if (currentChild.equals(visualizerStates[i])) {
                            SettingsClass.getInstance().setVisualizerState(visualizerStates[i]);
                            break;
                        }
                    }
                }
//                if (currentGroup.equals(headingItems[1])) {
//                    for (int i = 0; i < bitsPerSample.length; i++) {
//                        if (currentChild.equals(bitDepth[i])) {
//                            SettingsClass.getInstance().setBitDepth(bitsPerSample[i]);
//                            break;
//                        }
//                    }
//                }
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


}
