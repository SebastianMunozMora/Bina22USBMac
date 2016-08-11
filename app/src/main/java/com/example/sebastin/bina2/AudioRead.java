package com.example.sebastin.bina2;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

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
    byte [] audioSizeB = new byte[4];
    public short [] outRec,out,audioLeft,audioRight,
            outlInv,outrInv,
            outlOp, outrOp,
            lConv,rConv,
            stConv,audioSamples;
    public byte[] stConvbyte;
    int lCount = 0;
    int rCount = 0;
    public int il = 0,ir = 0;
    public RandomAccessFile randomAccessFile;
    byte [] samplingRateB = new byte[4];
    int samplingRateI;
    byte [] bitDepthB = new byte[2];
    short bitDepthS;
    byte []numChanB = new byte[2];
    short numChanS;
    double leftRmsValue = 0;
    double rightRmsValue = 0;
    double leftDbfsValue = 0;
    double rightDbfsValue = 0;
    int audioBufferSize = 0;
    int nSamples = 0;
    int resourceId;
    Context context;
    String recTime;
    DecimalFormat form = new DecimalFormat("00.000");
    ByteBuffer bbh;
//    String file = "your_binary_file.bin";
    AssetFileDescriptor afd = null;
//    FileInputStream fis = null;
    File tmpFile = null;
    byte[] audioBuffer;
    File cacheFile;
    short[] recSamples;
    short[] audioImpOpLeft;
    short[] audioImpOpRight;
    short[] audioInverseLeft,audioInverseRight;
    short[] audioImpOpLeftOut,audioImpOpRightOut;
    byte[] recBuffer;
    byte[] readSamples;


    //    RandomAccessFile raf = null;
    public AudioRead (){
        //def cons
    }
    public void setAudioRead (String filetoRead,int milliSeconds){
        try {
            file = filetoRead;
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
            randomAccessFile.seek(40);
            randomAccessFile.read(audioSizeB, 0, 4);
            bb = ByteBuffer.wrap(audioSizeB);
            audioBufferSize = Integer.reverseBytes(bb.getInt());
            Log.e("Audio","Audio read: bufferSize"+audioBufferSize+" num ch: "+numChanS
                    +" bitd:"+bitDepthS+" sr:"+samplingRateI);
            readSamples = new byte[audioBufferSize];
            nSamples = milliSeconds*samplingRateI/1000;
//            samples = new byte[4*nSamples];
            samples = new byte[44100*4];
            out = new short[samples.length / 2]; // will drop last byte if odd number
            audioLeft  = new short[samples.length / 4];
            audioRight  = new short[samples.length / 4];
            outlInv  = new short[audioLeft.length];
            outrInv  = new short[audioRight.length];
            outlOp = new short[outlInv.length];
            outrOp = new short[outrInv.length];
            lConv = new short[outlInv.length];
            rConv = new short[outrInv.length];
            stConv = new short[out.length];
            stConvbyte = new byte[stConv.length*2];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImpulseResponse (InputStream inputStream,Context context,int resourceId){
        int audioBufferSize;
        try {
            if (this.resourceId != resourceId) {
                fileClose(cacheFile);
                cacheFile = createCacheFile(context, resourceId, "delete-me-please");
                randomAccessFile = new RandomAccessFile(cacheFile, "r");
//            numero de canales
                randomAccessFile.seek(22);
                randomAccessFile.read(numChanB, 0, 2);
                bb = ByteBuffer.wrap(numChanB);
                numChanS = Short.reverseBytes(bb.getShort());
//            frecuencia de muestreo
                randomAccessFile.seek(24);
                randomAccessFile.read(samplingRateB, 0, 4);
                bb = ByteBuffer.wrap(samplingRateB);
                samplingRateI = Integer.reverseBytes(bb.getInt());
//            profundiad en bits
                randomAccessFile.seek(34);
                randomAccessFile.read(bitDepthB, 0, 2);
                bb = ByteBuffer.wrap(bitDepthB);
                bitDepthS = Short.reverseBytes(bb.getShort());
//            data size
                randomAccessFile.seek(74);
                randomAccessFile.read(audioSizeB, 0, 4);
                bb = ByteBuffer.wrap(audioSizeB);
                audioBufferSize = Integer.reverseBytes(bb.getInt());
//            variable init
                if (this.audioBufferSize != audioBufferSize) {
                    audioBuffer = new byte[audioBufferSize];
                    audioSamples = new short[audioBufferSize / 2];
                    audioLeft = new short[audioBufferSize / 4];
                    audioRight = new short[audioBufferSize / 4];
                    audioInverseLeft = new short[audioSamples.length/2];
                    audioInverseRight = new short[audioSamples.length/2];
                    this.audioBufferSize = audioBufferSize;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public byte[] binaProcessing (final byte [] recBuffer,InputStream impResp,final Context context, final int resourceId){
//            data
        this.recBuffer = recBuffer;
        this.resourceId = resourceId;
        this.context = context;
        this.audioBufferSize=0;
        t1.start();
//        new Thread(new BinaTask()).start();
        return stConvbyte;
    }
    public class BinaTask implements Runnable {
        @Override
        public void run() {

        }
    }
    byte [] ShortToByte_ByteBuffer_Method(short [] input)
    {
        int index;
        int iterations = input.length;

        ByteBuffer bb = ByteBuffer.allocate(input.length * 2);

        for(index = 0; index != iterations; ++index)
        {
            bb.putShort(input[index]);
        }

        return bb.array();
    }
    public void fileClose (File cacheFile){
        try {
            if (!(randomAccessFile == null)) {
                randomAccessFile.close();
                cacheFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    Thread t1 = new Thread(new Runnable() {
        public void run() {
            // code goes here.
            try{
//                    if (resou) {
                fileClose(cacheFile);
                cacheFile = createCacheFile(context, resourceId, "delete-me-please");
                randomAccessFile = new RandomAccessFile(cacheFile, "r");
//            numero de canales
                randomAccessFile.seek(22);
                randomAccessFile.read(numChanB, 0, 2);
                bb = ByteBuffer.wrap(numChanB);
                numChanS = Short.reverseBytes(bb.getShort());
//            frecuencia de muestreo
                randomAccessFile.seek(24);
                randomAccessFile.read(samplingRateB, 0, 4);
                bb = ByteBuffer.wrap(samplingRateB);
                samplingRateI = Integer.reverseBytes(bb.getInt());
//            profundiad en bits
                randomAccessFile.seek(34);
                randomAccessFile.read(bitDepthB, 0, 2);
                bb = ByteBuffer.wrap(bitDepthB);
                bitDepthS = Short.reverseBytes(bb.getShort());
//            data size
                randomAccessFile.seek(74);
                randomAccessFile.read(audioSizeB, 0, 4);
                bb = ByteBuffer.wrap(audioSizeB);
                audioBufferSize = Integer.reverseBytes(bb.getInt());
//            variable init
//                        if (this.audioBufferSize != audioBufferSize) {
                audioBuffer = new byte[audioBufferSize];
                audioSamples = new short[audioBufferSize / 2];
                audioLeft = new short[audioBufferSize / 4];
                audioRight = new short[audioBufferSize / 4];
                audioInverseLeft = new short[audioSamples.length/2];
                audioInverseRight = new short[audioSamples.length/2];
//                            this.audioBufferSize = audioBufferSize;
//                        }
                randomAccessFile.seek(78);
                randomAccessFile.read(audioBuffer,0, audioBufferSize);
                bb = ByteBuffer.wrap(audioBuffer);
                il = 0;
                ir = 0;
                for (int i = 0; i < audioSamples.length; i++) {
                    audioSamples[i] = Short.reverseBytes(bb.getShort());
                    if ((i % 2) == 0) {
                        // number is even
                        audioLeft[il]=audioSamples[i];
                        il++;
                    }
                    else {
                        // number is odd
                        audioRight[ir]=audioSamples[i];
                        ir++;
                    }
                }
                for(int i = 0; i < audioLeft.length; i++){
                    audioInverseLeft[i] = audioLeft[audioLeft.length-1 - i];
                    audioInverseRight[i] = audioRight[audioRight.length-1 - i];
                }
                bb = ByteBuffer.wrap(recBuffer);
                recSamples = new short[recBuffer.length / 2];
                lConv = new short[recSamples.length+audioLeft.length-1];
                rConv = new short[recSamples.length+audioRight.length-1];
                stConv = new short[2*lConv.length];
                stConvbyte = new byte[2*stConv.length];
                audioImpOpLeft = new short[recSamples.length];
                audioImpOpRight = new short[recSamples.length];
                audioImpOpLeftOut = new short[recSamples.length];
                audioImpOpRightOut = new short[recSamples.length];
                for (int i = 0; i < recSamples.length; i++) {
                    recSamples[i] = Short.reverseBytes(bb.getShort());
                }
                for (int p = 0; p < audioInverseLeft.length; p++) {
                    lCount = 0;
                    rCount = 0;
//                audioImpOpLeft = Arrays.copyOfRange(audioLeft, audioInverseLeft.length -1 - p, recSamples.length);
//                Arrays.fill(audioImpOpLeft, p + 1, audioImpOpLeft.length, (short) 0);
                    System.arraycopy(audioInverseLeft,audioInverseLeft.length-1-p,audioImpOpLeft,0,p+1);
                    System.arraycopy(audioInverseRight,audioInverseRight.length-1-p,audioImpOpRight,0,p+1);
                    for (int i = 0; i < recSamples.length; i++) {
                        lCount = recSamples[i] * audioImpOpLeft[i] + lCount;
                        rCount = recSamples[i] * audioImpOpRight[i] + rCount;
                    }
                    lConv[p] = (short) lCount;
                    rConv[p] = (short) rCount;
                }
                for (int q = 0; q < recSamples.length; q++) {
                    lCount = 0;
                    rCount = 0;
                    System.arraycopy(audioImpOpLeft,0,audioImpOpLeftOut,q+1,audioImpOpLeft.length-1-q);
                    System.arraycopy(audioImpOpRight,0,audioImpOpRightOut,q+1,audioImpOpRight.length-1-q);
                    Arrays.fill(audioImpOpLeftOut, 0, q, (short) 0);
                    Arrays.fill(audioImpOpRightOut, 0, q, (short) 0);
//                System.arraycopy(audioImpOpLeft,0,audioImpOpLeftOut,0,audioImpOpLeft.length-1-q);

                    for (int i = 0; i < recSamples.length; i++) {
                        lCount = recSamples[i] * audioImpOpLeft[i] + lCount;
                        rCount = recSamples[i] * audioImpOpRight[i] + rCount;
                    }
                    lConv[audioInverseLeft.length] = (short) lCount;
                    rConv[q] = (short) rCount;
                }
                int iil = 0,iir = 0;
                for (int i = 0; i < stConv.length; i++){
                    if ((i % 2) == 0) {
                        // number is even
                        stConv[i] = lConv[iil];
                        iil++;
                    }
                    else {
                        // number is odd
                        stConv[i] = rConv[iir];
                        iir++;
                    }
                }
                for (int i = 0; i < stConv.length; i++) {
                    stConv[i] = Short.reverseBytes(stConv[i]);
                }
                stConvbyte = ShortToByte_ByteBuffer_Method(stConv);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });
    public class process implements Runnable {
        @Override
        public void run() {

        }
    }
    public byte[] getbufAudioRead(long byteOffset){
        try {
            randomAccessFile.seek(byteOffset * 4 + 44);
            randomAccessFile.read(readSamples);
//            bb = ByteBuffer.wrap(samples);
//            il = 0;
//            ir = 0;
//            for (int i = 0; i < out.length; i++) {
//                out[i] = Short.reverseBytes(bb.getShort());
//                if ((i % 2) == 0) {
//                    // number is even
//                    audioLeft[il]=out[i];
////                    leftRmsValue = leftRmsValue + Math.pow(audioLeft[il],2);
//                    il++;
//                }
//                else {
//                    // number is odd
//                    audioRight[ir]=out[i];
//                    rightRmsValue = rightRmsValue + Math.pow(audioRight[ir],2);
//                    ir++;
//                }
//            }
//            leftRmsValue = (int) Math.sqrt(leftRmsValue/nSamples);
//            rightRmsValue = (int) Math.sqrt(rightRmsValue/nSamples);
//            if (leftRmsValue >= 0.001) {
//                leftDbfsValue = 10 * Math.log10(leftRmsValue / (Math.pow(2, bitDepthS - 1)));
//            }
//            else{
//                leftDbfsValue= -80;
//            }
//            if (rightRmsValue >= 0.001) {
//                rightDbfsValue = 10 * Math.log10(rightRmsValue / (Math.pow(2, bitDepthS - 1)));
//            }
//            else {
//                rightDbfsValue = -80;
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readSamples;
    }
    /**
     * Copies raw resource to a cache file.
     * @return File reference to cache file.
     * @throws IOException
     */
    private File createCacheFile(Context context, int resourceId, String filename)
            throws IOException {
        File cacheFile = new File(context.getCacheDir(), filename);

        if (cacheFile.createNewFile() == false) {
            cacheFile.delete();
            cacheFile.createNewFile();
        }

        // from: InputStream to: FileOutputStream.
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);

        byte[] buffer = new byte[1024 * 512];
        while (inputStream.read(buffer, 0, 1024 * 512) != -1) {
            fileOutputStream.write(buffer);
        }

        fileOutputStream.close();
        inputStream.close();

        return cacheFile;
    }
    public short[] getLeftData (){
        return audioLeft;
    }
    public short[] getRightData (){

        return audioRight;
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
        long emDint = Long.parseLong(emD)/1000;
        long min = emDint/60;
        long secs = emDint-60*min;
        min = Math.round(min);
        secs = Math.round(secs);
        String minString;
        String secString;
        if (min < 10){
             minString = "0"+min;
        }else {
            minString = min+"";
        }
        if (secs <10){
             secString = "0"+secs;
        }else{
            secString = secs+"";
        }
        recTime = minString+":"+secString+" s / "+bitDepthS+" Bits / "+ NumberFormat.getNumberInstance(Locale.US).format(samplingRateI)+" Hz ";

        return recTime;
    }

}
