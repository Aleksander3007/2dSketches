package com.blackteam.dsketches.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackteam.dsketches.R;
import com.blackteam.dsketches.Sketch;
import com.blackteam.dsketches.ui.MainActivity;

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

            ImageView sketchImage = (ImageView) sketchView.findViewById(R.id.sketch_img);
            sketchImage.setImageBitmap(sketch.getImage(128));

            TextView sketchNameTextView = (TextView) sketchView.findViewById(R.id.sketch_name);
            sketchNameTextView.setText(sketch.getName());

            TextView sketchProfitTextView = (TextView) sketchView.findViewById(R.id.sketch_profit);
            sketchProfitTextView.setText("+" + String.valueOf(sketch.getCost()));

            achievementsContainter.addView(sketchView);
        }
    }
}
