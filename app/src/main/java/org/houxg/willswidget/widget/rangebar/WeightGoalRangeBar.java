package org.houxg.willswidget.widget.rangebar;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import org.houxg.willswidget.R;

public class WeightGoalRangeBar extends RangeBar {

    private CurrentWeightAnchor mCurrentWeightAnchor;
    private WeightGoalThumb mWeightGoalThumb;

    public WeightGoalRangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources resources = context.getResources();

        Drawable bar = new LineBar(context, Color.LTGRAY);
        setBar(bar);
        int weightColor = resources.getColor(R.color.weight_color);
        mWeightGoalThumb = new WeightGoalThumb(context, weightColor, Color.LTGRAY, Color.DKGRAY);
        setThumb(mWeightGoalThumb);
        setProgressLine(new ColoredProgressLine(context, resources.getColor(R.color.weight_color)));
        mCurrentWeightAnchor = new CurrentWeightAnchor(context, "", bar.getIntrinsicHeight());
        addAnchor(mCurrentWeightAnchor);
    }

    public void setCurrentWeightText(String text) {
        mCurrentWeightAnchor.setCurrentWeightText(text);
        invalidate();
    }

    public void setGoalText(String text) {
        mWeightGoalThumb.setGoalText(text);
        invalidate();
    }
}
