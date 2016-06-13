package org.houxg.willswidget.widget.rangebar;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;

import org.houxg.willswidget.R;

public class WeightGoalThumb implements RangeBar.Thumb {

    private RectF mCircleRectF;
    private Paint mPaint;
    private String mTargetText;
    private String mGoalText = "";
    private float lineLength;
    private float mLineBottomMargin;

    private RectF mBound;

    private Paint mTargetTextPaint;
    private Paint mGoalTextPaint;

    public WeightGoalThumb(Context context,
                           @ColorInt int thumbAndLineColor,
                           @ColorInt int targetTextColor,
                           @ColorInt int goalTextColor) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(thumbAndLineColor);

        mTargetTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTargetTextPaint.setTextAlign(Paint.Align.CENTER);
        mTargetTextPaint.setColor(targetTextColor);

        mGoalTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGoalTextPaint.setTextAlign(Paint.Align.CENTER);
        mGoalTextPaint.setColor(goalTextColor);

        Resources resources = context.getResources();

        float rad = resources.getDimension(R.dimen.goal_range_bar_thumb_circle_rad);
        mCircleRectF = new RectF(0, 0, rad * 2, rad * 2);

        lineLength = resources.getDimension(R.dimen.weight_goal_range_bar_thumb_line_length);
        mPaint.setStrokeWidth(resources.getDimension(R.dimen.weight_goal_range_bar_thumb_line_width));
        mLineBottomMargin = resources.getDimension(R.dimen.weight_goal_range_bar_thumb_line_margin_bottom);

        mTargetText = resources.getString(R.string.goals);
        mTargetTextPaint.setTextSize(resources.getDimension(R.dimen.weight_goal_range_bar_thumb_target_text_size));
        mGoalTextPaint.setTextSize(resources.getDimension(R.dimen.weight_goal_range_bar_thumb_goal_text_size));

        float targetWidth = mTargetTextPaint.measureText(mTargetText);
        float targetHeight = getTextHeight(mTargetTextPaint.getFontMetrics());

        float goalTextMaxWidth = getMaxLengthGoalTextWidth(resources);
        int goalTextHeight = (int) getTextHeight(mGoalTextPaint.getFontMetrics());

        float bottom = mCircleRectF.height() / 2 + lineLength + mLineBottomMargin + targetHeight + goalTextHeight;
        float top = -mCircleRectF.height() / 2;
        float width = Math.max(mCircleRectF.width(), targetWidth);
        width = Math.max(width, goalTextMaxWidth);
        float halfWidth = width / 2;
        mBound = new RectF(-halfWidth, top, halfWidth, bottom);
    }

    //FIXME:
    private float getMaxLengthGoalTextWidth(Resources resources) {
        float loseStringWidth = mGoalTextPaint.measureText(resources.getString(R.string.lose_weight) + " " +
                999.9 + "lbs");
        float gainStringWidth = mGoalTextPaint.measureText(resources.getString(R.string.gain_weight) + " " +
                999.9 + "lbs");
        return Math.max(loseStringWidth, gainStringWidth);
    }

    @Override
    public RectF getBound() {
        return mBound;
    }

    @Override
    public void draw(Canvas canvas) {
        float middleX = mBound.width() / 2;
        canvas.save();
        canvas.translate((mBound.width() - mCircleRectF.width()) / 2, 0);
        canvas.drawOval(mCircleRectF, mPaint);
        canvas.restore();
        float lineStartY = mCircleRectF.height();
        float lineEndY = lineStartY + lineLength;
        canvas.drawLine(middleX, mCircleRectF.height(), middleX, lineEndY, mPaint);

        canvas.translate(0, lineEndY + mLineBottomMargin);
        Paint.FontMetrics metrics = mTargetTextPaint.getFontMetrics();
        canvas.drawText(mTargetText, middleX, Math.abs(metrics.top), mTargetTextPaint);

        canvas.translate(0, getTextHeight(metrics));
        metrics = mGoalTextPaint.getFontMetrics();
        canvas.drawText(mGoalText, middleX, Math.abs(metrics.top), mGoalTextPaint);
    }

    float getTextHeight(Paint.FontMetrics metrics) {
        return Math.abs(metrics.top) + metrics.bottom;
    }

    public void setGoalText(String goalText) {
        mGoalText = goalText;
    }
}
