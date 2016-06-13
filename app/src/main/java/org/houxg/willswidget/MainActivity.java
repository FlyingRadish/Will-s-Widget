package org.houxg.willswidget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.houxg.willswidget.widget.rangebar.RangeBar;
import org.houxg.willswidget.widget.rangebar.WeightGoalRangeBar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    int[] intDemo = new int[]{2, 3, 4};

    @Bind(R.id.range_bar)
    RangeBar rangeBar;

    @Bind(R.id.tv_message)
    TextView mMsgTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Context context = this;
        rangeBar.setProgressChangedCallback(new RangeBar.ProgressChangedCallback() {
            @Override
            public void onProgressChanged(RangeBar rangeBar, int progress, int originalPoint) {
                mMsgTv.setText("progress=" + progress + ", original=" + originalPoint);
                WeightGoalRangeBar weightGoalRangeBar = (WeightGoalRangeBar) rangeBar;
                weightGoalRangeBar.setGoalText("增加" + progress + "kg");
            }
        });
    }

    @OnClick(R.id.btn_a)
    void clickA() {
        Log.i("will", "click A");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("shine://authorize?response_type=token&app_id=QMGjHDa2OPAAAdLI&secret_hash=7dXpmYEosW8LtI5eS7qrE4kdyXrCj3OM"));
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        Toast.makeText(this, "size=" + activities.size(), Toast.LENGTH_SHORT);
        if (isIntentSafe) {
            startActivity(intent);
        }
    }

    @OnClick(R.id.btn_b)
    void clickB() {
        Log.i("will", "click B");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("shine-internal://authorize?response_type=token&app_id=QMGjHDa2OPAAAdLI&secret_hash=7dXpmYEosW8LtI5eS7qrE4kdyXrCj3OM"));

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        Toast.makeText(this, "size=" + activities.size(), Toast.LENGTH_SHORT);
        if (isIntentSafe) {
            startActivity(intent);
        }
    }

    @OnClick(R.id.btn_c)
    void clickC() {
        Log.i("will", "click C");
        rangeBar.setProgress(29);
    }

    @OnClick(R.id.btn_d)
    void clickD() {
        Log.i("will", "click D");
    }
}
