package com.blackteam.dsketches.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackteam.dsketches.Achievement;
import com.blackteam.dsketches.R;

import java.util.ArrayList;

public class AchievementsActivity extends Activity {

    public static final String TAG = SketchesActivity.class.getSimpleName();

    public static final String EXTRA_ACHIEVEMENT_DATA = "com.blackteam.dsketches.EXTRA_ACHIEVEMENT_DATA";
    public static final String BUNDLE_ACHIEVEMENT_ARRAY = "BUNDLE_ACHIEVEMENT_ARRAY";

    private LinearLayout mAchievementsContainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        mAchievementsContainter = (LinearLayout) findViewById(R.id.achievements);

        Bundle extra = getIntent().getBundleExtra(EXTRA_ACHIEVEMENT_DATA);
        @SuppressWarnings("unchecked")
        ArrayList<Achievement> achievements = (ArrayList<Achievement>)
                extra.getSerializable(BUNDLE_ACHIEVEMENT_ARRAY);

        try {
            for (Achievement achievement : achievements) addAchievementView(achievement);
        }
        catch (NullPointerException npex) {
            Log.e(TAG, npex.getMessage() + ": achievements array не найден в Extra bundle.");
            Toast.makeText(this, R.string.achievements_array_is_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    private void addAchievementView(Achievement achievement) {
        final View achievementView = getLayoutInflater()
                .inflate(R.layout.achievement_layout, mAchievementsContainter, false);
        TextView achievementName = (TextView) achievementView.findViewById(R.id.ach_name);
        achievementName.setText(achievement.getName());

        TextView achievementDescription = (TextView) achievementView.findViewById(R.id.ach_description);
        achievementDescription.setText(achievement.getDescription());

        ImageView achievementImage = (ImageView) achievementView.findViewById(R.id.ach_image);
        if (achievement.isEarned())
            achievementImage.setImageResource(R.drawable.star);
        else
            achievementImage.setImageResource(R.drawable.star_noactive);

        mAchievementsContainter.addView(achievementView);
    }
}
