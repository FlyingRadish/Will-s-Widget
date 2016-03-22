package org.houxg.willswidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.houxg.willswidget.R;


public class TutorialView extends View implements View.OnClickListener {

    private View mTargetView;
    private Paint mPaint = new Paint();
    private int[] mTempLocation = new int[2];
    private float mTouchX;
    private float mTouchY;
    private int mFadedColor;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    public TutorialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TutorialView);
        mFadedColor = typedArray.getColor(R.styleable.TutorialView_faded_color, 0x50000000);
        typedArray.recycle();
        initPaint();
        setOnClickListener(this);
    }

    private void initPaint() {
        mPaint.setColor(mFadedColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setTargetView(View targetView) {
        this.mTargetView = targetView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouchX = event.getX();
        mTouchY = event.getY();
        if (isTouchingTargetArea()) {
            return false;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTargetView == null) {
            return;
        }

        mTargetView.getLocationOnScreen(mTempLocation);
        int targetY = mTempLocation[1];
        int targetX = mTempLocation[0];
        getLocationOnScreen(mTempLocation);
        int drawY = targetY - mTempLocation[1];
        int drawX = targetX - mTempLocation[0];

        canvas.save();
        canvas.translate(drawX, drawY);
        mTargetView.draw(canvas);
        canvas.restore();
        mPaint.setXfermode(mXfermode);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
    }

    @Override
    public void onClick(View v) {
        Log.i("will", "click");
        setVisibility(GONE);
        if (mTargetView != null && isTouchingTargetArea()) {
            mTargetView.performClick();
        }
    }

    private boolean isTouchingTargetArea() {
        mTargetView.getLocationOnScreen(mTempLocation);
        int targetX = mTempLocation[0];
        int targetY = mTempLocation[1];
        getLocationOnScreen(mTempLocation);
        targetX -= mTempLocation[0];
        targetY -= mTempLocation[1];
        return mTouchX > targetX
                && mTouchX < targetX + mTargetView.getWidth()
                && mTouchY > targetY
                && mTouchY < targetY + mTargetView.getHeight();
    }
}
