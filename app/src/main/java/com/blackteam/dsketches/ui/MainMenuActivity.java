package com.blackteam.dsketches.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blackteam.dsketches.R;

public class MainMenuActivity extends Activity {

    public static final String EXTRA_ACHIEVEMENT_DATA = "com.blackteam.dsketches.EXTRA_ACHIEVEMENT_DATA";
    public static final String EXTRA_SKETCHES_DATA = "com.blackteam.dsketches.EXTRA_SKETCHES_DATA";

    public final static String CMD_RESTART_LVL = "com.blackteam.dsketches.CMD_RESTART";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void closeBtnOnClick(View view) {
        this.finish();
    }

    public void restartBtnOnClick(View view) {
        Intent answerIntent = new Intent();
        answerIntent.putExtra(CMD_RESTART_LVL, true);
        setResult(RESULT_OK, answerIntent);
        this.finish();
    }

    public void achievementsBtnOnClick(View view) {
        Intent achievementsIntent = new Intent(getBaseContext(), AchievementsActivity.class);
        achievementsIntent.putExtra(AchievementsActivity.EXTRA_ACHIEVEMENT_DATA,
                getIntent().getExtras().getBundle(EXTRA_ACHIEVEMENT_DATA)
        );
        startActivity(achievementsIntent);
    }

    public void sketchesBtnOnClick(View view) {
        Intent sketchesIntent = new Intent(getBaseContext(), SketchesActivity.class);
        sketchesIntent.putExtra(EXTRA_SKETCHES_DATA,
                getIntent().getExtras().getBundle(SketchesActivity.EXTRA_SKETCHES_DATA)
        );
        startActivity(sketchesIntent);
    }

    public void exitBtnOnClick(View view) {
        this.finishAffinity();
    }
}
