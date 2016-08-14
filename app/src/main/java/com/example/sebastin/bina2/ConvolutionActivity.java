package com.example.sebastin.bina2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class ConvolutionActivity extends AppCompatActivity {
    String dir,recordingTitle,fileToEdit;
    AudioRead aR = new AudioRead();
    RandomAccessFile randomAccessWriter;
    int payloadSize = 0;
    int currentImpulse;
    int[] impulseSamples = {R.raw.imp08000,R.raw.imp458000,R.raw.imp908000,R.raw.imp1358000,
            R.raw.impm458000, R.raw.impm908000, R.raw.impm1358000, R.raw.imp1808000};
    enum convState {START,STOP}
    convState currentConvState;

    ProgressBar spinner;
    ProgressDialog dialog;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        setContentView(R.layout.activity_convolution);
        Bundle bundle = getIntent().getExtras();
        dir = bundle.getString("dir");
        recordingTitle = bundle.getString("recordingTitle");
        fileToEdit = dir + "/" + recordingTitle;
        context = this;
        spinner =(ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
//        spinner = (ProgressBar)findViewById(R.id.progressBar1);
//        spinner.setVisibility(View.GONE);
//        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

    }

    public void convolutionButton(View view){

        switch (view.getId()) {
            case R.id.conv0:
                currentImpulse = impulseSamples[0];
                break;
            case R.id.conv45:
                currentImpulse = impulseSamples[1];
                break;
            case R.id.conv90:
                currentImpulse = impulseSamples[2];
                break;
            case R.id.conv135:
                currentImpulse = impulseSamples[3];
                break;
            case R.id.convm45:
                currentImpulse = impulseSamples[4];
                break;
            case R.id.convm90:
                currentImpulse = impulseSamples[5];
                break;
            case R.id.convm135:
                currentImpulse = impulseSamples[6];
                break;
            case R.id.conv180:
                currentImpulse = impulseSamples[7];
                break;
        }
        new Thread(new audioProcessing()).start();
    }
    public class audioProcessing implements Runnable {
        @Override
        public void run() {
            try{
                spinner.post(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.VISIBLE);
                        getWindow().getDecorView().findViewById(android.R.id.content).setEnabled(false);
                    }
                });
                currentConvState = convState.START;
                ByteBuffer bb;
//                    if (resou) {
//                fileClose(cacheFile);
                File fileToProcess = new File(fileToEdit);
                byte[] audioToProcess;
                aR.setAudioRead(fileToEdit, 0);
                int resourceId = currentImpulse;
                byte[] numChanB = new byte[2];
                byte[] bitDepthB = new byte[2];
                byte[] samplingRateB = new byte[4];
                byte[] audioSizeB = new byte[4];
                int audioBufferSize;
                byte[] audioBuffer,stConvbyte,recBuffer;
                short[]audioSamples,audioLeft,audioRight
                        ,recSamples,rConv,lConv,stConv;
                short numChanS,bitDepthS;
                int samplingRateI;
                long L=0,M = 0,N = 0;
                File cacheFile = createCacheFile( resourceId, "delete-me-please");
                RandomAccessFile randomAccessFile = new RandomAccessFile(cacheFile, "r");
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
                randomAccessFile.seek(70);
                randomAccessFile.read(audioSizeB);
//            data size
                randomAccessFile.seek(74);
                randomAccessFile.read(audioSizeB, 0, 4);
                bb = ByteBuffer.wrap(audioSizeB);
                audioBufferSize = Integer.reverseBytes(bb.getInt());
//            variable init
//                        if (this.audioBufferSize != audioBufferSize) {
                audioBuffer = new byte[audioBufferSize];
//                            this.audioBufferSize = audioBufferSize;
//                        }
                randomAccessFile.seek(78);
                randomAccessFile.read(audioBuffer,0, audioBufferSize);
                audioSamples = getBBSamples(audioBuffer);
                audioLeft = leftStSamples(audioSamples);
                audioRight = rightStSamples(audioSamples);
                float lmax,rmax;
                lmax = maxSamples(audioLeft);
                rmax = maxSamples(audioRight);
                audioLeft = normShortSamples(audioLeft, (long) lmax);
                audioRight = normShortSamples(audioRight,(long) rmax);
                Log.e("File", "wavWriter: imp samples ok");
                int audioLength =(int) Math.round(((double)aR.getbufAudioRead(0).length)/(double)audioBufferSize)-1;
                Log.e("al", "audioLengt" + audioLength);
                wavWriter(numChanS, bitDepthS, samplingRateI, dir.toString() + "/" + recordingTitle);
                audioToProcess = aR.getbufAudioRead(0);
                recSamples = getBBSamples(audioToProcess);
                short[] audioLeftOp, audioRightOp;
                audioLeftOp = samplesZeroPad(audioLeft,recSamples.length);
                audioRightOp = samplesZeroPad(audioRight,recSamples.length);
                lConv = convSamples(audioLeftOp,recSamples);
                rConv = convSamples(audioRightOp,recSamples);
                stConv = stereoWrap(lConv, rConv);
                stConvbyte = ShortToByte_Twiddle_Method(stConv);
                writeBuf(stConvbyte,0);
//                audioLength = 8;
//                for (int ri = 0;ri < audioLength; ri++) {
//                    audioToProcess = aR.getbufAudioRead(ri * audioSamples.length / 2);
//                    recSamples = getBBSamples(audioToProcess);
////                    Arrays.fill(recSamples, (recSamples.length + 1) / 2, recSamples.length, (short) 0);
//
////                    M = audioLeft.length;
////                    L = M;
////                    N = L+M-1;
////
////                    short [] hnl = new short[(int) N];
////                    short [] hnr = new short[(int) N];
////                    short [] xn = new short[(int) N];
////
////                    System.arraycopy(audioLeft, 0, hnl, 0, audioLeft.length-1);
////                    Arrays.fill(hnl, audioLeft.length, hnl.length - 1, (short) 0);
////
////                    System.arraycopy(audioRight, 0, hnr, 0, audioRight.length - 1);
////                    Arrays.fill(hnr, audioRight.length, hnr.length - 1, (short) 0);
////
////                    System.arraycopy(recSamples, 0, xn, 0, recSamples.length - 1);
////                    Arrays.fill(xn, recSamples.length, xn.length-1, (short) 0);
////
////                    lConv = convSamples(hnl, xn);
////                    rConv = convSamples(hnr, xn);
//                    stConv = stereoWrap(lConv, rConv);
//                    stConvbyte = ShortToByte_Twiddle_Method(stConv);
//                    writeBuf(stConvbyte, ri * stConvbyte.length);
//                }
                closeWav();
                currentConvState = convState.STOP;
//                Handler handler = new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        dialog.dismiss();
//                    }
//                };
                spinner.post(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.GONE);
                        getWindow().getDecorView().findViewById(android.R.id.content).setEnabled(true);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private File createCacheFile( int resourceId, String filename)
            throws IOException {
        File cacheFile = new File(this.getCacheDir(), filename);

        if (cacheFile.createNewFile() == false) {
            cacheFile.delete();
            cacheFile.createNewFile();
        }

        // from: InputStream to: FileOutputStream.
        InputStream inputStream = this.getResources().openRawResource(resourceId);
        FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);

        byte[] buffer = new byte[1024 * 512];
        while (inputStream.read(buffer, 0, 1024 * 512) != -1) {
            fileOutputStream.write(buffer);
        }

        fileOutputStream.close();
        inputStream.close();

        return cacheFile;
    }
    short[] getBBSamples(byte[] bArray){
        ByteBuffer bb;
        short[] sSamples = new short[bArray.length/2];
        bb = ByteBuffer.wrap(bArray);
        for (int i = 0; i < sSamples.length; i++){
            sSamples[i] = Short.reverseBytes(bb.getShort());
        }
        return sSamples;
    }
    short [] leftStSamples(short[] stereoSamples){
        int il = 0;
        short[] leftSamples = new short[stereoSamples.length/2];
        for (int i = 0; i < stereoSamples.length; i++) {
            if ((i % 2) == 0) {
                // number is even
                leftSamples[il]=stereoSamples[i];
                il++;
            }
        }
        return leftSamples;
    }
    short [] rightStSamples(short[] stereoSamples){
        int il = 0;
        short[] rightSamples = new short[stereoSamples.length/2];
        for (int i = 0; i < stereoSamples.length; i++) {
            if ((i % 2) != 0) {
                // number is odd
                rightSamples[il]=stereoSamples[i];
                il++;
            }
        }
        return rightSamples;
    }
    float maxSamples(short[] sArray){
        float maxSample = 0;
        for (int i = 0; i < sArray.length; i++) {
            if (Math.abs(sArray[i]) > maxSample) {
                maxSample = Math.abs(sArray[i]);
            }
        }
        return maxSample;
    }
    short[] normShortSamples(short[] sArray,long maxSample){
        for (int i = 0; i < sArray.length; i++) {
            sArray[i] =(short) ((double)sArray[i]*Short.MAX_VALUE/(double)maxSample);
        }
        return sArray;
    }
    short[] samplesZeroPad(short[] sArray,int lengthToPad){
        short[] paddedSamples = new short[lengthToPad];
        System.arraycopy(sArray, 0, paddedSamples, 0, sArray.length - 1);
        Arrays.fill(paddedSamples, sArray.length, paddedSamples.length - 1, (short) 0);
        return paddedSamples;
    }
    short[] convSamples (short[] x, short[] y){
        int convCount;
        short[]xInv = new short[x.length];
        short[]convOp = new short[xInv.length];
        short[]convout = new short[x.length*2-1];
        int[] intArray = new int[x.length*2-1];
        for(int i = 0; i < x.length; i++){
            xInv[i] = x[x.length-1 - i];
        }
        for (int p = 0; p < xInv.length; p++) {
            convCount = 0;
            System.arraycopy(xInv, xInv.length - 1 - p, convOp, 0, p + 1);
            for (int i = 0; i < convOp.length; i++) {
                convCount = y[i] * convOp[i] + convCount;
            }
            intArray[p] = convCount;
        }
        Log.e("File", "convSamples: first conv ok");
        for (int q = 0; q < xInv.length - 1; q++) {
            convCount = 0;
            System.arraycopy(xInv, 0, convOp, q + 1, xInv.length - 1 - q);
            Arrays.fill(convOp, 0, q, (short) 0);
            for (int i = 0; i < xInv.length; i++) {
                convCount = y[i] * convOp[i] + convCount;
            }
            intArray[xInv.length + q] = convCount;
        }
        for (int i = 0; i < intArray.length; i++) {
            convout[i] = (short) ((intArray[i] >> 16));
        }
        Log.e("File", "convSamples: 2 conv ok");
        return convout;
    }
    short[] stereoWrap (short[] left, short[] right){
        int iil = 0;
        int iir = 0;
        short[] stWrap = new short[left.length+right.length];
        for (int i = 0; i < stWrap.length; i++) {
            if ((i % 2) == 0) {
                // number is even
                stWrap[i] = left[iil];
                iil += 1;
            } else {
                // number is odd
                stWrap[i] = right[iir];
                iir += 1;
            }
        }
        return stWrap;
    }
    byte [] ShortToByte_Twiddle_Method(short [] input)
    {
        int short_index, byte_index;
        int iterations = input.length;

        byte [] buffer = new byte[input.length * 2];

        short_index = byte_index = 0;

        for(/*NOP*/; short_index != iterations; /*NOP*/)
        {
            buffer[byte_index]     = (byte) (input[short_index] & 0x00FF);
            buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

            ++short_index; byte_index += 2;
        }

        return buffer;
    }
    public void wavWriter (short nChannels,short mBitsPersample, int sRate,String filePath){

        try {
            Log.e("title","Rectitle: "+recordingTitle);
            randomAccessWriter = new RandomAccessFile(dir.toString() + "/"
                    + recordingTitle.substring(0, recordingTitle.length() - 4)+"cnv.wav", "rw");
            randomAccessWriter.setLength(0); // Set file length to 0, to prevent unexpected behavior in case the file already existed
            randomAccessWriter.writeBytes("RIFF");
            randomAccessWriter.writeInt(0); // Final file size not known yet, write 0
            randomAccessWriter.writeBytes("WAVE");
            randomAccessWriter.writeBytes("fmt ");
            randomAccessWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
            randomAccessWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
            randomAccessWriter.writeShort(Short.reverseBytes(nChannels));// Number of channels, 1 for mono, 2 for stereo
            randomAccessWriter.writeInt(Integer.reverseBytes(sRate)); // Sample rate
            randomAccessWriter.writeInt(Integer.reverseBytes(sRate * nChannels * mBitsPersample / 8)); // Byte rate, SampleRate*NumberOfChannels*mBitsPersample/8
            randomAccessWriter.writeShort(Short.reverseBytes((short) (nChannels * mBitsPersample / 8))); // Block align, NumberOfChannels*mBitsPersample/8
            randomAccessWriter.writeShort(Short.reverseBytes(mBitsPersample)); // Bits per sample
            randomAccessWriter.writeBytes("data");
            randomAccessWriter.writeInt(0); // Data chunk size not known yet, write 0

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void writeBuf (byte[] audioByte,long filePointer){
        try {
            randomAccessWriter.seek(filePointer+44);
            randomAccessWriter.write(audioByte);
            payloadSize += audioByte.length;
            Log.e("File", "wavWriter: done" );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void closeWav (){
        try {
            randomAccessWriter.seek(4); // Write size to RIFF header
            randomAccessWriter.writeInt(Integer.reverseBytes(36 + payloadSize));
            randomAccessWriter.seek(40); // Write size to Subchunk2Size field
            randomAccessWriter.writeInt(Integer.reverseBytes(payloadSize));
            randomAccessWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
