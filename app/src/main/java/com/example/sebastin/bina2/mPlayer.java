package com.example.sebastin.bina2;

/**
 * Created by Sebastián on 17/02/2016.
 */

import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;


public  class mPlayer {
    public enum playerState  {INITIALIZED,PLAYING,STOPPED,PAUSED}
    private playerState        playerState;
    MediaPlayer Play;

    public mPlayer (){
        Play = new MediaPlayer();

    }
    public void setFilePath(String filePath){
        try {
            Play = new MediaPlayer();
            Play.reset();
            Play.setDataSource(filePath);
            Play.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerState = playerState.INITIALIZED;
    }
    public  void startPlayBack  ()throws Exception{
            Play.start();
            Play.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer Play) {
                    playerState = playerState.STOPPED;
                    Play.release();
                }
            });
            playerState = playerState.PLAYING;

    }
    public void pausePlayback (){
        Play.pause();
        playerState = playerState.PAUSED;
    }
    public void stopPlayback () {
            Play.stop();
            Play.release();
            Play = null;
            playerState = playerState.STOPPED;
    }

    public  playerState getState(){
        return playerState;
    }
    public MediaPlayer getMedia (){return Play;}

}
