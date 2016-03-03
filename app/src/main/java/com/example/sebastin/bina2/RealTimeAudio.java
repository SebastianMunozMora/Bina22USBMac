package com.example.sebastin.bina2;

import android.app.Activity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Sebastian on 02/03/2016.
 */
public class RealTimeAudio extends Activity implements Runnable {
    public AudioRead aR;
    public byte [] myData;
    public int bufOffSet = 0;
    public String filetoplay;
    public mPlayer.playerState playerState;
    public RealTimeAudio (String myfiletoplay , mPlayer.playerState myplayerState){
        //def
        playerState = myplayerState;
        filetoplay = myfiletoplay;
    }
    @Override
    public void run() {

        }
}
