package com.example.sebastin.bina2;

/**
 * Created by Sebastián on 17/02/2016.
 */

import android.app.Application;
import android.media.MediaPlayer;
import android.net.Uri;

import java.net.URI;


public  class mPlayerResRaw extends Application{
    public enum playerState  {PLAYING,STOPPED}
    private playerState        playerState;
    public static  MediaPlayer Play;
    public mPlayerResRaw (){
        playerState = playerState.STOPPED;
        Play = new MediaPlayer();
    }

//    public void onCreate(String filePath) {
//        Uri myUri = Uri.parse(filePath);
////        uri(filePath);
//        super.onCreate();
//        final MediaPlayer mp = MediaPlayer.create(this, myUri);
//    }

    public  void startPlayBack  (String filePath)throws Exception{
        if (playerState == playerState.STOPPED) {
            Play = new MediaPlayer();
            Uri myUri = Uri.parse(filePath);
            Play.setDataSource(this,myUri);
            Play.prepare();
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
    }
    public void stopPlayback () {
        if (Play != null && (playerState == playerState.PLAYING)) {
            Play.stop();
            Play.release();
            Play = null;
            playerState = playerState.STOPPED;
        }
    }
    public  playerState getState(){
        return playerState;
    }
}
