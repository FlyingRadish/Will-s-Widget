package org.houxg.willswidget.widget;


import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import org.houxg.willswidget.DisplayUtils;

public class CountDownView extends View {

    private Paint mPaint;
    private long startTime;
    private long lastTime;
    private long count = 0;
    private Rect tempRect = new Rect();
    private boolean isRunning = false;
    private AnimateThread animateThread;
    private CountdownText[] countdownTexts;
    private final static int DURATION = 1000;
    private final static int TWICE_DURATION = DURATION * 2;

    public CountDownView(Context context) {
        super(context);
        init(context);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setColor(Color.LTGRAY);
        mPaint.setTextSize(DisplayUtils.dp2px(context, 32));
        countdownTexts = new CountdownText[2];
    }

    public void start(int value) {
        countdownTexts[0] = createCountDown(value, true);
        countdownTexts[1] = createCountDown(value - 1, false);

        startTime = System.currentTimeMillis();
        lastTime = startTime;
        isRunning = true;
        count = 0;
        if (animateThread != null) {
            animateThread.stopAnimate();
        }
        animateThread = new AnimateThread();
        postInvalidate();
        animateThread.start();
    }

    private CountdownText createCountDown(int value, boolean isMiddle) {
        float valueWidth = mPaint.measureText(String.valueOf(value));
        float moveLength = getWidth() + valueWidth;
        float beginDistance = isMiddle ? moveLength / 2 : 0;
        return new CountdownText(value, getWidth(), beginDistance, moveLength / 2, moveLength / 2);

    }

    public void stop() {
        if (animateThread != null) {
            animateThread.stopAnimate();
            animateThread = null;
        }
    }

    private class AnimateThread extends Thread {

        private boolean isRunning = true;

        @Override
        public void run() {
            super.run();
            while (isRunning) {
                try {
                    long nowTime = System.currentTimeMillis();
                    long dt = nowTime - lastTime;
                    lastTime = nowTime;
                    float ratio = (float) dt / TWICE_DURATION;
                    for (CountdownText countdownText : countdownTexts) {
                        countdownText.update(ratio);
                    }
                    long timeLeftInSecond = nowTime - startTime;
                    long lastCount = count;
                    count = timeLeftInSecond / DURATION;
                    if (lastCount != count) {
                        updateValue();
                    }
                    postInvalidate();
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopAnimate() {
            isRunning = false;
        }
    }

    private void updateValue() {
        int index = (int) (count - 1) % countdownTexts.length;
        int preIndex = (index + 1) % countdownTexts.length;
        CountdownText previous = countdownTexts[preIndex];
        countdownTexts[index] = createCountDown(previous.value() - 1, false);
    }


    private class CountdownText {
        int mValue;
        float mOffset;
        float mLength;
        float mOriginal;
        float mDeltaLength = 0;

        float fadeInDistance;
        float fadeOutDistance;
        TimeInterpolator mInInterpolator;
        TimeInterpolator mOutInterpolator;

        public CountdownText(int value, float offset, float beginDistance, float inDistance, float outDistance) {
            mValue = value;
            mOriginal = offset;
            mOffset = offset;
            mDeltaLength = beginDistance;
            mInInterpolator = new AccelerateDecelerateInterpolator();
            mOutInterpolator = new DecelerateInterpolator();
            fadeInDistance = inDistance;
            fadeOutDistance = outDistance;
            mLength = inDistance + outDistance;
        }

        public void update(float ratio) {
            mDeltaLength += mLength * ratio;
            if (mDeltaLength < fadeInDistance) {
                float progress = mDeltaLength / fadeInDistance;
                mOffset = mOriginal - fadeInDistance * mInInterpolator.getInterpolation(progress);
            } else {
                float progress = (mDeltaLength - fadeInDistance) / fadeOutDistance;
                mOffset = mOriginal - fadeOutDistance * mOutInterpolator.getInterpolation(progress) - fadeInDistance;
            }
        }

        public String string() {
            return String.valueOf(mValue);
        }

        public float offset() {
            return mOffset;
        }

        public int value() {
            return mValue;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isRunning) {
            for (CountdownText countDown : countdownTexts) {
                canvas.drawText(countDown.string(), countDown.offset(), getTextCenterY(countDown.string()), mPaint);
            }
        }
    }

    private float getTextCenterY(String text) {
        mPaint.getTextBounds(text, 0, text.length(), tempRect);
        return (getHeight() + tempRect.height()) / 2 - tempRect.bottom;
    }
}
