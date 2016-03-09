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
    byte [] samplingRateB;
    int samplingRateI;
    byte []bitDepthB;
    short bitDepthS;
    byte []numChanB ;
    short numChanS;
    double leftRmsValue = 0;
    double rightRmsValue = 0;
    double leftDbfsValue = 0;
    double rightDbfsValue = 0;
    int nSamples = 0;
    ByteBuffer bbh;
    public AudioRead (){
        //def cons
    }
    public void setAudioRead (String filetoRead,int milliSeconds){
        try {
            file = filetoRead;
            numChanB = new byte[2];
            samplingRateB = new byte[4];
            bitDepthB = new byte[2];
            randomAccessFile = new RandomAccessFile(filetoRead, "rw");
            randomAccessFile.seek(22);
            randomAccessFile.read(numChanB, 0, 2);
            bb = ByteBuffer.wrap(numChanB);
            numChanS = Short.reverseBytes(bb.getShort());
            randomAccessFile.seek(24);
            randomAccessFile.read(samplingRateB, 0, 4);
            bb = ByteBuffer.wrap(samplingRateB);
            samplingRateI = Integer.reverseBytes(bb.getInt());
            randomAccessFile.seek(34);
            randomAccessFile.read(bitDepthB, 0, 2);
            bb = ByteBuffer.wrap(bitDepthB);
            bitDepthS = Short.reverseBytes(bb.getShort());
            nSamples = milliSeconds*samplingRateI/1000;
            samples = new byte[4*nSamples];
            out = new short[samples.length / 2]; // will drop last byte if odd number
            outl  = new short[samples.length / 4];
            outr  = new short[samples.length / 4];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] getbufAudioRead(long byteOffset){
        try {
            randomAccessFile.seek(byteOffset * 4 + 44);
            randomAccessFile.read(samples,0,nSamples);
            bb = ByteBuffer.wrap(samples);
            il = 0;
            ir = 0;
            for (int i = 0; i < out.length; i++) {
                out[i] = Short.reverseBytes(bb.getShort());
                if ((i % 2) == 0) {
                    // number is even
                    outl[il]=out[i];
                    leftRmsValue = leftRmsValue + Math.pow(outl[il],2);
                    il++;
                }
                else {
                    // number is odd
                    outr[ir]=out[i];
                    rightRmsValue = rightRmsValue + Math.pow(outr[ir],2);
                    ir++;
                }
            }
            leftRmsValue = (int) Math.sqrt(leftRmsValue/nSamples);
            rightRmsValue = (int) Math.sqrt(rightRmsValue/nSamples);
            if (leftRmsValue >= 0.001) {
                leftDbfsValue = 10 * Math.log10(leftRmsValue / (Math.pow(2, bitDepthS - 1)));
            }
            else{
                leftDbfsValue= -80;
            }
            if (rightRmsValue >= 0.001) {
                rightDbfsValue = 10 * Math.log10(rightRmsValue / (Math.pow(2, bitDepthS - 1)));
            }
            else {
                rightDbfsValue = -80;
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
    public double getLeftRMSvalue (){
        return leftRmsValue;
    }
    public double getRightRMSvalue(){
        return rightRmsValue;
    }
    public double getLeftDbfsValue(){
        return leftDbfsValue;
    }
    public double getRightDbfsValue(){
        return rightDbfsValue;
    }
    public int getNumberSamples(){
        return nSamples;
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
