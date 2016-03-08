package com.example.sebastin.bina2;

import android.app.Application;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
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
    public ByteBuffer bb;
    public short[] out;
    public short[] outl;
    public short[] outr;
    public int il = 0;
    public int ir = 0;
    public RandomAccessFile randomAccessFile;
    public AudioRead (){
        //def cons
    }
    public void setAudioRead (String filetoRead){
        try {
            file = filetoRead;
            buf = new BufferedInputStream(new FileInputStream(filetoRead));
            //fis = new FileInputStream(filetoRead);
            //randomAccessFile = new RandomAccessFile(filetoRead, "rw");
            numbyte = buf.available();
            samples = new byte[20000];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] getbufAudioRead(long byteOffset){
        try {
            //randomAccessFile.seek(44+byteOffset);
            buf.skip(byteOffset*4+44);
            buf.read(samples, 0, 2*10000);
            out = new short[samples.length / 2]; // will drop last byte if odd number
            outl  = new short[samples.length / 4];
            outr  = new short[samples.length / 4];
            bb = ByteBuffer.wrap(samples);
            il = 0;
            ir = 0;
            for (int i = 0; i < out.length; i++) {
                //try {
                //out[i]=  Short.reverseBytes(randomAccessFile.readShort());
                // } catch (IOException e) {
                //   e.printStackTrace();
                //}
                bb.arrayOffset();
                out[i] = Short.reverseBytes(bb.getShort());
                if ((i % 2) == 0) {
                    // number is even
                    outl[il]=out[i];
                    il++;
                }
                else {
                    // number is odd
                    outr[ir]=out[i];
                    ir++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return samples;
    }
    public short[] getLeftData (){
        return outl;
    }
    public short[] getRightData (){
        return outr;
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

    public class Task implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < out.length; i++) {
                //try {
                   //out[i]=  Short.reverseBytes(randomAccessFile.readShort());
               // } catch (IOException e) {
                 //   e.printStackTrace();
                //}
                bb.arrayOffset();
                out[i] = Short.reverseBytes(bb.getShort());
                if ((i % 2) == 0) {
                    // number is even
                    outl[il]=out[i];
                    il++;
                }
                else {
                    // number is odd
                    outr[ir]=out[i];
                    ir++;
                }
            }
        }
    }


}
