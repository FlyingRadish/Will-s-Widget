package org.houxg.willswidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import org.houxg.willswidget.R;


/**
 * It's like a progressbar but the origin is on the middle of bar.
 * You can set its maximum/ minimum value and current value, then the widget will show the distance
 * between current value and middle point.
 */
public class PropertyBar extends View {

    private float mMax = 100;
    private float mMin = -100;
    private float mProperty = 50;

    private Drawable mBackgroundDraw;
    private Drawable mPropertyDraw;
    private Drawable mIndicatorDraw;
    private Drawable mMidPointDraw;

    private Paint mPaint;

    private float mBackgroundDrawHeight;
    private float mPropertyDrawHeight;
    private float mMidPointOffset;
    private float mIndicatorTextSize;
    private float mIndicatorSubTextSize;
    private String mIndicatorText;
    private String mIndicatorSubText;

    private Rect mTempRect = new Rect();

    public PropertyBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PropertyBar);
        mBackgroundDraw = typedArray.getDrawable(R.styleable.PropertyBar_background_drawable);
        mPropertyDraw = typedArray.getDrawable(R.styleable.PropertyBar_property_drawable);
        if (mBackgroundDraw != null) {
            mBackgroundDrawHeight = typedArray.getDimension(R.styleable.PropertyBar_background_height, mBackgroundDraw.getIntrinsicHeight());
            mBackgroundDrawHeight = mBackgroundDrawHeight > mBackgroundDraw.getIntrinsicHeight() ? mBackgroundDrawHeight : mBackgroundDraw.getIntrinsicHeight();
        }
        if (mPropertyDraw != null) {
            mPropertyDrawHeight = typedArray.getDimension(R.styleable.PropertyBar_property_height, mPropertyDraw.getIntrinsicHeight());
            mPropertyDrawHeight = mPropertyDrawHeight > mPropertyDraw.getIntrinsicHeight() ? mPropertyDrawHeight : mPropertyDraw.getIntrinsicHeight();
        }
        mIndicatorDraw = typedArray.getDrawable(R.styleable.PropertyBar_indicator);
        mMidPointDraw = typedArray.getDrawable(R.styleable.PropertyBar_mid_indicator);
        mMidPointOffset = typedArray.getDimension(R.styleable.PropertyBar_mid_offset, 0);
        int indicatorTextColor = typedArray.getColor(R.styleable.PropertyBar_indicator_text_color, Color.BLACK);
        mIndicatorTextSize = typedArray.getDimension(R.styleable.PropertyBar_indicator_text_size, 12);
        mIndicatorSubTextSize = typedArray.getDimension(R.styleable.PropertyBar_indicator_sub_text_size, 12);
        mIndicatorText = typedArray.getString(R.styleable.PropertyBar_indicator_text);
        mIndicatorSubText = typedArray.getString(R.styleable.PropertyBar_indicator_sub_text);
        typedArray.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setColor(indicatorTextColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            Drawable[] drawables = new Drawable[]{mBackgroundDraw, mPropertyDraw};
            height = getSuggestedMinimumHeight();
            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    height = height > drawable.getIntrinsicHeight() ? height : drawable.getIntrinsicHeight();
                }
            }

            int barHei = height;
            if (mIndicatorDraw != null && mIndicatorDraw.getIntrinsicHeight() > height) {
                height = mIndicatorDraw.getIntrinsicHeight();
            }

            if (mMidPointDraw != null && mMidPointDraw.getIntrinsicHeight() - mMidPointOffset > height) {
                if (height > barHei) {
                    height = (int) (mMidPointDraw.getIntrinsicHeight() - mMidPointOffset + ((height - barHei) / 2));
                } else {
                    height = (int) (mMidPointDraw.getIntrinsicHeight() - mMidPointOffset);
                }
            }
        }
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            width = getSuggestedMinimumWidth();
        }

        setMeasuredDimension(width, height);
    }

    public void setProperty(float val) {
        mProperty = val;
        invalidate();
    }

    public void setMax(float max) {
        mMax = max;
        invalidate();
    }

    public void setMin(float min) {
        mMin = min;
        invalidate();
    }

    public float getBackgourndBarHeight() {
        return mBackgroundDrawHeight;
    }

    public float getPropertyBarHeight() {
        return mBackgroundDrawHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //background
        if (mBackgroundDraw == null || mPropertyDraw == null) {
            return;
        }
        canvas.save();
        float tempMoveY;
        float tempMoveX;
        tempMoveY = getHeight() - mBackgroundDrawHeight;
        if (mIndicatorDraw != null && mIndicatorDraw.getIntrinsicHeight() > mBackgroundDrawHeight) {
            tempMoveY += (mBackgroundDrawHeight - mIndicatorDraw.getIntrinsicHeight()) / 2;
        }
        canvas.translate(0, tempMoveY);
        mBackgroundDraw.setBounds(0, 0, getWidth(), (int) mBackgroundDrawHeight);
        mBackgroundDraw.draw(canvas);
        canvas.restore();

        //progress
        float halfX = getWidth() / 2;
        int fix = mIndicatorDraw == null ? 0 : mIndicatorDraw.getIntrinsicWidth();
        float totalX = getWidth() - fix;
        float total = Math.abs(mMax - mMin);
        float propertyX = ((mProperty - mMin) - total / 2) / total * totalX + halfX;
        if (propertyX > halfX) {
            mPropertyDraw.setBounds((int) halfX, 0, (int) propertyX, (int) mPropertyDrawHeight);
        } else {
            mPropertyDraw.setBounds((int) propertyX, 0, (int) halfX, (int) mPropertyDrawHeight);
        }

        tempMoveY = getHeight() - mPropertyDrawHeight;
        if (mIndicatorDraw != null && mIndicatorDraw.getIntrinsicHeight() > mPropertyDrawHeight) {
            tempMoveY += (mPropertyDrawHeight - mIndicatorDraw.getIntrinsicHeight()) / 2;
        }
        canvas.save();
        canvas.translate(0, tempMoveY);
        mPropertyDraw.draw(canvas);
        canvas.restore();

        //midpoint
        if (mMidPointDraw != null) {
            mMidPointDraw.setBounds(0, 0, mMidPointDraw.getIntrinsicWidth(), mMidPointDraw.getIntrinsicHeight());
            tempMoveX = (getWidth() - mMidPointDraw.getIntrinsicWidth()) / 2;
            canvas.save();
            canvas.translate(tempMoveX, mMidPointOffset);
            mMidPointDraw.draw(canvas);
            canvas.restore();
        }

        //indicator
        if (mIndicatorDraw != null) {
            mIndicatorDraw.setBounds(0, 0, mIndicatorDraw.getIntrinsicWidth(), mIndicatorDraw.getIntrinsicHeight());
            tempMoveX = propertyX - mIndicatorDraw.getIntrinsicWidth() / 2;
            tempMoveY = tempMoveY + mPropertyDrawHeight / 2 - mIndicatorDraw.getIntrinsicHeight() / 2;
            canvas.save();
            canvas.translate(tempMoveX, tempMoveY);
            mIndicatorDraw.draw(canvas);

            //indicator text
            if (!TextUtils.isEmpty(mIndicatorText)) {
                mPaint.setTextSize(mIndicatorTextSize);
                mPaint.getTextBounds(mIndicatorText, 0, mIndicatorText.length(), mTempRect);
                float textX = (mIndicatorDraw.getIntrinsicWidth() - mTempRect.width()) / 2 - mTempRect.left;
                float textY = (mIndicatorDraw.getIntrinsicHeight()) / 2;
                canvas.drawText(mIndicatorText, textX, textY, mPaint);

                if (!TextUtils.isEmpty(mIndicatorSubText)) {
                    mPaint.setTextSize(mIndicatorSubTextSize);
                    mPaint.getTextBounds(mIndicatorSubText, 0, mIndicatorSubText.length(), mTempRect);
                    textX = (mIndicatorDraw.getIntrinsicWidth() - mTempRect.width()) / 2 - mTempRect.left;
                    textY = textY + mTempRect.height();
                    canvas.drawText(mIndicatorSubText, textX, textY, mPaint);

                }
            }
            canvas.restore();
        }
    }
}
