package com.emmaguy.animations;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    private AnimationView mAnimationView;
    private TextView mRangeValueTextView;
    private SeekBar mRangeSeekBar;
    private View mRangeOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRangeSeekBar = (SeekBar) findViewById(R.id.range);
        mRangeOptions = findViewById(R.id.range_options);
        mAnimationView = (AnimationView) findViewById(R.id.animation_view);
        mRangeValueTextView = (TextView) findViewById(R.id.range_value);

        mRangeSeekBar.setOnSeekBarChangeListener(this);
        mRangeSeekBar.setMax(1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.show_path).setChecked(mAnimationView.getShouldDrawPath());
        menu.findItem(R.id.show_range).setChecked(mRangeOptions.getVisibility() == View.VISIBLE);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_path:
                mAnimationView.setShouldDrawPath(item.isChecked());
                return true;
            case R.id.show_range:
                int visibility = mRangeOptions.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                mRangeOptions.setVisibility(visibility);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mRangeValueTextView.setText("" + progress);
        mAnimationView.setRange(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
