package com.example.sebastin.bina2;

import android.app.Application;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.AudioFormat;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Sebastián on 12/02/2016.
 */

public class startRecording {

    //Recstate
    public static boolean isRecording = true;
    //FileOptions
    String directory = "/Grabaciones2";
    String filename = "Grabacion";
    String format = ".wav";
    //RecOptions
    short nChannels = 2;
    int sRate = 44100;
    short rFormat = 16;
    public static int payloadSize = 0;
    public startRecording() {
        //DefConstructor
    }

    public void sets() {
        Thread recordingThread = new Thread (new Runnable(){
            @Override
            public void run (){
            //Recording Obj setup
            AudioFormat audioFormat;
            MediaRecorder mediaRecorder;
            int nchannelSetup;
            int rformatSetup;
            if(nChannels==1)

            {
                nchannelSetup = AudioFormat.CHANNEL_IN_MONO;
            }

            else

            {
                nchannelSetup = AudioFormat.CHANNEL_IN_STEREO;
            }

            if(rFormat==8)

            {
                rformatSetup = AudioFormat.ENCODING_PCM_8BIT;
            }

            else

            {
                rformatSetup = AudioFormat.ENCODING_PCM_16BIT;
            }

            int device = MediaRecorder.AudioSource.MIC;
            final int bufferSize = AudioRecord.getMinBufferSize(sRate, nchannelSetup, rformatSetup);
            AudioRecord audioRecord = new AudioRecord(device, sRate, nchannelSetup, rformatSetup,
                    bufferSize);
            //File setup
            //File Obj
            String state = Environment.getExternalStorageState();
            if(Environment.MEDIA_MOUNTED.equals(state))

            {
                File root = Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + directory);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File file = new File(dir, filename + format);
                //Wav header setup

                int TIMER_INTERVAL = 120;
                int mPeriodInFrames = sRate * TIMER_INTERVAL / 1000;
                int mBufferSize = mPeriodInFrames * 2 * nChannels * rFormat / 8;
                try {
                    RandomAccessFile randomAccessWriter = new RandomAccessFile(file, "rw");
                    randomAccessWriter.setLength(0); // Set file length to 0, to prevent unexpected behavior in case the file already existed
                    randomAccessWriter.writeBytes("RIFF");
                    randomAccessWriter.writeInt(0); // Final file size not known yet, write 0
                    randomAccessWriter.writeBytes("WAVE");
                    randomAccessWriter.writeBytes("fmt ");
                    randomAccessWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
                    randomAccessWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
                    randomAccessWriter.writeShort(Short.reverseBytes(nChannels));// Number of channels, 1 for mono, 2 for stereo
                    randomAccessWriter.writeInt(Integer.reverseBytes(sRate)); // Sample rate
                    randomAccessWriter.writeInt(Integer.reverseBytes(sRate * nChannels * rFormat / 8)); // Byte rate, SampleRate*NumberOfChannels*mBitsPersample/8
                    randomAccessWriter.writeShort(Short.reverseBytes((short) (nChannels * rFormat / 8))); // Block align, NumberOfChannels*mBitsPersample/8
                    randomAccessWriter.writeShort(Short.reverseBytes(rFormat)); // Bits per sample
                    randomAccessWriter.writeBytes("data");
                    randomAccessWriter.writeInt(0); // Data chunk size not known yet, write 0
                    final byte[] buffer = new byte[mPeriodInFrames * rFormat / 8 * nChannels];
                    //startRecordingtoSD
                    payloadSize = 0;
                    audioRecord.startRecording();
                    while (true)
                    {
                        if (!(isRecording)) break;
                        int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                        for (int i = 0; i < bufferReadResult; i++) {
                            randomAccessWriter.write(buffer);          // write audio data to file
                            payloadSize += buffer.length;
                        }


                    }
                    //wav Header end
                    randomAccessWriter.seek(4); // Write size to RIFF header
                    randomAccessWriter.writeInt(Integer.reverseBytes(36 + payloadSize));

                    randomAccessWriter.seek(40); // Write size to Subchunk2Size field
                    randomAccessWriter.writeInt(Integer.reverseBytes(payloadSize));

                    randomAccessWriter.close();
                    audioRecord.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        },"AudioRecorder Thread");
        recordingThread.start();
    }


    public static boolean stopRec () {
     isRecording = false;
        return isRecording;

    }
    public static int size(){
        return payloadSize;
    }



}
