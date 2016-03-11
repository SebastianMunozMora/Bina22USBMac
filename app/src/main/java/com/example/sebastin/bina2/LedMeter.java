package com.example.sebastin.bina2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Sebastian on 09/03/2016.
 */
public class LedMeter extends View {


    float left;
    float top ;
    float right ;
    float bottom ;
    float ledLeft;
    float ledRight;
    float ledTop;
    float ledBottom;
    float blockHeight;
    float ledHeight;
    Paint rackPaint,ledPaint;
    Rect rackRectangle,ledRectangle;
    int numLed = 0;
    int nl = 0;
    int Level= 0;
    boolean []ledState =new boolean[8];
    boolean []levelState = new boolean[8];
    public LedMeter(Context context) {
        super(context);
        init(null,0);

    }

    public LedMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        left = 0;
        top = 0;
        right  = canvas.getWidth()/3f;
        bottom = canvas.getHeight()/3f;
        blockHeight = bottom/8f;
        ledHeight = blockHeight*(2f/3f);
        ledLeft = left+blockHeight*(1f/3f);
        ledRight = right-blockHeight*(1f/3f);
        ledTop = top+blockHeight*(1f/3f);
        ledBottom = ledTop+ledHeight;
        canvas.drawRect(left,top,right,bottom+blockHeight*(1f/3f),rackPaint);
        for (int i = 0;i <= 7;i++) {
            canvas.drawRect(ledLeft,ledTop,ledRight, ledBottom, ledPaint);
            ledTop += blockHeight;
            ledBottom = ledTop+ledHeight;
        }
        blockHeight = bottom/8f;
        ledHeight = blockHeight*(2f/3f);
        ledLeft = left+blockHeight*(1f/3f);
        ledRight = right-blockHeight*(1f/3f);
        ledTop = top+blockHeight*(1f/3f);
        ledTop = ledTop+blockHeight*nl;
        ledBottom = ledTop+ledHeight;
        canvas.drawRect(ledLeft,ledTop,ledRight, ledBottom, ledPaint);


    }
    public void init(AttributeSet attributeSet, int defStyles){
        rackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rackPaint.setColor(Color.GRAY);
        rackPaint.setStyle(Paint.Style.FILL);
        ledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ledPaint.setStyle(Paint.Style.STROKE);
        ledPaint.setColor(Color.GREEN);
        ledPaint.setStyle(Paint.Style.FILL);
        ledPaint.setColor(Color.BLACK);
        ledRectangle = new Rect();
        rackRectangle = new Rect();

    }
    public void setLevel(long level) {
        int a ;
        LedSwitch ledSwitch = new LedSwitch(getContext());
        for (int i = 0;i < 9;i++) {
            a = 10*i;
            if (level <= a) {
                for (int k = 0;k < i;k++){
                    levelState[k]= true;
                }
                for (int j = i; j < 8; j++) {
                    levelState[j] = false;
                }
                break;
            }
        }
        for (int l = 0;l < 8;l++){
            if (levelState[l] != ledState[l]){
                ledState[l] = levelState[l];
                //draw

                //ledSwitch.turn();
            }
            nl = l;
            ledSwitch.turn();
            invalidate();
        }


    }


    private class LedSwitch extends View{
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            ledTop = ledTop+blockHeight*nl;
            ledBottom = ledTop+ledHeight;
            canvas.drawRect(ledLeft,ledTop,ledRight, ledBottom, ledPaint);
        }

        public LedSwitch(Context context) {
            super(context);
        }
        public void turn (){
            if (ledState[nl]) {
                ledPaint.setColor(Color.GREEN);
            }else {
                ledPaint.setColor(Color.BLACK);
            }
        }
    }
}

