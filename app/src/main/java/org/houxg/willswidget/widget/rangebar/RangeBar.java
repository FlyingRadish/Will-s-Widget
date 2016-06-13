package org.houxg.willswidget.widget.rangebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import org.houxg.willswidget.R;

import java.util.ArrayList;
import java.util.List;


/**
 * This is a flexible rangeBar, its properties are max/min/progress/original point and they are all integer.
 * you can define your custom move thumb by implement {@link RangeBar.Thumb},
 * custom bar by {@link RangeBar#setBar(Drawable)}. you can add any anchor you want on the rangeBar by
 * implement {@link RangeBar.Anchor}. If your rangeBar needs a line between the original point and
 * move thumb, you can {@link RangeBar#setProgressLine(Drawable)}
 */
public class RangeBar extends View {

    private int mMax = 100;
    private int mMin = 0;
    private int mProgress = 0;
    private float mIntervalInPixels;

    private float mMiddleY;
    private float mBarLeft;
    private float mBarRight;
    private float mBarLength;
    private int mMaxAnchorPaddingLeft;
    private int mMaxAnchorPaddingRight;

    private List<Anchor> mAnchors;
    private Thumb mThumb;
    private Drawable mProgressLine;
    private Drawable mBar;
    private ProgressChangedListener mListener;

