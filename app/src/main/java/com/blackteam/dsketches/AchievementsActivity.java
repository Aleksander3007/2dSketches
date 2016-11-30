package com.blackteam.dsketches;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class AchievementsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        Bundle extra = getIntent().getBundleExtra(MainActivity.ACHIEVEMENT_DATA);
        ArrayList<Achievement> achievements = (ArrayList<Achievement>) extra.getSerializable("objects");

        LinearLayout achievementsContainter = (LinearLayout) findViewById(R.id.achievements);

        for (Achievement ach : achievements) {
            final View achievementView = getLayoutInflater().inflate(R.layout.achievement_layout, null);
            TextView achievementName = (TextView) achievementView.findViewById(R.id.ach_name);
            achievementName.setText(ach.getName());

            TextView achievementDescription = (TextView) achievementView.findViewById(R.id.ach_description);
            achievementDescription.setText(ach.getDescription());

            ImageView achievementImage = (ImageView) achievementView.findViewById(R.id.ach_image);
            if (ach.isEarned())
                achievementImage.setImageResource(R.drawable.star);
            else
                achievementImage.setImageResource(R.drawable.star_noactive);

            achievementsContainter.addView(achievementView);
        }
    }
}
