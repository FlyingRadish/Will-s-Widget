package org.houxg.willswidget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.houxg.willswidget.widget.TutorialView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tutorial)
    TutorialView tutorialView;
    @Bind(R.id.target_view)
    View targetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_a)
    void clickA() {
        Log.i("will", "click A");
        tutorialView.setTargetView(targetView);
        tutorialView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_b)
    void clickB() {
        Log.i("will", "click B");
    }

    @OnClick(R.id.btn_c)
    void clickC() {
        Log.i("will", "click C");
    }

    @OnClick(R.id.btn_d)
    void clickD() {
        Log.i("will", "click D");
    }
}
