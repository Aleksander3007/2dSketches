package com.blackteam.dsketches;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainMenuActivity extends Activity {

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
        achievementsIntent.putExtra(MainActivity.ACHIEVEMENT_DATA,
                getIntent().getExtras().getBundle(MainActivity.ACHIEVEMENT_DATA)
        );
        startActivity(achievementsIntent);
    }

    public void sketchesBtnOnClick(View view) {
        Intent sketchesIntent = new Intent(getBaseContext(), SketchesActivity.class);
        sketchesIntent.putExtra(MainActivity.SKETCHES_DATA,
                getIntent().getExtras().getBundle(MainActivity.SKETCHES_DATA)
        );
        startActivity(sketchesIntent);
    }

    public void exitBtnOnClick(View view) {
        this.finishAffinity();
    }
}
