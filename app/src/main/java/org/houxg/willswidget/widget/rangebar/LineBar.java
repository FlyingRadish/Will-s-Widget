package org.houxg.willswidget.widget.rangebar;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

import org.houxg.willswidget.R;

public class LineBar extends Drawable {

    private Paint mPaint;
    private float mHeight;

    public LineBar(Context context, @ColorInt int color) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mHeight = context.getResources().getDimension(R.dimen.goal_range_bar_height);
        mPaint.setStrokeWidth(mHeight);
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) mHeight;
    }

    @Override
    public void draw(Canvas canvas) {
        Rect rect = getBounds();
        canvas.drawLine(0, 0, rect.width(), 0, mPaint);
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
