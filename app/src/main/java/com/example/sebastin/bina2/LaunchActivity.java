package com.example.sebastin.bina2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class LaunchActivity extends Activity {
    ImageView imageView;
    public enum imageState  {PLAYING,STOPPED}
    private imageState imageState;
    int imageTime = 1000;
    int[] imageResources = {R.drawable.is,R.drawable.us};
    int currentResource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        imageState = imageState.PLAYING;
        imageView = (ImageView)findViewById(R.id.imageView2);
        new Thread(new timedImage()).start();
    }
    public class timedImage implements Runnable {
        @Override
        public void run() {
            while(imageState == imageState.PLAYING){
                for (int i = 0; i < imageResources.length ;i++) {
                    try {
                        Thread.sleep(imageTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    currentResource = imageResources[i];
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageResource(currentResource);
                        }
                    });
                }
                imageState = imageState.STOPPED;
                finish();
                startActivity(new Intent(getApplicationContext(),ProjectActivity.class));
            }
        }
    }

}

