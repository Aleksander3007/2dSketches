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
}
