package org.houxg.willswidget.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class EcgView extends View {

    public static final int LINE_STROKE_WIDTH = 5;
    private final float MAX_FACTOR = 1f;
    private final float MIN_FACTOR = 1f;

    private float mMax = -1;
    private float mMin = -1;
    private float mScale;
    private int mPointInX = 90;
    private float mInterval;
    private Paint mPaint;
    private Path mPath = new Path();
    private List<Float> mBuffer = new ArrayList<>(100);

    public EcgView(Context context) {
        super(context);
        initPaint();
    }

    public EcgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(LINE_STROKE_WIDTH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mInterval = (float) getWidth() / mPointInX;
        mScale = getHeight() / Math.abs(mMax - mMin);
    }


    public void clear() {
        mBuffer.clear();
        postInvalidate();
    }

    public void resetScale(float max, float min) {
        mMax = max;
        mMin = min;
        mScale = getHeight() / Math.abs(mMax - mMin);
    }

    public void input(float data) {
        data = data > mMax ? mMax : data;
        data = data < mMin ? mMin : data;

        addToFifoBuffer(data);
        if (mBuffer.size() < 2) {
            return;
        }
        postInvalidate();
    }

    public void inputAndRescale(float data) {
        addToFifoBuffer(data);
        if (mBuffer.size() < 2) {
            return;
        }
        updateScale();
        postInvalidate();
    }

    public void inputAndRescale(float[] data) {
        addToFifoBuffer(data);
        if (mBuffer.size() < 2) {
            return;
        }
        updateScale();
        postInvalidate();
    }

    public float[] getInputs() {
        if (mBuffer == null) {
            return new float[0];
        }
        float[] data = new float[mBuffer.size()];
        for (int i = 0; i < mBuffer.size(); ++i) {
            data[i] = mBuffer.get(i);
        }
        return data;
    }

    private void addToFifoBuffer(float data) {
        mBuffer.add(data);
        if (mBuffer.size() > mPointInX) {
            mBuffer.subList(0, mBuffer.size() - mPointInX).clear();
        }
    }

    private void addToFifoBuffer(float[] data) {
        for (float val : data) {
            mBuffer.add(val);
        }
        if (mBuffer.size() > mPointInX) {
            mBuffer.subList(0, mBuffer.size() - mPointInX).clear();
        }
    }

    private void updateScale() {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (float val : mBuffer) {
            max = val > max ? val * MAX_FACTOR : max;
            min = val < min ? val * MIN_FACTOR : min;
        }
        mMin = min;
        mMax = max;
        mScale = getHeight() / Math.abs(mMax - mMin);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //right -> left
        if (mBuffer.size() > 2) {
            mPath.reset();
            float[] vals = getLinesForBezier();
            mPath.moveTo(vals[0], vals[1]);
            float preX = vals[0];
            float preY = vals[1];
            for (int i = 2; i < vals.length; i++) {
                float x = vals[i];
                i++;
                float y = vals[i];
                bezierPath(mPath, x, y, preX, preY);
                preX = x;
                preY = y;
            }
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void bezierPath(Path path, float x, float y, float preX, float preY) {
        float dx = Math.abs(x - preX);
        float dy = Math.abs(y - preY);
        if (dx >= 3 || dy >= 3) {
            float cX = (x + preX) / 2;
            float cY = (y + preY) / 2;
            path.quadTo(preX, preY, cX, cY);
        } else {
            path.lineTo(x, y);
        }
    }

    private float[] getLinesForBezier() {
        float[] lines = new float[mBuffer.size() * 2];
        float baseX = (mPointInX - mBuffer.size()) * mInterval;
        baseX += mInterval;
        int i = 0;
        for (int j = 0; j < mBuffer.size(); j++) {
            lines[i] = baseX;
            i++;
            lines[i] = getWithScale(j);
            i++;
            baseX += mInterval;
        }
        return lines;
    }

    private float getWithScale(int position) {
        return (mMax - mBuffer.get(position)) * mScale;
    }
}