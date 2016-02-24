package com.example.sebastin.bina2;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import static com.example.sebastin.bina2.R.raw.mi;

public class ConvolutionActivity extends AppCompatActivity {
    //public mPlayerResRaw mP = new mPlayerResRaw();
    public String filetoplay = "file";
    public int listcontrol = 0;
    public MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convolution);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        filetoplay = ""+R.raw.mi;
        mp = MediaPlayer.create(this, R.raw.animp100);
    }
    public void playImpulse (View view)
    {
//        if (mP.getState().equals(mPlayer.playerState.STOPPED)){
//            listcontrol = 0;
//        }
//        if (listcontrol == 0) {
//            try {
//
//                mP.startPlayBack(filetoplay);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            listcontrol = 1;
//        }
//        else if (listcontrol == 1){
//            mP.stopPlayback();
//            listcontrol = 0;
//        }
        mp.start();

    }

}
