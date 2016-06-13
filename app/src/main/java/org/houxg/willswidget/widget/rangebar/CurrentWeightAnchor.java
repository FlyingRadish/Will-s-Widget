package org.houxg.willswidget.widget.rangebar;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.ColorInt;

import org.houxg.willswidget.R;

public class CurrentWeightAnchor implements RangeBar.Anchor {

    private BitmapDrawable mCircleDrawable;
    private float mLineLength;
    private Paint mPaint;
    private String mCurrentWeightText;
    private int mAnchorProgress;
    private float mTextBottomMargin;
    private RectF mBound;
    private float mCompensation;

    public CurrentWeightAnchor(Context context, String currentText, int barHeight) {
        mCurrentWeightText = currentText;
        Resources resources = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_current_weight_dot);
        mCircleDrawable = new BitmapDrawable(resources, bitmap);

        mLineLength = resources.getDimension(R.dimen.weight_goal_range_bar_current_weight_anchor_line_length);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(resources.getDimension(R.dimen.weight_goal_range_bar_current_weight_anchor_text_size));
        mPaint.setColor(resources.getColor(R.color.weight_color));
        mPaint.setStrokeWidth(resources.getDimension(R.dimen.weight_goal_range_bar_current_weight_anchor_line_width));
        mTextBottomMargin = resources.getDimension(R.dimen.weight_goal_range_bar_current_weight_anchor_text_bottom_margin);
        mAnchorProgress = 0;

        mCircleDrawable.setBounds(0, 0, mCircleDrawable.getIntrinsicWidth(), mCircleDrawable.getIntrinsicHeight());
        mCompensation = mCircleDrawable.getIntrinsicHeight() / 2;
        float textWidth = getMaxTextWidth(context);
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        float textHeight = -metrics.top + metrics.bottom;
        float top = -(textHeight + mTextBottomMargin + mCircleDrawable.getIntrinsicHeight() + mLineLength);
        float bottom = barHeight / 2;
        float width = Math.max(textWidth, mCircleDrawable.getIntrinsicWidth());
        float halfWidth = width / 2;
        mBound = new RectF(-halfWidth, top, halfWidth, bottom);
    }

    public void setColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    public void setCurrentWeightText(String text) {
        mCurrentWeightText = text;
    }

    private float getMaxTextWidth(Context context) {
        String text = "current weight" + " " +
                Math.round(999.9) + "lbs";
        return mPaint.measureText(text);
    }

    @Override
    public int getAnchorProgress() {
        return mAnchorProgress;
    }

    @Override
    public RectF getBound() {
        return mBound;
    }

    @Override
    public void draw(Canvas canvas) {
        float width = mBound.width();
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        canvas.drawText(mCurrentWeightText, width / 2, Math.abs(metrics.top), mPaint);
        float textHeight = -metrics.top + metrics.bottom;
        canvas.translate((width - mCircleDrawable.getIntrinsicWidth()) / 2, textHeight + mTextBottomMargin);
        canvas.save();
        canvas.translate(mCircleDrawable.getIntrinsicWidth() / 2, mCircleDrawable.getIntrinsicHeight());
        canvas.drawLine(0, -mCompensation, 0, mLineLength, mPaint);
        canvas.restore();
        mCircleDrawable.draw(canvas);
    }
}
