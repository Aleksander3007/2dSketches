package com.blackteam.dsketches;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SketchesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketches);

        Bundle extra = getIntent().getBundleExtra(MainActivity.SKETCHES_DATA);
        ArrayList<Sketch> sketches = (ArrayList<Sketch>) extra.getSerializable("objects");

        LinearLayout achievementsContainter = (LinearLayout) findViewById(R.id.sketchesLayout);
        for (Sketch sketch : sketches) {
            final View sketchView = getLayoutInflater().inflate(R.layout.sketch_layout, null);
            TextView sketchNameTextView = (TextView) sketchView.findViewById(R.id.sketch_name);
            sketchNameTextView.setText(sketch.getName());
            TextView sketchProfitTextView = (TextView) sketchView.findViewById(R.id.sketch_profit);
            sketchProfitTextView.setText("+" + String.valueOf(sketch.getCost()));
            achievementsContainter.addView(sketchView);
        }
    }
}