    public RangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mAnchors = new ArrayList<>();
        mBar = new LineBar(context, Color.GRAY);
        mThumb = new DefaultThumb();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RangeBar);
        mMax = typedArray.getInteger(R.styleable.RangeBar_max, 100);
        mMin = typedArray.getInteger(R.styleable.RangeBar_min, 0);
        typedArray.recycle();
    }

    public void setProgress(int progress) {
        mProgress = progress;
        notifyListener();
        invalidate();
    }

    public void setMax(int max) {
        mMax = max;
        invalidate();
    }

    public void setMin(int min) {
        mMin = min;
        invalidate();
    }

    public void addAnchor(Anchor anchor) {
        mAnchors.add(anchor);
        invalidate();
    }

    public void removeAllAnchors() {
        mAnchors.clear();
        invalidate();
    }

    public void removeAnchor(Anchor anchor) {
        if (mAnchors.remove(anchor)) {
            invalidate();
        }
    }

    public void setThumb(Thumb thumb) {
        mThumb = thumb;
        invalidate();
    }

    public void setBar(Drawable bar) {
        mBar = bar;
        invalidate();
    }

    public void setProgressLine(Drawable line) {
        mProgressLine = line;
        invalidate();
    }

    public void setProgressChangedListener(ProgressChangedListener listener) {
        mListener = listener;
    }

    private void notifyListener() {
        if (mListener != null) {
            mListener.onProgressChanged(this, mProgress);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        mMaxAnchorPaddingLeft = Integer.MIN_VALUE;
        mMaxAnchorPaddingRight = Integer.MIN_VALUE;
        for (Anchor anchor : mAnchors) {
            mMaxAnchorPaddingLeft = Math.max(Math.abs((int) anchor.getBound().left), mMaxAnchorPaddingLeft);
            mMaxAnchorPaddingRight = Math.max(Math.abs((int) anchor.getBound().right), mMaxAnchorPaddingRight);
        }

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            int maxAnchorPositiveTop = Integer.MIN_VALUE;
            int maxAnchorBottom = Integer.MIN_VALUE;

            for (Anchor anchor : mAnchors) {
                maxAnchorPositiveTop = (int) Math.max(maxAnchorPositiveTop, Math.abs(anchor.getBound().top));
                maxAnchorBottom = (int) Math.max(maxAnchorBottom, anchor.getBound().bottom);
            }

            maxAnchorPositiveTop = (int) Math.max(maxAnchorPositiveTop, Math.abs(mThumb.getBound().top));
            maxAnchorBottom = (int) Math.max(maxAnchorBottom, mThumb.getBound().bottom);

            mMiddleY = maxAnchorPositiveTop;
            height = Math.max(maxAnchorBottom + maxAnchorPositiveTop, mBar.getIntrinsicHeight());
            if (mProgressLine != null) {
                height = Math.max(height, mProgressLine.getIntrinsicHeight());
            }
        }
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            width = getSuggestedMinimumWidth();
            for (Anchor anchor : mAnchors) {
                if (anchor != null) {
                    width = (int) Math.max(anchor.getBound().width(), width);
                }
            }
            width = (int) Math.max(mThumb.getBound().width(), width);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float barPadding = Math.max(Math.abs(mThumb.getBound().left), Math.abs(mThumb.getBound().right));
        mBarLeft = Math.max(barPadding, mMaxAnchorPaddingLeft);
        mBarRight = getWidth() - Math.max(barPadding, mMaxAnchorPaddingRight);
        mBarLength = mBarRight - mBarLeft;
        mIntervalInPixels = mBarLength / (mMax - mMin);
        mBar.setBounds(0, 0, (int) mBarLength, mBar.getIntrinsicHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mBarLeft, mMiddleY - mBar.getIntrinsicHeight() / 2);
        mBar.draw(canvas);
        canvas.restore();

        canvas.translate(mBarLeft, mMiddleY);
        int count = canvas.save();
        canvas.save();

        if (mProgressLine != null) {
            int originalPoint = 0;
            float originalOffsetX = convertProgressToOffsetX(originalPoint);
            canvas.translate(originalOffsetX, 0);
            mProgressLine.setBounds(0,
                    0,
                    (int) (convertProgressToOffsetX(mProgress) - originalOffsetX),
                    mProgressLine.getIntrinsicHeight());
            mProgressLine.draw(canvas);
        }
        canvas.restoreToCount(count);
        for (Anchor anchor : mAnchors) {
            canvas.save();
            drawAnchor(anchor, canvas);
            canvas.restoreToCount(count);
        }
        float offX = mThumb.getBound().left + convertProgressToOffsetX(mProgress);
        float offY = mThumb.getBound().top;
        canvas.translate(offX, offY);
        mThumb.draw(canvas);
    }

    private void drawAnchor(Anchor anchor, Canvas canvas) {

        float offX = anchor.getBound().left + convertProgressToOffsetX(anchor.getAnchorProgress());
        float offY = anchor.getBound().top;
        canvas.translate(offX, offY);
        anchor.draw(canvas);
    }

    private float convertProgressToOffsetX(int progress) {
        if (progress > mMax) {
            progress = mMax;
        }
        if (progress < mMin) {
            progress = mMin;
        }

        float range = mMax - mMin;
        float ratio = (progress - mMin) / range;
        return mBarLength * ratio;
    }

    private int mTouchSlop;
    private float mStartX;
    private float mStartY;
    private boolean isSwiping;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(event.getX(), event.getY());
                mStartX = event.getX();
                mStartY = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float deltaY = Math.abs(event.getY() - mStartY);
                if (!isSwiping && deltaY < mTouchSlop) {
                    onActionPressed(event.getX());
                }
                if (isSwiping) {
                    isSwiping = false;
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                onActionUp(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                float delta = Math.abs(event.getX() - mStartX);
                if (!isSwiping && delta > mTouchSlop) {
                    isSwiping = true;
                }
                if (isSwiping) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    onActionMove(event.getX());
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    void onActionDown(float x, float y) {
    }

    void onActionUp(float x, float y) {
    }

    void onActionPressed(float x) {
        moveThumb(x);
    }

    void onActionMove(float x) {
        moveThumb(x);
    }

    private void moveThumb(float x) {
        // If the user has moved their finger outside the range of the bar,
        // do not move the thumbs past the edge.
        if (mProgress > mMin && x < mBarLeft) {
            setProgress(mMin);
        }
        if (mProgress < mMax && x > mBarRight) {
            setProgress(mMax);
        } else if (x >= mBarLeft && x <= mBarRight) {
            float originalPointInPixels = mBarLeft - (float) mMin / (mMax - mMin) * mBarLength;
            setProgress(Math.round((x - originalPointInPixels) / mIntervalInPixels));
        }
    }

    public interface Anchor {

        int getAnchorProgress();

        /**
         * @return The bound of this anchor, bound's left is the offset between the anchorProgress and the anchor's left,
         * bound's top is the offset between the middle point of the bar and anchor's top
         */
        RectF getBound();

        void draw(Canvas canvas);
    }

    public interface Thumb {
        /**
         * @return The bound of this anchor, bound's left is the offset between the anchorProgress and the anchor's left,
         * bound's top is the offset between the middle point of the bar and anchor's top
         */
        RectF getBound();

        void draw(Canvas canvas);
    }

    public interface ProgressChangedListener {
        void onProgressChanged(RangeBar rangeBar, int progress);
    }

    private class DefaultThumb implements Thumb {

        private RectF mRect;
        private Paint mPaint;
        private RectF mBound;

        public DefaultThumb() {
            mRect = new RectF(0, 0, 20, 20);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.CYAN);
            mBound = new RectF(-10, -10, 10, 10);
        }

        @Override
        public RectF getBound() {
            return mBound;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawOval(mRect, mPaint);
        }
    }
}
