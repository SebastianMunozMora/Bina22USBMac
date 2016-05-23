package com.example.sebastin.bina2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class LaunchActivity extends Activity {
    ImageView imageView;
    public enum imageState  {PLAYING,STOPPED}
    private imageState imageState;
    int imageTime = 1000;
    int[] imageResources = {R.drawable.us,R.drawable.is,R.drawable.logo2};
    int currentResource;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        imageState = imageState.PLAYING;
        imageView = (ImageView)findViewById(R.id.imageView2);
//        new Thread(new timedImage()).start();


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        new Thread(new timedImage()).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new timedImage()).start();

    }
    public class timedImages implements Runnable {
        @Override
        public void run() {
            while (imageState == imageState.PLAYING) {
                for (int i = 0; i < imageResources.length; i++) {
                    final int a = i;
                    currentResource = imageResources[i];
                    Log.e("current picture", currentResource + "");
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageResource(currentResource);
                        }
                    });
                    new CountDownTimer(7000, 2000) {
                        public void onTick(long millisUntilFinished) {

                        }
                        public void onFinish() {
                            currentResource = imageResources[a];
        //                    finish();
        //                    startActivity(new Intent(getApplicationContext(), ProjectActivity.class));
                        }
                    }.start();
                }
//                try {
//                    Thread.sleep(imageTime);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                imageState = imageState.STOPPED;
                finish();
                startActivity(new Intent(getApplicationContext(),ProjectActivity.class));
            }
        }
    }

    public class timedImage implements Runnable {
        @Override
        public void run() {
            while (imageState == imageState.PLAYING) {
                try {
                    Thread.sleep(imageTime+2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 1; i < imageResources.length; i++) {
                    currentResource = imageResources[i];
                    Log.e("current picture", currentResource + "");
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageResource(currentResource);
                        }
                    });
//                    imageView.postDelayed(this,imageTime);
                    try {
                        Thread.sleep(imageTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(imageTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                imageState = imageState.STOPPED;
                finish();
                startActivity(new Intent(getApplicationContext(),ProjectActivity.class));
            }
        }
    }

}

