package com.example.sebastin.bina2;

import android.content.Context;
import android.content.res.Resources;
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
    float mRackWidth;
    float mRackHeight;
    Paint rackPaint,ledPaint;
    Rect rackRectangle,ledRectangle;
    int numLed = 8;
    boolean []levelState;
    double level;
    int rackColor;
    public LedMeter(Context context) {
        super(context);

        init(null,0);

    }

    public LedMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LedMeter,
                0, 0
        );
        mRackWidth = a.getDimension(R.styleable.LedMeter_ledWidth, 0.0f);
        mRackHeight = a.getDimension(R.styleable.LedMeter_ledHeight, 0.0f);
        numLed = a.getInt(R.styleable.LedMeter_numLed,8);
        rackColor = getResources().getColor(R.color.rack_color);
        init(attrs, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(left, top, right, bottom + blockHeight * (1f/ 3f), rackPaint);
        ledTop = top+blockHeight*(1f/3f);
        for (int i = 0;i < numLed;i++) {
            if (levelState[i]){
                if (i >= 7) {
                    ledPaint.setColor(Color.RED);
                }else if(i >= 4) {
                    ledPaint.setColor(Color.YELLOW);
                }else{
                    ledPaint.setColor(Color.GREEN);
                }
            }else{
                ledPaint.setColor(Color.BLACK);
            }
            ledBottom = bottom - blockHeight*i;
            ledTop = ledBottom-ledHeight;
            canvas.drawRect(ledLeft, ledTop, ledRight, ledBottom, ledPaint);
        }
    }
    public void init(AttributeSet attributeSet, int defStyles){

        rackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rackPaint.setColor(rackColor);
        rackPaint.setStyle(Paint.Style.FILL);
        ledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ledPaint.setStyle(Paint.Style.STROKE);
        ledPaint.setColor(Color.GREEN);
        ledPaint.setStyle(Paint.Style.FILL);
        ledPaint.setColor(Color.BLACK);
        ledRectangle = new Rect();
        rackRectangle = new Rect();
        left = 0;
        top = 0;
        right  =mRackWidth;
        bottom = mRackHeight-mRackHeight*(1/10);
        blockHeight = bottom/numLed;
        ledHeight = blockHeight*(2f/3f);
        ledLeft = left+blockHeight*(1f/3f);
        ledRight = right-blockHeight*(1f/3f);
        ledTop = top+blockHeight*(1f/3f);
        ledBottom = ledTop+ledHeight;
        levelState = new boolean[numLed];
        setLevel(10,80);
    }
    public void setLevel(double level,int max) {
        this.level = level;
        for (int i = 0;i < numLed-1;i++) {
            if (level <= (max/numLed)*i) {
                for (int k = 0;k < i;k++){
                    levelState[k]= true;
                }
                for (int j = i; j < numLed; j++) {
                    levelState[j] = false;
                }
                break;
            }
        }
        invalidate();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 200;
        int desiredHeight = 400;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

}

