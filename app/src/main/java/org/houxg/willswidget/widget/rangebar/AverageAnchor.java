package org.houxg.willswidget.widget.rangebar;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;

import org.houxg.willswidget.R;

public class AverageAnchor implements RangeBar.Anchor {

    private String mText;
    private Paint mTextPaint;
    private Paint mLinePaint;
    private float mLineLength;
    private int mAnchorProgress;
    private RectF mBound;
    private float mLineMargin;

    public AverageAnchor(Context context, int anchorProgress, @ColorInt int lineColor) {
        mAnchorProgress = anchorProgress;

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mTextPaint.setColor(Color.LTGRAY);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(lineColor);

        Resources resources = context.getResources();
        mTextPaint.setTextSize(resources.getDimension(R.dimen.goal_range_bar_average_anchor_text_size));
        mText = resources.getString(R.string.range_bar_average);

        mLinePaint.setStrokeWidth(resources.getDimension(R.dimen.goal_range_bar_average_anchor_line_width));

        mLineLength = resources.getDimension(R.dimen.goal_range_bar_average_anchor_line_length);
        mLineMargin = resources.getDimension(R.dimen.goal_range_bar_average_anchor_line_margin);

        Rect textBound = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), textBound);
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        float textHeight = -metrics.top + metrics.bottom;
        int top = 0;
        int bottom = (int) (mLineMargin * 2 + mLineLength + textHeight);
        int halfWidth = textBound.width() / 2;
        mBound = new RectF(-halfWidth, top, halfWidth, bottom);
    }

    @Override
    public int getAnchorProgress() {
        return mAnchorProgress;
    }

    public void setAnchorProgress(int progress) {
        mAnchorProgress = progress;
    }

    @Override
    public RectF getBound() {
        return mBound;
    }

    @Override
    public void draw(Canvas canvas) {
        float halfWidth = mBound.width() / 2;
        canvas.drawLine(halfWidth, mLineMargin, halfWidth, mLineMargin + mLineLength, mLinePaint);
        canvas.translate(0, mLineMargin * 2 + mLineLength);
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        canvas.drawText(mText, halfWidth, -metrics.top, mTextPaint);
    }
}
