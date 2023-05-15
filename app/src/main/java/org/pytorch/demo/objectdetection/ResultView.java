// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.

package org.pytorch.demo.objectdetection;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import android.util.Log;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ResultView extends View {

    private final static int TEXT_X = 40;
    private final static int TEXT_Y = 35;
    private final static int TEXT_WIDTH = 260;
    private final static int TEXT_HEIGHT = 50;

    private Paint mPaintRectangle;
    private Paint mPaintText;
    private ArrayList<Result> mResults;

    private DatabaseReference mDatabase;

    public ResultView(Context context) {
        super(context);
    }

    public ResultView(Context context, AttributeSet attrs){
        super(context, attrs);
        mPaintRectangle = new Paint();
        mPaintRectangle.setColor(Color.YELLOW);
        mPaintText = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mResults == null) return;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        String object;
        boolean coin_detected = false;
        double vertical_pix = 0;
        double horizontal_pix = 0;
        mDatabase.child("인식한것").removeValue();
        int i=0;

        for (Result result : mResults) {
            mPaintRectangle.setStrokeWidth(5);
            mPaintRectangle.setStyle(Paint.Style.STROKE);
            canvas.drawRect(result.rect, mPaintRectangle);

            Path mPath = new Path();
            RectF mRectF = new RectF(result.rect.left, result.rect.top, result.rect.left + TEXT_WIDTH,  result.rect.top + TEXT_HEIGHT);
            mPath.addRect(mRectF, Path.Direction.CW);
            mPaintText.setColor(Color.MAGENTA);
            canvas.drawPath(mPath, mPaintText);

            mPaintText.setColor(Color.WHITE);
            mPaintText.setStrokeWidth(0);
            mPaintText.setStyle(Paint.Style.FILL);
            mPaintText.setTextSize(32);

            //put object width and height in variable
            int objectWidth = result.rect.right - result.rect.left;
            int objectHeight = result.rect.bottom - result.rect.top;

            //DatabaseReference testing = database.getReference(PrePostProcessor.mClasses[result.classIndex]);
            //mDatabase.child("인식한것").removeValue();
            mDatabase.child("coin_detected").removeValue();
            mDatabase.child("인식한것").child(String.valueOf(i)).setValue(PrePostProcessor.mClasses[result.classIndex]);
            mDatabase.child("인식한것").child(String.valueOf(i)).child("width").setValue(objectWidth);
            mDatabase.child("인식한것").child(String.valueOf(i)).child("height").setValue(objectHeight);
            Log.d("width", String.valueOf(objectWidth));
            Log.d("height", String.valueOf(objectHeight));

            object= PrePostProcessor.mClasses[result.classIndex];

            if(object.equals("dog")){
                coin_detected = true;
                Log.d("dog w", String.valueOf(objectWidth));
                Log.d("dog h", String.valueOf(objectHeight));
                vertical_pix = objectWidth/2.4;
                horizontal_pix = objectHeight/2.4;

            }
            i++;

            canvas.drawText(String.format("%s %.2f", PrePostProcessor.mClasses[result.classIndex], result.score), result.rect.left + TEXT_X, result.rect.top + TEXT_Y, mPaintText);
        }
        if(coin_detected){
            //set textview id:testing_result in activity_main.xml to "coin detected"
            //MainActivity.testing_result.setText("coin detected");
            mDatabase.child("coin_detected").child("vertical_pix").setValue(vertical_pix);
            mDatabase.child("coin_detected").child("horizontal_pix").setValue(horizontal_pix);
        }

    }

    public void setResults(ArrayList<Result> results) {
        mResults = results;
    }
}
