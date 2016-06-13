package org.houxg.willswidget.widget.rangebar;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import org.houxg.willswidget.R;

public class GoalRangeBar extends RangeBar {

    private AverageAnchor mAverageAnchor;
    private SuggestionAnchor mSuggestionAnchor;

    public GoalRangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources resources = context.getResources();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GoalRangeBar);
        int mainColor = typedArray.getColor(R.styleable.GoalRangeBar_main_color, resources.getColor(R.color.setting_activity_point));
        int suggestionResId = typedArray.getResourceId(R.styleable.GoalRangeBar_suggestion_drawable, R.drawable.activity_suggested_logo);
        typedArray.recycle();

        setBar(new LineBar(context, Color.LTGRAY));
        setThumb(new CircleThumb(context, mainColor));
        setProgressLine(new ColoredProgressLine(context, mainColor));
        mAverageAnchor = new AverageAnchor(context, 40, mainColor);
        mSuggestionAnchor = new SuggestionAnchor(context, suggestionResId, 60);
        addAnchor(mAverageAnchor);
        addAnchor(mSuggestionAnchor);
    }

    public void setAveragePoint(int point) {
        mAverageAnchor.setAnchorProgress(point);
        invalidate();
    }

    public void setSuggestionPoint(int point) {
        mSuggestionAnchor.setAnchorProgress(point);
        invalidate();
    }
}
