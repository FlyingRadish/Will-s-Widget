package org.houxg.willswidget.widget.rangebar;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

import org.houxg.willswidget.R;

public class ColoredProgressLine extends Drawable {

    private Paint mPaint;
    private float mStrokeWidth;

    public ColoredProgressLine(Context context, @ColorInt int color) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        Resources resources = context.getResources();
        mStrokeWidth = resources.getDimension(R.dimen.goal_range_bar_connection_line_stroke_width);
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) mStrokeWidth;
    }

    @Override
    public void draw(Canvas canvas) {
        float offsetY = -mStrokeWidth / 2;
        canvas.drawLine(0, offsetY, getBounds().width(), offsetY, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
