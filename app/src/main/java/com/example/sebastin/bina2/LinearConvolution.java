package com.example.sebastin.bina2;

import android.media.AudioFormat;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by 212equipo23 on 22/02/16.
 */
public class LinearConvolution {

    byte []samples;
    public String fileconv;
    public FileInputStream fis;
    InputStream is  = null;
    public String directory = "/impResponses";
    private File root = Environment.getExternalStorageDirectory();
    private File dir = new File(root.getAbsolutePath() + directory);

    public LinearConvolution(String filetoconv){
// defconst
    }
    public   void readFile (String file) throws IOException {
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
        try {
            buf.read(samples, 0, 2000);
        }
        finally  {
            if (is != null) {
                is.close();
            }
        }
// leer impulso hola
        if (!dir.exists()) {
            dir.mkdir();
        }
        String impName = "impResponse";
        File fileImp = new File(dir,impName);
        String impPath = fileImp.toString();
        BufferedInputStream bufImp = new BufferedInputStream(new FileInputStream(file));
        try {
            buf.read(samples, 0, 1000);
        }
        finally  {
            if (is != null) {
                is.close();
            }
        }
    }
}


