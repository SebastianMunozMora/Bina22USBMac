package com.example.sebastin.bina2;

import android.app.Application;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Sebastian on 02/03/2016.
 */
public class AudioRead extends Application {
    public byte []samples ;
    public String fileconv;
    public FileInputStream fis;
    public InputStream is  = null;
    private File root = Environment.getExternalStorageDirectory();
    public File dir;
    public String file;
    public BufferedInputStream buf;
    public int numbyte;
    public FileChannel fc;
    public AudioRead (){
        //def cons
    }
    public void setAudioRead (String filetoRead){
        try {
            file = filetoRead;
            buf = new BufferedInputStream(new FileInputStream(filetoRead));
            fis = new FileInputStream(filetoRead);
            numbyte = buf.available();
            samples = new byte[numbyte];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] getbufAudioRead(int byteOffset){
        try {
            buf.skip(44+byteOffset);
            buf.read(samples,0,10000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return samples;
    }
    public FileChannel getByteAudioRead (){
        try {
            fc = fis.getChannel().position(4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fc;
    }
    public String getAudioMetaData (){
        MediaMetadataRetriever mmdR = new MediaMetadataRetriever();
        mmdR.setDataSource(file);
        String emD = mmdR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return emD;
    }

}
