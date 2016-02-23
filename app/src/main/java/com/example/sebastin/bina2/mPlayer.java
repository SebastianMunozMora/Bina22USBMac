package com.example.sebastin.bina2;

/**
 * Created by Sebastián on 17/02/2016.
 */

import android.media.MediaPlayer;



public  class mPlayer {
    public enum playerState  {PLAYING,STOPPED}
    private playerState        playerState;
    public static  MediaPlayer Play;
    public mPlayer (){
        playerState = playerState.STOPPED;
        Play = new MediaPlayer();
    }
    public  void startPlayBack  (String filePath)throws Exception{
        if (playerState == playerState.STOPPED) {
            Play = new MediaPlayer();
            Play.setDataSource(filePath);
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
