package org.houxg.willswidget.widget.rangebar;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.DrawableRes;

import org.houxg.willswidget.R;

public class SuggestionAnchor implements RangeBar.Anchor {

    private BitmapDrawable mThumb;
    private String mText;
    private Paint mTextPaint;
    private int mAnchorProgress;
    private RectF mBound;
    private float mTextBottomMargin;

    public SuggestionAnchor(Context context, @DrawableRes int thumbResId, int anchorProgress) {
        mAnchorProgress = anchorProgress;

        Resources resources = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, thumbResId);
        mThumb = new BitmapDrawable(resources, bitmap);
        mTextBottomMargin = resources.getDimension(R.dimen.goal_range_bar_suggested_text_bottom_margin);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(resources.getDimension(R.dimen.goal_range_bar_suggested_text_size));
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mText = resources.getString(R.string.range_bar_suggested);

        Rect textBound = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), textBound);

        mThumb.setBounds(0, 0, mThumb.getIntrinsicWidth(), mThumb.getIntrinsicHeight());
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        int top = (int) -(textHeight + mTextBottomMargin + mThumb.getIntrinsicHeight() / 2);
        int bottom = mThumb.getIntrinsicHeight() / 2;
        int halfWidth = Math.max(mThumb.getIntrinsicWidth(), textBound.width()) / 2;
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
        int width = (int) mBound.width();
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        canvas.drawText(mText, width / 2, Math.abs(fontMetrics.top), mTextPaint);
        canvas.translate((width - mThumb.getIntrinsicWidth()) / 2, mBound.height() - mThumb.getIntrinsicHeight());
        mThumb.draw(canvas);
    }
}
