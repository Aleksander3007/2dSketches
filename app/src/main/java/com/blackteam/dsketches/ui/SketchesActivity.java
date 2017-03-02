package com.blackteam.dsketches.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackteam.dsketches.R;
import com.blackteam.dsketches.Sketch;

import java.util.ArrayList;

public class SketchesActivity extends Activity {

    public static final String TAG = SketchesActivity.class.getSimpleName();

    public static final String EXTRA_SKETCHES_DATA = "com.blackteam.dsketches.EXTRA_SKETCHES_DATA";
    public static final String BUNDLE_SKETCHES_ARRAY = "BUNDLE_SKETCHES_ARRAY";

    private LinearLayout mAchievementsContainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketches);

        mAchievementsContainter = (LinearLayout) findViewById(R.id.sketchesLayout);

        Bundle extra = getIntent().getBundleExtra(EXTRA_SKETCHES_DATA);
        @SuppressWarnings("unchecked")
        ArrayList<Sketch> sketches = (ArrayList<Sketch>) extra.getSerializable(BUNDLE_SKETCHES_ARRAY);

        try {
            for (Sketch sketch : sketches) addSketchView(sketch);
        }
        catch (NullPointerException npex) {
            Log.e(TAG, npex.getMessage() + ": sketches array не найден в Extra bundle.");
            Toast.makeText(this, R.string.sketches_array_is_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    private void addSketchView(Sketch sketch) {
        final View sketchView = getLayoutInflater()
                .inflate(R.layout.sketch_layout, mAchievementsContainter, false);

        ImageView sketchImage = (ImageView) sketchView.findViewById(R.id.sketch_img);
        sketchImage.setImageBitmap(sketch.getImage(128));

        TextView sketchNameTextView = (TextView) sketchView.findViewById(R.id.sketch_name);
        sketchNameTextView.setText(sketch.getName());

        TextView sketchProfitTextView = (TextView) sketchView.findViewById(R.id.sketch_profit);
        sketchProfitTextView.setText("+" + String.valueOf(sketch.getCost()));

        mAchievementsContainter.addView(sketchView);
    }
}
