package org.houxg.willswidget.widget.rangebar;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;

import org.houxg.willswidget.R;

public class CircleThumb implements RangeBar.Thumb {

    private RectF mCircleRectF;
    private Paint mPaint;
    private RectF mBound;
    private float mBarPadding;

    public CircleThumb(Context context, @ColorInt int thumbColor) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(thumbColor);
        Resources resources = context.getResources();
        float rad = resources.getDimension(R.dimen.goal_range_bar_thumb_circle_rad);
        mCircleRectF = new RectF(0, 0, rad * 2, rad * 2);

        float bottom = mCircleRectF.height() / 2;
        float top = -mCircleRectF.height() / 2;
        float halfWidth = mCircleRectF.width() / 2;
        mBound = new RectF(-halfWidth, top, halfWidth, bottom);
        mBarPadding = halfWidth;
    }

    @Override
    public RectF getBound() {
        return mBound;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawOval(mCircleRectF, mPaint);
    }
}
